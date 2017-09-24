package com.target.chess.utils;

public class Constants {

    public enum Color{
        WHITE("WHITE"),
        BLACK("BLACK");

        private String value;

        Color(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum MoveType{
        NORMAL("NORMAL"),
        DOUBLE_STEP("DOUBLE_STEP"),
        PROMOTION("PROMOTION"),
        CASTLE("CASTLE"),
        EN_PASSENT("EN_PASSENT"),
        ILLEGAL("ILLEGAL");

        private String value;

        MoveType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PieceType{
        PAWN("PAWN"),
        ROOK("ROOK"),
        KNIGHT("KNIGHT"),
        BISHOP("BISHOP"),
        QUEEN("QUEEN"),
        KING("KING");

        private String value;

        PieceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    public static final String DEFAULT_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public static String getCharForNumber(int i) {
        return i > -1 && i < 26 ? String.valueOf((char)(i + 97)) : null;
    }
}
