package ubc.cosc322;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class Heuristics{
	public static int minimumDistance(queenLocationBoardPair qlbp, boolean isBlack) {
	    int heuristic = 0;
	    List<int[]> whiteQueens = qlbp.getQueenLocations(false);
	    List<int[]> blackQueens = qlbp.getQueenLocations(true);

	    // Create a board to track minimum distances for both players.
	    int boardSize = qlbp.getBoard().length;
	    int[][] whiteDistances = new int[boardSize][boardSize];
	    int[][] blackDistances = new int[boardSize][boardSize];
	    for (int[] row : whiteDistances) Arrays.fill(row, Integer.MAX_VALUE);
	    for (int[] row : blackDistances) Arrays.fill(row, Integer.MAX_VALUE);

	    // Perform BFS for the white queens.
	    for (int[] queen : whiteQueens) {
	        bfs(qlbp.getBoard(), queen[0], queen[1], whiteDistances);
	    }

	    // Perform BFS for the black queens.
	    for (int[] queen : blackQueens) {
	        bfs(qlbp.getBoard(), queen[0], queen[1], blackDistances);
	    }

	    // Calculate heuristic based on minimum move distances.
	    for (int i = 0; i < boardSize; i++) {
	        for (int j = 0; j < boardSize; j++) {
	            if (qlbp.getBoard()[i][j] == 0) { // Only consider empty squares
	                int whiteDist = whiteDistances[i][j];
	                int blackDist = blackDistances[i][j];

	                if (whiteDist < blackDist) {
	                    heuristic += 1; // White can reach it faster
	                } else if (blackDist < whiteDist) {
	                    heuristic -= 1; // Black can reach it faster
	                }
	            }
	        }
	    }

	    return isBlack ? -heuristic : heuristic;
	}
	public static void bfs(int[][] board, int startX, int startY, int[][] distances) {
	    int boardSize = board.length;
	    Queue<int[]> queue = new LinkedList<>();
	    queue.offer(new int[]{startX, startY});
	    distances[startX][startY] = 0;

	    // Directions: up, down, left, right, top-left, top-right, bottom-left, bottom-right
	    int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};  // row changes
	    int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};  // column changes

	    while (!queue.isEmpty()) {
	        int[] current = queue.poll();
	        int x = current[0];
	        int y = current[1];

	        // Explore all 8 directions
	        for (int i = 0; i < 8; i++) {
	            int newX = x + dx[i];
	            int newY = y + dy[i];

	            // Continue moving in the current direction until we hit the board limits or an obstacle
	            while (newX >= 0 && newX < boardSize && newY >= 0 && newY < boardSize && board[newX][newY] == 0 && distances[newX][newY] == Integer.MAX_VALUE) {
	                distances[newX][newY] = distances[x][y] + 1;
	                queue.offer(new int[]{newX, newY});
	                
	                // Move further in the same direction
	                newX += dx[i];
	                newY += dy[i];
	            }
	        }
	    }
	}
	public static int mobility(queenLocationBoardPair qlbp, boolean isBlack) {
		int whiteTotal = 0;
        int blackTotal = 0;
        List<int[]> whiteQueens = qlbp.getQueenLocations(false);
        List<int[]> blackQueens = qlbp.getQueenLocations(true);
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
}