package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.PAWN ){
            return pawnMovesCalculator(board, myPosition);
        } else if (type == PieceType.QUEEN) {
            return queenMovesCalculator(board, myPosition);
        } else if (type == PieceType.KING) {
            return kingMovesCalculator(board, myPosition);
        } else if (type == PieceType.BISHOP) {
            return bishopMovesCalculator(board, myPosition);
        } else if (type == PieceType.KNIGHT) {
            return knightMovesCalculator(board, myPosition);
        } else if (type == PieceType.ROOK) {
            return rookMovesCalculator(board, myPosition);
        }
        else {
            throw new RuntimeException("pieceMove not recognized");
        }
    }

    private Collection<ChessMove> queenMovesCalculator(ChessBoard board, ChessPosition myPosition){

    }

    private Collection<ChessMove> kingMovesCalculator(ChessBoard board, ChessPosition myPosition){

    }

    private Collection<ChessMove> bishopMovesCalculator(ChessBoard board, ChessPosition myPosition){

    }

    private Collection<ChessMove> knightMovesCalculator(ChessBoard board, ChessPosition myPosition){

    }

    private Collection<ChessMove> rookMovesCalculator(ChessBoard board, ChessPosition myPosition){

    }

    private Collection<ChessMove> pawnMovesCalculator(ChessBoard board, ChessPosition myPosition){

    }
}
