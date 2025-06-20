import GameLib.GameLib;
import Modules.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Phase {
    private List<Enemy1> inimigosTipo1;
    private List<Enemy2> inimigosTipo2;
    private Boss boss;
    private boolean isRunningPhase = false;
    private boolean isCompleted = false;
    private boolean isMorto = false;
    private long delta;
    private long currentTime;
    private long startTime;
    private Player player;
    private List<Projetil> projetils;
    private List<Enemy1> enemies;
    private List<Enemy2> enemies2;
    private List<Projetil> e_projetils;
    private Fundo fundoPrimeiroPlano;
    private Fundo fundoSegundoPlano;
    private List<PowerUp> powerUps;
    private int inimigosDerrotados;
    private List<EnemyConfig> enemyConfig;

    public Phase(String phaseConfig) {
        this.inimigosDerrotados = 0;
        this.inimigosTipo1 = inimigosTipo1;
        this.inimigosTipo2 = inimigosTipo2;
        this.boss = boss;
        this.isRunningPhase = isRunningPhase;
        this.isCompleted = false;

        this.startTime = System.currentTimeMillis();
        this.currentTime = this.startTime;

        /* variáveis do player */

        this.player = new Player(EstadosEnum.ACTIVE,
                (double) GameLib.WIDTH / 2,
                GameLib.HEIGHT * 0.90,
                Constantes.V_INICIAL,
                Constantes.V_INICIAL,
                12.0,
                0,
                0,
                currentTime,
                3);

        /* variáveis dos projéteis disparados pelo player */

        this.projetils = new ArrayList<>();

        /* variáveis dos inimigos tipo 1 */

        this.enemies = new ArrayList<>();
        enemyConfig = new ArrayList<>();

        /* variáveis dos inimigos tipo 2 */

        this.enemies2 = new ArrayList<>();

        /* variáveis dos projéteis lançados pelos inimigos (tanto tipo 1, quanto tipo 2) */

        this.e_projetils = new ArrayList<>();

        /* estrelas que formam o fundo de primeiro plano */

        this.fundoPrimeiroPlano = new Fundo(20, 0.070);

        /* estrelas que formam o fundo de segundo plano */

        this.fundoSegundoPlano = new Fundo(50, 0.045);

        /* Powerups */

        this.powerUps = new ArrayList<>();

        /* inicializações */

        Utils.inicializacoes(fundoPrimeiroPlano, fundoSegundoPlano, projetils, e_projetils, enemies, enemies2, currentTime, powerUps);

        /* iniciado interface gráfica */

        loadPhaseConfiguration(phaseConfig);
    }

    public void verificaLancamentos(long currentTime) {
        for (EnemyConfig config : this.enemyConfig) {

            if (currentTime >= config.getQuando() && !config.isLancado()) {
                config.setLancado(true);
                switch (config.getTipo()) {
                    case 1: {
                        int free = Utils.findFreeIndex(this.enemies);
                        if (free < this.enemies.size()) {
                            Enemy1 enemy = this.enemies.get(free);
                            enemy.setX(config.getPosX());
                            enemy.setY(config.getPosY()); // Usa a posição do arquivo
                            enemy.setV(0.20 + Math.random() * 0.15);
                            enemy.setAngle((3 * Math.PI) / 2);
                            enemy.setRV(0.0);
                            enemy.setState(EstadosEnum.ACTIVE);
                            enemy.setNextShoot(currentTime + 500);
                        }
                        break;
                    }
                    case 2: { // Lançar Inimigo do TIPO 2
                        int free = Utils.findFreeIndex(this.enemies2);
                        if (free < this.enemies2.size()) {
                            Enemy2 enemy = this.enemies2.get(free);
                            enemy.setX(Enemy2.getEnemy2_spawnX());
                            enemy.setY(-10.0);
                            enemy.setV(0.42);
                            enemy.setAngle((3 * Math.PI) / 2);
                            enemy.setRV(0.0);
                            enemy.setState(EstadosEnum.ACTIVE);
                            Enemy2.setEnemy2_count(Enemy2.getEnemy2_count() + 1);
                        }
                        break; // Importante sair do switch
                    }
                }
            }
        }
    }

    private void loadPhaseConfiguration(String configFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String linha;

            while ((linha = reader.readLine()) != null) {

                if (linha.trim().startsWith("#") || linha.trim().isEmpty()) {
                    continue;
                }
                String[] palavras = linha.split("\\s+");

                if (palavras.length == 5 && palavras[0].equalsIgnoreCase("INIMIGO")) {
                    int tipo = Integer.parseInt(palavras[1]);
                    long quando = this.startTime + Long.parseLong(palavras[2]);
                    double posX = Double.parseDouble(palavras[3]);
                    double posY = Double.parseDouble(palavras[4]);

                    EnemyConfig novoInimigo = new EnemyConfig(tipo, quando, posX, posY);

                    this.enemyConfig.add(novoInimigo);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePhase() {
        /* Usada para atualizar o estado dos elementos do jogo    */
        /* (player, projéteis e inimigos) "delta" indica quantos  */
        /* ms se passaram desde a última atualização.             */

        this.delta = System.currentTimeMillis() - this.currentTime;

        /* Já a variável "currentTime" nos dá o timestamp atual.  */

        this.currentTime = System.currentTimeMillis();

        /* Verificação de colisões */

        if(player.getState() == EstadosEnum.ACTIVE){

            /* colisões player - projeteis (inimigo) */

            player.MortePlayer(this.e_projetils, this.currentTime);
            player.MortePlayer(this.enemies, this.currentTime);
            player.MortePlayer(this.enemies2, this.currentTime);
            player.colisaoPowerUp(this.powerUps, this.currentTime);
        }

        /* colisões projeteis (player) - inimigos */

        for (Projetil projetil : this.projetils) {

            if (projetil.getState() != EstadosEnum.ACTIVE) {
                continue;
            }

            if(EnemyBase.verificaColisaoComProjetil(projetil, this.enemies, this.currentTime)) {
                this.inimigosDerrotados++;
            }

            if (EnemyBase.verificaColisaoComProjetil(projetil, this.enemies2, this.currentTime)) {
                this.inimigosDerrotados++;
            }

        }

        /* colisões powerups (player) */


        /* Atualizações de estados */


        /* projeteis (player) */

        for(Projetil projetil : this.projetils) {
            projetil.atualizaEstadoProjetilPlayer(this.delta);
        }

        /* projeteis (inimigos) */

        for (Projetil projetil : this.e_projetils) {
            projetil.atualizaEstadoProjetilInimigo(this.delta);
        }

        /* inimigos tipo 1 */


        for(Enemy1 enemy1 : this.enemies) enemy1.comportamento(this.currentTime, this.delta, player, this.e_projetils);

        /* inimigos tipo 2 */

        for(Enemy2 enemy2 : this.enemies2) enemy2.comportamento(this.currentTime, this.delta, player, this.e_projetils);

        /* verificando se novos inimigos (tipo 1) devem ser "lançados" */

        this.verificaLancamentos(this.currentTime);

        //

        for (PowerUp powerUp : this.powerUps) {
            powerUp.atualizaEstadoPowerUp(this.delta);
        }

        PowerUp.verificaSeNovosPowerUpsDevemSerLancados(this.currentTime, this.powerUps);
        player.atualizarPowerUps(this.powerUps, this.currentTime);

        /* Verificando se a explosão do player já acabou.         */
        /* Ao final da explosão, o player volta a ser controlável */

        player.verificaSeExplosaoAcabou(this.currentTime);


        /* Verificando entrada do usuário (teclado) */

        processInput(player, this.projetils, this.currentTime, this.delta);

        /* Verificando se coordenadas do player ainda estão dentro */
        /* da tela de jogo após processar entrada do usuário.      */

        /* Desenho da cena */

        /* desenhando plano fundo distante */

        this.fundoSegundoPlano.desenhaPlanoFundoDistante(this.delta);

        /* desenhando plano de fundo próximo */

        this.fundoPrimeiroPlano.desenhaPlanoFundoProximo(this.delta);


        /* desenhando player */

        player.desenhaPlayer(this.currentTime);


        /* deenhando projeteis (player) */

        for(Projetil projetil : this.projetils) {
            projetil.desenhaProjetilPlayer();
        }

        /* desenhando projeteis (inimigos) */

        for(Projetil e_projetil : this.e_projetils) {
            e_projetil.desenhaProjetilInimigo();
        }


        /* desenhando inimigos (tipo 1) */

        for(Enemy1 enemy1 : this.enemies) {
            enemy1.desenhaInimigo(this.currentTime);
        }

        /* desenhando inimigos (tipo 2) */

        for(Enemy2 enemy2 : this.enemies2) {
            enemy2.desenhaInimigo(this.currentTime);
        }

        /* desenhando inimigos (tipo 2) */
        for(PowerUp powerUp : this.powerUps) {
            powerUp.desenhaPowerUp();
        }

        if(getInimigosDerrotados() == 10) {
            this.isCompleted = true;
        }

        if(player.getVida() == 0) {
            this.isMorto = true;
        }

        Utils.desenhaVida(player.getVida());
        Utils.desenhaVidaBoss(10.0);

        /* chamada a display() da classe GameLib.GameLib atualiza o desenho exibido pela interface do jogo. */

        GameLib.display();

        /* faz uma pausa de modo que cada execução do laço do main loop demore aproximadamente 3 ms. */

        busyWait(currentTime + 3);
    }

    public static void busyWait(long time){
        while(System.currentTimeMillis() < time) Thread.yield();
    }

    static void processInput(Player player, List<Projetil> projetils, long currentTime, long delta) {
        player.verificaEntradaUsuario(projetils, currentTime, delta);
        player.verificaCoordenadasDentroJogo();
    }

    public List<Enemy1> getInimigosTipo1() {
        return inimigosTipo1;
    }

    public List<Enemy2> getInimigosTipo2() {
        return inimigosTipo2;
    }

    public Boss getBoss() {
        return boss;
    }

    public boolean isRunningPhase() {
        return isRunningPhase;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public int getInimigosDerrotados() {
        return inimigosDerrotados;
    }

    public void setInimigosDerrotados(int inimigosDerrotados) {
        this.inimigosDerrotados = inimigosDerrotados;
    }

    public boolean isMorto() {
        return isMorto;
    }

    public void setMorto(boolean morto) {
        isMorto = morto;
    }
}
