package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn;
    ChessBoard board;

    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     *
     * Takes as input a position on the chessboard and returns all moves the piece there can legally make.
     * If there is no piece at that location, this method returns null.
     * A move is valid if it is a "piece move" for the piece at the input location and making that move would not leave the team’s king in danger of check.
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //piece at the location?
        ChessPiece piece = board.getPiece(startPosition);
        //if no pice then return null
        if (piece == null){
            return null;
        }

        //valid options to move
        Collection<ChessMove> options = piece.pieceMoves(board, startPosition);
        //list of legal moves
        Collection<ChessMove> legalMoves = new ArrayList<>();

        //for each move in options
        for (ChessMove move : options) {
            ChessBoard copyBoard = new ChessBoard();
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition pos = new ChessPosition(row, col);
                    ChessPiece posPiece = board.getPiece(pos);

                    if (posPiece != null) {
                        copyBoard.addPiece(pos, posPiece);
                    }
                }
            }

            ChessPosition start = move.getStartPosition();
            ChessPosition go = move.getEndPosition();
            ChessPiece moving = copyBoard.getPiece(start);
            //clear current square
            copyBoard.addPiece(start, null);

            //make sure toa ccount for promotions?
            if (move.getPromotionPiece() != null) {
                copyBoard.addPiece(go, new ChessPiece(moving.getTeamColor(), move.getPromotionPiece()));
            } else {
                copyBoard.addPiece(go, moving);
            }

            //check if king is in check on the copy before adding to real board
            ChessBoard ogBoard = this.board;
            this.board = copyBoard;
            boolean inCheck = isInCheck(moving.getTeamColor());
            this.board = ogBoard;

            if (!inCheck) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     *
     * Receives a given move and executes it, provided it is a legal move.
     * If the move is illegal, it throws an InvalidMoveException.
     * A move is illegal if it is not a "valid" move for the piece at the starting location, or if it’s not the corresponding team's turn.
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition go = move.getEndPosition();

        ChessPiece piece = board.getPiece(start);

        //piece at start?
        if (piece == null){
            throw new InvalidMoveException("No piece at starting position");
        }
        //check whos turn it is, if not right then throw exception
        if(piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("Not your team's turn!!");
        }
        //valid move? if not, throw
        Collection<ChessMove> legal = validMoves(start);
        if (legal == null || !legal.contains(move)) {
            throw new InvalidMoveException("Invalid move!!");
        }
        //if passes all throws then can add move
        board.addPiece(start, null);

        //promoting pawn?
        if (move.getPromotionPiece() != null) {
            //add piece of same color but promoted ofc
            board.addPiece(go, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece())
            );
        } else {
            //no need to promote just add the piece!
            board.addPiece(go, piece);
        }

        //next turn = opposite color now
        if (teamTurn == TeamColor.BLACK) {
            teamTurn = TeamColor.WHITE;
        } else {
            teamTurn = TeamColor.BLACK;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     *
     * Returns true if the specified team’s King could be captured by an opposing piece.
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = null;

        //where is the king rn for this teamColor?
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                //if there is a piece AND correct color AND the type is king
                if ((piece != null) && (piece.getTeamColor() == teamColor) && (piece.getPieceType() == ChessPiece.PieceType.KING)) {
                    kingPos = pos;
                    break;
                }
            }
            if (kingPos != null) {
                break;
            }
        }

        //nothing there then false
        if (kingPos == null) {
            return false;
        }

        //check for capture options...
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition enemyPos = new ChessPosition(row, col);
                ChessPiece enemyPiece = board.getPiece(enemyPos);

                //if piece AND not the same color then its enemey
                if ((enemyPiece != null) && (enemyPiece.getTeamColor() != teamColor)) {
                    Collection<ChessMove> enemyMoves = enemyPiece.pieceMoves(board, enemyPos);

                    //for each move in enemyMoves
                    for (ChessMove move : enemyMoves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     *
     * Returns true if the given team has no way to protect their king from being captured.
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
        //if in check AND no legal moves =true
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     *
     * Returns true if the given team has no legal moves but their king is not in immediate danger.
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //is NOT in check AND no legal moves = true
        //if king is in danger -->return false
        if(isInCheck(teamColor)){
            return false;
        }
        //return true if no possible moves
        //look at each piece on board
        for (int row = 1; row<=8; row++){
            for(int col = 1; col<=8; col++){
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                //if there is a piece and its the right color, then add to moves
                if((piece != null) && (piece.getTeamColor() == teamColor)){
                    Collection<ChessMove> moves = validMoves(pos);
                    //if moves isnt empty then there is no stalemate
                    if(moves != null && !moves.isEmpty()){
                        return false;
                    }
                }

            }
        }
        //if not false then true
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }
}
