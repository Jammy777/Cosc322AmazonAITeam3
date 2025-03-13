package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveGenerator {
    private static final int BOARD_SIZE = 10;
    
    public static Map<String, Object> generateMove(int[][] boardState, boolean isBlack) {
        HeuristicEvaluator evaluator = new HeuristicEvaluator();
        int player = isBlack ? 2 : 1;
        
        List<int[]> queens = findQueens(boardState, isBlack);
        if (queens.isEmpty()) {
            System.out.println("Game Over: No more queens left to move.");
            return null;
        }
        
        List<Integer> scores = new ArrayList<>();
        Map<Map<String, Object>, Integer> moveScores = new HashMap<>();
        Map<String, Object> bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        // Iterate over each queen.
        for (int[] queen : queens) {
            List<int[]> queenMoves = getValidMoves(boardState, queen[0], queen[1]);
            for (int[] newQueenPos : queenMoves) {
                int[][] boardAfterQueen = cloneBoard(boardState);
                boardAfterQueen[queen[0]][queen[1]] = 0;
                boardAfterQueen[newQueenPos[0]][newQueenPos[1]] = player;
                
                List<int[]> arrowMoves = getValidMoves(boardAfterQueen, newQueenPos[0], newQueenPos[1]);
                for (int[] arrowPos : arrowMoves) {
                    int[][] boardAfterMove = cloneBoard(boardAfterQueen);
                    boardAfterMove[arrowPos[0]][arrowPos[1]] = 3;
    
                    Board newBoard = new Board(boardAfterMove);
                    int score = evaluator.evaluate(newBoard, player);
                    scores.add(score);
                    
                    Map<String, Object> move = new HashMap<>();
                    move.put("queen-position-current", Arrays.asList(queen[0], queen[1]));
                    move.put("queen-position-new", Arrays.asList(newQueenPos[0], newQueenPos[1]));
                    move.put("arrow-position", Arrays.asList(arrowPos[0], arrowPos[1]));
                    
                    moveScores.put(move, score);
    
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = move;
                    }
                }
            }
        }
        
        if (bestMove == null) return null;
        
        // Compute average score
        double avgScore = scores.stream().mapToInt(Integer::intValue).average().orElse(0);
        double stdDev = Math.sqrt(scores.stream().mapToDouble(s -> Math.pow(s - avgScore, 2)).average().orElse(0));
        
        for (Map.Entry<Map<String, Object>, Integer> entry : moveScores.entrySet()) {
            Map<String, Object> move = entry.getKey();
            int score = entry.getValue();
            
            String evaluation = "Neutral move.";
            if (score > avgScore + stdDev) {
                evaluation = "Good move.";
            } else if (score < avgScore - stdDev) {
                evaluation = "Bad move.";
            }
            
            System.out.println("Move: " + move + " | Score: " + score + " | " + evaluation);
        }
        
        return bestMove;
    }
    
    private static List<int[]> findQueens(int[][] boardState, boolean isBlack) {
        List<int[]> queens = new ArrayList<>();
        int queenValue = isBlack ? 2 : 1;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (boardState[i][j] == queenValue) {
                    queens.add(new int[]{i, j});
                }
            }
        }
        return queens;
    }
    
    private static List<int[]> getValidMoves(int[][] boardState, int row, int col) {
        List<int[]> moves = new ArrayList<>();
        int[] directions = {-1, 0, 1};
        for (int dr : directions) {
            for (int dc : directions) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr, c = col + dc;
                while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && boardState[r][c] == 0) {
                    moves.add(new int[]{r, c});
                    r += dr;
                    c += dc;
                }
            }
        }
        return moves;
    }
    
    // Helper method to clone the board.
    private static int[][] cloneBoard(int[][] boardState) {
        int rows = boardState.length;
        int cols = boardState[0].length;
        int[][] copy = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(boardState[i], 0, copy[i], 0, cols);
        }
        return copy;
    }
}