package Modules.Others;

import GameLib.GameLib;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Fundo {
    private List<Double> background_X;
    private List<Double> background_Y;
    private double background_speed;
    private double background_count;

    public Fundo(int numeroDeEstrelas, double speed) {
        this.background_X = new ArrayList<>();
        this.background_Y = new ArrayList<>();
        this.background_speed = speed;
        this.background_count = 0.0;

        // Inicializa as estrelas
        for (int i = 0; i < numeroDeEstrelas; i++) {
            this.background_X.add(Math.random() * GameLib.WIDTH);
            this.background_Y.add(Math.random() * GameLib.HEIGHT);
        }
    }

    // Inicializa fundo1

    public void inicializaFundo() {
        for (int i = 0; i < this.background_X.size(); i++) {
            background_X.set(i, Math.random() * GameLib.WIDTH);
            background_Y.set(i, Math.random() * GameLib.HEIGHT);
        }
    }

    public void desenhaPlanoFundoProximo(long delta) {
        GameLib.setColor(Color.GRAY);
        this.background_count += this.background_speed * delta;

        for (int i = 0; i < this.background_X.size(); i++) {

            GameLib.fillRect(this.background_X.get(i), (this.background_Y.get(i) + this.background_count) % GameLib.HEIGHT, 3, 3);
        }
    }

    public void desenhaPlanoFundoDistante(long delta) {
        GameLib.setColor(Color.DARK_GRAY);
        background_count += background_speed * delta;

        for (int i = 0; i < background_X.size(); i++) {
            GameLib.fillRect(background_X.get(i), (background_Y.get(i) + background_count) % GameLib.HEIGHT, 2, 2);
        }
    }
}
