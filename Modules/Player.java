package Modules;

import GameLib.GameLib;

import java.awt.*;
import java.util.List;

public class Player extends Entidade{
    private double explosionStart;
    private double explosionEnd;
    private long nextShot;

    public Player(EstadosEnum state, double x, double y, double VX, double VY,
                  double radius, double explosionStart, double explosionEnd, long nextShot) {
        super(state, x, y, VX, VY, radius);
        this.explosionStart = explosionStart;
        this.explosionEnd = explosionEnd;
        this.nextShot = nextShot;
    }

    public void verificaSeExplosaoAcabou(long currentTime) {
        if(this.getState() == EstadosEnum.EXPLODING){

            if(currentTime > this.getExplosionEnd()){

                this.setState(EstadosEnum.ACTIVE);
            }
        }
    }

    public void verificaCoordenadasDentroJogo() {
        if(this.getX() < 0.0) this.setX(0.0);
        if(this.getX() >= GameLib.WIDTH) this.setX(GameLib.WIDTH - 1);
        if(this.getY() < 25.0) this.setY(25.0);
        if(this.getY() >= GameLib.HEIGHT) this.setY(GameLib.HEIGHT - 1);

    }

    public void resetPlayerStats() {
        this.setVX(Constantes.V_INICIAL);
        this.setVY(Constantes.V_INICIAL);
    }

    public void verificaEntradaUsuario(List<Projetil> projetils, long currentTime, long delta) {
        if(this.getState() == EstadosEnum.ACTIVE){

            if(GameLib.iskeyPressed(GameLib.KEY_UP)) this.setY(this.getY() - delta * this.getVY());
            if(GameLib.iskeyPressed(GameLib.KEY_DOWN)) this.setY(this.getY() + delta * this.getVY());
            if(GameLib.iskeyPressed(GameLib.KEY_LEFT)) this.setX(this.getX() - delta * this.getVX());
            if(GameLib.iskeyPressed(GameLib.KEY_RIGHT)) this.setX(this.getX() + delta * this.getVX());

            if(GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {

                if(currentTime > this.getNextShot()){

                    int free = Utils.findFreeIndex(projetils);

                    if(free < projetils.size()){

                        projetils.get(free).setX(this.getX());
                        projetils.get(free).setY(this.getY() - 2 * this.getRadius());
                        projetils.get(free).setVX(0.0);
                        projetils.get(free).setVY(-1.0);
                        projetils.get(free).setState(EstadosEnum.ACTIVE);
                        this.setNextShot(currentTime + 100);
                    }
                }
            }
        }
    }

    public <T extends Entidade> void MortePlayer(List<T> entidade, long currentTime) {
        for (T t : entidade) {

            double dx = t.getX() - this.getX();
            double dy = t.getY() - this.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < (this.getRadius() + t.getRadius()) * 0.8) {
                resetPlayerStats();
                this.setState(EstadosEnum.EXPLODING);
                this.setExplosionStart(currentTime);
                this.setExplosionEnd(currentTime + 2000);
            }
        }
    }

    public void colisaoPowerUp(List<PowerUp> powerUps, long currentTime) {
        for (PowerUp powerup : powerUps) {

            double dx = powerup.getX() - this.getX();
            double dy = powerup.getY() - this.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < (this.getRadius() + powerup.getRadius()) * 0.8) {
                if(!powerup.isEfeitoAplicado()) {
                    this.setVX(this.getVX() * powerup.getAumentarStatus());
                    this.setVY(this.getVY() * powerup.getAumentarStatus());
                    powerup.setExpiracao(currentTime + 30000);
                    powerup.setEfeitoAplicado(true);
                }
                powerup.setState(EstadosEnum.INACTIVE);
            }
        }
    }

    public void atualizarPowerUps(List<PowerUp> powerUps, long currentTime) {
        for (PowerUp powerup : powerUps) {
            if (powerup.isEfeitoAplicado() && currentTime > powerup.getExpiracao()) {
                this.setVX(Constantes.V_INICIAL);
                this.setVY(Constantes.V_INICIAL);
                powerup.setState(EstadosEnum.INACTIVE); // remove da tela
                powerup.setEfeitoAplicado(false);
            }
        }
    }

    public void desenhaPlayer(long currentTime) {
        if(this.getState() == EstadosEnum.EXPLODING){

            double alpha = (currentTime - this.getExplosionStart()) / (this.getExplosionEnd() - this.getExplosionStart());
            GameLib.drawExplosion(this.getX(), this.getY(), alpha);
        }
        else{

            GameLib.setColor(Color.RED);
            GameLib.drawPlayer(this.getX(), this.getY(), this.getRadius());
        }
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
}
