package ubc.cosc322;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class IterativeDeepening{

    public static valueMovePair iterativeDeepeningSearch(int[][] boardState, boolean isBlack, int timeLimitSec, Map<String, Object> lastMove) {
        AtomicBoolean timeUp = new AtomicBoolean(false); // Thread-safe flag
        valueMovePair bestResult = new valueMovePair(0, null);
        valueMovePair temp = new valueMovePair(0, null);

        // Scheduled executor to stop search after the time limit
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            timeUp.set(true);
            System.out.println("Time's up! Returning best result found so far...");
        }, timeLimitSec, TimeUnit.SECONDS);

        int depth = 1; // Start with depth 1
        while (!timeUp.get()) { // Check flag atomically
            valueMovePair result = miniMaxSearch(boardState, isBlack, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, lastMove, timeUp);
            if (result != null) {
                temp = result; // Update best result
                System.out.println("Depth " + depth + " evaluation: " +  result.getMove() + " with evaluation " + result.getValue());
            }
            if (timeUp.get()) {
            	System.out.println("Evaluation at depth = "+depth+" got cut off, returning result from depth = "+(depth-1));
            	return bestResult;
            }else {
            	bestResult=temp;
            }
            
            depth++; // Increase depth
        }

        scheduler.shutdown();
        System.out.println("Final best result from Iterative Deepening: " + bestResult.getValue());
        return bestResult;
    }

    private static valueMovePair miniMaxSearch(int[][] boardState, boolean isBlack, int depth,
                                                int alpha, int beta, Map<String, Object> lastMove, AtomicBoolean timeUp) {
        int currentDepth = 0;
        // For simplicity, if it's isBlack turn, call maxValue; adjust as per your design.
        return maxValue(boardState, isBlack, depth, currentDepth, alpha, beta, lastMove, timeUp);
    }

    public static valueMovePair maxValue(int[][] boardState, boolean isBlack, int depth, int currentDepth, int alpha, int beta,
                                         Map<String, Object> receivedMove, AtomicBoolean timeUp) {
        // Check time flag at the start of the call.
        if (timeUp.get() || currentDepth >= depth) {
            return new valueMovePair(evaluate(boardState, isBlack), receivedMove);
        }
        
        if (isTerminal(boardState, isBlack)) {
            return new valueMovePair(evaluate(boardState, isBlack), null);
        }
        
        int bestValue = Integer.MIN_VALUE;
        valueMovePair bestPair = new valueMovePair(bestValue, null);
        for (Map<String, Object> move : MoveGenerator.generateAllMoves(boardState, isBlack)) {
            // Check time in the loop as well
            if (timeUp.get()) {
                break;
            }
            valueMovePair candidate = minValue(MoveGenerator.simulateMove(boardState, move), isBlack, depth, currentDepth + 1, alpha, beta, move, timeUp);
            if (candidate.getValue() > bestPair.getValue()) {
                bestPair = new valueMovePair(candidate.getValue(), move);
                alpha = Math.max(alpha, candidate.getValue());
            }
            if (bestPair.getValue() > beta) {
                return bestPair;
            }
        }
        return bestPair;
    }

    public static valueMovePair minValue(int[][] boardState, boolean isBlack, int depth, int currentDepth, int alpha, int beta,
                                         Map<String, Object> receivedMove, AtomicBoolean timeUp) {
        if (timeUp.get() || currentDepth >= depth) {
            return new valueMovePair(evaluate(boardState, isBlack), receivedMove);
        }
        
        if (isTerminal(boardState, !isBlack)) {
            return new valueMovePair(evaluate(boardState, isBlack), null);
        }
        
        int bestValue = Integer.MAX_VALUE;
        valueMovePair bestPair = new valueMovePair(bestValue, null);
        // Note: For minValue, we generate moves for the opponent (adjust if needed)
        for (Map<String, Object> move : MoveGenerator.generateAllMoves(boardState, !isBlack)) {
            if (timeUp.get()) {
                break;
            }
            valueMovePair candidate = maxValue(MoveGenerator.simulateMove(boardState, move), isBlack, depth, currentDepth + 1, alpha, beta, move, timeUp);
            if (candidate.getValue() < bestPair.getValue()) {
                bestPair = new valueMovePair(candidate.getValue(), move);
                beta = Math.min(beta, candidate.getValue());
            }
            if (bestPair.getValue() < alpha) {
                return bestPair;
            }
        }
        return bestPair;
    }

    // Placeholder: Evaluate board state using a heuristic.
    public static int evaluate(int[][] boardState, boolean isBlack) {
        int whiteTotal = 0;
        int blackTotal = 0;
        List<int[]> whiteQueens = MoveGenerator.findQueens(boardState, false);
        List<int[]> blackQueens = MoveGenerator.findQueens(boardState, true);
        for (int[] queen : whiteQueens) {
            List<int[]> validMoves = MoveGenerator.getValidMoves(boardState, queen[0], queen[1]);
            whiteTotal += validMoves.size();
        }
        for (int[] queen : blackQueens) {
            List<int[]> validMoves = MoveGenerator.getValidMoves(boardState, queen[0], queen[1]);
            blackTotal += validMoves.size();
        }
        return isBlack ? (blackTotal - whiteTotal) : (whiteTotal - blackTotal);
    }

    // Placeholder: Determine if the board state is terminal (game over) for the given player.
    public static boolean isTerminal(int[][] boardState, boolean isBlack) {
        List<int[]> queens = isBlack ? MoveGenerator.findQueens(boardState, true) : MoveGenerator.findQueens(boardState, false);
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] queen : queens) {
            int row = queen[0], col = queen[1];
            for (int[] dir : directions) {
                int r = row + dir[0], c = col + dir[1];
                if (r >= 0 && r < boardState.length && c >= 0 && c < boardState[0].length && boardState[r][c] == 0) {
                    return false; // Found a move, so not terminal.
                }
            }
        }
        return true;
    }

    // Main method to set up a board and run iterative deepening for testing.
    public static void main(String[] args) {
        int boardSize = 10;
        int[][] boardState = new int[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            Arrays.fill(boardState[i], 0);
        }
        
        // Place one white queen and one black queen.
        boardState[2][1] = 2; // White queen.
        boardState[2][2] = 2;
        boardState[2][5] = 2;
        boardState[4][4] = 2;
        boardState[5][5] = 1; // Black queen.
        boardState[5][3] = 1;
        boardState[5][1] = 1;
        boardState[4][3] = 1;
        
        
        
        // Place some arrows (3) to block moves near the white queen.
        boardState[0][1] = 3;
        boardState[1][0] = 3;
        
        System.out.println("Initial Board State:");
        for (int i = 0; i < boardSize; i++) {
            System.out.println(Arrays.toString(boardState[i]));
        }
       
        Map<String, Object> lastmoveBlack = new HashMap<>();
        lastmoveBlack.put("queen-position-current", new ArrayList<>(Arrays.asList(4, 5)));
        lastmoveBlack.put("queen-position-next", new ArrayList<>(Arrays.asList(5, 5)));
        Map<String, Object> lastmoveWhite = new HashMap<>();
        lastmoveWhite.put("queen-position-current", new ArrayList<>(Arrays.asList(1, 1)));
        lastmoveWhite.put("queen-position-next", new ArrayList<>(Arrays.asList(2, 1)));
        
        // Run iterative deepening search for white to move.
        System.out.println("\nRunning iterative deepening search for white to move:");
        valueMovePair bestWhiteMove = iterativeDeepeningSearch(boardState, false, 30, null);
        System.out.println("Best move for white: " + bestWhiteMove.getMove() + " with evaluation " + bestWhiteMove.getValue());
        
        // Run iterative deepening search for black to move.
        System.out.println("\nRunning iterative deepening search for black to move:");
        valueMovePair bestBlackMove = iterativeDeepeningSearch(boardState, true, 30, null);
        System.out.println("Best move for black: " + bestBlackMove.getMove() + " with evaluation " + bestBlackMove.getValue());
    }
}

