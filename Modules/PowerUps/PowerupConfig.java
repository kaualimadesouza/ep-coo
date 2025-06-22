package Modules.PowerUps;

public class PowerupConfig {
    private int tipo;
    private long quando;
    private double posX;
    private double posY;
    private boolean isLancado = false;

    public PowerupConfig(int tipo, long quando, double posX, double posY) {
        this.tipo = tipo;
        this.quando = quando;
        this.posX = posX;
        this.posY = posY;
    }

    public int getTipo() {
        return tipo;
    }
    public long getQuando() {
        return quando;
    }
    public double getPosX() {
        return posX;
    }
    public double getPosY() {
        return posY;
    }
    public boolean isLancado() {
        return isLancado;
    }
    public void setLancado(boolean lancado) {
        isLancado = lancado;
    }
}
