package Modules;

import GameLib.GameLib;

import java.awt.*;
import java.util.List;

public class PowerUp extends Entidade {
    private double aumentarStatus;
    private static double nextPowerUp = System.currentTimeMillis() + 3000;
    private long expiracao;
    private boolean efeitoAplicado = false;



    public PowerUp(EstadosEnum state, double x, double y, double radius, double aumentarStatus, double nextPowerUp) {
        super(state, x, y, radius);
        this.aumentarStatus = aumentarStatus;
        PowerUp.nextPowerUp = nextPowerUp;
    }

    public static void verificaSeNovosPowerUpsDevemSerLancados(long currentTime, List<PowerUp> powerups) {
        if (currentTime > PowerUp.nextPowerUp) {

            int free = Utils.findFreeIndex(powerups);

            if (free < powerups.size()) {

                powerups.get(free).setX(Math.random() * (GameLib.WIDTH - 20.0) + 10.0);
                powerups.get(free).setY(-10.0);
                powerups.get(free).setState(EstadosEnum.ACTIVE);

                PowerUp.nextPowerUp = currentTime + 8000;
            }
        }
    }

    public void atualizaEstadoPowerUp(long delta) {
        if (this.getState() == EstadosEnum.ACTIVE) {
            this.setY(this.getY() + 0.1 * delta);

            if (this.getY() > GameLib.HEIGHT) {
                this.setState(EstadosEnum.INACTIVE);
            }
        }
    }

    public void desenhaPowerUp() {
        if (this.getState() == EstadosEnum.ACTIVE) {

            GameLib.setColor(Color.BLUE);
            GameLib.drawDiamond(this.getX(), this.getY(), this.getRadius());
        }
    }

    public double getNextPowerUp() {
        return nextPowerUp;
    }

    public double getAumentarStatus() {
        return aumentarStatus;
    }

    public void setAumentarStatus(double aumentarStatus) {
        this.aumentarStatus = aumentarStatus;
    }

    public void setNextPowerUp(double nextPowerUp) {
        this.nextPowerUp = nextPowerUp;
    }

    public boolean isEfeitoAplicado() {
        return efeitoAplicado;
    }

    public void setEfeitoAplicado(boolean efeitoAplicado) {
        this.efeitoAplicado = efeitoAplicado;
    }

    public long getExpiracao() {
        return expiracao;
    }

    public void setExpiracao(long expiracao) {
        this.expiracao = expiracao;
    }
}
