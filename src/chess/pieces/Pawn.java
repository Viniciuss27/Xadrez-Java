package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	private ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);
		if (getColor() == Color.WHITE) {

			// verifica se a casa ta vazia
			p.setValues(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// na 1° jogada, verifica se as 2 casas estão vazias
			p.setValues(position.getRow() - 2, position.getColumn());
			Position p2 = new Position(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2)
					&& !getBoard().thereIsAPiece(p2) && getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// verifica se tem peça na diagonal
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// verifica se tem peça na diagonal
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// en passant white
			if (position.getRow() == 3) {// linha 3 do tabuleiro
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				if (getBoard().positionExists(left) && isThereOpponenPiece(left)
						&& getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getRow() - 1][left.getColumn()] = true;
				}

				Position rigth = new Position(position.getRow(), position.getColumn() + 1);
				if (getBoard().positionExists(rigth) && isThereOpponenPiece(rigth)
						&& getBoard().piece(rigth) == chessMatch.getEnPassantVulnerable()) {
					mat[rigth.getRow() - 1][rigth.getColumn()] = true;
				}
			}

		} else {
			// verifica se a casa ta vazia
			p.setValues(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// na 1° jogada, verifica se as 2 casas estão vazias
			p.setValues(position.getRow() + 2, position.getColumn());
			Position p2 = new Position(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && getBoard().positionExists(p2)
					&& !getBoard().thereIsAPiece(p2) && getMoveCount() == 0) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// verifica se tem peça na diagonal
			p.setValues(position.getRow() + 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// verifica se tem peça na diagonal
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponenPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// en passant black
			if (position.getRow() == 4) {//linha 4 do tabuleiro
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				if (getBoard().positionExists(left) && isThereOpponenPiece(left)
						&& getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getRow() + 1][left.getColumn()] = true;
				}

				Position rigth = new Position(position.getRow(), position.getColumn() + 1);
				if (getBoard().positionExists(rigth) && isThereOpponenPiece(rigth)
						&& getBoard().piece(rigth) == chessMatch.getEnPassantVulnerable()) {
					mat[rigth.getRow() + 1][rigth.getColumn()] = true;
				}
			}
		}

		return mat;
	}

	@Override
	public String toString() {
		return "P";
	}

}
