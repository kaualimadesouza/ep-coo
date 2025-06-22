package Modules.Enemy;

import GameLib.GameLib;
import Modules.Others.Entidade;
import Modules.Enum.EstadosEnum;
import Modules.Player.Player;
import Modules.Others.Projetil;

import java.awt.*;
import java.util.List;

public class Enemy1 extends EnemyBase {
    private static long nextEnemy1 = 0;
    private static long enemy2_spawnX = 0;
    private long nextShoot;
    public Enemy1(EstadosEnum state, double x, double y, double v, double angle, double RV, double explosionStart, double explosionEnd, double radius, long nextShoot, long nextEnemy1) {
        super(state, x, y, v, angle, RV, explosionStart, explosionEnd, radius);
        Enemy1.nextEnemy1 = nextEnemy1;
        this.nextShoot = nextShoot;
    }

    public static Enemy1 criaInimigo1Vazio(double radius, long currentTime) {
        return new Enemy1(EstadosEnum.INACTIVE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, radius, 0L, currentTime + 2000);
    }

    public static <T extends Entidade> int findFreeIndex(List<T> stateArray) {

        int i;

        for (i = 0; i < stateArray.size(); i++) {

            if (stateArray.get(i).getState() == EstadosEnum.INACTIVE) break;
        }

        return i;
    }

    public static long getEnemy2_spawnX() {
        return enemy2_spawnX;
    }

    public static void setEnemy2_spawnX(long enemy2_spawnX) {
        Enemy1.enemy2_spawnX = enemy2_spawnX;
    }

    public void comportamento(long currentTime, long delta, Player player, List<Projetil> e_projetils) {
        if (this.getState() == EstadosEnum.EXPLODING) {

            if (currentTime > this.getExplosionEnd()) {

                this.setState(EstadosEnum.INACTIVE);
            }
        }

        if (this.getState() == EstadosEnum.ACTIVE) {

            /* verificando se inimigo saiu da tela */
            if (this.getY() > GameLib.HEIGHT + 10) {

                this.setState(EstadosEnum.INACTIVE);
            } else {

                this.setX(this.getV() * Math.cos(this.getAngle()) * delta + this.getX());
                this.setY(this.getV() * Math.sin(this.getAngle()) * delta * (-1.0) + this.getY());
                this.setAngle(this.getRV() * delta + this.getAngle());

                if (currentTime > this.getNextShoot() && this.getY() < player.getY()) {

                    int free = findFreeIndex(e_projetils);

                    if (free < e_projetils.size()) {

                        e_projetils.get(free).setX(this.getX());
                        e_projetils.get(free).setY(this.getY());
                        e_projetils.get(free).setVX(Math.cos(this.getAngle()) * 0.45);
                        e_projetils.get(free).setVY(Math.sin(this.getAngle()) * 0.45 * (-1.0));
                        e_projetils.get(free).setState(EstadosEnum.ACTIVE);

                        this.setNextShoot((long) (currentTime + 200 + Math.random() * 500));
                    }
                }
            }
        }
    }

    public void desenhaInimigo(long currentTime) {
        if (this.getState() == EstadosEnum.EXPLODING) {

            double alpha = (currentTime - this.getExplosionStart()) / (this.getExplosionEnd() - this.getExplosionStart());
            GameLib.drawExplosion(this.getX(), this.getY(), alpha);
        }

        if (this.getState() == EstadosEnum.ACTIVE) {

            GameLib.setColor(Color.CYAN);
            GameLib.drawCircle(this.getX(), this.getY(), this.getRadius());
        }
    }

    public static long getNextEnemy1() {
        return nextEnemy1;
    }

    public static void setNextEnemy1(long nextEnemy1) {
        Enemy1.nextEnemy1 = nextEnemy1;
    }

    public long getNextShoot() {
        return nextShoot;
    }

    public void setNextShoot(long nextShoot) {
        this.nextShoot = nextShoot;
    }
}
