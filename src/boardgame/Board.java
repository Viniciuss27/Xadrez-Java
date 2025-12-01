package boardgame;

public class Board {

	private int rows;
	private int columns;
	private Piece[][] pieces;
	
	public Board(int rows, int columns) {
		if(rows < 1 || columns < 1) {
			throw new BoardException("Erro criando tabuleiro: tem que ter pelo menos 1 coluna e 1 linha");
		}
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}
	
	public Piece piece(int row, int column) {
		if(!positionExist(row, column)) {
			throw new BoardException("posiçaõ invalida no tabuleiro");
		}
		return pieces[row] [column];
	}
	
	public Piece piece(Position position) {
		if(!positionExists(position)) {
			throw new BoardException("posiçaõ invalida no tabuleiro");
		}
		return pieces[position.getRow()][position.getColumn()];
	}
	
	public void placePiece(Piece piece, Position position) {
		if(thereIsAPiece(position)) {
			throw new BoardException("Não tem peça na posição: " + position);
		}
		pieces[position.getRow()][position.getColumn()] = piece;// coloca a peça na posiçaõ dada
		piece.position = position;
	}
	
	public Piece removePiece(Position position) {
		if(!positionExists(position)) {//verifica se exite a posição
			throw new BoardException("posiçaõ invalida no tabuleiro");
		}
		if(piece(position) == null) {//verifica se tem peça na posição
			return null;
		}
		Piece aux = piece(position);// pega a peça
		aux.position = null;// remove da posição
		pieces[position.getRow()][position.getColumn()] = null; // deixa a posição vazia
		return aux; //retorna a peça "vaga"
	}
	
	private boolean positionExist(int row, int column) {// confirma a posição
		return row >= 0 && row < rows && column >= 0 && column < columns;
	}
	
	public boolean positionExists(Position position) {
		return positionExist(position.getRow(), position.getColumn());
	}
	
	public boolean thereIsAPiece(Position position) {// verifica se ta vazio, o espaço
		if(!positionExists(position)) {
			throw new BoardException("posiçaõ invalida no tabuleiro");
		}
		return piece(position) != null;
	}
}
