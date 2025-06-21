import Controller.Game;

import java.io.IOException;


public class Main {
	public static void main(String [] args) throws IOException {
		Game game = new Game();
		System.out.println("-- Iniciando Jogo --");
		game.run();
		System.out.println("-- Finalizando Jogo --");
	}
}
