import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import GameLib.GameLib;
import Modules.*;

/*                                                                     */
/* Para jogar:                                                         */
/*                                                                     */
/*    - cima, baixo, esquerda, direita: movimentação do player.        */
/*    - control: disparo de projéteis.                                 */
/*    - ESC: para sair do jogo.                                        */
/*                                                                     */
/***********************************************************************/

public class Main {
	public static void busyWait(long time){
		while(System.currentTimeMillis() < time) Thread.yield();
	}

	private static void processInput(Player player, List<Projetil> projetils, long currentTime, long delta) {
		player.verificaEntradaUsuario(projetils, currentTime, delta);
		player.verificaCoordenadasDentroJogo();
	}

	/* Método principal */

	public static void main(String [] args) throws IOException {

		Engine engine = new Engine();
		ConfigJogo config = engine.GameConfig("GameConfig.txt");


		if (config == null) {
			System.out.println("Erro ao carregar o arquivo de configuração.");
			return;
		}

		System.out.println("Vida do jogador: " + config.getVidaInicialJogador());
		System.out.println("Número de fases: " + config.getNumeroDeFases());
		System.out.println("Arquivos das fases: " + config.getArquivosFase());




		/* Indica que o jogo está em execução */

		boolean running = true;

		/* variáveis usadas no controle de tempo efetuado no main loop */

		long delta;
		long currentTime = System.currentTimeMillis();

		/* variáveis do player */

		Player player = new Player(EstadosEnum.ACTIVE,
				(double) GameLib.WIDTH / 2,
				GameLib.HEIGHT * 0.90,
				Constantes.V_INICIAL,
				Constantes.V_INICIAL,
				12.0,
				0,
				0,
				currentTime);

		/* variáveis dos projéteis disparados pelo player */

		List<Projetil> projetils = new ArrayList<>();

		/* variáveis dos inimigos tipo 1 */

		List<Enemy1> enemies = new ArrayList<>();

		/* variáveis dos inimigos tipo 2 */

		List<Enemy2> enemies2 = new ArrayList<>();

		/* variáveis dos projéteis lançados pelos inimigos (tanto tipo 1, quanto tipo 2) */

		List<Projetil> e_projetils = new ArrayList<>();

		/* estrelas que formam o fundo de primeiro plano */

		Fundo fundoPrimeiroPlano = new Fundo(20, 0.070);

		/* estrelas que formam o fundo de segundo plano */

		Fundo fundoSegundoPlano = new Fundo(50, 0.045);

		/* Powerups */

		List<PowerUp> powerUps = new ArrayList<>();


		/* inicializações */

		Utils.inicializacoes(fundoPrimeiroPlano, fundoSegundoPlano, projetils, e_projetils, enemies, enemies2, currentTime, powerUps);

		/* iniciado interface gráfica */

		GameLib.initGraphics();
		//GameLib.GameLib.initGraphics_SAFE_MODE();  // chame esta versão do método caso nada seja desenhado na janela do jogo.

        /*                                                                                               */
		/* Main loop do jogo                                                                             */
		/* -----------------                                                                             */
		/*                                                                                               */
		/* O main loop do jogo executa as seguintes operações:                                           */
		/*                                                                                               */
		/* 1) Verifica se há colisões e atualiza estados dos elementos conforme a necessidade.           */
		/*                                                                                               */
		/* 2) Atualiza estados dos elementos baseados no tempo que correu entre a última atualização     */
		/*    e o timestamp atual: posição e orientação, execução de disparos de projéteis, etc.         */
		/*                                                                                               */
		/* 3) Processa entrada do usuário (teclado) e atualiza estados do player conforme a necessidade. */
		/*                                                                                               */
		/* 4) Desenha a cena, a partir dos estados dos elementos.                                        */
		/*                                                                                               */
		/* 5) Espera um período de tempo (de modo que delta seja aproximadamente sempre constante).      */
		/*
		* */
        while(running){
			/* Usada para atualizar o estado dos elementos do jogo    */
			/* (player, projéteis e inimigos) "delta" indica quantos  */
			/* ms se passaram desde a última atualização.             */

			delta = System.currentTimeMillis() - currentTime;

			/* Já a variável "currentTime" nos dá o timestamp atual.  */

			currentTime = System.currentTimeMillis();

            /* Verificação de colisões */

            if(player.getState() == EstadosEnum.ACTIVE){

				/* colisões player - projeteis (inimigo) */

				player.MortePlayer(e_projetils, currentTime);
				player.MortePlayer(enemies, currentTime);
				player.MortePlayer(enemies2, currentTime);
				player.colisaoPowerUp(powerUps, currentTime);
			}

			/* colisões projeteis (player) - inimigos */

			for (Projetil projetil : projetils) {
				EnemyBase.verificaColisaoComProjetil(projetil, enemies, currentTime);
				EnemyBase.verificaColisaoComProjetil(projetil, enemies2, currentTime);
			}

			/* colisões powerups (player) */


            /* Atualizações de estados */


            /* projeteis (player) */

			for(Projetil projetil : projetils) {
				projetil.atualizaEstadoProjetilPlayer(delta);
			}

			/* projeteis (inimigos) */

			for (Projetil projetil : e_projetils) {
				projetil.atualizaEstadoProjetilInimigo(delta);
			}

			/* inimigos tipo 1 */


			for(Enemy1 enemy1 : enemies) enemy1.comportamento(currentTime, delta, player, e_projetils);

			/* inimigos tipo 2 */

			for(Enemy2 enemy2 : enemies2) enemy2.comportamento(currentTime, delta, player, e_projetils);

			/* verificando se novos inimigos (tipo 1) devem ser "lançados" */

			Enemy1.verificaSeNovosEnemy1DevemSerLancados(currentTime, enemies);

			/* verificando se novos inimigos (tipo 2) devem ser "lançados" */

			Enemy2.verificaSeNovosEnemy2DevemSerLancados(currentTime, enemies2);

			//

			for (PowerUp powerUp : powerUps) {
				powerUp.atualizaEstadoPowerUp(delta);
			}

			PowerUp.verificaSeNovosPowerUpsDevemSerLancados(currentTime, powerUps);
			player.atualizarPowerUps(powerUps, currentTime);

			/* Verificando se a explosão do player já acabou.         */
			/* Ao final da explosão, o player volta a ser controlável */

			player.verificaSeExplosaoAcabou(currentTime);


            /* Verificando entrada do usuário (teclado) */

            processInput(player, projetils, currentTime, delta);

			/* Verificando se coordenadas do player ainda estão dentro */
			/* da tela de jogo após processar entrada do usuário.      */

            /* Desenho da cena */

            /* desenhando plano fundo distante */

			fundoSegundoPlano.desenhaPlanoFundoDistante(delta);

			/* desenhando plano de fundo próximo */

			fundoPrimeiroPlano.desenhaPlanoFundoProximo(delta);


			/* desenhando player */

			player.desenhaPlayer(currentTime);


			/* deenhando projeteis (player) */

			for(Projetil projetil : projetils) {
				projetil.desenhaProjetilPlayer();
			}

			/* desenhando projeteis (inimigos) */

			for(Projetil e_projetil : e_projetils) {
				e_projetil.desenhaProjetilInimigo();
			}


			/* desenhando inimigos (tipo 1) */

			for(Enemy1 enemy1 : enemies) {
				enemy1.desenhaInimigo(currentTime);
			}

			/* desenhando inimigos (tipo 2) */

			for(Enemy2 enemy2 : enemies2) {
				enemy2.desenhaInimigo(currentTime);
			}

			/* desenhando inimigos (tipo 2) */
			for(PowerUp powerUp : powerUps) {
				powerUp.desenhaPowerUp();
			}


			engine.desenhaVida();
			


			/* chamada a display() da classe GameLib.GameLib atualiza o desenho exibido pela interface do jogo. */

			GameLib.display();

			/* faz uma pausa de modo que cada execução do laço do main loop demore aproximadamente 3 ms. */

			busyWait(currentTime + 3);
		}

		System.exit(0);
	}
}
