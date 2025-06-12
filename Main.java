import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import Modules.*;

/***********************************************************************/
/*                                                                     */
/* Para jogar:                                                         */
/*                                                                     */
/*    - cima, baixo, esquerda, direita: movimentação do player.        */
/*    - control: disparo de projéteis.                                 */
/*    - ESC: para sair do jogo.                                        */
/*                                                                     */
/***********************************************************************/

public class Main {
	
	/* Constantes relacionadas aos estados que os elementos   */
	/* do jogo (player, projeteis ou inimigos) podem assumir. */
	
	public static final int INACTIVE = 0;
	public static final int ACTIVE = 1;
	public static final int EXPLODING = 2;
	
	/* Espera, sem fazer nada, até que o instante de tempo atual seja */
	/* maior ou igual ao instante especificado no parâmetro "time.    */
	
	public static void busyWait(long time){
		
		while(System.currentTimeMillis() < time) Thread.yield();
	}
	
	/* Encontra e devolve o primeiro índice do  */
	/* array referente a uma posição "inativa". */

	public static int findFreeIndex(int [] stateArray){

		int i;

		for(i = 0; i < stateArray.length; i++){

			if(stateArray[i] == INACTIVE) break;
		}

		return i;
	}

	public static <T extends Entidade> int findFreeIndex(List<T> stateArray){
		
		int i;
		
		for(i = 0; i < stateArray.size(); i++){
			
			if(stateArray.get(i).getState() == INACTIVE) break;
		}
		
		return i;
	}


	public static <T extends EnemyBase> int findFreeEnemyIndex(List<T> stateArray) {
		for (int i = 0; i < stateArray.size(); i++) {
			if (stateArray.get(i).getState() == INACTIVE) {
				return i;
			}
		}
		return stateArray.size();
	}

	/* Encontra e devolve o conjunto de índices (a quantidade */
	/* de índices é defnida através do parâmetro "amount") do */
	/* array referente a posições "inativas".                 */ 

	public static <T extends Entidade> int [] findFreeIndex(List<T> stateArray, int amount){

		int i, k;
		int [] freeArray = new int[amount];

		for(i = 0; i < freeArray.length; i++) freeArray[i] = stateArray.size();
		
		for(i = 0, k = 0; i < stateArray.size() && k < amount; i++){
				
			if(stateArray.get(i).getState() == INACTIVE) {
				
				freeArray[k] = i; 
				k++;
			}
		}
		
		return freeArray;
	}
	
	/* Método principal */
	
	public static void main(String [] args){

		/* Indica que o jogo está em execução */

		boolean running = true;

		/* variáveis usadas no controle de tempo efetuado no main loop */
		
		long delta;
		long currentTime = System.currentTimeMillis();

		/* variáveis do player */

		Player player = new Player(ACTIVE,
				GameLib.WIDTH / 2,
				GameLib.HEIGHT * 0.90,
				0.25,
				0.25,
				12.0,
				0,
				0,
				currentTime);

		/* variáveis dos projéteis disparados pelo player */

		List<Projetil> projetils = new ArrayList<>();

		/* variáveis dos inimigos tipo 1 */

		List<Enemy1> enemies = new ArrayList<>();
		long nextEnemy1 = currentTime + 2000;					// instante em que um novo inimigo 1 deve aparecer
		
		/* variáveis dos inimigos tipo 2 */

		List<Enemy2> enemies2 = new ArrayList<>();
		double enemy2_spawnX = GameLib.WIDTH * 0.20;				// coordenada x do próximo inimigo tipo 2 a aparecer
		int enemy2_count = 0;							// contagem de inimigos tipo 2 (usada na "formação de voo")
		long nextEnemy2 = currentTime + 7000;					// instante em que um novo inimigo 2 deve aparecer
		
		/* variáveis dos projéteis lançados pelos inimigos (tanto tipo 1, quanto tipo 2) */

		List<Projetil> e_projetils = new ArrayList<>();

		int [] e_projectile_states = new int[200];				// estados
		double [] e_projectile_X = new double[200];				// coordenadas x
		double [] e_projectile_Y = new double[200];				// coordenadas y
		double [] e_projectile_VX = new double[200];				// velocidade no eixo x
		double [] e_projectile_VY = new double[200];				// velocidade no eixo y
		double e_projectile_radius = 2.0;					// raio (tamanho dos projéteis inimigos)
		
		/* estrelas que formam o fundo de primeiro plano */
		
		double [] background1_X = new double[20];
		double [] background1_Y = new double[20];
		double background1_speed = 0.070;
		double background1_count = 0.0;
		
		/* estrelas que formam o fundo de segundo plano */
		
		double [] background2_X = new double[50];
		double [] background2_Y = new double[50];
		double background2_speed = 0.045;
		double background2_count = 0.0;
		
		/* inicializações */
		
		for(int i = 0; i < 10; i++) projetils.add(new Projetil(INACTIVE, 0.0, 0.0, 0.0, 0.0));
		for(int i = 0; i < 200; i++) e_projetils.add(new Projetil(INACTIVE, 0.0, 0.0, 0.0, 0.0));
		for (int i = 0; i < 10; i++) enemies.add(new Enemy1(INACTIVE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 9.0, 0L));
		for(int i = 0; i < 10; i++) enemies2.add(new Enemy2(INACTIVE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 12.0, 0.0));


		for(int i = 0; i < background1_X.length; i++){
			
			background1_X[i] = Math.random() * GameLib.WIDTH;
			background1_Y[i] = Math.random() * GameLib.HEIGHT;
		}
		
		for(int i = 0; i < background2_X.length; i++){
			
			background2_X[i] = Math.random() * GameLib.WIDTH;
			background2_Y[i] = Math.random() * GameLib.HEIGHT;
		}
						
		/* iniciado interface gráfica */
		
		GameLib.initGraphics();
		//GameLib.initGraphics_SAFE_MODE();  // chame esta versão do método caso nada seja desenhado na janela do jogo.
		
		/*************************************************************************************************/
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
		/*                                                                                               */
		/*************************************************************************************************/
		
		while(running){
		
			/* Usada para atualizar o estado dos elementos do jogo    */
			/* (player, projéteis e inimigos) "delta" indica quantos  */
			/* ms se passaram desde a última atualização.             */
			
			delta = System.currentTimeMillis() - currentTime;

			/* Já a variável "currentTime" nos dá o timestamp atual.  */
			
			currentTime = System.currentTimeMillis();
			
			/***************************/
			/* Verificação de colisões */
			/***************************/
						
			if(player.getState() == ACTIVE){
				
				/* colisões player - projeteis (inimigo) */
				
				for(int i = 0; i < e_projetils.size(); i++){
					
					double dx = e_projetils.get(i).getX() - player.getX();
					double dy = e_projetils.get(i).getY() - player.getY();
					double dist = Math.sqrt(dx * dx + dy * dy);
					
					if(dist < (player.getRadius() + e_projectile_radius) * 0.8){
						
						player.setState(EXPLODING);
						player.setExplosionStart(currentTime);
						player.setExplosionEnd(currentTime + 2000);
					}
				}
			
				/* colisões player - inimigos */
							
				for(int i = 0; i < enemies.size(); i++){
					
					double dx = enemies.get(i).getX() - player.getX();
					double dy = enemies.get(i).getY() - player.getY();
					double dist = Math.sqrt(dx * dx + dy * dy);
					
					if(dist < (player.getRadius() + enemies.get(i).getRadius()) * 0.8){
						player.setState(EXPLODING);
						player.setExplosionStart(currentTime);
						player.setExplosionEnd(currentTime + 2000);
					}
				}
				
				for(int i = 0; i < enemies2.size(); i++){
					
					double dx = enemies2.get(i).getX() - player.getX();
					double dy = enemies2.get(i).getY() - player.getY();
					double dist = Math.sqrt(dx * dx + dy * dy);
					
					if(dist < (player.getRadius() + enemies2.get(i).getRadius()) * 0.8){

						player.setState(EXPLODING);
						player.setExplosionStart(currentTime);
						player.setExplosionEnd(currentTime + 2000);
					}
				}
			}
			
			/* colisões projeteis (player) - inimigos */
			
			for(int k = 0; k < projetils.size(); k++){
				
				for(int i = 0; i < enemies.size(); i++){
										
					if(enemies.get(i).getState() == ACTIVE){
					
						double dx = enemies.get(i).getX() - projetils.get(k).getX();
						double dy = enemies.get(i).getY() - projetils.get(k).getY();
						double dist = Math.sqrt(dx * dx + dy * dy);
						
						if(dist < enemies.get(i).getRadius()){

							enemies.get(i).setState(EXPLODING);
							enemies.get(i).setExplosionStart(currentTime);
							enemies.get(i).setExplosionEnd(currentTime + 500);
						}
					}
				}
				
				for(int i = 0; i < enemies2.size(); i++){
					
					if(enemies2.get(i).getState() == ACTIVE){
						
						double dx = enemies2.get(i).getX() - projetils.get(k).getX();
						double dy = enemies2.get(i).getY() - projetils.get(k).getY();
						double dist = Math.sqrt(dx * dx + dy * dy);
						
						if(dist < enemies2.get(i).getRadius()){

							enemies2.get(i).setState(EXPLODING);
							enemies2.get(i).setExplosionStart(currentTime);
							enemies2.get(i).setExplosionEnd(currentTime + 500);
						}
					}
				}
			}
				
			/***************************/
			/* Atualizações de estados */
			/***************************/
			
			/* projeteis (player) */
			
			for(int i = 0; i < projetils.size(); i++){
				
				if(projetils.get(i).getState() == ACTIVE){
					
					/* verificando se projétil saiu da tela */
					if(projetils.get(i).getY() < 0) {

						projetils.get(i).setState(INACTIVE);
					}
					else {

						projetils.get(i).setX(projetils.get(i).getX() + projetils.get(i).getVX() * delta);
						projetils.get(i).setY(projetils.get(i).getY() + projetils.get(i).getVY() * delta);
					}
				}
			}
			
			/* projeteis (inimigos) */
			
			for(int i = 0; i < e_projetils.size(); i++){
				
				if(e_projetils.get(i).getState() == ACTIVE){
					
					/* verificando se projétil saiu da tela */
					if(e_projetils.get(i).getY() > GameLib.HEIGHT) {
						
						e_projetils.get(i).setState(INACTIVE);
					}
					else {

						e_projetils.get(i).setX(e_projetils.get(i).getVX() * delta + e_projetils.get(i).getX());
						e_projetils.get(i).setY(e_projetils.get(i).getVY() * delta + e_projetils.get(i).getY()) ;
					}
				}
			}
			
			/* inimigos tipo 1 */
			
			for(int i = 0; i < enemies.size(); i++){
				
				if(enemies.get(i).getState() == EXPLODING){
					
					if(currentTime > enemies.get(i).getExplosionEnd()){
						
						enemies.get(i).setState(INACTIVE);
					}
				}
				
				if(enemies.get(i).getState() == ACTIVE){
					
					/* verificando se inimigo saiu da tela */
					if(enemies.get(i).getY() > GameLib.HEIGHT + 10) {

						enemies.get(i).setState(INACTIVE);
					}
					else {
					
						enemies.get(i).setX(enemies.get(i).getV() * Math.cos(enemies.get(i).getAngle()) * delta + enemies.get(i).getX());
						enemies.get(i).setY(enemies.get(i).getV() * Math.sin(enemies.get(i).getAngle()) * delta * (-1.0) + enemies.get(i).getY());
						enemies.get(i).setAngle(enemies.get(i).getRV() * delta + enemies.get(i).getAngle());
						
						if(currentTime > enemies.get(i).getNextShoot() && enemies.get(i).getY() < player.getY()){
																							
							int free = findFreeIndex(e_projetils);
							
							if(free < e_projetils.size()){

								e_projetils.get(free).setX(enemies.get(i).getX());
								e_projetils.get(free).setY(enemies.get(i).getY());
								e_projetils.get(free).setVX(Math.cos(enemies.get(i).getAngle()) * 0.45);
								e_projetils.get(free).setVY(Math.sin(enemies.get(i).getAngle()) * 0.45 * (-1.0));
								e_projetils.get(free).setState(ACTIVE);
								
								enemies.get(i).setNextShoot((long) (currentTime + 200 + Math.random() * 500));
							}
						}
					}
				}
			}
			
			/* inimigos tipo 2 */
			
			for(int i = 0; i < enemies2.size(); i++){
				
				if(enemies2.get(i).getState() == EXPLODING){
					
					if(currentTime > enemies2.get(i).getExplosionEnd()){

						enemies2.get(i).setState(INACTIVE);
					}
				}
				
				if(enemies2.get(i).getState() == ACTIVE){
					
					/* verificando se inimigo saiu da tela */
					if(	enemies2.get(i).getX() < -10 || enemies2.get(i).getX() > GameLib.WIDTH + 10 ) {

						enemies2.get(i).setState(INACTIVE);
					}
					else {
						
						boolean shootNow = false;
						double previousY = enemies2.get(i).getY();


						enemies2.get(i).setX((enemies2.get(i).getV() * Math.cos(enemies2.get(i).getAngle()) * delta) + enemies2.get(i).getX());
						enemies2.get(i).setY(enemies2.get(i).getV() * Math.sin(enemies2.get(i).getAngle()) * delta * (-1.0) + enemies2.get(i).getY());
						enemies2.get(i).setAngle(enemies2.get(i).getRV() * delta + enemies2.get(i).getAngle());
						
						double threshold = GameLib.HEIGHT * 0.30;
						
						if(previousY < threshold && enemies2.get(i).getY() >= threshold) {
							
							if(enemies2.get(i).getX() < GameLib.WIDTH / 2) enemies2.get(i).setRV(0.003);
							else enemies2.get(i).setRV(-0.003);
						}
						
						if(enemies2.get(i).getRV() > 0 && Math.abs(enemies.get(i).getAngle() - 3 * Math.PI) < 0.05){

							enemies2.get(i).setRV(0.0);
							enemies.get(i).setAngle(3 * Math.PI);
							shootNow = true;
						}
						
						if(enemies2.get(i).getRV() < 0 && Math.abs(enemies.get(i).getAngle()) < 0.05){

							enemies2.get(i).setRV(0.0);
							enemies.get(i).setAngle(0.0);
							shootNow = true;
						}
																		
						if(shootNow){

							double [] angles = { Math.PI/2 + Math.PI/8, Math.PI/2, Math.PI/2 - Math.PI/8 };
							int [] freeArray = findFreeIndex(e_projetils, angles.length);

							for(int k = 0; k < freeArray.length; k++){
								
								int free = freeArray[k];
								
								if(free < e_projetils.size()){
									
									double a = angles[k] + Math.random() * Math.PI/6 - Math.PI/12;
									double vx = Math.cos(a);
									double vy = Math.sin(a);

									enemies.get(free).setX(enemies2.get(i).getX());
									enemies.get(free).setY(enemies2.get(i).getY());
									e_projetils.get(free).setVX(vx * 0.30);
									e_projetils.get(free).setVY(vy * 0.30);
									e_projetils.get(free).setState(ACTIVE);
								}
							}
						}
					}
				}
			}
			
			/* verificando se novos inimigos (tipo 1) devem ser "lançados" */
			
			if(currentTime > nextEnemy1){
				
				int free = findFreeEnemyIndex(enemies);
								
				if(free < enemies.size()){
					
					enemies.get(free).setX(Math.random() * (GameLib.WIDTH - 20.0) + 10.0);
					enemies.get(free).setY(-10.0);
					enemies.get(free).setV(0.20 + Math.random() * 0.15);
					enemies.get(free).setAngle((3 * Math.PI) / 2);
					enemies.get(free).setRV(0.0);
					enemies.get(free).setState(ACTIVE);
					enemies.get(free).setNextShoot(currentTime + 500);
					nextEnemy1 = currentTime + 500;
				}
			}
			
			/* verificando se novos inimigos (tipo 2) devem ser "lançados" */
			
			if(currentTime > nextEnemy2){
				
				int free = findFreeEnemyIndex(enemies2);
								
				if(free < enemies2.size()){

					enemies2.get(free).setX(enemy2_spawnX);
					enemies2.get(free).setY(-10.0);
					enemies2.get(free).setV(0.42);
 					enemies2.get(free).setAngle((3 * Math.PI) / 2);
					enemies2.get(free).setRV(0.0);
					enemies2.get(free).setState(ACTIVE);

					enemy2_count++;
					
					if(enemy2_count < 10){
						
						nextEnemy2 = currentTime + 120;
					}
					else {
						
						enemy2_count = 0;
						enemy2_spawnX = Math.random() > 0.5 ? GameLib.WIDTH * 0.2 : GameLib.WIDTH * 0.8;
						nextEnemy2 = (long) (currentTime + 3000 + Math.random() * 3000);
					}
				}
			}
			
			/* Verificando se a explosão do player já acabou.         */
			/* Ao final da explosão, o player volta a ser controlável */
			if(player.getState() == EXPLODING){
				
				if(currentTime > player.getExplosionEnd()){
					
					player.setState(ACTIVE);
				}
			}
			
			/********************************************/
			/* Verificando entrada do usuário (teclado) */
			/********************************************/
			
			if(player.getState() == ACTIVE){
				
				if(GameLib.iskeyPressed(GameLib.KEY_UP)) player.setY(player.getY() - delta * player.getVY());
				if(GameLib.iskeyPressed(GameLib.KEY_DOWN)) player.setY(player.getY() + delta * player.getVY());
				if(GameLib.iskeyPressed(GameLib.KEY_LEFT)) player.setX(player.getX() - delta * player.getVX());
				if(GameLib.iskeyPressed(GameLib.KEY_RIGHT)) player.setX(player.getX() + delta * player.getVX());
				
				if(GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {
					
					if(currentTime > player.getNextShot()){
						
						int free = findFreeIndex(projetils);
												
						if(free < projetils.size()){
							
							projetils.get(free).setX(player.getX());
                            projetils.get(free).setY(player.getY() - 2 * player.getRadius());
                            projetils.get(free).setVX(0.0);
                            projetils.get(free).setVY(-1.0);
                            projetils.get(free).setState(ACTIVE);
							player.setNextShot(currentTime + 100);
						}
					}	
				}
			}
			
			if(GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) running = false;
			
			/* Verificando se coordenadas do player ainda estão dentro */
			/* da tela de jogo após processar entrada do usuário.      */
			
			if(player.getX() < 0.0) player.setX(0.0);
			if(player.getX() >= GameLib.WIDTH) player.setX(GameLib.WIDTH - 1);
			if(player.getY() < 25.0) player.setY(25.0);
			if(player.getY() >= GameLib.HEIGHT) player.setY(GameLib.HEIGHT - 1);

			/*******************/
			/* Desenho da cena */
			/*******************/
			
			/* desenhando plano fundo distante */
			
			GameLib.setColor(Color.DARK_GRAY);
			background2_count += background2_speed * delta;
			
			for(int i = 0; i < background2_X.length; i++){
				
				GameLib.fillRect(background2_X[i], (background2_Y[i] + background2_count) % GameLib.HEIGHT, 2, 2);
			}
			
			/* desenhando plano de fundo próximo */
			
			GameLib.setColor(Color.GRAY);
			background1_count += background1_speed * delta;
			
			for(int i = 0; i < background1_X.length; i++){
				
				GameLib.fillRect(background1_X[i], (background1_Y[i] + background1_count) % GameLib.HEIGHT, 3, 3);
			}
						
			/* desenhando player */
			
			if(player.getState() == EXPLODING){
				
				double alpha = (currentTime - player.getExplosionStart()) / (player.getExplosionEnd() - player.getExplosionStart());
				GameLib.drawExplosion(player.getX(), player.getY(), alpha);
			}
			else{
				
				GameLib.setColor(Color.BLUE);
				GameLib.drawPlayer(player.getX(), player.getY(), player.getRadius());
			}
				
			/* deenhando projeteis (player) */
			
			for(int i = 0; i < projetils.size(); i++){
				
				if(projetils.get(i).getState() == ACTIVE){
					
					GameLib.setColor(Color.GREEN);
					GameLib.drawLine(projetils.get(i).getX(), projetils.get(i).getY() - 5, projetils.get(i).getX(), projetils.get(i).getY() + 5);
					GameLib.drawLine(projetils.get(i).getX() - 1, projetils.get(i).getY() - 3, projetils.get(i).getX() - 1, projetils.get(i).getY() + 3);
					GameLib.drawLine(projetils.get(i).getX() + 1, projetils.get(i).getY() - 3, projetils.get(i).getX() + 1, projetils.get(i).getY() + 3);
				}
			}
			
			/* desenhando projeteis (inimigos) */
		
			for(int i = 0; i < e_projetils.size(); i++){
				
				if(e_projetils.get(i).getState() == ACTIVE){
	
					GameLib.setColor(Color.RED);
					GameLib.drawCircle(e_projetils.get(i).getX(), e_projetils.get(i).getY(), e_projectile_radius);
				}
			}
			
			/* desenhando inimigos (tipo 1) */
			
			for(int i = 0; i < enemies.size(); i++){
				
				if(enemies.get(i).getState() == EXPLODING){
					
					double alpha = (currentTime - enemies.get(i).getExplosionStart()) / (enemies.get(i).getExplosionEnd() - enemies.get(i).getExplosionStart());
					GameLib.drawExplosion(enemies.get(i).getX(), enemies.get(i).getY(), alpha);
				}
				
				if(enemies.get(i).getState() == ACTIVE){
			
					GameLib.setColor(Color.CYAN);
					GameLib.drawCircle(enemies.get(i).getX(), enemies.get(i).getY(), enemies.get(i).getRadius());
				}
			}
			
			/* desenhando inimigos (tipo 2) */
			
			for(int i = 0; i < enemies2.size(); i++){
				
				if(enemies2.get(i).getState() == EXPLODING){

					double alpha = (currentTime - enemies2.get(i).getExplosionStart()) / (enemies2.get(i).getExplosionEnd() - enemies2.get(i).getExplosionStart());
					GameLib.drawExplosion(enemies2.get(i).getX(), enemies2.get(i).getY(), alpha);
				}
				
				if(enemies2.get(i).getState() == ACTIVE){
			
					GameLib.setColor(Color.MAGENTA);
					GameLib.drawDiamond(enemies2.get(i).getX(), enemies2.get(i).getY(), enemies2.get(i).getRadius());
				}
			}
			
			/* chamada a display() da classe GameLib atualiza o desenho exibido pela interface do jogo. */
			
			GameLib.display();
			
			/* faz uma pausa de modo que cada execução do laço do main loop demore aproximadamente 3 ms. */
			
			busyWait(currentTime + 3);
		}
		
		System.exit(0);
	}
}
