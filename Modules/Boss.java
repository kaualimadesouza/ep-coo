package Modules;

public class Boss extends EnemyBase{
    public Boss(EstadosEnum state, double x, double y, double v, double angle, double RV, double explosionStart, double explosionEnd, double radius) {
        super(state, x, y, v, angle, RV, explosionStart, explosionEnd, radius);
    }
}
