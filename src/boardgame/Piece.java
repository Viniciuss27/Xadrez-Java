package boardgame;

public abstract class Piece {

	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
		position = null;
	}

	protected Board getBoard() {
		return board;
	}
	
	public abstract boolean[][]possibleMoves();
	
	public boolean possibleMove(Position position) {//posição da peça para movimento
		return possibleMoves()[position.getRow()][position.getColumn()];
	}
	
	public boolean isThereAnyPossibleMove() {// verifica se pode mover a peça
		boolean[][] mat = possibleMoves();
		for(int i=0; i<mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				if(mat[i][j]) {// caso estaja livre o caminho
					return true;
				}
			}
		}
		return false;
	}
}
