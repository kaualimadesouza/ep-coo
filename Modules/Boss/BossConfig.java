package Modules.Boss;

public class BossConfig {
    private int tipo;
    private long quando;
    private int pontosVida;
    private double posX;
    private double posY;
    private boolean isLancado = false;

    public BossConfig(int tipo, int pontosVida, long quando, double posX, double posY) {
        this.tipo = tipo;
        this.pontosVida = pontosVida;
        this.quando = quando;
        this.posX = posX;
        this.posY = posY;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public long getQuando() {
        return quando;
    }

    public void setQuando(long quando) {
        this.quando = quando;
    }

    public int getPontosVida() {
        return pontosVida;
    }

    public void setPontosVida(int pontosVida) {
        this.pontosVida = pontosVida;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public boolean isLancado() {
        return isLancado;
    }

    public void setLancado(boolean lancado) {
        isLancado = lancado;
    }
}
