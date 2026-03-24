package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardPrinter {

    public String printWhiteBoard(ChessGame game) {
        return printBoard(game, true);
    }
    public String printBlackBoard(ChessGame game) {
        return printBoard(game, false);
    }

    private String printBoard(ChessGame game, boolean whiteView) {
        ChessBoard board = game.getBoard();
        String result = "";

        if (whiteView) {
            result += "  a  b  c  d  e  f  g  h\n";
            for (int row = 8; row >= 1; row--) {
                result += row + " ";
                for (int col = 1; col <= 8; col++) {
                    result += pieceString(board, row, col);
                }
                result += " " + row + "\n";
            }
            result += "  a  b  c  d  e  f  g  h";

        } else {
            result += "  h  g  f  e  d  c  b  a\n";
            for(int row = 1; row <= 8; row++) {
                result += row + " ";
                for (int col = 8; col >= 1; col--) {
                    result += pieceString(board, row, col);
                }
                result += " " + row + "\n";
            }
            result += "  h  g  f  e  d  c  b  a";
        }
        return result;
    }

    private String pieceString(ChessBoard board, int row, int col) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null) {
            return ".  ";
        }
        return pieceSymbol(piece) + " ";
    }

    private String pieceSymbol(ChessPiece piece) {
        ChessPiece.PieceType type = piece.getPieceType();
        ChessGame.TeamColor color = piece.getTeamColor();

        if (color == ChessGame.TeamColor.WHITE) {
            switch (type) {
                case KING:
                    return "K";
                case QUEEN:
                    return "Q";
                case ROOK:
                    return "R";
                case BISHOP:
                    return "B";
                case KNIGHT:
                    return "N";
                case PAWN:
                    return "P";
            }
        } else {
            switch (type) {
                case KING:
                    return "k";
                case QUEEN:
                    return "q";
                case ROOK:
                    return "r";
                case BISHOP:
                    return "b";
                case KNIGHT:
                    return "n";
                case PAWN:
                    return "p";
            }
        }
        return "?";
    }
}