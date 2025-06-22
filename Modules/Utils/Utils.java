package Modules.Utils;

import GameLib.GameLib;
import Modules.Enemy.Enemy1;
import Modules.Enemy.Enemy2;
import Modules.Enum.EstadosEnum;
import Modules.Others.Entidade;
import Modules.Others.Fundo;
import Modules.Others.Projetil;
import Modules.PowerUps.PowerUp1;
import Modules.PowerUps.PowerUp2;

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

    public static int findFreeIndexPlayer(List<? extends Entidade> entidades, int startIndex) {
        for (int i = startIndex; i < entidades.size(); i++) {
            if (entidades.get(i).getState() == EstadosEnum.INACTIVE) {
                return i;
            }
        }
        return entidades.size();
    }

    public static void inicializacoes(Fundo fundoPrimeiroPlano, Fundo fundoSegundoPlano, List<Projetil> projetils, List<Projetil> e_projetils, List<Enemy1> enemies, List<Enemy2> enemies2, long currentTime, List<PowerUp1> powerups, List<PowerUp2> powerups2) {
        for(int i = 0; i < Constantes.MAX_PROJETEIS_PLAYER; i++) projetils.add(Projetil.criaProjetilVazio(Constantes.RAIO_PROJETIL));
        for(int i = 0; i < Constantes.MAX_PROJETEIS_INIMIGOS ; i++) e_projetils.add(Projetil.criaProjetilVazio(Constantes.RAIO_PROJETIL));
        for (int i = 0; i < Constantes.MAX_INIMIGOS_TIPO1 ; i++) enemies.add(Enemy1.criaInimigo1Vazio(12.0, currentTime));
        for(int i = 0; i < Constantes.MAX_INIMIGOS_TIPO2 ; i++) enemies2.add(Enemy2.criaInimigo2Vazio(12.0));
        fundoPrimeiroPlano.inicializaFundo();
        fundoSegundoPlano.inicializaFundo();
        for(int i = 0; i < Constantes.MAX_POWERUPS; i++) powerups.add(new PowerUp1(EstadosEnum.INACTIVE, 0.0, 0.0, 3.0, 2, System.currentTimeMillis() + 3000));
        for(int i = 0; i < Constantes.MAX_POWERUPS; i++) powerups2.add(new PowerUp2(EstadosEnum.INACTIVE, 0.0, 0.0, 3.0, 2, System.currentTimeMillis() + 3000));
    }

    public static void desenhaVidaPlayer(int vida) {
        GameLib.setColor(Color.RED);

        int vidas = vida;
        double raio = 7.0;
        double espacamento = 2 * raio + 5;
        double larguraTotal = (vidas - 1) * espacamento;
        double posXInicial = GameLib.WIDTH / 2 - larguraTotal / 2;
        double posY = 75.0;

        GameLib.setColor(Color.RED);

        for (int i = 0; i < vidas; i++) {
            double posX = posXInicial + i * espacamento;
            GameLib.drawCircle(posX, posY, raio);
        }
    }

    public static void desenhaVidaBossAtual(int vidaAtualBoss) {
        int vidaMaxima = 30;
        double larguraTotal = 300;
        double larguraAtual = (vidaAtualBoss / (double) vidaMaxima) * larguraTotal;
        double altura = 10;
        double cx = GameLib.WIDTH / 2;
        double cy = GameLib.HEIGHT - 30;

        GameLib.setColor(Color.RED);
        GameLib.fillRect(cx, cy, larguraAtual, altura);
    }
}
