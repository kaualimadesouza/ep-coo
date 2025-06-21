package Modules.PowerUps;

import GameLib.GameLib;
import Modules.Enum.EstadosEnum;
import Modules.Others.Entidade;

import java.awt.*;

public class PowerUp2 extends Entidade implements PowerupInterface {

    private double aumentarStatus;
    private static double nextPowerUp = System.currentTimeMillis() + 3000;
    private long expiracao;
    private boolean efeitoAplicado = false;

    public PowerUp2(EstadosEnum state, double x, double y, double radius, double aumentarStatus, double nextPowerUp) {
        super(state, x, y, radius);
        this.aumentarStatus = aumentarStatus;
        PowerUp2.nextPowerUp = nextPowerUp;
    }

    @Override
    public void atualizaEstadoPowerUp(long delta) {
        if (this.getState() == EstadosEnum.ACTIVE) {
            this.setY(this.getY() + 0.1 * delta);

            if (this.getY() > GameLib.HEIGHT) {
                this.setState(EstadosEnum.INACTIVE);
            }
        }
    }

    @Override
    public void desenhaPowerUp() {
        if (this.getState() == EstadosEnum.ACTIVE || this.isEfeitoAplicado()) {
            double x = this.getX();
            double y = this.getY();
            double r = this.getRadius();

            // Núcleo amarelo (poder ofensivo)
            GameLib.setColor(Color.YELLOW);
            GameLib.drawCircle(x, y, r * 0.8);

            // Anéis pulsantes ciano
            double brilho = 0.5 * Math.sin(System.currentTimeMillis() * 0.008) + 1.5;
            GameLib.setColor(Color.CYAN);
            GameLib.drawCircle(x, y, r * brilho);
            GameLib.drawCircle(x, y, r * (brilho + 0.3));

            // Símbolo de duplicação (cruz girada em "X")
            GameLib.setColor(Color.WHITE);
            GameLib.drawLine(x - r, y - r, x + r, y + r); // diagonal \
            GameLib.drawLine(x + r, y - r, x - r, y + r); // diagonal /

            // Diamante ao redor
            GameLib.setColor(Color.MAGENTA);
            GameLib.drawDiamond(x, y, r * 1.4);
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
        PowerUp2.nextPowerUp = nextPowerUp;
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
