package Modules;

import GameLib.GameLib;

import java.awt.*;
import java.util.List;

public class Boss extends EnemyBase {

    private long nextShoot;
    private int vida;
    private boolean isLancado = false;
    private boolean isDerrotado = false;

    public Boss(EstadosEnum state, double x, double y, double v, double angle, double RV,
                double explosionStart, double explosionEnd, double radius, long nextShoot, int vidaInicial) {
        super(state, x, y, v, angle, RV, explosionStart, explosionEnd, radius);
        this.nextShoot = nextShoot;
        this.vida = vidaInicial;
    }

    public static Boss criaBossInicial(long currentTime, double x, double y, int vida) {
        return new Boss(
                EstadosEnum.ACTIVE,
                x,
                y,
                0.2,
                0.0,
                0.0,
                0.0,
                0.0,
                12.5,
                currentTime + 2000,
                vida
        );
    }

    public void comportamento(long currentTime, List<Projetil> e_projetils) {
        if (this.getState() == EstadosEnum.EXPLODING) {
            if (currentTime > this.getExplosionEnd()) {
                this.setState(EstadosEnum.INACTIVE);
            }
        }

        if (this.getState() == EstadosEnum.ACTIVE) {
            double novaX = this.getX() + Math.cos(currentTime * 0.001) * 2.5;
            novaX = Math.max(this.getRadius(), Math.min(GameLib.WIDTH - this.getRadius(), novaX));
            this.setX(novaX);

            // Tiro radial
            if (currentTime > this.getNextShoot()) {
                int freeCount = 0;
                for (Projetil p : e_projetils) {
                    if (p.getState() == EstadosEnum.INACTIVE) freeCount++;
                }

                int tiros = Math.min(12, freeCount); // máximo 12 projéteis por tiro
                for (int i = 0, usados = 0; i < e_projetils.size() && usados < tiros; i++) {
                    Projetil p = e_projetils.get(i);
                    if (p.getState() == EstadosEnum.INACTIVE) {
                        double angle = 2 * Math.PI * usados / tiros;
                        p.setX(this.getX());
                        p.setY(this.getY());
                        p.setVX(Math.cos(angle) * 0.3);
                        p.setVY(Math.sin(angle) * 0.3);
                        p.setState(EstadosEnum.ACTIVE);
                        usados++;
                    }
                }
                this.setNextShoot(currentTime + 1500); // tempo entre disparos
            }
        }
    }

    public void desenhaInimigo(long currentTime) {
        if (this.getState() == EstadosEnum.EXPLODING) {
            double alpha = (currentTime - this.getExplosionStart()) / (this.getExplosionEnd() - this.getExplosionStart());
            GameLib.drawExplosion(this.getX(), this.getY(), alpha);
        }

        if (this.getState() == EstadosEnum.ACTIVE) {
            GameLib.setColor(Color.RED);
            GameLib.drawCircle(this.getX(), this.getY(), this.getRadius());
            GameLib.drawCircle(this.getX()+35, this.getY(), this.getRadius());
            GameLib.drawCircle(this.getX()-35, this.getY(), this.getRadius());
            GameLib.drawCircle(this.getX(), this.getY()-35, this.getRadius());
            GameLib.drawCircle(this.getX(), this.getY()+35, this.getRadius());
        }
    }

    private boolean colisaoComCirculo(double cx, double cy, Projetil p) {
        double dx = cx - p.getX();
        double dy = cy - p.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < (this.getRadius() + p.getRadius());
    }

    public boolean verificaColisaoComProjetil(Projetil projetil, long currentTime) {
        if (this.getState() != EstadosEnum.ACTIVE || projetil.getState() != EstadosEnum.ACTIVE) return false;

        boolean colidiu =
                colisaoComCirculo(getX(), getY(), projetil) ||
                        colisaoComCirculo(getX() + 35, getY(), projetil) ||
                        colisaoComCirculo(getX() - 35, getY(), projetil) ||
                        colisaoComCirculo(getX(), getY() + 35, projetil) ||
                        colisaoComCirculo(getX(), getY() - 35, projetil);

        if (colidiu) {
            projetil.setState(EstadosEnum.INACTIVE);
            this.vida--;

            if (this.vida < 1) {
                this.setState(EstadosEnum.EXPLODING);
                this.setExplosionStart(currentTime);
                this.setExplosionEnd(currentTime + 500);
            }
            return true;
        }

        return false;
    }

    public boolean isLancado() {
        return isLancado;
    }

    public void setLancado(boolean lancado) {
        isLancado = lancado;
    }

    public int getVida() {
        return vida;
    }

    public void setNextShoot(long nextShoot) {
        this.nextShoot = nextShoot;
    }

    public long getNextShoot() {
        return nextShoot;
    }

    public boolean estaDerrotado() {
        return this.getState() == EstadosEnum.INACTIVE;
    }
}
