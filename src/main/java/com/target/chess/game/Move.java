package com.target.chess.game;

import com.target.chess.utils.Constants;
import com.target.chess.utils.Point;

public class Move {
    private Point from;
    private Point to;
    private Piece killedPiece;
    private Constants.MoveType moveType;
    Constants.PieceType promotionType;
    private Point enPassent;
    private boolean isWhiteKingSideCastlingAvailable;
    private boolean isWhiteQueenSideCastlingAvailable;
    private boolean isBlackKingSideCastlingAvailable;
    private boolean isBlackQueenSideCastlingAvailable;
    private int halfMoveClock;
    private int numFullMoves;

    public Move(Point from, Point to, Piece killedPiece, Constants.MoveType moveType, Constants.PieceType promotionType, Point enPassent, boolean isWhiteKingSideCastlingAvailable, boolean isWhiteQueenSideCastlingAvailable, boolean isBlackKingSideCastlingAvailable, boolean isBlackQueenSideCastlingAvailable, int halfMoveClock, int numFullMoves) {
        this.from = from;
        this.to = to;
        this.killedPiece = killedPiece;
        this.moveType = moveType;
        this.promotionType = promotionType;
        this.enPassent = enPassent;
        this.isWhiteKingSideCastlingAvailable = isWhiteKingSideCastlingAvailable;
        this.isWhiteQueenSideCastlingAvailable = isWhiteQueenSideCastlingAvailable;
        this.isBlackKingSideCastlingAvailable = isBlackKingSideCastlingAvailable;
        this.isBlackQueenSideCastlingAvailable = isBlackQueenSideCastlingAvailable;
        this.halfMoveClock = halfMoveClock;
        this.numFullMoves = numFullMoves;
    }

//    public Move(Point from, Point to, Piece killedPiece, Constants.MoveType moveType, Point enPassent) {
//        this.from = from;
//        this.to = to;
//        this.killedPiece = killedPiece;
//        this.moveType = moveType;
//        this.enPassent = enPassent;
//    }


    public Point getFrom() {
        return from;
    }

    public Point getTo() {
        return to;
    }

    public Piece getKilledPiece() {
        return killedPiece;
    }

    public Constants.MoveType getMoveType() {
        return moveType;
    }

    public Point getEnPassent() {
        return enPassent;
    }

    public boolean isWhiteKingSideCastlingAvailable() {
        return isWhiteKingSideCastlingAvailable;
    }

    public boolean isWhiteQueenSideCastlingAvailable() {
        return isWhiteQueenSideCastlingAvailable;
    }

    public boolean isBlackKingSideCastlingAvailable() {
        return isBlackKingSideCastlingAvailable;
    }

    public boolean isBlackQueenSideCastlingAvailable() {
        return isBlackQueenSideCastlingAvailable;
    }

    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    public int getNumFullMoves() {
        return numFullMoves;
    }
}
