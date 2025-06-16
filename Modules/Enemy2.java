package Modules;

import GameLib.GameLib;

import java.awt.*;
import java.util.List;

public class Enemy2 extends EnemyBase{
    private static long enemy2_count = 0;
    private static long enemy2_spawnX = 0;
    private static long nextEnemy2;

    public Enemy2(EstadosEnum state, double x, double y, double v, double angle, double RV, double explosionStart, double explosionEnd, double radius, long spawnX) {
        super(state, x, y, v, angle, RV, explosionStart, explosionEnd, radius);
    }

    public static Enemy2 criaInimigo2Vazio(double radius) {
        return new Enemy2(EstadosEnum.INACTIVE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, radius, (long) (Constantes.ENEMY2_SPAWN_X_INICIAL));
    }

    public static <T extends Entidade> int [] findFreeIndex(List<T> stateArray, int amount){

        int i, k;
        int [] freeArray = new int[amount];

        for(i = 0; i < freeArray.length; i++) freeArray[i] = stateArray.size();

        for(i = 0, k = 0; i < stateArray.size() && k < amount; i++){

            if(stateArray.get(i).getState() == EstadosEnum.INACTIVE) {

                freeArray[k] = i;
                k++;
            }
        }

        return freeArray;
    }

    public static long getNextEnemy2() {
        return nextEnemy2;
    }

    public static void setNextEnemy2(long nextEnemy2) {
        Enemy2.nextEnemy2 = nextEnemy2;
    }

    public static long getEnemy2_count() {
        return enemy2_count;
    }

    public static void setEnemy2_count(long enemy2_count) {
        Enemy2.enemy2_count = enemy2_count;
    }

    public void comportamento(long currentTime, long delta, Player player, List<Projetil> e_projetils) {
        if(this.getState() == EstadosEnum.EXPLODING){

            if(currentTime > this.getExplosionEnd()){

                this.setState(EstadosEnum.INACTIVE);
            }
        }

        if(this.getState() == EstadosEnum.ACTIVE){

            /* verificando se inimigo saiu da tela */
            if(this.getX() < -10 || this.getX() > GameLib.WIDTH + 10 ) {

                this.setState(EstadosEnum.INACTIVE);
            }
            else {

                boolean shootNow = false;
                double previousY = this.getY();


                this.setX((this.getV() * Math.cos(this.getAngle()) * delta) + this.getX());
                this.setY(this.getV() * Math.sin(this.getAngle()) * delta * (-1.0) + this.getY());
                this.setAngle(this.getRV() * delta + this.getAngle());

                double threshold = GameLib.HEIGHT * 0.30;

                if(previousY < threshold && this.getY() >= threshold) {

                    if(this.getX() < (double) GameLib.WIDTH / 2) this.setRV(0.003);
                    else this.setRV(-0.003);
                }

                if(this.getRV() > 0 && Math.abs(this.getAngle() - 3 * Math.PI) < 0.05){
                    this.setRV(0.0);
                    this.setAngle(3 * Math.PI);
                    shootNow = true;
                }

                if(this.getRV() < 0 && Math.abs(this.getAngle()) < 0.05){
                    this.setRV(0.0);
                    this.setAngle(0.0);
                    shootNow = true;
                }

                if(shootNow){

                    double [] angles = { Math.PI/2 + Math.PI/8, Math.PI/2, Math.PI/2 - Math.PI/8 };
                    int [] freeArray = findFreeIndex(e_projetils, angles.length);

                    for(int k = 0; k < freeArray.length; k++){

                        int free = freeArray[k];

                        if(free < e_projetils.size()){

                            double a = angles[k] + Math.random() * Math.PI/6 - Math.PI/12;
                            double vx = Math.cos(a);
                            double vy = Math.sin(a);

                            e_projetils.get(free).setX(this.getX());
                            e_projetils.get(free).setY(this.getY());
                            e_projetils.get(free).setVX(vx * 0.30);
                            e_projetils.get(free).setVY(vy * 0.30);
                            e_projetils.get(free).setState(EstadosEnum.ACTIVE);
                        }
                    }
                }
            }
        }
    }


    public void desenhaInimigo(long currentTime) {
        if(this.getState() == EstadosEnum.EXPLODING){

            double alpha = (currentTime - this.getExplosionStart()) / (this.getExplosionEnd() - this.getExplosionStart());
            GameLib.drawExplosion(this.getX(), this.getY(), alpha);
        }

        if(this.getState() == EstadosEnum.ACTIVE){

            GameLib.setColor(Color.MAGENTA);
            GameLib.drawDiamond(this.getX(), this.getY(), this.getRadius());
        }
    }

    public static void verificaSeNovosEnemy2DevemSerLancados(long currentTime, List<Enemy2> enemies2) {
        if(currentTime > Enemy2.getNextEnemy2()){

            int free = Utils.findFreeIndex(enemies2);

            if(free < enemies2.size()){

                enemies2.get(free).setX(Enemy2.getEnemy2_spawnX());
                enemies2.get(free).setY(-10.0);
                enemies2.get(free).setV(0.42);
                enemies2.get(free).setAngle((3 * Math.PI) / 2);
                enemies2.get(free).setRV(0.0);
                enemies2.get(free).setState(EstadosEnum.ACTIVE);

                Enemy2.setEnemy2_count(Enemy2.getEnemy2_count() + 1);

                if(Enemy2.getEnemy2_count() < 10){

                    Enemy2.setNextEnemy2(currentTime + 120);
                }
                else {

                    Enemy2.setEnemy2_count(0);
                    Enemy2.setEnemy2_spawnX((long) (Math.random() > 0.5 ? GameLib.WIDTH * 0.2 : GameLib.WIDTH * 0.8));
                    Enemy2.setNextEnemy2((long) (currentTime + 3000 + Math.random() * 3000));
                }
            }
        }
    }


    public static long getEnemy2_spawnX() {
        return enemy2_spawnX;
    }

    public static void setEnemy2_spawnX(long enemy2_spawnX) {
        Enemy2.enemy2_spawnX = enemy2_spawnX;
    }
}
