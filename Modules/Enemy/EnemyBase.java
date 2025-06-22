package Modules.Enemy;

import Modules.Others.Entidade;
import Modules.Enum.EstadosEnum;
import Modules.Others.Projetil;

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

    // Verifica colisão com projetils
    public boolean verificaColisaoComProjetil(Projetil projetil, long currentTime) {
        if (this.getState() != EstadosEnum.ACTIVE || projetil.getState() != EstadosEnum.ACTIVE) return false;

        double dx = this.getX() - projetil.getX();
        double dy = this.getY() - projetil.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        double colisaoDist = this.getRadius() + projetil.getRadius(); // projétil também pode ter raio

        if (dist < colisaoDist) {
            this.setState(EstadosEnum.EXPLODING);
            projetil.setState(EstadosEnum.INACTIVE);
            this.setExplosionStart(currentTime);
            this.setExplosionEnd(currentTime + 500);
            return true;
        }

        return false;
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
