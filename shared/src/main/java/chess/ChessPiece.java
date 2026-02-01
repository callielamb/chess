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

        //r+1, c+2
        int nextRow = currentRow +1;
        int nextCol = currentCol +2;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r+1, c-2
        nextCol = currentCol -2;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r-1,c-2
        nextRow = currentRow -1;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //row -1, c+2
        nextRow = currentRow -1;
        nextCol = currentCol +2;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r+2, c+1
        nextRow = currentRow +2;
        nextCol = currentCol +1;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r+2, c-1
        nextCol = currentCol -1;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r-2,c-1
        nextRow = currentRow -2;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r-2,c+1
        nextCol = currentCol +1;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
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

        //r+1, c+1
        int nextRow = currentRow +1;
        int nextCol = currentCol +1;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r+1, c-1
        nextCol = currentCol -1;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r-1,c-1
        nextRow = currentRow -1;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r-1, c+1
        nextCol = currentCol +1;
        if(nextRow >=1 && nextRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r, c+1
        if(currentRow >=1 && currentRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(currentRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r,c-1
        nextCol = currentCol -1;
        if(currentRow >=1 && currentRow <=8 && nextCol >=1 && nextCol <=8){
            ChessPosition nextPos = new ChessPosition(currentRow,nextCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r-1, c
        if(nextRow >=1 && nextRow <=8 && currentCol >=1 && currentCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,currentCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        //r+1, c
        nextRow = currentRow +1;
        if(nextRow >=1 && nextRow <=8 && currentCol >=1 && currentCol <=8){
            ChessPosition nextPos = new ChessPosition(nextRow,currentCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null || nextPiece.getTeamColor() != pieceColor){
                moves.add(new ChessMove(myPosition, nextPos, null));
            }
        }

        return moves;
    }

    //rook move calc = up/down all the way til stop
    //4 options
    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        //for until it hits a piece in row
        //if the next piece is null, add the move and keep going
        //if the next piece is enemy, add the move and STOP
        for (int row = currentRow +1; row <=8; row++){
            ChessPosition nextPos = new ChessPosition(row,currentCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null){
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else{
                break;
            }
        }

        //for until it hits a piece in col
        //same logic
        for (int col = currentCol +1; col <=8; col++){
            ChessPosition nextPos = new ChessPosition(currentRow,col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null){
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else{
                break;
            }
        }

        //for until it hits a piece in col other way
        //same logic
        for (int col = currentCol -1; col >=1; col--){
            ChessPosition nextPos = new ChessPosition(currentRow,col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null){
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else{
                break;
            }
        }

        //for until it hits a piece in row other way
        //same logic
        for (int row = currentRow -1; row >=1; row--){
            ChessPosition nextPos = new ChessPosition(row,currentCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null){
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else{
                break;
            }
        }

        return moves;
    }

    // bishop move calc = diagonal all the way til stop
    //4 options
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        //for until it hits a piece in diagonal line ROW OR COLUMN
        //if the next piece is null, add the move and keep going
        //if the next piece is enemy, add the move and STOP
        //r+1, c+1
        for (int row = currentRow +1, col = currentCol +1; row <=8 && col <=8; row++, col++){
            ChessPosition nextPos = new ChessPosition(row, col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else {
                break;
            }
        }

        //r+1, c-1
        for (int row = currentRow +1, col = currentCol -1; row <=8 && col >=1; row++, col--){
            ChessPosition nextPos = new ChessPosition(row, col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else {
                break;
            }
        }

        //r-1, c+1
        for (int row = currentRow -1, col = currentCol +1; row >=1 && col <=8; row--, col++){
            ChessPosition nextPos = new ChessPosition(row, col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else {
                break;
            }
        }

        //r-1, c-1
        for (int row = currentRow -1, col = currentCol -1; row >=1 && col >=1; row--, col--){
            ChessPosition nextPos = new ChessPosition(row, col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else {
                break;
            }
        }

        return moves;
    }

    //queen move calc = bishop + rook = 8 options
    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();

        for (int row = currentRow +1; row <=8; row++){
            ChessPosition nextPos = new ChessPosition(row,currentCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null){
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else{
                break;
            }
        }

        for (int col = currentCol +1; col <=8; col++){
            ChessPosition nextPos = new ChessPosition(currentRow,col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null){
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else{
                break;
            }
        }

        for (int col = currentCol -1; col >=1; col--){
            ChessPosition nextPos = new ChessPosition(currentRow,col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null){
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else{
                break;
            }
        }

        for (int row = currentRow -1; row >=1; row--){
            ChessPosition nextPos = new ChessPosition(row,currentCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null){
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else{
                break;
            }
        }
        //r+1, c+1
        for (int row = currentRow +1, col = currentCol +1; row <=8 && col <=8; row++, col++){
            ChessPosition nextPos = new ChessPosition(row, col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else {
                break;
            }
        }

        //r+1, c-1
        for (int row = currentRow +1, col = currentCol -1; row <=8 && col >=1; row++, col--){
            ChessPosition nextPos = new ChessPosition(row, col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else {
                break;
            }
        }

        //r-1, c+1
        for (int row = currentRow -1, col = currentCol +1; row >=1 && col <=8; row--, col++){
            ChessPosition nextPos = new ChessPosition(row, col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else {
                break;
            }
        }

        //r-1, c-1
        for (int row = currentRow -1, col = currentCol -1; row >=1 && col >=1; row--, col--){
            ChessPosition nextPos = new ChessPosition(row, col);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if (nextPiece == null) {
                moves.add(new ChessMove(myPosition, nextPos, null));
            } else if (nextPiece.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, nextPos, null));
                break;
            } else {
                break;
            }
        }

        return moves;
    }

    //pawn move calc = the most work
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        //direction depends on color; -1 for black and +1 for white
        //promotion row also depends on color; 1 for black and 8 for white
        //starting row depends on color; 7 for black and 2 for white
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
        //col doesnt changte unless capturing diagonally
        //if nextpiece is null then move forward is an option
        //if diagonal  piece is enemy then move there is an option
        //if current row is 1 or 8 then promotion
        //if current row is 2 or 7 then can move up to 2! but also still 1 or diagonal

        //move forward 1 or 2
        if (movement >=1 && movement <=8){
            ChessPosition nextPos = new ChessPosition(movement,currentCol);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece == null){
                //promotion
                if(movement == promo){
                    moves.add(new ChessMove(myPosition, nextPos, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, nextPos, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, nextPos, PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, nextPos, PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(myPosition, nextPos, null));
                }
                //move 2 at start option
                if(currentRow == start){
                    int twoMoves = currentRow + (2*direction);
                    if(twoMoves >=1 && twoMoves <=8){
                        ChessPosition twoNextPos = new ChessPosition(twoMoves,currentCol);
                        ChessPiece twoNextPiece = board.getPiece(twoNextPos);
                        if(twoNextPiece == null){
                            moves.add(new ChessMove(myPosition, twoNextPos, null));
                        }
                    }
                }
            }
        }
        int colDirection = currentCol+1;
        if(movement >=1 && movement <=8 && colDirection >=1 && colDirection <=8){
            ChessPosition nextPos = new ChessPosition(movement,colDirection);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece != null){
                if(nextPiece.getTeamColor() != pieceColor){
                    if(movement == promo){
                        moves.add(new ChessMove(myPosition, nextPos, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, nextPos, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, nextPos, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, nextPos, PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPosition, nextPos, null));
                    }
                }
            }
        }
        colDirection = currentCol-1;
        if(movement >=1 && movement <=8 && colDirection >=1 && colDirection <=8){
            ChessPosition nextPos = new ChessPosition(movement,colDirection);
            ChessPiece nextPiece = board.getPiece(nextPos);
            if(nextPiece != null){
                if(nextPiece.getTeamColor() != pieceColor){
                    if(movement == promo){
                        moves.add(new ChessMove(myPosition, nextPos, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, nextPos, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, nextPos, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, nextPos, PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPosition, nextPos, null));
                    }
                }
            }
        }

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
