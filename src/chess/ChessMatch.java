package chess;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;

	private List<Piece> capturedPieces = new ArrayList<>();
	private List<Piece> piecesOnTheBoard = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}

	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}

	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}

	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {// movendo peça
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);

		if (testCheck(currentPlayer)) {// testa se o jogador se colocou em check
			undoMove(source, target, capturedPiece);
			throw new ChessException("Você não pode se colocar em check");
		}

		ChessPiece movedPiece = (ChessPiece) board.piece(target);// peça que se moveu

		//movimento especial Promoção
		promoted = null;
		if(movedPiece instanceof Pawn) {
			if(movedPiece.getColor() == Color.WHITE && target.getRow() == 0 || movedPiece.getColor() == Color.BLACK && target.getRow() == 7) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;// se não estiver em check, continua

		if (testCheckMate(opponent(currentPlayer))) {// se não estiver em checkMate
			checkMate = true;
		} else {
			nextTurn();// proximo a joga
		}

		// movimento especial en passant
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2)
				|| (target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
		}

		return (ChessPiece) capturedPiece;
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if(promoted == null) {
			throw new IllegalStateException("Não tem peça a ser promovida!!!"); 
		}
		if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			throw new InvalidParameterException("Promoção invalida!!!");
		}
		
		Position pos = promoted.getChessPosition().toPosition();// pega a posição
		Piece p = board.removePiece(pos);// remove a peça
		piecesOnTheBoard.remove(p);// remove da lista do tabuleiro
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);// peça promovida
		piecesOnTheBoard.add(newPiece);// adiciona a peça
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if(type.equals("N")) return new Knight(board, color);// Cavalo
		if(type.equals("B")) return new Bishop(board, color);// Bispo
		if(type.equals("Q")) return new Queen(board, color);// Rainha
		return new Rook(board, color);// Torre
	}

	private Piece makeMove(Position source, Position target) {// fazenndo a jogada
		ChessPiece p = (ChessPiece) board.removePiece(source);// remove a peça da posição de origem
		p.increaseMoveCount();// contabiliza a jogada
		Piece capturedPiece = board.removePiece(target);// capitura a peça que esta na posição destino
		board.placePiece(p, target); // coloca a peça da posição de origem na de destino

		if (capturedPiece != null) {// verifica se teve peça capturada
			piecesOnTheBoard.remove(capturedPiece);// retira do tabuleiro
			capturedPieces.add(capturedPiece);// adiciona em peças capturadas
		}

		// #movimento especial Roque-Pequeno
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {// Rei moveu 2 casas
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3); // torre da direita do Rei
			Position targetT = new Position(source.getRow(), source.getColumn() + 1); // posição pos Roque
			ChessPiece rook = (ChessPiece) board.removePiece(sourceT);// tira torre
			board.placePiece(rook, targetT);// coloca a peça removida na posição pos Roque
			rook.increaseMoveCount();
		}

		// #movimento especial Roque-Grande
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {// Rei moveu 2 casas
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4); // torre da esquerda do Rei
			Position targetT = new Position(source.getRow(), source.getColumn() - 1); // posição pos Roque
			ChessPiece rook = (ChessPiece) board.removePiece(sourceT);// tira torre
			board.placePiece(rook, targetT);// coloca a peça removida na posição pos Roque
			rook.increaseMoveCount();
		}

		// movimento especial en Passant
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				} else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = board.removePiece(pawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
		}

		return capturedPiece;
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {// voltando a jogada
		ChessPiece p = (ChessPiece) board.removePiece(target);// pega a peça removida da posição destino
		p.decreaseMoveCount();// remove a jogada do calculo
		board.placePiece(p, source);// devolve a peça para posição de origem

		if (capturedPiece != null) {// voltar a peça a posiçaõ de destino
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);// tira de capturados
			piecesOnTheBoard.add(capturedPiece);// coloca na lista de tabuleiro
		}

		// #movimento especial Roque-Pequeno
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {// Rei moveu 2 casas
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3); // torre da direita do Rei
			Position targetT = new Position(source.getRow(), source.getColumn() + 1); // posição pos Roque
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);// tira torre
			board.placePiece(rook, sourceT);// volta a peça antes do Roque
			rook.decreaseMoveCount();
		}

		// #movimento especial Roque-Grande
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {// Rei moveu 2 casas
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4); // torre da esquerda do Rei
			Position targetT = new Position(source.getRow(), source.getColumn() - 1); // posição pos Roque
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);// tira torre
			board.placePiece(rook, sourceT);// volta a posição antes do Roque
			rook.decreaseMoveCount();
		}

		// movimento especial en Passant
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece)board.removePiece(target);
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, target.getColumn());
				} else {
					pawnPosition = new Position(4, target.getColumn());
				}
				board.placePiece(pawn, pawnPosition);
			}
		}
	}

	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("Não existe peça na posição de origem");
		}
		if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessException("A peça escolhida não é sua");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {// se não tiver movimento possivel
			throw new ChessException("Não existe movimento possivel para a peça");
		}
	}

	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("A peça escolhida não pode mover para posição escolhida");
		}
	}

	private void nextTurn() {// muda o jogador a cada jogada
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private Color opponent(Color color) {// retorna a cor do adversario
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {// se for uma instancia deo rei, achei o rei
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("O Rei da " + color + " não foi encontrado");
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream()
				.filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {// percorre a lista de filtro
			boolean[][] mat = p.possibleMoves();// observa os movimentos possiveis
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {// se não estiver em check, não esta em checkMate
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());// filtra a lista por cor
		for (Piece p : list) {// percorre a lista
			boolean[][] mat = p.possibleMoves();// possiveis movimentos
			for (int i = 0; i < board.getRows(); i++) {// percorre linha
				for (int j = 0; j < board.getColumns(); j++) {// percorre coluna
					if (mat[i][j]) {
						Position source = ((ChessPiece) p).getChessPosition().toPosition();// pega a posição do check
						Position target = new Position(i, j);// movimento possivel
						Piece capturedPiece = makeMove(source, target);// tira do check com o movimento possivel
						boolean testCheck = testCheck(color);// testa se o Rei da mesma cor ainda esta em check
						undoMove(source, target, capturedPiece);// volta a jogada apos testa se tem movimento possivel
						if (!testCheck) {// se não estava em check
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}

	private void initialSetup() {// coloca peças na tabuleiro
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
	}
}