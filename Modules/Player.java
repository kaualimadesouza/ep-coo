package Modules;

import java.util.List;

public class Player extends Entidade{
    private double radius;
    private double explosionStart;
    private double explosionEnd;
    private long nextShot;



    public Player(EstadosEnum state, double x, double y, double VX, double VY,
                  double radius, double explosionStart, double explosionEnd, long nextShot) {
        super(state, x, y, VX, VY); // chamada obrigatória
        this.radius = radius;
        this.explosionStart = explosionStart;
        this.explosionEnd = explosionEnd;
        this.nextShot = nextShot;
    }

    public void MortePlayer(List<Projetil> e_projetils, long currentTime) {
        for(int i = 0; i < e_projetils.size(); i++){

            double dx = e_projetils.get(i).getX() - this.getX();
            double dy = e_projetils.get(i).getY() - this.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if(dist < (this.getRadius() + e_projetils.get(i).getRadius()) * 0.8){

                this.setState(EstadosEnum.EXPLODING);
                this.setExplosionStart(currentTime);
                this.setExplosionEnd(currentTime + 2000);
            }
        }
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
