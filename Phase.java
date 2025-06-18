import GameLib.GameLib;
import Modules.*;
import jdk.jshell.execution.Util;

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
    private Player player;
    private List<Projetil> projetils;
    private List<Enemy1> enemies;
    private List<Enemy2> enemies2;
    private List<Projetil> e_projetils;
    private Fundo fundoPrimeiroPlano;
    private Fundo fundoSegundoPlano;
    private List<PowerUp> powerUps;
    private int inimigosDerrotados;

    public Phase() {
        this.inimigosDerrotados = 0;
        this.inimigosTipo1 = inimigosTipo1;
        this.inimigosTipo2 = inimigosTipo2;
        this.boss = boss;
        this.isRunningPhase = isRunningPhase;
        this.isCompleted = false;

        this.currentTime = System.currentTimeMillis();

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
    }

    public void updatePhase() {
        /* Usada para atualizar o estado dos elementos do jogo    */
        /* (player, projéteis e inimigos) "delta" indica quantos  */
        /* ms se passaram desde a última atualização.             */

        System.out.println("Inimigos Derrotados: " + getInimigosDerrotados());

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

        Enemy1.verificaSeNovosEnemy1DevemSerLancados(this.currentTime, this.enemies);

        /* verificando se novos inimigos (tipo 2) devem ser "lançados" */

        Enemy2.verificaSeNovosEnemy2DevemSerLancados(this.currentTime, this.enemies2);

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
