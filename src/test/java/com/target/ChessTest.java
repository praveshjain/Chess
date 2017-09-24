package com.target;

import com.target.chess.game.Board;
import junit.framework.TestCase;

public class ChessTest extends TestCase {

    public void testMatch1() throws Exception{
        Board board = new Board();
        String[] moves = {"Nf3","Nf6","c4","g6","Nc3","Bg7","d4","0-0","Bf4","d5","Qb3","dxc4","Qxc4","c6","e4","Nbd7","Rd1","Nb6","Qc5","Bg4","Bg5","Na4","Qa3","Nxc3","bxc3","Nxe4","Bxe7","Qb6","Bc4","Nxc3","Bc5","Rfe8+","Kf1","Be6","Bxb6","Bxc4","Kg1","Ne2+","Kf1","Nxd4+","Kg1","Ne2+","Kf1","Nc3+","Kg1","axb6","Qb4","Ra4","Qxb6","Nxd1","h3","Rxa2","Kh2","Nxf2","Re1","Rxe1","Qd8+","Bf8","Nxe1","Bd5","Nf3","Ne4","Qb8","b5","h4","h5","Ne5","Kg7","Kg1","Bc5+","Kf1","Ng3+","Ke1","Bb4+","Kd1","Bb3+","Kc1","Ne2+","Kb1","Nc3+","Kc1","Rc2#"};
        String fen = null;
        for (int i=0 ; i<moves.length ; i++){
            fen = board.tryMove(moves[i]);
            if (i==20) assertEquals(fen, "r2q1rk1/pp2ppbp/1np2np1/2Q3B1/3PP1b1/2N2N2/PP3PPP/3RKB1R b K - 6 11");
            if (i==32) assertEquals(fen, "r3r1k1/pp3pbp/1qp3p1/2B5/2BP2b1/Q1n2N2/P4PPP/3R1K1R b - - 3 17");
        }
        assertEquals(fen, "1Q6/5pk1/2p3p1/1p2N2p/1b5P/1bn5/2r3P1/2K5 w - - 16 42");
    }

    public void testPromotion() throws Exception{
        Board board = new Board();
        String[] moves = {"e4","d5","exd5","c6","dxc6","b6","c7","Nc6","cxd8=Q+"};
        String fen = "";
        for (String move:moves){
            fen = board.tryMove(move);
        }
        assertEquals(fen, "r1bQkbnr/p3pppp/1pn5/8/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 5");
    }
}
