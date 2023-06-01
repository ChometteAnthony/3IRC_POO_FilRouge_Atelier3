package atelier3.gui;

import atelier3.controller.InputViewData;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import java.util.List;

/**
 * @author francoiseperrin
 *         <p>
 *         Cette classe repr�sente le damier de la vue
 *         <p>
 *         Elle tire les valeurs d'affichage d'une fabrique de constante
 *         (GuiConfig)
 *         public final static int size = 10;
 *         public final static double height = 600.0;
 * 
 *         Elle d�l�gue � une fabrique le soin de cr�er et positionner les cases
 *         noires et blanches
 *         et de cr�er et positionner les pi�ces � leur position initiale
 * 
 *         Lorsque le model est MAJ, la m�thode moveCapturePromotion() est
 *         invoqu�e pour
 *         d�placer effectivement la pi�ce sur le damier et �ventuellement
 *         prendre et/ou promouvoir une PieceGui
 *         (invocation � partir du controller en passant par classe View)
 * 
 */
class Board extends GridPane {

	public Board(EventHandler<MouseEvent> clicListener) {
		super();

		int nbCol, nbLig;
		nbCol = nbLig = GuiConfig.SIZE;

		BorderPane square = null;
		ImageView piece = null;

		for (int ligne = 0; ligne < nbLig; ligne++) {
			for (int col = 0; col < nbCol; col++) {

				///// Cr�ation d'une case /////

				// cr�ation d'un BorderPane
				square = GuiFactory.createSquare(col, ligne);

				// ajout d'un �couteur sur le carr�
				square.setOnMouseClicked(clicListener);

				// taille des carr�s = taille de la fenetre / nombre de carr�s par lignes
				square.prefWidthProperty().bind(this.prefWidthProperty().divide(nbCol));
				square.prefHeightProperty().bind(this.prefHeightProperty().divide(nbLig));

				// ajout du carre sur le damier
				this.add(square, col, ligne);

				///// Si une pi�ce doit se trouver sur cette case /////

				// cr�ation de la pi�ce uniquement si doit �tre sur cette case
				piece = GuiFactory.createPiece(col, ligne);

				if (piece != null) {

					// ajout d'un �couteur de souris
					// si la pi�ce est s�lectionn�e, elle sera supprim� de son emplacement actuel
					// et repositionn�e sur une autre case
					piece.setOnMouseClicked(clicListener);

					// gestion de la taille et position de la pi�ce (au centre du carr�)
					piece.fitWidthProperty().bind(square.widthProperty().divide(1.5));
					piece.fitHeightProperty().bind(square.heightProperty().divide(1.5));
					piece.xProperty().bind((square.widthProperty().subtract(piece.fitWidthProperty())).divide(2));
					piece.yProperty().bind(square.heightProperty().subtract(piece.fitHeightProperty()).divide(2));

					// Ajout de la pi�ce sur le carr� noir
					square.getChildren().add(piece);

				}

			}
		}
	}

	/////////////////////////////////////////////////////////////
	// Actions sur la view
	// initi�es par le controller en passant par la classe View
	/////////////////////////////////////////////////////////////

	/**
	 * @param dataToRefreshView
	 *                          Cette m�thode est appel�e par le controller en
	 *                          passant par la classe View
	 *                          afin de rafraichir la view lorsque le model a �t�
	 *                          mis � jour
	 */
	public void actionOnGui(InputViewData<Integer> dataToRefreshView) {

		if (dataToRefreshView != null) {

			////////////////////////////////////////////////////
			// la PieceGui de la vue est effectivement d�plac�e
			////////////////////////////////////////////////////
			if (dataToRefreshView.toMovePieceIndex != -1 && dataToRefreshView.targetSquareIndex != -1) {
				ImageView toMovePiece = null;
				BorderPane toMovePieceSquare = (BorderPane) this.getChildren().get(dataToRefreshView.toMovePieceIndex);
				BorderPane targetSquare = (BorderPane) this.getChildren().get(dataToRefreshView.targetSquareIndex);

				// Ajout sur la case de destination
				if (!toMovePieceSquare.getChildren().isEmpty())
					toMovePiece = (ImageView) toMovePieceSquare.getChildren().get(0);

				// clear la case d'origine de la pi�ce d�plac�e
				if (toMovePiece != null) {
					targetSquare.getChildren().add(toMovePiece);
					toMovePieceSquare.getChildren().removeAll();
				}
			}

			////////////////////////////////////////////////////
			// la PieceGui de la vue est �ventuellement promue
			////////////////////////////////////////////////////
			if (dataToRefreshView.promotedPieceIndex != -1) {
				BorderPane targetSquare = (BorderPane) this.getChildren().get(dataToRefreshView.promotedPieceIndex);
				ImageView piece = (ImageView) targetSquare.getChildren().get(0);
				// d�l�gation � la fabrique qui sait comment fabriquer des images
				GuiFactory.PromotePiece(piece, dataToRefreshView.promotedPieceColor);
			}

			////////////////////////////////////////////////////
			// l'�ventuelle pi�ce interm�diaire est supprim�e
			////////////////////////////////////////////////////
			if (dataToRefreshView.capturedPieceIndex != -1) {
				// clear la case d'origine de la pi�ce supprim�e
				BorderPane capturedPieceSquare = (BorderPane) this.getChildren()
						.get(dataToRefreshView.capturedPieceIndex);
				capturedPieceSquare.getChildren().clear();
			}
		}

	}

	public void performRafle(int pieceSquareIndex) {
		// Obtenez la liste des indices des cases cibles possibles pour la rafle
		List<Integer> targetSquareIndices = getTargetSquareIndices(pieceSquareIndex);

		// Vérifiez s'il existe une case cible pour effectuer une rafle
		if (!targetSquareIndices.isEmpty()) {
			// Vous pouvez implémenter votre logique de sélection de la case cible ici
			// Par exemple, sélectionnez simplement la première case cible disponible
			int targetSquareIndex = targetSquareIndices.get(0);

			// Déplacez la pièce vers la case cible
			movePiece(pieceSquareIndex, targetSquareIndex);

			// Répétez le processus de rafle si d'autres rafles sont possibles
			performRafle(targetSquareIndex);
		}
	}

	/**
	 * Méthode pour obtenir les indices des cases cibles possibles pour une rafle
	 * 
	 * @param pieceSquareIndex L'indice de la case où se trouve la pièce effectuant
	 *                         la rafle
	 * @return La liste des indices des cases cibles possibles
	 */
	private List<Integer> getTargetSquareIndices(int pieceSquareIndex) {
		List<Integer> targetSquareIndices = new ArrayList<>();

		// Ajoutez votre logique pour déterminer les cases cibles possibles pour la
		// rafle
		// Par exemple, vous pouvez vérifier les cases voisines pour voir si elles
		// contiennent des pièces adverses
		// et si la case suivante après une pièce adverse est vide, cela peut être une
		// cible de rafle

		// Ajoutez les indices des cases cibles possibles à la liste
		// targetSquareIndices.add(targetSquareIndex);

		return targetSquareIndices;
	}

	/**
	 * Méthode pour déplacer une pièce d'une case à une autre
	 * 
	 * @param sourceSquareIndex L'indice de la case source
	 * @param targetSquareIndex L'indice de la case cible
	 */
	private void movePiece(int sourceSquareIndex, int targetSquareIndex) {
		// Implémentez votre logique pour déplacer la pièce d'une case à une autre
		// Par exemple, vous pouvez obtenir les objets BorderPane représentant les cases
		// source et cible
		// puis déplacer l'ImageView de la pièce de la case source à la case cible
	}

}
