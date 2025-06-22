package Modules.Boss;

import GameLib.GameLib;
import Modules.Enemy.EnemyBase;
import Modules.Enum.EstadosEnum;
import Modules.Others.Projetil;

import java.awt.*;
import java.util.List;

public class Boss2 extends EnemyBase implements BossInterface {

    private long nextShoot;
    private int vida;
    private boolean isLancado = false;

    private long lastPatternChange = 0;
    private int currentPattern = 0;
    private double velocityX = 0;
    private double velocityY = 0;

    public Boss2(EstadosEnum state, double x, double y, double v, double angle, double RV,
                 double explosionStart, double explosionEnd, double radius,
                 long currentTime, int vidaInicial) {
        super(state, x, y, v, angle, RV, explosionStart, explosionEnd, radius);
        this.nextShoot = currentTime;
        this.vida = vidaInicial;
    }

    public static Boss2 criaBoss(long currentTime, double x, double y, int vida) {
        return new Boss2(
                EstadosEnum.ACTIVE,
                x,
                y,
                0.2,
                0.0,
                0.0,
                0.0,
                0.0,
                12.5,
                currentTime + 2000,
                vida
        );
    }

    @Override
    public boolean verificaColisaoComProjetil(Projetil projetil, long currentTime) {
        if (this.getState() != EstadosEnum.ACTIVE || projetil.getState() != EstadosEnum.ACTIVE) return false;

        boolean colidiu =
                colisaoComCirculo(getX(), getY(), projetil) ||
                        colisaoComCirculo(getX() + 35, getY(), projetil) ||
                        colisaoComCirculo(getX() - 35, getY(), projetil) ||
                        colisaoComCirculo(getX(), getY() + 35, projetil) ||
                        colisaoComCirculo(getX(), getY() - 35, projetil);

        if (colidiu) {
            projetil.setState(EstadosEnum.INACTIVE);
            this.vida--;

            if (this.vida < 1) {
                this.setState(EstadosEnum.EXPLODING);
                this.setExplosionStart(currentTime);
                this.setExplosionEnd(currentTime + 500);
            }
        }

        return colidiu;
    }

    private boolean colisaoComCirculo(double cx, double cy, Projetil p) {
        double dx = cx - p.getX();
        double dy = cy - p.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < (this.getRadius() + p.getRadius());
    }

    public void update(long currentTime, List<Projetil> e_projetils) {
        if (this.getState() == EstadosEnum.EXPLODING && currentTime > this.getExplosionEnd()) {
            this.setState(EstadosEnum.INACTIVE);
            return;
        }

        if (this.getState() == EstadosEnum.ACTIVE) {

            // Atualiza velocidade aleatória a cada 2 segundos
            if (currentTime - lastPatternChange > 2000) {
                lastPatternChange = currentTime;
                currentPattern = (currentPattern + 1) % 2;

                // Velocidade aleatória suave entre -1.5 e +1.5
                velocityX = (Math.random() - 0.5) * 3;
                velocityY = (Math.random() - 0.5) * 3;
            }

            // Atualiza posição com a velocidade atual
            double newX = this.getX() + velocityX;
            double newY = this.getY() + velocityY;

            // Mantém dentro dos limites da tela
            double r = this.getRadius();
            newX = Math.max(r, Math.min(GameLib.WIDTH - r, newX));
            newY = Math.max(r, Math.min(GameLib.HEIGHT / 2, newY)); // Boss fica na metade superior da tela

            this.setX(newX);
            this.setY(newY);

            if (currentTime > this.nextShoot) {

                int freeCount = 0;
                for (Projetil p : e_projetils) {
                    if (p.getState() == EstadosEnum.INACTIVE) freeCount++;
                }

                int tiros;

                if (currentPattern == 0) {
                    // Padrão 1: tiros em círculo (12 tiros)
                    tiros = Math.min(12, freeCount);
                    int usados = 0;
                    for (Projetil p : e_projetils) {
                        if (p.getState() == EstadosEnum.INACTIVE && usados < tiros) {
                            double angle = 2 * Math.PI * usados / tiros;
                            p.setX(this.getX());
                            p.setY(this.getY());
                            p.setVX(Math.cos(angle) * 0.3);
                            p.setVY(Math.sin(angle) * 0.3);
                            p.setState(EstadosEnum.ACTIVE);
                            usados++;
                        }
                    }
                } else {
                    // Padrão 2: rajada de 5 tiros em linha reta para baixo (ou direção fixa)
                    tiros = Math.min(5, freeCount);
                    int usados = 0;
                    double startX = this.getX() - 20;
                    for (Projetil p : e_projetils) {
                        if (p.getState() == EstadosEnum.INACTIVE && usados < tiros) {
                            p.setX(startX + usados * 10);
                            p.setY(this.getY());
                            p.setVX(0);
                            p.setVY(0.5);  // tiro direto para baixo
                            p.setState(EstadosEnum.ACTIVE);
                            usados++;
                        }
                    }
                }

                // Próximo tiro em 1.5s
                this.nextShoot = currentTime + 1500;
            }
        }
    }

    @Override
    public void desenhaBoss(long currentTime) {
        if (this.getState() == EstadosEnum.EXPLODING) {
            double alpha = (currentTime - this.getExplosionStart()) / (this.getExplosionEnd() - this.getExplosionStart());
            GameLib.drawExplosion(this.getX(), this.getY(), alpha);
        }

        if (this.getState() == EstadosEnum.ACTIVE) {
            double x = this.getX();
            double y = this.getY();
            double r = this.getRadius();

            // Núcleo pulsante
            GameLib.setColor(Color.MAGENTA);
            GameLib.drawCircle(x, y, r);

            // Olhos
            GameLib.setColor(Color.RED);
            GameLib.drawCircle(x - 15, y - 10, r * 0.3);
            GameLib.drawCircle(x + 15, y - 10, r * 0.3);

            // Armadura externa (diamantes em cruz)
            GameLib.setColor(Color.ORANGE);
            GameLib.drawDiamond(x + 45, y, r);
            GameLib.drawDiamond(x - 45, y, r);
            GameLib.drawDiamond(x, y + 45, r);
            GameLib.drawDiamond(x, y - 45, r);

            // Anel girando em volta (usa seno/cosseno para girar com o tempo)
            GameLib.setColor(Color.CYAN);
            double t = currentTime * 0.005;
            for (int i = 0; i < 8; i++) {
                double angle = t + i * Math.PI / 4;
                double dx = Math.cos(angle) * 60;
                double dy = Math.sin(angle) * 60;
                GameLib.drawCircle(x + dx, y + dy, 4);
            }

            // Antenas
            GameLib.setColor(Color.LIGHT_GRAY);
            GameLib.drawLine(x - 10, y - 20, x - 20, y - 40);
            GameLib.drawLine(x + 10, y - 20, x + 20, y - 40);
        }
    }

    @Override
    public boolean isLancado() {
        return isLancado;
    }

    @Override
    public void setLancado(boolean lancado) {
        this.isLancado = lancado;
    }

    @Override
    public int getVida() {
        return vida;
    }

    @Override
    public void setVida(int vida) {
        this.vida = vida;
    }

    @Override
    public long getNextShoot() {
        return nextShoot;
    }

    @Override
    public void setNextShoot(long nextShoot) {
        this.nextShoot = nextShoot;
    }

    @Override
    public boolean estaDerrotado() {
        return this.getState() == EstadosEnum.INACTIVE;
    }
}
