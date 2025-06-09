package Modules;

public class Player extends Entidade{
    private double radius;
    private double explosionStart;
    private double explosionEnd;
    private long nextShot;



    public Player(int state, double x, double y, double VX, double VY,
                  double radius, double explosionStart, double explosionEnd, long nextShot) {
        super(state, x, y, VX, VY); // chamada obrigatória
        this.radius = radius;
        this.explosionStart = explosionStart;
        this.explosionEnd = explosionEnd;
        this.nextShot = nextShot;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
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
