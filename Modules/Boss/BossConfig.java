package Modules.Boss;

public final class BossConfig {
    /* Classe que armazena cada linha do arquivo de fase, cada instancia dessa classe na lista é um boss */

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

    public long getQuando() {
        return quando;
    }

    public int getPontosVida() {
        return pontosVida;
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
