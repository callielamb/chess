package client;

import chess.*;
import ui.EscapeSequences;

import java.util.Collection;

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
            result += headerWhite();
            for (int row = 8; row >= 1; row--) {
                result += EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + " ";
                for (int col = 1; col <= 8; col++) {
                    boolean lightSquare= (row + col) % 2 != 0;
                    result += squareString(board, row, col, lightSquare);
                }
                result += EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + "\n";
            }
            result += headerWhite();
        } else {
            result += headerBlack();

            for (int row = 1; row <= 8; row++) {
                result += EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + " ";

                for (int col = 8; col >= 1; col--) {
                    boolean lightSquare =(row + col) % 2 != 0;
                    result += squareString(board, row, col, lightSquare);
                }
                result += EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + "\n";
            }
            result += headerBlack();
        }
        result += EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;
        return result;
    }

    private String headerWhite() {
        String result = EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_WHITE + "   ";
        for (char file = 'a'; file <= 'h'; file++) {
            result += "\u2003" + file + " ";
        }
        return result + "\n";
    }

    private String headerBlack() {
        String result = EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_WHITE + "   ";
        for (char file = 'h'; file >= 'a'; file--) {
            result += "\u2003" + file + " ";
        }
        return result + "\n";
    }

    private String squareString(ChessBoard board, int row, int col, boolean lightSquare) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        String background;
        if (lightSquare) {
            background = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        } else {
            background = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        }
        return background + pieceSymbol(piece);
    }

    private String pieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.SET_TEXT_COLOR_BLACK + EscapeSequences.EMPTY;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return EscapeSequences.SET_TEXT_COLOR_WHITE + switch (piece.getPieceType()) {
                case KING -> EscapeSequences.WHITE_KING;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case PAWN -> EscapeSequences.WHITE_PAWN;
            };
        } else {
            return EscapeSequences.SET_TEXT_COLOR_BLACK + switch (piece.getPieceType()) {
                case KING -> EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        }
    }
    public String printHighlightedBoard(ChessGame game, ChessPosition selected, boolean blackView) {
        Collection<ChessMove> validMoves = game.validMoves(selected);
        return printBoardWithHighlights(game, selected, validMoves, blackView);
    }

    private String printBoardWithHighlights(ChessGame game, ChessPosition selected,
                                            Collection<ChessMove> validMoves, boolean blackView) {
        ChessBoard board = game.getBoard();
        String result = "";

        if (!blackView) {
            result += headerWhite();

            for (int row = 8; row >= 1; row--) {
                result += EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + " ";

                for (int col = 1; col <= 8; col++) {
                    result += highlightedSquareString(board, selected, validMoves, row, col);
                }

                result += EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + "\n";
            }

            result += headerWhite();
        } else {
            result += headerBlack();

            for (int row = 1; row <= 8; row++) {
                result += EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + " ";

                for (int col = 8; col >= 1; col--) {
                    result += highlightedSquareString(board, selected, validMoves, row, col);
                }

                result += EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + "\n";
            }

            result += headerBlack();
        }

        result += EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR;
        return result;
    }
    private String highlightedSquareString(ChessBoard board, ChessPosition selected,
                                           Collection<ChessMove> validMoves, int row, int col) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));

        String background;

        if (isSelectedSquare(selected, row, col)) {
            background = EscapeSequences.SET_BG_COLOR_YELLOW;
        } else if (isValidMoveSquare(validMoves, row, col)) {
            background = EscapeSequences.SET_BG_COLOR_GREEN;
        } else {
            boolean lightSquare = (row + col) % 2 != 0;
            if (lightSquare) {
                background = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
            } else {
                background = EscapeSequences.SET_BG_COLOR_DARK_GREY;
            }
        }

        return background + pieceSymbol(piece);
    }
    private boolean isSelectedSquare(ChessPosition selected, int row, int col) {
        return selected.getRow() == row && selected.getColumn() == col;
    }

    private boolean isValidMoveSquare(Collection<ChessMove> validMoves, int row, int col) {
        for (ChessMove move : validMoves) {
            if (move.getEndPosition().getRow() == row && move.getEndPosition().getColumn() == col) {
                return true;
            }
        }
        return false;
    }
}