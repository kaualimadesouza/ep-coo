package Modules;

import GameLib.GameLib;

import java.util.ArrayList;
import java.util.List;

public class Game {
    long currentTime = System.currentTimeMillis();

    Player player = new Player(EstadosEnum.ACTIVE,
            (double) GameLib.WIDTH / 2,
            GameLib.HEIGHT * 0.90,
            0.25,
            0.25,
            12.0,
            0,
            0,
            currentTime);
    List<Projetil> projetils = new ArrayList<>();
    List<Enemy1> enemies = new ArrayList<>();
    List<Enemy2> enemies2 = new ArrayList<>();
    List<Projetil> e_projetils = new ArrayList<>();
    Fundo fundoPrimeiroPlano = new Fundo(20, 0.070);
    Fundo fundoSegundoPlano = new Fundo(50, 0.045);

}
