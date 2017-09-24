package com.target.chess;

import com.target.chess.game.Board;

import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Board board = new Board();

        Scanner scanner = new Scanner(System.in);
        String inputStr = "";
        while (!"exit".equals(inputStr)) {
            inputStr = scanner.nextLine();
            System.out.println(board.tryMove(inputStr));
            board.print();
        }
    }
}
