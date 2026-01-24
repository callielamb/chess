package chess;

import java.util.ArrayList;
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
        Collection<ChessMove> moves = new ArrayList<>();


        return moves;
    }

    private Collection<ChessMove> kingMovesCalculator(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        if ((currentRow+1 >= 1 && currentRow+1 <=8) && (currentCol+1 >=1 && currentCol+1 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow+1, currentCol+1)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+1, currentCol+1), null));
            } else if (board.getPiece(new ChessPosition(currentRow+1, currentCol+1)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+1, currentCol+1), null));
            }
        }
        if ((currentRow+1 >= 1 && currentRow+1 <=8) && (currentCol-1 >=1 && currentCol-1 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow+1, currentCol-1)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+1, currentCol-1), null));
            } else if (board.getPiece(new ChessPosition(currentRow+1, currentCol-1)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+1, currentCol-1), null));
            }
        }
        if ((currentRow-1 >= 1 && currentRow-1 <=8) && (currentCol+1 >=1 && currentCol+1 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow-1, currentCol+1)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-1, currentCol+1), null));
            } else if (board.getPiece(new ChessPosition(currentRow-1, currentCol+1)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-1, currentCol+1), null));
            }
        }
        if ((currentRow-1 >= 1 && currentRow-1 <=8) && (currentCol-1 >=1 && currentCol-1 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow-1, currentCol-1)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-1, currentCol-1), null));
            } else if (board.getPiece(new ChessPosition(currentRow-1, currentCol-1)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-1, currentCol-1), null));
            }
        }
        if ((currentRow+1 >= 1 && currentRow+1 <=8) && (currentCol >=1 && currentCol <=8)) {
            if (board.getPiece(new ChessPosition(currentRow+1, currentCol)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+1, currentCol), null));
            } else if (board.getPiece(new ChessPosition(currentRow+1, currentCol)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+1, currentCol), null));
            }
        }
        if ((currentRow-1 >= 1 && currentRow-1 <=8) && (currentCol >=1 && currentCol <=8)) {
            if (board.getPiece(new ChessPosition(currentRow-1, currentCol)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-1, currentCol), null));
            } else if (board.getPiece(new ChessPosition(currentRow-1, currentCol)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-1, currentCol), null));
            }
        }
        if ((currentRow >= 1 && currentRow <=8) && (currentCol+1 >=1 && currentCol+1 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow, currentCol+1)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow, currentCol+1), null));
            } else if (board.getPiece(new ChessPosition(currentRow, currentCol+1)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow, currentCol+1), null));
            }
        }
        if ((currentRow >= 1 && currentRow <=8) && (currentCol-1 >=1 && currentCol-1 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow, currentCol-1)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow, currentCol-1), null));
            } else if (board.getPiece(new ChessPosition(currentRow, currentCol-1)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow, currentCol-1), null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> bishopMovesCalculator(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        for (int r = currentRow + 1, c = currentCol + 1; r <= 8 && c <= 8; r++, c++) {
            if (board.getPiece(new ChessPosition(r, c)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
            } else if (board.getPiece(new ChessPosition(r, c)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                break;
            } else {
                break;

            }
        }
        for (int r = currentRow +1, c = currentCol -1; r <= 8 && c >= 1; r++, c--) {
            if (board.getPiece(new ChessPosition(r, c)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
            } else if (board.getPiece(new ChessPosition(r, c)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                break;
            } else {
                break;
            }
        }
        for (int r = currentRow -1, c = currentCol +1; r >= 1 && c <= 8; r--, c++) {
            if (board.getPiece(new ChessPosition(r, c)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
            } else if (board.getPiece(new ChessPosition(r, c)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                break;
            } else {
                break;
            }
        }
        for (int r = currentRow -1, c = currentCol -1; r >= 1 && c >= 1; r--, c--) {
            if (board.getPiece(new ChessPosition(r, c)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
            } else if (board.getPiece(new ChessPosition(r, c)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                break;
            } else {
                break;
            }
        }
        return moves;
    }

    private Collection<ChessMove> knightMovesCalculator(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        if ((currentRow+2 >= 1 && currentRow+2 <=8) && (currentCol+1 >=1 && currentCol+1 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow+2, currentCol+1)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+2, currentCol+1), null));
            } else if (board.getPiece(new ChessPosition(currentRow+2, currentCol+1)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+2, currentCol+1), null));
            }
        }
        if ((currentRow+2 >= 1 && currentRow+2 <=8) && (currentCol-1 >=1 && currentCol-1 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow+2, currentCol-1)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+2, currentCol-1), null));
            } else if (board.getPiece(new ChessPosition(currentRow+2, currentCol-1)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+2, currentCol-1), null));
            }
        }
        if ((currentRow-2 >= 1 && currentRow-2 <=8) && (currentCol+1 >=1 && currentCol+1 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow-2, currentCol+1)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-2, currentCol+1), null));
            } else if (board.getPiece(new ChessPosition(currentRow-2, currentCol+1)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-2, currentCol+1), null));
            }
        }
        if ((currentRow-2 >= 1 && currentRow-2 <=8) && (currentCol-1 >=1 && currentCol-1 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow-2, currentCol-1)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-2, currentCol-1), null));
            } else if (board.getPiece(new ChessPosition(currentRow-2, currentCol-1)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-2, currentCol-1), null));
            }
        }
        if ((currentRow+1 >= 1 && currentRow+1 <=8) && (currentCol+2 >=1 && currentCol+2 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow+1, currentCol+2)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+1, currentCol+2), null));
            } else if (board.getPiece(new ChessPosition(currentRow+1, currentCol+2)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+1, currentCol+2), null));
            }
        }
        if ((currentRow+1 >= 1 && currentRow+1 <=8) && (currentCol-2 >=1 && currentCol-2 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow+1, currentCol-2)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+1, currentCol-2), null));
            } else if (board.getPiece(new ChessPosition(currentRow+1, currentCol-2)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow+1, currentCol-2), null));
            }
        }
        if ((currentRow-1 >= 1 && currentRow-1 <=8) && (currentCol+2 >=1 && currentCol+2 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow-1, currentCol+2)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-1, currentCol+2), null));
            } else if (board.getPiece(new ChessPosition(currentRow-1, currentCol+2)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-1, currentCol+2), null));
            }
        }
        if ((currentRow-1 >= 1 && currentRow-1 <=8) && (currentCol-2 >=1 && currentCol-2 <=8)) {
            if (board.getPiece(new ChessPosition(currentRow-1, currentCol-2)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-1, currentCol-2), null));
            } else if (board.getPiece(new ChessPosition(currentRow-1, currentCol-2)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow-1, currentCol-2), null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> rookMovesCalculator(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        for (int r = currentRow + 1; r <= 8; r++){
            if (board.getPiece(new ChessPosition(r, currentCol)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(r, currentCol),null));
            } else if (board.getPiece(new ChessPosition(r,currentCol)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r, currentCol),null));
                break;
            } else {
                break;
            }
        }
        for (int c = currentCol + 1; c <= 8; c++){
            if (board.getPiece(new ChessPosition(currentRow, c)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow, c),null));
            } else if (board.getPiece(new ChessPosition(currentRow,c)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow, c),null));
                break;
            } else {
                break;
            }
        }
        for (int c = currentCol - 1; c >= 1; c--){
            if (board.getPiece(new ChessPosition(currentRow, c)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow, c),null));
            } else if (board.getPiece(new ChessPosition(currentRow,c)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(currentRow, c),null));
                break;
            } else {
                break;
            }
        }
        for (int r = currentRow - 1; r >= 1; r--){
            if (board.getPiece(new ChessPosition(r, currentCol)) == null){
                moves.add(new ChessMove(myPosition, new ChessPosition(r, currentCol),null));
            } else if (board.getPiece(new ChessPosition(r,currentCol)).getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r, currentCol),null));
                break;
            } else {
                break;
            }
        }
        return moves;
    }

    private Collection<ChessMove> pawnMovesCalculator(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
