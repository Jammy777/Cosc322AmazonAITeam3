package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

class Heuristics{
	
    
	public static int minimumDistance(queenLocationBoardPair qlbp, boolean isBlack) {
		int[][] whiteDistances;
	    int[][] blackDistances;
	    int heuristic = 0;
	    List<int[]> whiteQueens = qlbp.getQueenLocations(false);
	    List<int[]> blackQueens = qlbp.getQueenLocations(true);

	    // Create a board to track minimum distances for both players.
	    int boardSize = qlbp.getBoard().length;
	    whiteDistances = new int[boardSize][boardSize];
	    blackDistances = new int[boardSize][boardSize];
	    for (int[] row : whiteDistances) Arrays.fill(row, Integer.MAX_VALUE);
	    for (int[] row : blackDistances) Arrays.fill(row, Integer.MAX_VALUE);

	    // Perform BFS for the white queens.
	    for (int[] queen : whiteQueens) {
	        int[][] thisQueensDistance=bfs(qlbp.getBoard(), queen[0], queen[1]);
	        updateMinDistanceArray(thisQueensDistance, whiteDistances);
	    }

	    // Perform BFS for the black queens.
	    for (int[] queen : blackQueens) {
	    	int[][] thisQueensDistance=bfs(qlbp.getBoard(), queen[0], queen[1]);
	        updateMinDistanceArray(thisQueensDistance, blackDistances);
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
	public static int[][] bfs(int[][] board, int startX, int startY) {
		int[][] distances = new int[board.length][board.length];
	    for (int[] row : distances) { Arrays.fill(row, Integer.MAX_VALUE);}
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
	    return distances;
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
	public static int mobilityTesting(int[][] board, boolean isBlack) {
		int whiteTotal = 0;
        int blackTotal = 0;
        List<int[]> whiteQueens = MoveGenerator.findQueens(board, false);
        List<int[]> blackQueens = MoveGenerator.findQueens(board, true);
        for (int[] queen : whiteQueens) {
            List<int[]> validMoves = MoveGenerator.getValidMoves(board, queen[0], queen[1]);
            whiteTotal += validMoves.size();
        }
        for (int[] queen : blackQueens) {
            List<int[]> validMoves = MoveGenerator.getValidMoves(board, queen[0], queen[1]);
            blackTotal += validMoves.size();
        }
        return isBlack ? (blackTotal - whiteTotal) : (whiteTotal - blackTotal);
	}
	public static int[][] updateMinDistanceArray(int[][] singleQueenDistanceBoard, int[][] totalQueenDistanceBoard ) {
		 for (int i = 0; i < singleQueenDistanceBoard.length; i++) {
		        for (int j = 0; j < singleQueenDistanceBoard[0].length; j++) {
		        	if (singleQueenDistanceBoard[i][j]<totalQueenDistanceBoard[i][j]) {
		        		totalQueenDistanceBoard[i][j]=singleQueenDistanceBoard[i][j];
		        		}
		        }
		 }
		 return totalQueenDistanceBoard;
	}
	public static int KingTerritoryControl(queenLocationBoardPair state, boolean isBlack) {
	    int[][] board = state.getBoard();
	    int boardSize = board.length;
	    int[][] whiteDistancesKing;
	    int[][] blackDistancesKing;

	    whiteDistancesKing = new int[boardSize][boardSize];
	    blackDistancesKing = new int[boardSize][boardSize];

	    // Fill distances with "infinity"
	    for (int i = 0; i < boardSize; i++) {	
	        Arrays.fill(whiteDistancesKing[i], Integer.MAX_VALUE);
	        Arrays.fill(blackDistancesKing[i], Integer.MAX_VALUE);
	    }

	    // Collect queens
	    List<int[]> whiteQueens = state.getQueenLocations(false);
	    List<int[]> blackQueens = state.getQueenLocations(true);

	    // Run BFS from each queen (like multisource BFS)
	    for (int[] p : blackQueens) {
	    	
	    	int[][] thisKingsDistance=bfsKingMoves(state.getBoard(), p[0], p[1]);
	        updateMinDistanceArray(thisKingsDistance, blackDistancesKing);
	    }
	    for (int[] p : whiteQueens) {
	    	int[][] thisKingsDistance=bfsKingMoves(state.getBoard(), p[0], p[1]);
	        updateMinDistanceArray(thisKingsDistance, whiteDistancesKing);
	    }

	    // Compare control
	    int score = 0;
	    for (int x = 0; x < boardSize; x++) {
	        for (int y = 0; y < boardSize; y++) {
	            if (board[x][y] != 0) continue; // skip blocked cells

	            int blackDist = blackDistancesKing[x][y];
	            int whiteDist = whiteDistancesKing[x][y];

	            if (blackDist < whiteDist) score--;
	            else if (blackDist > whiteDist) score++;
	        }
	    }

	    return isBlack? -score:score;
	}

	private static int[][] bfsKingMoves(int[][] board, int startX, int startY) {
		int[][] distances = new int[board.length][board.length];
	    for (int[] row : distances) { Arrays.fill(row, Integer.MAX_VALUE);}
	    int boardSize = board.length;
	    Queue<int[]> queue = new LinkedList<>();
	    queue.offer(new int[]{startX, startY});
	    distances[startX][startY] = 0;

	    int[] dx = {-1, 1, 0, 0, -1, -1, 1, 1};
	    int[] dy = {0, 0, -1, 1, -1, 1, -1, 1};

	    while (!queue.isEmpty()) {
	        int[] current = queue.poll();
	        int x = current[0];
	        int y = current[1];

	        for (int i = 0; i < 8; i++) {
	            int newX = x + dx[i];
	            int newY = y + dy[i];

	            if (newX >= 0 && newX < boardSize && newY >= 0 && newY < boardSize &&
	                board[newX][newY] == 0 && distances[newX][newY] == Integer.MAX_VALUE) {
	                
	                distances[newX][newY] = distances[x][y] + 1;
	                queue.offer(new int[]{newX, newY});
	            }
	        }
	    }
	    return distances;
	}
	

	
	


}