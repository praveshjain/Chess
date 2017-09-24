package com.target.chess.game;

import com.target.chess.utils.Point;
import com.target.chess.utils.Constants;

public class Piece {

    private Point location;
    private Constants.Color color;
    private Constants.PieceType pieceType;

    public Piece(Point location, Constants.Color color, Constants.PieceType pieceType) {
        this.location = location;
        this.color = color;
        this.pieceType = pieceType;
    }

    public void setPieceType(Constants.PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public Piece(Point location, char pieceChar) throws IllegalArgumentException {
        Constants.Color color;
        Constants.PieceType pieceType;

        switch (pieceChar){
            case 'p':
                color = Constants.Color.BLACK;
                pieceType = Constants.PieceType.PAWN;
                break;
            case 'r':
                color = Constants.Color.BLACK;
                pieceType = Constants.PieceType.ROOK;
                break;
            case 'b':
                color = Constants.Color.BLACK;
                pieceType = Constants.PieceType.BISHOP;
                break;
            case 'n':
                color = Constants.Color.BLACK;
                pieceType = Constants.PieceType.KNIGHT;
                break;
            case 'q':
                color = Constants.Color.BLACK;
                pieceType = Constants.PieceType.QUEEN;
                break;
            case 'k':
                color = Constants.Color.BLACK;
                pieceType = Constants.PieceType.KING;
                break;
            case 'P':
                color = Constants.Color.WHITE;
                pieceType = Constants.PieceType.PAWN;
                break;
            case 'R':
                color = Constants.Color.WHITE;
                pieceType = Constants.PieceType.ROOK;
                break;
            case 'B':
                color = Constants.Color.WHITE;
                pieceType = Constants.PieceType.BISHOP;
                break;
            case 'N':
                color = Constants.Color.WHITE;
                pieceType = Constants.PieceType.KNIGHT;
                break;
            case 'Q':
                color = Constants.Color.WHITE;
                pieceType = Constants.PieceType.QUEEN;
                break;
            case 'K':
                color = Constants.Color.WHITE;
                pieceType = Constants.PieceType.KING;
                break;
            default:
                throw new IllegalArgumentException("Invalid piece");
        }

        this.location = location;
        this.color = color;
        this.pieceType = pieceType;
    }

    public char getPieceChar(){
        char ch = 'p';
        switch (pieceType){
            case PAWN:
                ch = 'p';
                break;
            case ROOK:
                ch = 'r';
                break;
            case KNIGHT:
                ch = 'n';
                break;
            case BISHOP:
                ch = 'b';
                break;
            case QUEEN:
                ch = 'q';
                break;
            case KING:
                ch = 'k';
                break;
        }
        if (this.color == Constants.Color.WHITE){
            ch -= 32;
        }
        return ch;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Constants.Color getColor() {
        return color;
    }

    public Constants.PieceType getPieceType() {
        return pieceType;
    }

    public static Constants.PieceType getPieceType(char pieceChar){
        switch (pieceChar){
            case 'R':
                return Constants.PieceType.ROOK;
            case 'N':
                return Constants.PieceType.KNIGHT;
            case 'B':
                return Constants.PieceType.BISHOP;
            case 'Q':
                return Constants.PieceType.QUEEN;
            case 'K':
                return Constants.PieceType.KING;
            default:
                return Constants.PieceType.PAWN;
        }
    }

    @Override
    public String toString() {
        return "" + this.color.getValue() + " " + this.pieceType.getValue() + " at " + this.location;
    }
}
