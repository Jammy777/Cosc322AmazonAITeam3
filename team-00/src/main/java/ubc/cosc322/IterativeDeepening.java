package ubc.cosc322;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IterativeDeepening {

	public static valueMovePair iterativeDeepeningSearch(queenLocationBoardPair qlbp, boolean isBlack, int timeLimitSec,
			Map<String, Object> lastMove, String heuristic) {
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
			valueMovePair result = miniMaxSearch(qlbp, isBlack, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, lastMove,
					timeUp, heuristic);
			if (result != null) {
				temp = result; // Update best result
				System.out.println("Depth " + depth + " evaluation: " + result.getMove() + " with evaluation "
						+ result.getValue() +" using "+heuristic);
			}
			if (timeUp.get()) {
				System.out.println("Evaluation at depth = " + depth + " got cut off, returning result from depth = "
						+ (depth - 1));
				return bestResult;
			} else {
				bestResult = temp;
			}

			depth++; // Increase depth
		}

		scheduler.shutdown();
		System.out.println("Final best result from Iterative Deepening: " + bestResult.getValue() +" using "+heuristic);
		return bestResult;
	}

	private static valueMovePair miniMaxSearch(queenLocationBoardPair qlbp, boolean isBlack, int depth, int alpha,
			int beta, Map<String, Object> lastMove, AtomicBoolean timeUp, String heuristic) {
		int currentDepth = 0;
		
		return maxValue(qlbp, isBlack, depth, currentDepth, alpha, beta, lastMove, timeUp, heuristic);
	}

	public static valueMovePair maxValue(queenLocationBoardPair qlbp, boolean isBlack, int depth, int currentDepth,
			int alpha, int beta, Map<String, Object> receivedMove, AtomicBoolean timeUp, String heuristic) {
		if (timeUp.get() || currentDepth >= depth) {
			switch (heuristic) {
			case ("mobility"): return new valueMovePair(Heuristics.mobility(qlbp, isBlack), receivedMove);
			case ("minimumDistance"): return new valueMovePair(Heuristics.minimumDistance(qlbp, isBlack), receivedMove);
			default: return new valueMovePair(Heuristics.mobility(qlbp, isBlack), receivedMove);
			}
		}

		if (isTerminal(qlbp, isBlack)) {
			
			switch (heuristic) {
			case ("mobility"): return new valueMovePair(Heuristics.mobility(qlbp, isBlack), null);
			case ("minimumDistance"): return new valueMovePair(Heuristics.minimumDistance(qlbp, isBlack), null);
			default: return new valueMovePair(Heuristics.mobility(qlbp, isBlack), null);
			}
		}

		AtomicInteger alphaAtomic = new AtomicInteger(alpha);
		ConcurrentHashMap<Integer, Map<String, Object>> bestMoveMap = new ConcurrentHashMap<>();

		List<valueMovePair> results = MoveGenerator.generateAllMoves(qlbp, isBlack).parallelStream()
				.takeWhile(move -> !timeUp.get()) // Stop early if time runs out
				.map(move -> {
					valueMovePair candidate = minValue(MoveGenerator.simulateMove(qlbp, move), isBlack, depth,
							currentDepth + 1, alphaAtomic.get(), beta, move, timeUp, heuristic);
					synchronized (bestMoveMap) {
						if (candidate.getValue() > alphaAtomic.get()) {
							alphaAtomic.set(candidate.getValue());
							bestMoveMap.put(candidate.getValue(), move);
						}
					}
					return candidate;
				}).toList();

		int bestValue = results.stream().mapToInt(valueMovePair::getValue).max().orElse(Integer.MIN_VALUE);
		return new valueMovePair(bestValue, bestMoveMap.get(bestValue));
	}

	public static valueMovePair minValue(queenLocationBoardPair qlbp, boolean isBlack, int depth, int currentDepth,
			int alpha, int beta, Map<String, Object> receivedMove, AtomicBoolean timeUp, String heuristic) {
		if (timeUp.get() || currentDepth >= depth) {
			switch (heuristic) {
			case ("mobility"): return new valueMovePair(Heuristics.mobility(qlbp, isBlack), receivedMove);
			case ("minimumDistance"): return new valueMovePair(Heuristics.minimumDistance(qlbp, isBlack), receivedMove);
			default: return new valueMovePair(Heuristics.mobility(qlbp, isBlack), receivedMove);
			}
		}

		if (isTerminal(qlbp, !isBlack)) {
			switch (heuristic) {
			case ("mobility"): return new valueMovePair(Heuristics.mobility(qlbp, isBlack), null);
			case ("minimumDistance"): return new valueMovePair(Heuristics.minimumDistance(qlbp, isBlack), null);
			default: return new valueMovePair(Heuristics.mobility(qlbp, isBlack), null);
			}
		}

		AtomicInteger betaAtomic = new AtomicInteger(beta);
		ConcurrentHashMap<Integer, Map<String, Object>> bestMoveMap = new ConcurrentHashMap<>();

		List<valueMovePair> results = MoveGenerator.generateAllMoves(qlbp, !isBlack).parallelStream()
				.takeWhile(move -> !timeUp.get()) // Stop early if time runs out
				.map(move -> {
					valueMovePair candidate = maxValue(MoveGenerator.simulateMove(qlbp, move), isBlack, depth,
							currentDepth + 1, alpha, betaAtomic.get(), move, timeUp, heuristic);
					synchronized (bestMoveMap) {
						if (candidate.getValue() < betaAtomic.get()) {
							betaAtomic.set(candidate.getValue());
							bestMoveMap.put(candidate.getValue(), move);
						}
					}
					return candidate;
				}).toList();

		int bestValue = results.stream().mapToInt(valueMovePair::getValue).min().orElse(Integer.MAX_VALUE);
		return new valueMovePair(bestValue, bestMoveMap.get(bestValue));
	}

	


	// Placeholder: Determine if the board state is terminal (game over) for the
	// given player.
	public static boolean isTerminal(queenLocationBoardPair qlbp, boolean isBlack) {
		List<int[]> queens = qlbp.getQueenLocations(isBlack);
		int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };

		for (int[] queen : queens) {
			int row = queen[0], col = queen[1];
			for (int[] dir : directions) {
				int r = row + dir[0], c = col + dir[1];
				if (r >= 0 && r < qlbp.getBoard().length && c >= 0 && c < qlbp.getBoard()[0].length
						&& qlbp.getBoard()[r][c] == 0) {
					return false; // Found a move, so not terminal.
				}
			}
		}
		return true;
	}

	public static boolean isTerminal(int[][] board, boolean isBlack) { //used for testing
		List<int[]> queens = MoveGenerator.findQueens(board, isBlack);
		int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };

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
		valueMovePair bestBlackMove = iterativeDeepeningSearch(initialState, true, 28, dummyMove, "mobility");
		System.out.println(
				"Best move for black: " + bestBlackMove.getMove() + " with evaluation " + bestBlackMove.getValue());
	}
}
