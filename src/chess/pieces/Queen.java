package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Queen extends ChessPiece {

	public Queen(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "Q";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);

		// cima
		p.setValues(position.getRow() - 1, position.getColumn());
		// -1 na linha pra marca a direção que vai testar
		while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			// enquanto existir casa vazia e exitir o espaço no tabuleiro
			mat[p.getRow()][p.getColumn()] = true; // a peça pode se move
			p.setRow(p.getRow() - 1);// direção que a peça vai mover
		}

		if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {// se tem peça adversaria
			mat[p.getRow()][p.getColumn()] = true;
		}

		// baixo
		p.setValues(position.getRow() + 1, position.getColumn());
		while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setRow(p.getRow() + 1);
		}

		if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// esquerda
		p.setValues(position.getRow(), position.getColumn() - 1);
		while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true; //
			p.setColumn(p.getColumn() - 1);
		}

		if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// direita
		p.setValues(position.getRow(), position.getColumn() + 1);
		while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setColumn(p.getColumn() + 1);
		}

		if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// Nw -> noroeste
		p.setValues(position.getRow() - 1, position.getColumn() - 1);// -1 em duas direções para mover e diagonal
		while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			// enquanto existir casa vazia e exitir o espaço no tabuleiro
			mat[p.getRow()][p.getColumn()] = true; // a peça pode se move
			p.setValues(p.getRow() - 1, p.getColumn() - 1);// direção que a peça vai mover
		}

		if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {// se tem peça adversaria
			mat[p.getRow()][p.getColumn()] = true;
		}

		// Ne -> Nordeste
		p.setValues(position.getRow() - 1, position.getColumn() + 1);
		while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setValues(p.getRow() - 1, p.getColumn() + 1);
		}

		if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// Se -> Sudeste
		p.setValues(position.getRow() + 1, position.getColumn() + 1);
		while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true; //
			p.setValues(p.getRow() + 1, p.getColumn() + 1);
			;
		}

		if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// Sw -> Sudoeste
		p.setValues(position.getRow() + 1, position.getColumn() - 1);
		while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
			p.setValues(p.getRow() + 1, p.getColumn() - 1);
			;
		}

		if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		return mat;
	}
}
