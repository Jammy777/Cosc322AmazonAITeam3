package ubc.cosc322;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IterativeDeepening {

    static valueMovePair bestResult;
    static int depth;
    static valueMovePair temp;
    static valueMovePair result;
    static long startTime;
    static long timeLimitMillis;
    static ForkJoinPool pool = new ForkJoinPool(); // Static pool for parallel execution

    public static valueMovePair iterativeDeepeningSearch(queenLocationBoardPair qlbp, boolean isBlack, int timeLimitSec,
                                                         Map<String, Object> lastMove, String heuristic) {

        bestResult = null;
        temp = null;
        result = null;
        depth = 1;
        startTime = System.currentTimeMillis();
        timeLimitMillis = timeLimitSec * 1000;

        while (System.currentTimeMillis() - startTime < timeLimitMillis) {
            result = miniMaxSearch(qlbp, isBlack, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, lastMove, heuristic);

            if (result != null && result.getMove() != null && System.currentTimeMillis() - startTime < timeLimitMillis) {
                temp = result;
                System.out.println("Depth " + depth + " evaluation: " + result.getMove() + " with evaluation "
                        + result.getValue() + " using " + heuristic);
            }

            if (temp != null && temp.getMove() != null && System.currentTimeMillis() - startTime < timeLimitMillis) {
                bestResult = temp;
            }

            if (System.currentTimeMillis() - startTime < timeLimitMillis) {
                depth++;
            }
        }

        System.out.println("Evaluation at depth = " + depth + " got cut off, returning result from depth = "
                + (depth - 1));

        if (bestResult != null) {
            System.out.println("Final best result from Iterative Deepening: " + bestResult.getMove() + " using " + heuristic);
        }

        return bestResult;
    }

    private static valueMovePair miniMaxSearch(queenLocationBoardPair qlbp, boolean isBlack, int depth, int alpha,
                                               int beta, Map<String, Object> lastMove, String heuristic) {
        int currentDepth = 0;
        return maxValue(qlbp, isBlack, depth, currentDepth, alpha, beta, lastMove, heuristic);
    }

    public static valueMovePair maxValue(queenLocationBoardPair qlbp, boolean isBlack, int depth, int currentDepth,
                                         int alpha, int beta, Map<String, Object> receivedMove, String heuristic) {

        if (isTerminal(qlbp, isBlack)) {
            return evaluateTerminal(qlbp, isBlack, heuristic, null);
        }

        if (System.currentTimeMillis() - startTime >= timeLimitMillis || currentDepth >= depth) {
            return evaluateTerminal(qlbp, isBlack, heuristic, receivedMove);
        }

        AtomicInteger alphaAtomic = new AtomicInteger(alpha);
        ConcurrentHashMap<Integer, Map<String, Object>> bestMoveMap = new ConcurrentHashMap<>();
        List<Map<String, Object>> allMoves = MoveGenerator.generateAllMoves(qlbp, isBlack);

        List<ForkJoinTask<valueMovePair>> tasks = new ArrayList<>();

        for (Map<String, Object> move : allMoves) {
            if (System.currentTimeMillis() - startTime >= timeLimitMillis) break;
            ForkJoinTask<valueMovePair> task = pool.submit(() -> {
                valueMovePair candidate = minValue(
                        MoveGenerator.simulateMove(qlbp, move), isBlack, depth, currentDepth + 1,
                        alphaAtomic.get(), beta, move, heuristic);
                synchronized (bestMoveMap) {
                    if (candidate.getValue() > alphaAtomic.get()) {
                        alphaAtomic.set(candidate.getValue());
                        bestMoveMap.put(candidate.getValue(), move);
                    }
                }
                return candidate;
            });
            tasks.add(task);
        }

        int bestValue = Integer.MIN_VALUE;
        for (ForkJoinTask<valueMovePair> task : tasks) {
            try {
                valueMovePair candidate = task.get();
                bestValue = Math.max(bestValue, candidate.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new valueMovePair(bestValue, bestMoveMap.get(bestValue));
    }

    public static valueMovePair minValue(queenLocationBoardPair qlbp, boolean isBlack, int depth, int currentDepth,
                                         int alpha, int beta, Map<String, Object> receivedMove, String heuristic) {

        if (isTerminal(qlbp, !isBlack)) {
            return evaluateTerminal(qlbp, isBlack, heuristic, null);
        }

        if (System.currentTimeMillis() - startTime >= timeLimitMillis || currentDepth >= depth) {
            return evaluateTerminal(qlbp, isBlack, heuristic, receivedMove);
        }

        AtomicInteger betaAtomic = new AtomicInteger(beta);
        ConcurrentHashMap<Integer, Map<String, Object>> bestMoveMap = new ConcurrentHashMap<>();
        List<Map<String, Object>> allMoves = MoveGenerator.generateAllMoves(qlbp, !isBlack);

        List<ForkJoinTask<valueMovePair>> tasks = new ArrayList<>();

        for (Map<String, Object> move : allMoves) {
            if (System.currentTimeMillis() - startTime >= timeLimitMillis) break;
            ForkJoinTask<valueMovePair> task = pool.submit(() -> {
                valueMovePair candidate = maxValue(
                        MoveGenerator.simulateMove(qlbp, move), isBlack, depth, currentDepth + 1,
                        alpha, betaAtomic.get(), move, heuristic);
                synchronized (bestMoveMap) {
                    if (candidate.getValue() < betaAtomic.get()) {
                        betaAtomic.set(candidate.getValue());
                        bestMoveMap.put(candidate.getValue(), move);
                    }
                }
                return candidate;
            });
            tasks.add(task);
        }

        int bestValue = Integer.MAX_VALUE;
        for (ForkJoinTask<valueMovePair> task : tasks) {
            try {
                valueMovePair candidate = task.get();
                bestValue = Math.min(bestValue, candidate.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new valueMovePair(bestValue, bestMoveMap.get(bestValue));
    }

    private static valueMovePair evaluateTerminal(queenLocationBoardPair qlbp, boolean isBlack, String heuristic,
                                                  Map<String, Object> move) {
        switch (heuristic) {
            case "mobility":
                return new valueMovePair(Heuristics.mobility(qlbp, isBlack), move);
            case "minimumDistance":
                return new valueMovePair(Heuristics.minimumDistance(qlbp, isBlack), move);
            case "mixed":
            	return new valueMovePair(Heuristics.mobility(qlbp, isBlack), move);
            case "mobileAndKingTerritoryMixed":
                return new valueMovePair((int) ( Heuristics.KingTerritoryControl(qlbp, isBlack))
                        +  Heuristics.mobility(qlbp, isBlack) , move);
            default:
                return new valueMovePair(Heuristics.mobility(qlbp, isBlack), move);
        }
    }

    public static boolean isTerminal(queenLocationBoardPair qlbp, boolean isBlack) {
        List<int[]> queens = qlbp.getQueenLocations(isBlack);
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };

        for (int[] queen : queens) {
            int row = queen[0], col = queen[1];
            for (int[] dir : directions) {
                int r = row + dir[0], c = col + dir[1];
                if (r >= 0 && r < qlbp.getBoard().length && c >= 0 && c < qlbp.getBoard()[0].length
                        && qlbp.getBoard()[r][c] == 0) {
                    return false;
                }
            }
        }
        return true;
    }
    public static boolean isTerminal(int[][] board, boolean isBlack) {
        List<int[]> queens = MoveGenerator.findQueens(board, isBlack);
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };

        for (int[] queen : queens) {
            int row = queen[0], col = queen[1];
            for (int[] dir : directions) {
                int r = row + dir[0], c = col + dir[1];
                if (r >= 0 && r < board.length && c >= 0 && c < board[0].length
                        && board[r][c] == 0) {
                    return false;
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
		queenLocations.add(new int[] { 5, 5 });
		queenLocations.add(new int[] { 5, 3 });
		queenLocations.add(new int[] { 5, 1 });
		queenLocations.add(new int[] { 4, 3 });
		// White queen positions.
		queenLocations.add(new int[] { 2, 1 });
		queenLocations.add(new int[] { 2, 2 });
		queenLocations.add(new int[] { 2, 5 });
		queenLocations.add(new int[] { 4, 4 });

		// Create the queenLocationBoardPair state.
		queenLocationBoardPair initialState = new queenLocationBoardPair(boardState, queenLocations);

		// Dummy move map (if needed by your search; otherwise pass null)
		Map<String, Object> dummyMove = new HashMap<>();

		// Run iterative deepening search for white to move.
		System.out.println("\nRunning iterative deepening search for white to move:");
		valueMovePair bestWhiteMove = iterativeDeepeningSearch(initialState, false, 28, dummyMove, "minimumDistance");
		System.out.println(
				"Best move for white: " + bestWhiteMove.getMove() + " with evaluation " + bestWhiteMove.getValue());

		// Run iterative deepening search for black to move.
		System.out.println("\nRunning iterative deepening search for black to move:");
		valueMovePair bestBlackMove = iterativeDeepeningSearch(initialState, true, 28, dummyMove, "mobileAndKingTerritoryMixedy");
		System.out.println(
				"Best move for black: " + bestBlackMove.getMove() + " with evaluation " + bestBlackMove.getValue());
	}
}