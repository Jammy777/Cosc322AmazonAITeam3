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
        
        boolean hasMoves = false;
        for (int[] q : queens) {
            if (!getValidMoves(boardState, q[0], q[1]).isEmpty()) {
                hasMoves = true;
                break;
            }
        }
        if (!hasMoves) {
            System.out.println("Game Over: No possible moves left.");
            return null;
        }
        
        Map<String, Object> bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        
        // Iterate over each queen.
        for (int[] queen : queens) {
            List<int[]> queenMoves = getValidMoves(boardState, queen[0], queen[1]);
            for (int[] newQueenPos : queenMoves) {
                // Clone board and simulate queen move.
                int[][] boardAfterQueen = cloneBoard(boardState);
                boardAfterQueen[queen[0]][queen[1]] = 0;
                boardAfterQueen[newQueenPos[0]][newQueenPos[1]] = player;
                
                // Get arrow moves from new queen position.
                List<int[]> arrowMoves = getValidMoves(boardAfterQueen, newQueenPos[0], newQueenPos[1]);
                for (int[] arrowPos : arrowMoves) {
                    // Clone board and simulate arrow shot.
                    int[][] boardAfterMove = cloneBoard(boardAfterQueen);
                    boardAfterMove[arrowPos[0]][arrowPos[1]] = 3; // 3 represents a blocked cell.
                    
                    Board newBoard = new Board(boardAfterMove);
                    int score = evaluator.evaluate(newBoard, player);
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new HashMap<>();
                        ArrayList<Integer> qcurr = new ArrayList<>(Arrays.asList(queen[0], queen[1]));
                        ArrayList<Integer> qnew = new ArrayList<>(Arrays.asList(newQueenPos[0], newQueenPos[1]));
                        ArrayList<Integer> arrowList = new ArrayList<>(Arrays.asList(arrowPos[0], arrowPos[1]));
                        bestMove.put("queen-position-current", qcurr);
                        bestMove.put("queen-position-new", qnew);
                        bestMove.put("arrow-position", arrowList);
                    }
                }
            }
        }
        
        if (bestMove == null) {
            return null;
        }
        
        ArrayList<Integer> qcurr = (ArrayList<Integer>) bestMove.get("queen-position-current");
        ArrayList<Integer> qnew = (ArrayList<Integer>) bestMove.get("queen-position-new");
        ArrayList<Integer> arrowList = (ArrayList<Integer>) bestMove.get("arrow-position");
        System.out.println("Selected move: Queen from " + qcurr + " to " + qnew + " with arrow at " + arrowList);
        
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