import GameLib.GameLib;
import Modules.Player;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Engine {
    public ConfigJogo GameConfig(String pathGame) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(pathGame))) {

            int vidaInicial = Integer.parseInt(br.readLine());
            int numeroDeFases = Integer.parseInt(br.readLine());

            List<String> arquivosFase = new ArrayList<>();
            for (int i = 0; i < numeroDeFases; i++) {
                arquivosFase.add(br.readLine());
            }

            return new ConfigJogo(vidaInicial, numeroDeFases, arquivosFase);

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void desenhaVida() {
        GameLib.setColor(Color.RED);

        int vidas = 3;
        double raio = 7.0;
        double espacamento = 2 * raio + 5; // espaço entre bolinhas
        double posXInicial = 35.0;
        double posY = 60.0;

        GameLib.setColor(Color.RED);
        GameLib.drawCircle(35.0, 60, 4);

        for (int i = 0; i < vidas; i++) {
            double posX = posXInicial + i * espacamento;
            GameLib.drawCircle(posX, posY, raio);
        }
    }
}
