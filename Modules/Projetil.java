package Modules;

public class Projetil extends Entidade{
    private double radius;
    public Projetil(EstadosEnum state, double x, double y, double VX, double VY, double radius) {
        super(state, x, y, VX, VY);
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
