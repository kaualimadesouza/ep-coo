package Modules.Player;

import GameLib.GameLib;
import Modules.Enum.EstadosEnum;
import Modules.Others.Entidade;
import Modules.PowerUps.PowerUp1;
import Modules.Others.Projetil;
import Modules.PowerUps.PowerUp2;
import Modules.Utils.Constantes;

import java.awt.*;
import java.util.List;

public class Player extends Entidade {
    private int vida;
    private double explosionStart;
    private double explosionEnd;
    private long nextShot;
    private long invulneravelAte;
    private boolean comBoostVelocidade = false;
    private long powerUp2Expiracao = 0;
    private int powerUp2Level = 0; // 0 = tiro simples, 1 = 2 tiros, 2 = 4 tiros, etc

    public Player(EstadosEnum state, double x, double y, double VX, double VY,
                  double radius, double explosionStart, double explosionEnd, long nextShot, int vida) {
        super(state, x, y, VX, VY, radius);
        this.explosionStart = explosionStart;
        this.explosionEnd = explosionEnd;
        this.nextShot = nextShot;
        this.vida = vida;
    }

    public void verificaSeExplosaoAcabou(long currentTime) {
        if (this.getState() == EstadosEnum.EXPLODING) {
            if (currentTime > this.getExplosionEnd()) {
                this.setState(EstadosEnum.ACTIVE);
            }
        }
    }

    public void verificaCoordenadasDentroJogo() {
        if (this.getX() < 0.0) this.setX(0.0);
        if (this.getX() >= GameLib.WIDTH) this.setX(GameLib.WIDTH - 1);
        if (this.getY() < 25.0) this.setY(25.0);
        if (this.getY() >= GameLib.HEIGHT) this.setY(GameLib.HEIGHT - 1);
    }

    public void resetSpeed() {
        this.setVX(Constantes.V_INICIAL);
        this.setVY(Constantes.V_INICIAL);
    }

    public static int findFreeIndex(List<? extends Entidade> entidades, int startIndex) {
        for (int i = startIndex; i < entidades.size(); i++) {
            if (entidades.get(i).getState() == EstadosEnum.INACTIVE) {
                return i;
            }
        }
        return entidades.size(); // Nenhum livre encontrado
    }

    public void verificaEntradaUsuario(List<Projetil> projetils, long currentTime, long delta) {
        if (this.getState() == EstadosEnum.ACTIVE) {

            // Movimento do player
            if (GameLib.iskeyPressed(GameLib.KEY_UP)) this.setY(this.getY() - delta * this.getVY());
            if (GameLib.iskeyPressed(GameLib.KEY_DOWN)) this.setY(this.getY() + delta * this.getVY());
            if (GameLib.iskeyPressed(GameLib.KEY_LEFT)) this.setX(this.getX() - delta * this.getVX());
            if (GameLib.iskeyPressed(GameLib.KEY_RIGHT)) this.setX(this.getX() + delta * this.getVX());

            // Disparo
            if (GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {

                if (currentTime > this.getNextShot()) {

                    int lastFree = 0;

                    if (currentTime < powerUp2Expiracao && powerUp2Level > 0) {
                        int numTiros = (int) Math.pow(2, powerUp2Level);
                        double espacamento = 15.0;

                        for (int i = 0; i < numTiros; i++) {
                            int free = Player.findFreeIndex(projetils, lastFree);
                            if (free >= projetils.size()) break;

                            double deslocamentoX = (i - (numTiros - 1) / 2.0) * espacamento;

                            Projetil p = projetils.get(free);
                            p.setX(this.getX() + deslocamentoX);
                            p.setY(this.getY() - 2 * this.getRadius());
                            p.setVX(0.0);
                            p.setVY(-1.0);
                            p.setState(EstadosEnum.ACTIVE);

                            lastFree = free + 1;
                        }
                    } else {
                        // Tiro simples sem power-up
                        int free = Player.findFreeIndex(projetils, lastFree);
                        if (free < projetils.size()) {
                            Projetil p = projetils.get(free);
                            p.setX(this.getX());
                            p.setY(this.getY() - 2 * this.getRadius());
                            p.setVX(0.0);
                            p.setVY(-1.0);
                            p.setState(EstadosEnum.ACTIVE);
                        }
                    }

                    this.setNextShot(currentTime + 250);
                }
            }
        }
    }

    public <T extends Entidade> void MortePlayer(List<T> entidades, long currentTime) {
        if (currentTime < this.getInvulneravelAte()) return;

        for (T t : entidades) {
            if (t.getY() < 0 || t.getY() > GameLib.HEIGHT) continue;

            double dx = t.getX() - this.getX();
            double dy = t.getY() - this.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < (this.getRadius() + t.getRadius()) * 0.8) {
                if (this.vida > 0 && this.getState() == EstadosEnum.ACTIVE) {
                    resetSpeed();
                    this.setState(EstadosEnum.EXPLODING);
                    this.setExplosionStart(currentTime);
                    this.setExplosionEnd(currentTime + 2000);
                    this.vida--;
                    this.invulneravelAte = currentTime + 3000;
                }
            }
        }
    }

    public void colisaoPowerUp(List<PowerUp1> powerUps, long currentTime) {
        for (PowerUp1 powerup : powerUps) {
            double dx = powerup.getX() - this.getX();
            double dy = powerup.getY() - this.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < (this.getRadius() + powerup.getRadius()) * 0.8) {
                if (!powerup.isEfeitoAplicado()) {
                    this.setVX(this.getVX() * powerup.getAumentarStatus());
                    this.setVY(this.getVY() * powerup.getAumentarStatus());
                    powerup.setExpiracao(currentTime + 30000);
                    powerup.setEfeitoAplicado(true);
                    this.comBoostVelocidade = true;
                }
                powerup.setState(EstadosEnum.INACTIVE);
            }
        }
    }

    public void colisaoPowerUp2(List<PowerUp2> powerUps2, long currentTime) {
        for (PowerUp2 powerup : powerUps2) {
            double dx = powerup.getX() - this.getX();
            double dy = powerup.getY() - this.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < (this.getRadius() + powerup.getRadius()) * 1.2) {
                if (!powerup.isEfeitoAplicado()) {
                    this.powerUp2Level++;
                    this.powerUp2Expiracao = currentTime + 30000;
                    powerup.setEfeitoAplicado(true);
                }
                powerup.setState(EstadosEnum.INACTIVE);
            }
        }

    }

    public void atualizarPowerUp2(long currentTime) {
        if (powerUp2Expiracao > 0 && currentTime > powerUp2Expiracao) {
            powerUp2Level = 0;
            powerUp2Expiracao = 0;
        }
    }

    public void atualizarPowerUps(List<PowerUp1> powerUps, long currentTime) {
        for (PowerUp1 powerup : powerUps) {
            if (powerup.isEfeitoAplicado() && currentTime > powerup.getExpiracao()) {
                this.setVX(Constantes.V_INICIAL);
                this.setVY(Constantes.V_INICIAL);
                powerup.setState(EstadosEnum.INACTIVE);
                powerup.setEfeitoAplicado(false);
                this.comBoostVelocidade = false;
            }
        }
    }

    public void desenhaPlayer(long currentTime) {
        if (this.getState() == EstadosEnum.EXPLODING) {
            double alpha = (currentTime - this.getExplosionStart()) / (this.getExplosionEnd() - this.getExplosionStart());
            alpha = Math.max(0.0, Math.min(1.0, alpha));
            GameLib.drawExplosion(this.getX(), this.getY(), alpha);
        } else {
            // Piscar durante invulnerabilidade
            if (currentTime < invulneravelAte && (currentTime / 200) % 2 == 0) return;

            double x = this.getX();
            double y = this.getY();
            double r = this.getRadius();

            // Efeito visual de velocidade
            if (comBoostVelocidade) {
                GameLib.setColor(Color.CYAN);
                GameLib.drawLine(x - r * 0.5, y + r, x - r * 1.5, y + r * 2);
                GameLib.drawLine(x, y + r, x, y + r * 2);
                GameLib.drawLine(x + r * 0.5, y + r, x + r * 1.5, y + r * 2);
            }

            // Efeito visual do PowerUp2 (tiros duplicados ou quadruplicados)
            if (powerUp2Level > 0) {
                GameLib.setColor(Color.MAGENTA);
                GameLib.drawDiamond(x, y, r * 1.2);

                GameLib.setColor(Color.WHITE);
                GameLib.drawDiamond(x, y, r * 0.8);

                GameLib.setColor(Color.CYAN);
                GameLib.drawLine(x - r * 1.4, y + r * 0.5, x - r * 1.4, y - r * 1.5);
                GameLib.drawLine(x + r * 1.4, y + r * 0.5, x + r * 1.4, y - r * 1.5);
            }

            // Desenho principal do player
            GameLib.setColor(Color.RED);
            GameLib.drawPlayer(x, y, r);
        }
    }


    // Getters e setters

    public long getInvulneravelAte() {
        return invulneravelAte;
    }

    public double getExplosionStart() {
        return explosionStart;
    }

    public void setExplosionStart(double explosionStart) {
        this.explosionStart = explosionStart;
    }

    public double getExplosionEnd() {
        return explosionEnd;
    }

    public void setExplosionEnd(double explosionEnd) {
        this.explosionEnd = explosionEnd;
    }

    public long getNextShot() {
        return nextShot;
    }

    public void setNextShot(long nextShot) {
        this.nextShot = nextShot;
    }

    public int getVida() {
        return vida;
    }
}
