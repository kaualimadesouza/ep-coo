package Controller;

import GameLib.GameLib;
import Modules.Boss.Boss1;
import Modules.Boss.Boss2;
import Modules.Boss.BossConfig;
import Modules.Boss.BossInterface;
import Modules.Enemy.Enemy1;
import Modules.Enemy.Enemy2;
import Modules.Enemy.EnemyBase;
import Modules.Enemy.EnemyConfig;
import Modules.Enum.EstadosEnum;
import Modules.Others.Fundo;
import Modules.PowerUps.PowerUp1;
import Modules.Others.Projetil;
import Modules.Utils.Utils;
import Modules.Player.Player;
import Modules.PowerUps.PowerUp2;
import Modules.PowerUps.PowerupConfig;
import Modules.Utils.Constantes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Phase {
    private List<Enemy1> inimigosTipo1;
    private List<Enemy2> inimigosTipo2;
    private BossInterface boss;
    private BossConfig bossConfig;
    private List<PowerupConfig> poweupConfig;
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
    private List<PowerUp1> powerUps;
    private List<PowerUp2> powerUps2;
    private int inimigosDerrotados;
    private List<EnemyConfig> enemyConfig;

    public Phase(String phaseConfig) {
        this.inimigosDerrotados = 0;
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
                10);

        /* variáveis dos projéteis disparados pelo player */

        this.projetils = new ArrayList<>();

        /* variáveis dos inimigos tipo 1 */

        this.enemies = new ArrayList<>();
        enemyConfig = new ArrayList<>();

        this.poweupConfig = new ArrayList<>();

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
        this.powerUps2 = new ArrayList<>();

        /* inicializações */

        Utils.inicializacoes(fundoPrimeiroPlano, fundoSegundoPlano, projetils, e_projetils, enemies, enemies2, currentTime, powerUps, powerUps2);

        /* iniciado interface gráfica */

        loadPhaseConfiguration(phaseConfig);
    }

    public void verificaLancamentosBoss(long currentTime) {
        if (this.bossConfig == null) return;

        if(currentTime >= bossConfig.getQuando() && !bossConfig.isLancado()) {
            bossConfig.setLancado(true);

            switch (bossConfig.getTipo()) {
                case 1:
                    this.boss = Boss1.criaBoss(currentTime, bossConfig.getPosX(), bossConfig.getPosY(), bossConfig.getPontosVida());
                    this.boss.setLancado(true);
                    break;
                case 2:
                    this.boss = Boss2.criaBoss(currentTime, bossConfig.getPosX(), bossConfig.getPosY(), bossConfig.getPontosVida());
                    this.boss.setLancado(true);
                    break;
            }
        }
    }

    public void verificaLancamentosInimigos(long currentTime) {
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
                    case 2: {
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
                        break;
                    }
                }
            }
        }
    }

    public void verificaSeNovosPowerUpsDevemSerLancados(long currentTime, List<PowerUp1> powerups, List<PowerUp2> powerups2) {
        for (PowerupConfig powerupConfig : this.poweupConfig) {
            if (currentTime >= powerupConfig.getQuando() && !powerupConfig.isLancado()) {
                powerupConfig.setLancado(true);

                int free;

                switch (powerupConfig.getTipo()) {
                    case 1:
                        free = Utils.findFreeIndex(powerups);

                        if (free < powerups.size()) {
                            powerups.get(free).setX(powerupConfig.getPosX());
                            powerups.get(free).setY(powerupConfig.getPosY());
                            powerups.get(free).setState(EstadosEnum.ACTIVE);
                        }
                        break;
                    case 2:
                        free = Utils.findFreeIndex(powerups2);

                        if (free < powerups2.size()) {
                            PowerUp2 powerup = powerups2.get(free); // Pega o objeto reutilizado

                            powerup.setEfeitoAplicado(false); // Reseta a "memória" do power-up

                            powerup.setX(powerupConfig.getPosX());
                            powerup.setY(powerupConfig.getPosY());
                            powerup.setState(EstadosEnum.ACTIVE);
                        }
                        break;
                }

            }
        }
    }

    private void loadPhaseConfiguration(String configFile) {
    try (BufferedReader reader = new BufferedReader(new FileReader("TextPhases/" + configFile))) {
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
                else if (palavras.length == 6 && palavras[0].equalsIgnoreCase("CHEFE")) {
                    int tipo = Integer.parseInt(palavras[1]);
                    int pontosVida = Integer.parseInt(palavras[2]);
                    long quando = this.startTime + Long.parseLong(palavras[3]);
                    double posX = Double.parseDouble(palavras[4]);
                    double posY = Double.parseDouble(palavras[5]);

                    this.bossConfig = new BossConfig(tipo, pontosVida, quando, posX, posY);
                }
                else if (palavras.length == 5 && palavras[0].equalsIgnoreCase("POWERUP")) {
                    int tipo = Integer.parseInt(palavras[1]);
                    long quando = this.startTime + Long.parseLong(palavras[2]);
                    double posX = Double.parseDouble(palavras[3]);
                    double posY = Double.parseDouble(palavras[4]);

                    PowerupConfig newPowerUpConfig = new PowerupConfig(tipo, quando, posX, posY);

                    this.poweupConfig.add(newPowerUpConfig);
                }
            }
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
            player.colisaoPowerUp(this.powerUps2, this.currentTime);

        }

        player.atualizarPowerUp2(currentTime);
        player.atualizarPowerUps(powerUps, currentTime);
        player.verificaEntradaUsuario(projetils, currentTime, delta);
        player.verificaCoordenadasDentroJogo();
        player.desenhaPlayer(currentTime);

        /* colisões projeteis (player) - inimigos */

        for (Projetil projetil : this.projetils) {

            if (projetil.getState() != EstadosEnum.ACTIVE) {
                continue;
            }


            for(EnemyBase inimigo : this.enemies) {
                if(inimigo.verificaColisaoComProjetil(projetil, this.currentTime)) {
                    this.inimigosDerrotados++;
                }
            }

            for(EnemyBase inimigo : this.enemies2) {
                if(inimigo.verificaColisaoComProjetil(projetil, this.currentTime)) {
                    this.inimigosDerrotados++;
                }
            }

            if(this.boss != null && this.boss.getState() == EstadosEnum.ACTIVE) {
                this.boss.verificaColisaoComProjetil(projetil, currentTime);
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

        /* Boss */
        if(this.boss != null)  {
            this.boss.update(currentTime, e_projetils);
        }


        /* verificando se novos inimigos (tipo 1) devem ser "lançados" */

        this.verificaLancamentosInimigos(this.currentTime);

        // Verifica se o boss deve ser lançado
        this.verificaLancamentosBoss(this.currentTime);

        this.verificaSeNovosPowerUpsDevemSerLancados(this.currentTime, this.powerUps, this.powerUps2);

        //

        for (PowerUp1 powerUp : this.powerUps) {
            powerUp.atualizaEstadoPowerUp(this.delta);
        }

        for (PowerUp2 powerUp : this.powerUps2) {
            powerUp.atualizaEstadoPowerUp(this.delta);
        }

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
        for(PowerUp1 powerUp : this.powerUps) {
            powerUp.desenhaPowerUp();
        }

        for (PowerUp2 powerUp : this.powerUps2) {
            powerUp.desenhaPowerUp();
        }

        Utils.desenhaVidaPlayer(player.getVida());

        if(player.getVida() == 0) {
            this.isMorto = true;
        }


        if (this.boss != null) {
            if (this.boss.getState() == EstadosEnum.ACTIVE) {
                Utils.desenhaVidaBossAtual(this.boss.getVida());
                this.boss.desenhaBoss(this.currentTime);
            }

            if (this.boss.isDerrotado() && !this.isCompleted) {
                this.isCompleted = true;
                System.out.println("Fase vencida!");
            }
        }

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
