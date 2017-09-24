package com.target.chess.game;

import com.target.chess.exceptions.IllegalMoveException;
import com.target.chess.utils.Constants;
import com.target.chess.utils.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.target.chess.utils.Constants.Color.BLACK;
import static com.target.chess.utils.Constants.Color.WHITE;

public class Board {

    private Piece[][] grid;
    private Point enpassent;
    private Constants.Color turn;
    private boolean isWhiteKingSideCastlingAvailable;
    private boolean isWhiteQueenSideCastlingAvailable;
    private boolean isBlackKingSideCastlingAvailable;
    private boolean isBlackQueenSideCastlingAvailable;
    private int halfMoveClock;
    private int numFullMoves;
    private Stack<Move> history;

    public Board() {
        this.grid = new Piece[Constants.HEIGHT][Constants.WIDTH];
        resetGameState();
    }

    public Piece getPieceAt(Point point){
        return getPieceAt(point.getX(), point.getY());
    }

    public Piece getPieceAt(int x, int y){
        return grid[x][y];
    }

    public void print(){
        System.out.println("___________________________");
        for (int i=0 ; i<grid.length ; i++){
            System.out.print(Constants.HEIGHT - i);
            for (int j=0 ; j<grid[0].length ; j++){
                System.out.print(" ");
                Piece piece = grid[i][j];
                System.out.print(piece == null ? "-" : piece.getPieceChar());
            }
            System.out.println();
        }
        System.out.print(" ");
        for (int j=0 ; j<grid[0].length ; j++){
            System.out.print(" " + Constants.getCharForNumber(j));
        }
        System.out.println();
        System.out.println("___________________________");
    }

    public String getFEN(){
        StringBuilder fenBuilder = new StringBuilder();
        for (int i=0 ; i<Constants.HEIGHT ; i++){
            StringBuilder rowBuilder = new StringBuilder();
            int emptyCells = 0;
            for (int j=0 ; j<Constants.WIDTH ; j++){
                Piece piece = grid[i][j];
                if (piece == null){
                    emptyCells ++;
                } else{
                    if (emptyCells !=0) rowBuilder.append(emptyCells);
                    rowBuilder.append(piece.getPieceChar());
                    emptyCells = 0;
                }
            }
            if (emptyCells !=0) rowBuilder.append(emptyCells);
            if (i!=Constants.HEIGHT-1) rowBuilder.append("/");
            fenBuilder.append(rowBuilder);
        }

        fenBuilder.append(" ");
        fenBuilder.append(BLACK.equals(turn) ? "b" : "w");

        fenBuilder.append(" ");

        StringBuilder castlingBuilder = new StringBuilder();
        castlingBuilder.append(isWhiteKingSideCastlingAvailable ? "K" : "");
        castlingBuilder.append(isWhiteQueenSideCastlingAvailable ? "Q" : "");
        castlingBuilder.append(isBlackKingSideCastlingAvailable ? "k" : "");
        castlingBuilder.append(isBlackQueenSideCastlingAvailable ? "q" : "");
        fenBuilder.append(castlingBuilder.length() != 0 ? castlingBuilder : "-");

        fenBuilder.append(" ");
        fenBuilder.append(enpassent != null ? enpassent : "-");

        fenBuilder.append(" ");
        fenBuilder.append(halfMoveClock);

        fenBuilder.append(" ");
        fenBuilder.append(numFullMoves);

        return fenBuilder.toString();
    }

    public String tryMove(String moveNotation){
        if (moveNotation.charAt(moveNotation.length()-1) == '+' || moveNotation.charAt(moveNotation.length()-1) == '#'){
            moveNotation = moveNotation.substring(0, moveNotation.length() - 1);
        }
        int N = moveNotation.length();
        Constants.PieceType promotionType = null;
        if (moveNotation.contains("=")){
            char promotedTo = moveNotation.charAt(N-1);
            promotionType = Piece.getPieceType(promotedTo);
            moveNotation = moveNotation.substring(0, N-2);
        }
        List<Point> results = getSourceAndDestination(moveNotation);
        if (results.size() != 2) return "Invalid Move";
        try{
            return tryMove(results.get(0), results.get(1), promotionType);
        } catch (IllegalMoveException e){
            e.printStackTrace();
            return "Invalid move";
        }
    }

    private List<Point> getSourceAndDestination(String moveNotation){
        List<Point> results = new ArrayList<Point>();
        if (moveNotation.equals("0-0")){
            if (WHITE.equals(turn)){
                results.add(new Point(7, 4));
                results.add(new Point(7, 6));
                return results;
            } else{
                results.add(new Point(0, 4));
                results.add(new Point(0, 6));
                return results;
            }
        } else if (moveNotation.equals("0-0-0")){
            if (WHITE.equals(turn)){
                results.add(new Point(7, 4));
                results.add(new Point(7, 2));
                return results;
            } else{
                results.add(new Point(0, 4));
                results.add(new Point(0, 2));
                return results;
            }
        }
        int N = moveNotation.length();
        String destinationStr = moveNotation.substring(N-2, N);
        Point destination = new Point(destinationStr);
        if (N == 2){
            List<Piece> pawns = getPieceFromBoard(turn, Constants.PieceType.PAWN);
            for (Piece pawn : pawns){
                if (!Constants.MoveType.ILLEGAL.equals(getMoveType(pawn, destination))){
                    results.add(pawn.getLocation());
                    break;
                }
            }
        } else{
            String pieceStr;
            if (moveNotation.charAt(N-3) == 'x'){
                pieceStr = moveNotation.substring(0, N-3);
            } else{
                pieceStr = moveNotation.substring(0, N-2);
            }
            Constants.PieceType pieceType = Piece.getPieceType(pieceStr.charAt(0));

            List<Piece> pieces = getPieceFromBoard(turn, pieceType);
            String pieceLocationInfo = Constants.PieceType.PAWN.equals(pieceType) ? pieceStr : pieceStr.substring(1);
            for (Piece piece : pieces){
                if (piece.getLocation().toString().contains(pieceLocationInfo) && !Constants.MoveType.ILLEGAL.equals(getMoveType(piece, destination))){
                    results.add(piece.getLocation());
                    break;
                }
            }
        }
        results.add(destination);
        return results;
    }

    private String tryMove(Point source, Point destination, Constants.PieceType promotionType) throws IllegalMoveException{
        Piece pieceAtSource = getPieceAt(source);
        if (pieceAtSource == null) throw new IllegalMoveException("No piece at " + source);
        if (!turn.equals(pieceAtSource.getColor())) throw new IllegalMoveException("Please wait for your turn");
        Constants.MoveType moveType = getMoveType(pieceAtSource, destination);
        if (Constants.MoveType.ILLEGAL.equals(moveType)) throw new IllegalMoveException("Move not allowed");
        if (Constants.MoveType.PROMOTION.equals(moveType) && promotionType == null) throw new IllegalMoveException("Move not allowed");
        Point ep = null;
        if (Constants.MoveType.DOUBLE_STEP.equals(moveType)){
            int y = BLACK.equals(pieceAtSource.getColor()) ? -1 : 1;
            ep = new Point(destination.getX(), destination.getY() + y);
        }
        makeMove(source, destination, ep, moveType, promotionType);
        if (isKingInCheck(pieceAtSource.getColor())){
            undoLastMove();
            throw new IllegalMoveException("Move not allowed");
        }
        return getFEN();
    }

    public String undoLastMove(){
        if (!history.isEmpty()) {
            Move lastMove = history.pop();
            Point from = lastMove.getFrom();
            Point to = lastMove.getTo();
            Piece movedPiece = getPieceAt(lastMove.getTo());
            movePiece(to, from);
            if (lastMove.promotionType != null) movedPiece.setPieceType(Constants.PieceType.PAWN);
            if (Constants.MoveType.CASTLE.equals(lastMove.getMoveType())) {
                grid[to.getX()][to.getY()] = null;
                Point rookFrom;
                Point rookTo;
                if (to.getX() == 0 && to.getY() == 2) {
                    rookFrom = new Point(0, 3);
                    rookTo = new Point(0, 0);
                } else if (to.getX() == 0 && to.getY() == 6) {
                    rookFrom = new Point(0, 5);
                    rookTo = new Point(0, 7);
                } else if (to.getX() == 7 && to.getY() == 2) {
                    rookFrom = new Point(7, 3);
                    rookTo = new Point(7, 0);
                } else {
                    rookFrom = new Point(7, 5);
                    rookTo = new Point(7, 7);
                }
                movePiece(rookFrom, rookTo);
            } else if (Constants.MoveType.EN_PASSENT.equals(lastMove.getMoveType())) {
                grid[to.getX()][to.getY()] = null;
                int x = to.getX();
                Piece killedPawn;
                Point pawnLocation;
                if (BLACK.equals(movedPiece.getColor())) {
                    pawnLocation = new Point(x, to.getY() - 1);
                    killedPawn = new Piece(pawnLocation, WHITE, Constants.PieceType.PAWN);
                } else {
                    pawnLocation = new Point(x, to.getY() + 1);
                    killedPawn = new Piece(pawnLocation, BLACK, Constants.PieceType.PAWN);
                }
                grid[pawnLocation.getX()][pawnLocation.getY()] = killedPawn;
            } else {
                grid[to.getX()][to.getY()] = lastMove.getKilledPiece();
            }

            if (history.isEmpty()) {
                enpassent = null;
                numFullMoves = 1;
                halfMoveClock = 0;
                isWhiteKingSideCastlingAvailable = true;
                isWhiteQueenSideCastlingAvailable = true;
                isBlackKingSideCastlingAvailable = true;
                isBlackQueenSideCastlingAvailable = true;
            } else {
                Move moveBeforeLast = history.peek();
                enpassent = moveBeforeLast.getEnPassent();
                numFullMoves = moveBeforeLast.getNumFullMoves();
                halfMoveClock = moveBeforeLast.getHalfMoveClock();
                isWhiteKingSideCastlingAvailable = moveBeforeLast.isWhiteKingSideCastlingAvailable();
                isWhiteQueenSideCastlingAvailable = moveBeforeLast.isWhiteQueenSideCastlingAvailable();
                isBlackKingSideCastlingAvailable = moveBeforeLast.isBlackKingSideCastlingAvailable();
                isBlackQueenSideCastlingAvailable = moveBeforeLast.isBlackQueenSideCastlingAvailable();
            }
            flipTurn();

        }
        return getFEN();
    }

    private Constants.MoveType getMoveType(Piece piece, Point destination){
        Point source = piece.getLocation();
        Piece pieceAtDestination = getPieceAt(destination);
        int xDiff = destination.getX() - source.getX();
        int yDiff = destination.getY() - source.getY();
        if (pieceAtDestination != null && pieceAtDestination.getColor().equals(piece.getColor())) return Constants.MoveType.ILLEGAL;
        switch (piece.getPieceType()){
            case PAWN:
                return getMoveTypeForPawn(source, destination, xDiff, yDiff);
            case ROOK:
                return getMoveTypeForRook(source, destination, xDiff, yDiff);
            case KNIGHT:
                return getMoveTypeForKnight(source, destination, xDiff, yDiff);
            case BISHOP:
                return getMoveTypeForBishop(source, destination, xDiff, yDiff);
            case QUEEN:
                return getMoveTypeForQueen(source, destination, xDiff, yDiff);
            case KING:
                return getMoveTypeForKing(source, destination, xDiff, yDiff);
        }
        return Constants.MoveType.ILLEGAL;
    }

    private Constants.MoveType getMoveTypeForPawn(Point source, Point destination, int xDiff, int yDiff){
        Piece piece = getPieceAt(source);
        Piece pieceAtDestination = getPieceAt(destination);
        if (yDiff == 0){
            // Forward makeMove
            if (Constants.Color.BLACK.equals(piece.getColor())) {
                if (xDiff == 1){
                    if (destination.getX() == Constants.HEIGHT) return Constants.MoveType.PROMOTION;
                    return Constants.MoveType.NORMAL;
                }
                if ((xDiff == 2) && (source.getX() == 1) && pieceAtDestination == null && isPathClear(source, destination)) return Constants.MoveType.DOUBLE_STEP;
            } else {
                if (xDiff == -1){
                    if (destination.getX() == 0) return Constants.MoveType.PROMOTION;
                    return Constants.MoveType.NORMAL;
                }
                if ((xDiff == -2) && (source.getX() == 6) && pieceAtDestination == null && isPathClear(source, destination)) return Constants.MoveType.DOUBLE_STEP;
            }
        } else{
            // Capture
            if (Constants.Color.BLACK.equals(piece.getColor())) {
                if ((yDiff == 1 || yDiff == -1) && (xDiff == 1)){
                    if (pieceAtDestination != null){
                        if (destination.getX() == Constants.HEIGHT) return Constants.MoveType.PROMOTION;
                        return Constants.MoveType.NORMAL;
                    }
                    else if (destination.equals(enpassent)) return Constants.MoveType.EN_PASSENT;
                }
            } else{
                if ((yDiff == 1 || yDiff == -1) && (xDiff == -1)){
                    if (pieceAtDestination != null){
                        if (destination.getX() == 0) return Constants.MoveType.PROMOTION;
                        return Constants.MoveType.NORMAL;
                    }
                    else if (destination.equals(enpassent)) return Constants.MoveType.EN_PASSENT;
                }
            }
        }
        return Constants.MoveType.ILLEGAL;
    }

    private Constants.MoveType getMoveTypeForRook(Point source, Point destination, int xDiff, int yDiff){
        if ((xDiff == 0 || yDiff == 0) && isPathClear(source, destination)) return Constants.MoveType.NORMAL;
        return Constants.MoveType.ILLEGAL;
    }

    private Constants.MoveType getMoveTypeForKnight(Point source, Point destination, int xDiff, int yDiff){
        if ( (Math.abs(xDiff) == 2 && Math.abs(yDiff) == 1) || (Math.abs(xDiff) == 1 && Math.abs(yDiff) == 2)) return Constants.MoveType.NORMAL;
        return Constants.MoveType.ILLEGAL;
    }

    private Constants.MoveType getMoveTypeForBishop(Point source, Point destination, int xDiff, int yDiff){
        if (Math.abs(xDiff) == Math.abs(yDiff) && isPathClear(source, destination)) return Constants.MoveType.NORMAL;
        return Constants.MoveType.ILLEGAL;
    }

    private Constants.MoveType getMoveTypeForQueen(Point source, Point destination, int xDiff, int yDiff){
        if (getMoveTypeForRook(source, destination, xDiff, yDiff).equals(Constants.MoveType.NORMAL) || getMoveTypeForBishop(source, destination, xDiff, yDiff).equals(Constants.MoveType.NORMAL)) return Constants.MoveType.NORMAL;
        return Constants.MoveType.ILLEGAL;
    }

    private Constants.MoveType getMoveTypeForKing(Point source, Point destination, int xDiff, int yDiff){
        Piece piece = getPieceAt(source);
        if ((Math.abs(xDiff) == 1 && yDiff == 0) || (xDiff == 0 && Math.abs(yDiff) == 1) || (Math.abs(xDiff) == 1 && Math.abs(yDiff) == 1)){
            return Constants.MoveType.NORMAL;
        } else if (Math.abs(yDiff) == 2 && xDiff == 0){
            if (isReachable(source, getComplementaryColor(piece.getColor()))) return Constants.MoveType.ILLEGAL;
            if (!isPathClear(source, destination)) return Constants.MoveType.ILLEGAL;
            if (yDiff == 2){
                // King side castle
                if (isReachable(new Point(source.getX(), source.getY() + 1), getComplementaryColor(piece.getColor()))) return Constants.MoveType.ILLEGAL;
                if (isReachable(new Point(source.getX(), source.getY() + 2), getComplementaryColor(piece.getColor()))) return Constants.MoveType.ILLEGAL;
                return Constants.MoveType.CASTLE;
            } else{
                if (isReachable(new Point(source.getX(), source.getY() - 1), getComplementaryColor(piece.getColor()))) return Constants.MoveType.ILLEGAL;
                if (isReachable(new Point(source.getX(), source.getY() - 2), getComplementaryColor(piece.getColor()))) return Constants.MoveType.ILLEGAL;
                return Constants.MoveType.CASTLE;
            }
        }
        return Constants.MoveType.ILLEGAL;
    }

    private boolean isPathClear(Point source, Point destination){
        int xDiff = destination.getX() - source.getX();
        int xDir = xDiff == 0 ? 0 : xDiff/Math.abs(xDiff);
        int yDiff = destination.getY() - source.getY();
        int yDir = yDiff == 0 ? 0 : yDiff/Math.abs(yDiff);

        if (xDiff == 0){
            for (int y=source.getY() + yDir; yDir > 0 ? y<destination.getY() : y>destination.getY() ; y+=yDir){
                if (grid[source.getX()][y] != null){
                    return false;
                }
            }
            return true;
        } else if(yDiff == 0){
            for (int x=source.getX() + xDir; xDir > 0 ? x<destination.getX() : x>destination.getX() ; x+=xDir){
                if (grid[x][source.getY()] != null){
                    return false;
                }
            }
            return true;
        } else{
            // Diagonal moves
            for (int y=source.getY() + yDir, x=source.getX() + xDir; yDir > 0 ? y<destination.getY() : y>destination.getY() ; y+=yDir, x+=xDir){
                if (grid[x][y] != null) return false;
            }
            return true;
        }
    }

    private boolean isKingInCheck(Constants.Color kingsColor){
        Piece king = getPieceFromBoard(kingsColor, Constants.PieceType.KING).get(0);
        return isReachable(king.getLocation(), getComplementaryColor(kingsColor));
    }

    private List<Piece> getPieceFromBoard(Constants.Color color, Constants.PieceType pieceType){
        List<Piece> pieces = new ArrayList<Piece>();
        for (int i=0 ; i<Constants.HEIGHT ; i++){
            for (int j=0 ; j<Constants.WIDTH ; j++){
                Piece piece = getPieceAt(i, j);
                if (piece != null && piece.getColor().equals(color) && piece.getPieceType().equals(pieceType)) pieces.add(piece);
            }
        }
        return pieces;
    }

    private boolean isReachable(Point destination, Constants.Color color){
        for (int i=0 ; i<Constants.HEIGHT ; i++) {
            for (int j = 0; j < Constants.WIDTH; j++) {
                Piece piece = getPieceAt(i, j);
                if (piece != null && piece.getColor().equals(color) && !Constants.MoveType.ILLEGAL.equals(getMoveType(piece, destination))){
                    return true;
                }
            }
        }
        return false;
    }

    private Constants.Color getComplementaryColor(Constants.Color color){
        return (WHITE.equals(color)) ? BLACK : WHITE;
    }

    private void makeMove(Point from, Point to, Point ep, Constants.MoveType moveType, Constants.PieceType promotionType){
        // Make the move
        Piece movedPiece = getPieceAt(from);
        Piece killedPiece = getPieceAt(to);
        movePiece(from, to);
        if (Constants.MoveType.CASTLE.equals(moveType)) {
            moveRookForCastle(getCastlingRook(to));
        } else if (Constants.MoveType.EN_PASSENT.equals(moveType)){
            int x = from.getX();
            int y = BLACK.equals(movedPiece.getColor()) ? from.getY() + 1 : from.getY() - 1;
            grid[x][y] = null;
        }

        if (movedPiece.getPieceType().equals(Constants.PieceType.PAWN)){
            if (movedPiece.getColor().equals(BLACK) && to.getX() == Constants.HEIGHT) movedPiece.setPieceType(promotionType);
            else if (to.getX() == 0) movedPiece.setPieceType(promotionType);
        }

        // Update the game state
        if (Constants.PieceType.PAWN.equals(movedPiece.getPieceType()) || killedPiece != null){
            halfMoveClock = 0;
        } else{
            halfMoveClock ++;
        }
        if (BLACK.equals(movedPiece.getColor())) numFullMoves ++;
        enpassent = ep;

        if (Constants.PieceType.KING.equals(movedPiece.getPieceType())){
            if (WHITE.equals(movedPiece.getColor())){
                isWhiteKingSideCastlingAvailable = false;
                isWhiteQueenSideCastlingAvailable = false;
            } else{
                isBlackKingSideCastlingAvailable = false;
                isBlackQueenSideCastlingAvailable = false;
            }
        } else if(Constants.PieceType.ROOK.equals(movedPiece.getPieceType())){
            if ((WHITE.equals(movedPiece.getColor()))){
                if (from.getX() == 7 && from.getY() == 0) isWhiteQueenSideCastlingAvailable = false;
                else if (from.getX() == 7 && from.getY() == 7) isWhiteKingSideCastlingAvailable = false;
            } else{
                if (from.getX() == 0 && from.getY() == 0) isBlackQueenSideCastlingAvailable = false;
                else if (from.getX() == 0 && from.getY() == 7) isBlackKingSideCastlingAvailable = false;
            }
        }

        // Change the turn
        flipTurn();

        // Record the move in history
        Move move = new Move(from, to, killedPiece, moveType, promotionType, ep, isWhiteKingSideCastlingAvailable, isWhiteQueenSideCastlingAvailable, isBlackKingSideCastlingAvailable, isBlackQueenSideCastlingAvailable, halfMoveClock, numFullMoves);
        history.push(move);
    }

    private Point getCastlingRook(Point kingsLanding){
        if (kingsLanding.getX() == 0 && kingsLanding.getY() == 2) return new Point(0, 0);
        else if (kingsLanding.getX() == 0 && kingsLanding.getY() == 6) return new Point(0, 7);
        else if (kingsLanding.getX() == 7 && kingsLanding.getY() == 2) return new Point(7, 0);
        else return new Point(7, 7);
    }

    private void moveRookForCastle(Point origin){
        Point destination;
        if (origin.getX() == 0 && origin.getY() == 0) destination = new Point(0, 3);
        else if (origin.getX() == 0 && origin.getY() == 7) destination = new Point(0, 5);
        else if (origin.getX() == 7 && origin.getY() == 0) destination = new Point(7, 3);
        else destination = new Point(7, 5);

        movePiece(origin, destination);
    }

    private void movePiece(Point p1, Point p2){
        Piece piece = grid[p1.getX()][p1.getY()];
        grid[p1.getX()][p1.getY()] = null;
        grid[p2.getX()][p2.getY()] = piece;
        piece.setLocation(p2);
    }

    private void flipTurn(){
        this.turn = WHITE.equals(this.turn) ? BLACK : WHITE;
    }

    private void resetGameState(){
        setGameState(Constants.DEFAULT_FEN);
    }

    private void setGameState(String fenString) throws IllegalArgumentException{
        validate(fenString);
        String parts[] = fenString.split(" ");

        this.history = new Stack<Move>();

        String[] gridParts = parts[0].split("/");
        for (int i=0 ; i<Constants.HEIGHT ; i++){
            String rowString = gridParts[i];
            int j=0;
            while (j<Constants.WIDTH){
                char pieceChar = rowString.charAt(j);
                if (Character.isDigit(pieceChar)){
                    j += (int)pieceChar;
                } else{
                    Point location = new Point(i, j);
                    Piece piece = new Piece(location, pieceChar);
                    grid[i][j] = piece;
                    j++;
                }
            }
        }

        this.turn = parts[1].equals("w") ? WHITE : BLACK;
        this.isWhiteKingSideCastlingAvailable = parts[2].contains("K");
        this.isWhiteQueenSideCastlingAvailable = parts[2].contains("Q");
        this.isBlackKingSideCastlingAvailable = parts[2].contains("k");
        this.isBlackQueenSideCastlingAvailable = parts[2].contains("q");

        this.enpassent = parts[3].equals("-") ? null : new Point(parts[2]);
        this.halfMoveClock = Integer.valueOf(parts[4]);
        this.numFullMoves = Integer.valueOf(parts[5]);
    }

    private void validate(String fenString){
        String parts[] = fenString.split(" ");
        if (parts.length != 6) throw new IllegalArgumentException("Invalid FEN");
        String[] gridParts = fenString.split("/");
        if (gridParts.length != Constants.HEIGHT) throw new IllegalArgumentException("Invalid FEN");
        for (String part : gridParts){
            if (part.length() == 0) throw new IllegalArgumentException("Invalid FEN");
        }
    }
}
