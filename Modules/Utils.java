package Modules;

import GameLib.GameLib;

import java.awt.*;
import java.util.List;

public class Utils {
    public static <T extends Entidade> int findFreeIndex(List<T> stateArray) {
        for (int i = 0; i < stateArray.size(); i++) {
            if (stateArray.get(i).getState() == EstadosEnum.INACTIVE) {
                return i;
            }
        }
        return stateArray.size();
    }

    public static void inicializacoes(Fundo fundoPrimeiroPlano, Fundo fundoSegundoPlano, List<Projetil> projetils, List<Projetil> e_projetils, List<Enemy1> enemies, List<Enemy2> enemies2, long currentTime, List<PowerUp> powerups) {
        for(int i = 0; i < Constantes.MAX_PROJETEIS_PLAYER; i++) projetils.add(Projetil.criaProjetilVazio(Constantes.RAIO_PROJETIL));
        for(int i = 0; i < Constantes.MAX_PROJETEIS_INIMIGOS ; i++) e_projetils.add(Projetil.criaProjetilVazio(Constantes.RAIO_PROJETIL));
        for (int i = 0; i < Constantes.MAX_INIMIGOS_TIPO1 ; i++) enemies.add(Enemy1.criaInimigo1Vazio(9.0, currentTime));
        for(int i = 0; i < Constantes.MAX_INIMIGOS_TIPO2 ; i++) enemies2.add(Enemy2.criaInimigo2Vazio(12.0));
        fundoPrimeiroPlano.inicializaFundo();
        fundoSegundoPlano.inicializaFundo();
        for(int i = 0; i < Constantes.MAX_POWERUPS; i++) powerups.add(new PowerUp(EstadosEnum.INACTIVE, 0.0, 0.0, 3.0, 2, System.currentTimeMillis() + 3000));
    }

    public static void desenhaVida(int vida) {
        GameLib.setColor(Color.RED);

        int vidas = vida;
        double raio = 7.0;
        double espacamento = 2 * raio + 5; // espaço entre bolinhas
        double posXInicial = 35.0;
        double posY = 60.0;

        GameLib.setColor(Color.RED);

        for (int i = 0; i < vidas; i++) {
            double posX = posXInicial + i * espacamento;
            GameLib.drawCircle(posX, posY, raio);
        }
    }

    public static void desenhaVidaBoss(double percentual) {
        double larguraMaxima = 50;   // largura máxima da barra de vida
        double altura = 10;           // altura da barra
        double cx = GameLib.HEIGHT/2;              // centro da barra no eixo X
        double cy = GameLib.HEIGHT - 30; // posição vertical (próxima da parte inferior)

        GameLib.setColor(Color.WHITE);
        GameLib.fillRect(cx, cy, larguraMaxima, altura);

    }
}
