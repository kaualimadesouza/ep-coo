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
	public static void main(String [] args) throws IOException {
		Game game = new Game();
		System.out.println("-- Iniciando Jogo --");
		game.run();
		System.out.println("-- Finalizando Jogo --");
	}

}
