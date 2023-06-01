package atelier3.gui;

import atelier3.nutsAndBolts.PieceSquareColor;
import javafx.scene.image.Image;

/**
 * @author francoise.perrin
 *         Cette interface permet de v�rifier qu'un Node
 *         est fonctionnellement une pi�ce du jeu
 * 
 *         Lorsque le pion du model est promu en dame
 *         le visuel change
 * 
 *         La m�thode hasSameColorAsGamer sera utilise en mode Client/server
 *         pour emp�cher un joueur de jouer une pi�ce qui ne lui appartient pas
 */
public interface CheckersPieceGui {

	public void promote(Image image);

	public boolean hasSameColorAsGamer(PieceSquareColor gamerColor);

	void removePiece();
}
