package Modules.Enemy;

import GameLib.GameLib;
import Modules.Enum.EstadosEnum;
import Modules.Utils.Utils;
import Modules.Player.Player;
import Modules.Others.Projetil;

import java.awt.*;
import java.util.List;

public class Enemy1 extends EnemyBase {
    private long nextShoot;
    public Enemy1(EstadosEnum state, double x, double y, double v, double angle, double RV, double explosionStart, double explosionEnd, double radius, long nextShoot) {
        super(state, x, y, v, angle, RV, explosionStart, explosionEnd, radius);
        this.nextShoot = nextShoot;
    }

    // Inicializa um inimigo tipo 1 vazio
    public static Enemy1 criaInimigo1Vazio(double radius, long currentTime) {
        return new Enemy1(EstadosEnum.INACTIVE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, radius, 0L);
    }

    // Função de update de estado do enemy1
    public void comportamento(long currentTime, long delta, Player player, List<Projetil> e_projetils) {
        if (this.getState() == EstadosEnum.EXPLODING) {
            verificarExplosaoEncerrada(currentTime);
            return;
        }

        if (this.getState() == EstadosEnum.ACTIVE) {
            mover(delta);
            verificarSaidaTela();
            atirar(currentTime, player, e_projetils);
        }
    }

    // Função Auxiliar de "Comportamento": verifica se a animação de explosão acabou e deixa o inimigo INACTIVE
    private void verificarExplosaoEncerrada(long currentTime) {
        if (currentTime > this.getExplosionEnd()) {
            this.setState(EstadosEnum.INACTIVE);
        }
    }

    // Função Auxiliar de "Comportamento": Atualiza a movimentação do enemy1
    private void mover(long delta) {
        this.setX(this.getX() + this.getV() * Math.cos(this.getAngle()) * delta);
        this.setY(this.getY() - this.getV() * Math.sin(this.getAngle()) * delta);
        this.setAngle(this.getAngle() + this.getRV() * delta);
    }

    // Função Auxiliar de "Comportamento": Verifica quando o inimigo sair da tela e torna ele inativo.
    private void verificarSaidaTela() {
        if (this.getY() > GameLib.HEIGHT + 10) {
            this.setState(EstadosEnum.INACTIVE);
        }
    }

    // Função Auxiliar de "Comportamento": faz a função de atirar do enemy1
    private void atirar(long currentTime, Player player, List<Projetil> e_projetils) {
        if (currentTime > this.nextShoot && this.getY() < player.getY()) {
            int free = Utils.findFreeIndex(e_projetils);

            if (free < e_projetils.size()) {
                Projetil p = e_projetils.get(free);
                p.setX(this.getX());
                p.setY(this.getY());
                p.setVX(Math.cos(this.getAngle()) * 0.45);
                p.setVY(-Math.sin(this.getAngle()) * 0.45);
                p.setState(EstadosEnum.ACTIVE);

                this.nextShoot = currentTime + 200 + (long)(Math.random() * 500);
            }
        }
    }

    // Desenha o inimigo 1
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

    public void setNextShoot(long nextShoot) {
        this.nextShoot = nextShoot;
    }
}
