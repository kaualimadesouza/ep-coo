package Modules.PowerUps;

import GameLib.GameLib;
import Modules.Enum.EstadosEnum;
import Modules.Others.Entidade;

import java.awt.*;

public class PowerUp1 extends Entidade implements PowerupInterface {
    private double aumentarStatus;
    private long expiracao;
    private boolean efeitoAplicado = false;


    public PowerUp1(EstadosEnum state, double x, double y, double radius, double aumentarStatus, double nextPowerUp) {
        super(state, x, y, radius);
        this.aumentarStatus = aumentarStatus;
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
            double x = this.getX();
            double y = this.getY();
            double r = this.getRadius();

            // Círculo central brilhante
            GameLib.setColor(Color.CYAN);
            GameLib.drawCircle(x, y, r);

            // Anel externo pulsante (efeito de brilho leve)
            double brilho = 0.5 * Math.sin(System.currentTimeMillis() * 0.005) + 1.5;
            GameLib.setColor(Color.BLUE);
            GameLib.drawCircle(x, y, r * brilho);

            // Cruz luminosa
            GameLib.setColor(Color.WHITE);
            GameLib.drawLine(x - r * 1.2, y, x + r * 1.2, y); // horizontal
            GameLib.drawLine(x, y - r * 1.2, x, y + r * 1.2); // vertical

            // Diamante ao redor para enfeitar
            GameLib.setColor(Color.MAGENTA);
            GameLib.drawDiamond(x, y, r * 1.3);
        }
    }

    public double getAumentarStatus() {
        return aumentarStatus;
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
