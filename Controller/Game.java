package Controller;

import GameLib.GameLib;
import Modules.Enum.EstadosEnum;
import Modules.Player.Player;
import Modules.Utils.Constantes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private Player player;
    private List<String> phaseFiles = new ArrayList<>();
    private boolean isRunning = false;
    private int currentPhaseIndex = -1;
    private Phase faseAtual;
    private int playerHealth;

    public Game() {
        loadGameConfiguration("TextPhases/GameConfig.txt");
        GameLib.initGraphics();
    }

    private void loadGameConfiguration(String configFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            // Lê os pontos de vida do jogador
            this.playerHealth = Integer.parseInt(reader.readLine());

            // Lê o número de fases para saber quantos arquivos ler
            int numberOfPhases = Integer.parseInt(reader.readLine());

            // Lê o nome de cada arquivo de configuração de fase
            for (int i = 0; i < numberOfPhases; i++) {
                phaseFiles.add(reader.readLine());
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo de configuração principal: " + e.getMessage());
            this.isRunning = false;
        }
    }

    public void run() throws IOException {
        this.isRunning = true;
        System.out.println("Numero de fases: " + phaseFiles.size());

        startNextPhase();

        while (isRunning) {
            if(this.faseAtual != null) {
                faseAtual.updatePhase();
            }

            // 2 Chefes derrotados
            if ((this.faseAtual != null && this.faseAtual.isCompleted())) {
                if(this.currentPhaseIndex == this.phaseFiles.size()) {
                    System.out.println("Voce venceu");
                    return;
                }
                else {
                    System.out.println("Começando outra fase");
                    startNextPhase();
                    System.out.println();
                }
            }

            if (this.faseAtual.isMorto()) {
                System.out.println("Voce perdeu");
                System.exit(1);
            }

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                this.isRunning = false;
            }
        }
        System.exit(0);
    }

    private void startNextPhase() {
        currentPhaseIndex++;
        if (currentPhaseIndex >= phaseFiles.size()) {
            System.out.println("Parabéns, você venceu o jogo!");
            this.isRunning = false;
        } else {
            String nextPhaseFile = phaseFiles.get(currentPhaseIndex);
            faseAtual = new Phase(nextPhaseFile, this.playerHealth);
            System.out.println("Iniciando fase " + (currentPhaseIndex + 1) + " a partir de '" + nextPhaseFile + "'...");
        }
    }

    public int getCurrentPhaseIndex() {
        return currentPhaseIndex;
    }

    public void setCurrentPhaseIndex(int currentPhaseIndex) {
        this.currentPhaseIndex = currentPhaseIndex;
    }
}
