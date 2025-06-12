package Modules;

public abstract class Entidade {
    private EstadosEnum state;
    private double X;
    private double Y;
    private double VX;
    private double VY;

    public Entidade(EstadosEnum state, double x, double y, double VX, double VY) {
        this.state = state;
        X = x;
        Y = y;
        this.VX = VX;
        this.VY = VY;
    }

    public Entidade(EstadosEnum state, double x, double y) {
        this.state = state;
        X = x;
        Y = y;
    }

    public EstadosEnum getState() {
        return state;
    }

    public void setState(EstadosEnum state) {
        this.state = state;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public double getVX() {
        return VX;
    }

    public void setVX(double VX) {
        this.VX = VX;
    }

    public double getVY() {
        return VY;
    }

    public void setVY(double VY) {
        this.VY = VY;
    }
}
