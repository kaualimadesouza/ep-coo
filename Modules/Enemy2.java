package Modules;

public class Enemy2 extends EnemyBase{
    private double spawnX;

    public Enemy2(int state, double x, double y, double v, double angle, double RV, double explosionStart, double explosionEnd, double radius, double spawnX) {
        super(state, x, y, v, angle, RV, explosionStart, explosionEnd, radius);
        this.spawnX = spawnX;
    }

    public double getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(double spawnX) {
        this.spawnX = spawnX;
    }
}
