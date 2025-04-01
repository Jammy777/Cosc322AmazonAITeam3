package ubc.cosc322;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class IterativeDeepening{

	public static valueMovePair iterativeDeepeningSearch(queenLocationBoardPair qlbp, boolean isBlack, int timeLimitSec, Map<String, Object> lastMove) {
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
            valueMovePair result = miniMaxSearch(qlbp, isBlack, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, lastMove, timeUp);
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

    private static valueMovePair miniMaxSearch(queenLocationBoardPair qlbp, boolean isBlack, int depth,
                                                int alpha, int beta, Map<String, Object> lastMove, AtomicBoolean timeUp) {
        int currentDepth = 0;
        // For simplicity, if it's isBlack turn, call maxValue; adjust as per your design.
        return maxValue(qlbp, isBlack, depth, currentDepth, alpha, beta, lastMove, timeUp);
    }

    public static valueMovePair maxValue(queenLocationBoardPair qlbp, boolean isBlack, int depth, int currentDepth, int alpha, int beta,
                                         Map<String, Object> receivedMove, AtomicBoolean timeUp) {
        // Check time flag at the start of the call.
        if (timeUp.get() || currentDepth >= depth) {
            return new valueMovePair(evaluate(qlbp, isBlack), receivedMove);
        }
        
        if (isTerminal(qlbp, isBlack)) {
            return new valueMovePair(evaluate(qlbp, isBlack), null);
        }
        
        int bestValue = Integer.MIN_VALUE;
        valueMovePair bestPair = new valueMovePair(bestValue, null);
        for (Map<String, Object> move : MoveGenerator.generateAllMoves(qlbp, isBlack)) {
            // Check time in the loop as well
            if (timeUp.get()) {
                break;
            }
            valueMovePair candidate = minValue(MoveGenerator.simulateMove(qlbp, move), isBlack, depth, currentDepth + 1, alpha, beta, move, timeUp);
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

    public static valueMovePair minValue(queenLocationBoardPair qlbp, boolean isBlack, int depth, int currentDepth, int alpha, int beta,
                                         Map<String, Object> receivedMove, AtomicBoolean timeUp) {
        if (timeUp.get() || currentDepth >= depth) {
            return new valueMovePair(evaluate(qlbp, isBlack), receivedMove);
        }
        
        if (isTerminal(qlbp, !isBlack)) {
            return new valueMovePair(evaluate(qlbp, isBlack), null);
        }
        
        int bestValue = Integer.MAX_VALUE;
        valueMovePair bestPair = new valueMovePair(bestValue, null);
        // Note: For minValue, we generate moves for the opponent (adjust if needed)
        for (Map<String, Object> move : MoveGenerator.generateAllMoves(qlbp, !isBlack)) {
            if (timeUp.get()) {
                break;
            }
            valueMovePair candidate = maxValue(MoveGenerator.simulateMove(qlbp, move), isBlack, depth, currentDepth + 1, alpha, beta, move, timeUp);
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
    public static int evaluate(queenLocationBoardPair qlbp, boolean isBlack) {
        int whiteTotal = 0;
        int blackTotal = 0;
        List<int[]> whiteQueens = qlbp.getQueenLocations(false);
        List<int[]> blackQueens = qlbp.getQueenLocations(true);
		/*
		 * System.out.println("-------------B"); for (int[] arr : blackQueens) {
		 * System.out.println(Arrays.toString(arr)); }
		 * System.out.println("-------------W"); for (int[] arr : whiteQueens) {
		 * System.out.println(Arrays.toString(arr)); }
		 */
        for (int[] queen : whiteQueens) {
            List<int[]> validMoves = MoveGenerator.getValidMoves(qlbp.getBoard(), queen[0], queen[1]);
            whiteTotal += validMoves.size();
        }
        for (int[] queen : blackQueens) {
            List<int[]> validMoves = MoveGenerator.getValidMoves(qlbp.getBoard(), queen[0], queen[1]);
            blackTotal += validMoves.size();
        }
        return isBlack ? (blackTotal - whiteTotal) : (whiteTotal - blackTotal);
    }

    // Placeholder: Determine if the board state is terminal (game over) for the given player.
    public static boolean isTerminal(queenLocationBoardPair qlbp, boolean isBlack) {
        List<int[]> queens = qlbp.getQueenLocations(isBlack);
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] queen : queens) {
            int row = queen[0], col = queen[1];
            for (int[] dir : directions) {
                int r = row + dir[0], c = col + dir[1];
                if (r >= 0 && r < qlbp.getBoard().length && c >= 0 && c < qlbp.getBoard()[0].length && qlbp.getBoard()[r][c] == 0) {
                    return false; // Found a move, so not terminal.
                }
            }
        }
        return true;
    }
    public static boolean isTerminal(int[][] board, boolean isBlack) {
        List<int[]> queens = MoveGenerator.findQueens(board,  isBlack);
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] queen : queens) {
            int row = queen[0], col = queen[1];
            for (int[] dir : directions) {
                int r = row + dir[0], c = col + dir[1];
                if (r >= 0 && r < board.length && c >= 0 && c < board[0].length && board[r][c] == 0) {
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
        // Initialize board with zeros.
        for (int i = 0; i < boardSize; i++) {
            Arrays.fill(boardState[i], 0);
        }
        
        
        // Place queens (using convention: 2 = white, 1 = black).
        // White queens:
        boardState[2][1] = 2;
        boardState[2][2] = 2;
        boardState[2][5] = 2;
        boardState[4][4] = 2;
        // Black queens:
        boardState[5][5] = 1;
        boardState[5][3] = 1;
        boardState[5][1] = 1;
        boardState[4][3] = 1;
        
        // Optionally place some arrows (value 3) to block moves.
        boardState[0][1] = 3;
        boardState[1][0] = 3;
        
        // Print initial board state.
        System.out.println("Initial Board State:");
        for (int i = 0; i < boardSize; i++) {
            System.out.println(Arrays.toString(boardState[i]));
        }
       
        // Build the list of queen locations (combining both colors).
        // In a real implementation you might build these lists by scanning the board.
        List<int[]> queenLocations = new ArrayList<>();
        
        // Black queen positions.
        queenLocations.add(new int[]{5, 5});
        queenLocations.add(new int[]{5, 3});
        queenLocations.add(new int[]{5, 1});
        queenLocations.add(new int[]{4, 3});
     // White queen positions.
        queenLocations.add(new int[]{2, 1});
        queenLocations.add(new int[]{2, 2});
        queenLocations.add(new int[]{2, 5});
        queenLocations.add(new int[]{4, 4});
        
        // Create the queenLocationBoardPair state.
        queenLocationBoardPair initialState = new queenLocationBoardPair(boardState, queenLocations);
        
        
        
        // Dummy move map (if needed by your search; otherwise pass null)
        Map<String, Object> dummyMove = new HashMap<>();
        
        
        
        // Run iterative deepening search for white to move.
        System.out.println("\nRunning iterative deepening search for white to move:");
        valueMovePair bestWhiteMove = iterativeDeepeningSearch(initialState, false, 28, dummyMove);
        System.out.println("Best move for white: " + bestWhiteMove.getMove() 
                           + " with evaluation " + bestWhiteMove.getValue());
        
        // Run iterative deepening search for black to move.
        System.out.println("\nRunning iterative deepening search for black to move:");
        valueMovePair bestBlackMove = iterativeDeepeningSearch(initialState, true, 28, dummyMove);
        System.out.println("Best move for black: " + bestBlackMove.getMove() 
                           + " with evaluation " + bestBlackMove.getValue());
    }
}

