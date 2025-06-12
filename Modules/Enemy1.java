package Modules;

public class Enemy1 extends EnemyBase{
    private long nextShoot;

    public Enemy1(EstadosEnum state, double x, double y, double v, double angle, double RV, double explosionStart, double explosionEnd, double radius, long nextShoot) {
        super(state, x, y, v, angle, RV, explosionStart, explosionEnd, radius);
        this.nextShoot = nextShoot;
    }

    public long getNextShoot() {
        return nextShoot;
    }

    public void setNextShoot(long nextShoot) {
        this.nextShoot = nextShoot;
    }
}
