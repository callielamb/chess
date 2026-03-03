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
        this.type = type;
        this.pieceColor = pieceColor;
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
     *
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.BISHOP){
            return bishopMoves(board, myPosition);
        } else if (type == PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        } else if (type == PieceType.KING) {
            return kingMoves(board, myPosition);
        } else if (type == PieceType.ROOK) {
            return rookMoves(board, myPosition);
        } else if (type == PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        } else if (type == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        } else{
            return null;
        }
    }

    /**
     * knight move calc, 8 moves available
     * @return moves of the knight (an arraylist?)
     */
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        //use a grid to go through each row/col offset available
        int[][] offsets = {
                { 1,  2}, { 1, -2}, {-1,  2}, {-1, -2},
                { 2,  1}, { 2, -1}, {-2,  1}, {-2, -1}
        };

        for (int[] off : offsets) {
            addStepMove(moves, board, myPosition, currentRow + off[0], currentCol + off[1]);
        }
        return moves;
    }

    /**
     * king move calc, up/down/left/right/diagonal
     * @return moves of king, same as above
     */
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        int[][] offsets = {
                { 1,  1}, { 1,  0}, { 1, -1},
                { 0,  1},           { 0, -1},
                {-1,  1}, {-1,  0}, {-1, -1}
        };
        for (int[] off : offsets) {
            addStepMove(moves, board, myPosition, currentRow + off[0], currentCol + off[1]);
        }
        return moves;
    }

    //rook move calc = up/down all the way til stop
    //4 options
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                { 1,  0},
                {-1,  0},
                { 0,  1},
                { 0, -1}
        };

        for (int[] d : directions) {
            addLineMoves(moves, board, myPosition, d[0], d[1]);
        }
        return moves;
    }

    // bishop move calc = diagonal all the way til stop
    //4 options
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                { 1,  1},
                { 1, -1},
                {-1,  1},
                {-1, -1}
        };

        for (int[] d : directions) {
            addLineMoves(moves, board, myPosition, d[0], d[1]);
        }

        return moves;
    }

    //queen move calc = bishop + rook = 8 options
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                { 1,  0}, {-1,  0}, { 0,  1}, { 0, -1},
                { 1,  1}, { 1, -1}, {-1,  1}, {-1, -1}
        };

        for (int[] d : directions) {
            addLineMoves(moves, board, myPosition, d[0], d[1]);
        }
        return moves;
    }

    //pawn move calc = the most work
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        /**
        // direction depends on color; -1 for black and +1 for white
        //promotion row also depends on color; 1 for black and 8 for white
        //starting row depends on color; 7 for black and 2 for white
         */
        int direction = 0;
        int start =0;
        int promo = 0;
        if (pieceColor == ChessGame.TeamColor.BLACK){
            direction = -1;
            start = 7;
            promo = 1;
        } else if (pieceColor == ChessGame.TeamColor.WHITE) {
            direction =1;
            start =2;
            promo = 8;
        }
        int movement = currentRow +direction;
        /**
        //col doesnt changte unless capturing diagonally
        //if nextpiece is null then move forward is an option
        //if diagonal  piece is enemy then move there is an option
        //if current row is 1 or 8 then promotion
        //if current row is 2 or 7 then can move up to 2! but also still 1 or diagonal
        */

        //move forward 1 or 2
        if (onBoard(movement, currentCol)) {
            ChessPosition nextPos = new ChessPosition(movement, currentCol);
            ChessPiece nextPiece = board.getPiece(nextPos);

            if (nextPiece == null) {
                //promotion
                if (movement == promo) {
                    addPromotionMoves(moves, myPosition, nextPos);
                } else {
                    moves.add(new ChessMove(myPosition, nextPos, null));
                }
                //move 2 at start option
                int twoMoves = currentRow + (2 * direction);
                if (currentRow == start && onBoard(twoMoves, currentCol)) {
                    ChessPosition twoNextPos = new ChessPosition(twoMoves, currentCol);
                    ChessPiece twoNextPiece = board.getPiece(twoNextPos);
                    if (twoNextPiece == null) {
                        moves.add(new ChessMove(myPosition, twoNextPos, null));
                    }

                }
            }
        }
        //diagonal captures (left and right)
        int[] colOffsets = { 1, -1 };
        for (int off : colOffsets) {
            int captureCol = currentCol + off;
            if (!onBoard(movement, captureCol)) {
                continue;
            }
            ChessPosition nextPos = new ChessPosition(movement, captureCol);
            ChessPiece nextPiece = board.getPiece(nextPos);

            if (nextPiece == null) {
                continue;
            }
            if (nextPiece.getTeamColor() != pieceColor) {
                if (movement == promo) {
                    addPromotionMoves(moves, myPosition, nextPos);
                } else {
                    moves.add(new ChessMove(myPosition, nextPos, null));
                }
            }
        }

        return moves;
    }

    private static void addPromotionMoves(Collection<ChessMove> moves, ChessPosition from, ChessPosition to) {
        moves.add(new ChessMove(from, to, PieceType.QUEEN));
        moves.add(new ChessMove(from, to, PieceType.ROOK));
        moves.add(new ChessMove(from, to, PieceType.BISHOP));
        moves.add(new ChessMove(from, to, PieceType.KNIGHT));
    }

    //helpers for chess
    private boolean onBoard(int row, int col){
        return row >= 1 && row <= 8 && col >=1 && col <=8;
    }

    private void addStepMove(Collection<ChessMove> moves, ChessBoard board, ChessPosition from, int toRow, int toCol) {
        if (!onBoard(toRow, toCol)) {

            return;
        }
        ChessPosition to = new ChessPosition(toRow, toCol);
        ChessPiece target = board.getPiece(to);

        if (target == null || target.getTeamColor() != pieceColor) {
            moves.add(new ChessMove(from, to, null));
        }
    }

    private void addLineMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition from, int dRow, int dCol) {
        int row = from.getRow() + dRow;
        int col = from.getColumn() + dCol;

        while (onBoard(row, col)) {
            ChessPosition to = new ChessPosition(row, col);
            ChessPiece target = board.getPiece(to);

            if (target == null) {
                moves.add(new ChessMove(from, to, null));
            } else {
                if (target.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(from, to, null));
                }
                break; //stop if there is a piece
            }
            row += dRow;
            col += dCol;
        }
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
