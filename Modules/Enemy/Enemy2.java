package Modules.Enemy;

import GameLib.GameLib;
import Modules.Enum.EstadosEnum;
import Modules.Utils.Utils;
import Modules.Player.Player;
import Modules.Others.Projetil;
import Modules.Utils.Constantes;

import java.awt.*;
import java.util.List;

public class Enemy2 extends EnemyBase {
    private static long enemy2_count = 0;
    private static long enemy2_spawnX = 0;
    private static long nextEnemy2;

    public Enemy2(EstadosEnum state, double x, double y, double v, double angle, double RV, double explosionStart, double explosionEnd, double radius, long spawnX) {
        super(state, x, y, v, angle, RV, explosionStart, explosionEnd, radius);
    }

    public static Enemy2 criaInimigo2Vazio(double radius) {
        return new Enemy2(EstadosEnum.INACTIVE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, radius, (long) (Constantes.ENEMY2_SPAWN_X_INICIAL));
    }

    public static long getEnemy2_count() {
        return enemy2_count;
    }

    public static void setEnemy2_count(long enemy2_count) {
        Enemy2.enemy2_count = enemy2_count;
    }

    // Função de update de estado do enemy1
    public void comportamento(long currentTime, long delta, Player player, List<Projetil> e_projetils) {

        if (getState() == EstadosEnum.EXPLODING) {
            verificarFimExplosao(currentTime);
            return;
        }

        if (getState() == EstadosEnum.ACTIVE) {
            if (verificarSaidaTela()) {
                setState(EstadosEnum.INACTIVE);
                return;
            }

            mover(delta);
            boolean podeAtirar = ajustarRotacao();
            if (podeAtirar) {
                atirar(e_projetils);
            }
        }
    }

    // Função Auxiliar de "Comportamento": verifica se a animação de explosão acabou e deixa o inimigo INACTIVE
    private void verificarFimExplosao(long currentTime) {
        if (currentTime > this.getExplosionEnd()) {
            this.setState(EstadosEnum.INACTIVE);
        }
    }

    // Função Auxiliar de "Comportamento": Verifica quando o inimigo sair da tela e torna ele inativo.
    private boolean verificarSaidaTela() {
        return getX() < -10 || getX() > GameLib.WIDTH + 10;
    }

    // Função Auxiliar de "Comportamento": realiza a movimentação do enemy2
    private void mover(long delta) {
        double novaX = getX() + getV() * Math.cos(getAngle()) * delta;
        double novaY = getY() - getV() * Math.sin(getAngle()) * delta;
        setX(novaX);
        setY(novaY);
        setAngle(getAngle() + getRV() * delta);
    }

    // Função Auxiliar de "Comportamento": Faz o enemy2 rodar
    private boolean ajustarRotacao() {
        double limiarY = GameLib.HEIGHT * 0.30;

        if (getY() >= limiarY && getY() - getV() < limiarY) {
            if (getX() < GameLib.WIDTH / 2.0) {
                setRV(0.003);
            } else {
                setRV(-0.003);
            }
        }

        if (getRV() > 0 && Math.abs(getAngle() - 3 * Math.PI) < 0.05) {
            setRV(0.0);
            setAngle(3 * Math.PI);
            return true;
        }

        if (getRV() < 0 && Math.abs(getAngle()) < 0.05) {
            setRV(0.0);
            setAngle(0.0);
            return true;
        }

        return false;
    }

    // Função Auxiliar de "Comportamento": faz a função de atirar do enemy2
    private void atirar(List<Projetil> e_projetils) {
        double[] angulos = {
                Math.PI / 2 + Math.PI / 8,
                Math.PI / 2,
                Math.PI / 2 - Math.PI / 8
        };

        int[] indicesLivres = Utils.findFreeIndex(e_projetils, angulos.length);

        for (int i = 0; i < indicesLivres.length; i++) {
            int index = indicesLivres[i];
            if (index < e_projetils.size()) {
                double anguloAleatorio = angulos[i] + (Math.random() * Math.PI / 6 - Math.PI / 12);
                double vx = Math.cos(anguloAleatorio) * 0.30;
                double vy = Math.sin(anguloAleatorio) * 0.30;

                Projetil p = e_projetils.get(index);
                p.setX(getX());
                p.setY(getY());
                p.setVX(vx);
                p.setVY(vy);
                p.setState(EstadosEnum.ACTIVE);
            }
        }
    }


    // desenha o enemy2
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

    public static long getEnemy2_spawnX() {
        return enemy2_spawnX;
    }
}
