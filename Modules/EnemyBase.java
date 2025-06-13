package Modules;

import java.util.List;

public abstract class EnemyBase extends Entidade {
    private double V;
    private double angle;
    private double RV;
    private double explosionStart;
    private double explosionEnd;

    public EnemyBase(EstadosEnum state, double x, double y, double v, double angle, double RV, double explosionStart, double explosionEnd, double radius) {
        super(state, x, y, radius);
        V = v;
        this.angle = angle;
        this.RV = RV;
        this.explosionStart = explosionStart;
        this.explosionEnd = explosionEnd;
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
}
