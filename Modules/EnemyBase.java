package Modules;

public abstract class EnemyBase extends Entidade {
    private double V;
    private double angle;
    private double RV;
    private double explosionStart;
    private double explosionEnd;
    private double radius;

    public EnemyBase(EstadosEnum state, double x, double y, double v, double angle, double RV, double explosionStart, double explosionEnd, double radius) {
        super(state, x, y);
        V = v;
        this.angle = angle;
        this.RV = RV;
        this.explosionStart = explosionStart;
        this.explosionEnd = explosionEnd;
        this.radius = radius;
    }

    public double getV() {
        return V;
    }

    public void setV(double v) {
        V = v;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getRV() {
        return RV;
    }

    public void setRV(double RV) {
        this.RV = RV;
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
