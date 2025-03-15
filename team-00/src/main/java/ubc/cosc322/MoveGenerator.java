package ubc.cosc322;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MoveGenerator {

    private static final int BOARD_SIZE = 10;

    public static Map<String, Object> generateMove(int[][] boardState, boolean isBlack) {
        List<Map<String, Object>> allValidMoves = generateAllMoves(boardState, isBlack);
        if (allValidMoves.isEmpty()) {
            System.out.println("Game Over: No valid moves available.");
            return null;
        }

        Random rand = new Random();
        Map<String, Object> selectedMove = allValidMoves.get(rand.nextInt(allValidMoves.size()));
        return selectedMove;
    }

    public static List<Map<String, Object>> generateAllMoves(int[][] boardState, boolean isBlack) {
        //System.out.println("From MoveGenerator: Generating all moves");
        List<Map<String, Object>> moves = new ArrayList<>();
        List<int[]> queens = findQueens(boardState, isBlack);

        for (int[] queen : queens) {
            List<int[]> validMoves = getValidMoves(boardState, queen[0], queen[1]);
            for (int[] newPos : validMoves) {
                List<int[]> arrowMoves = getValidMoves(boardState, newPos[0], newPos[1]);
                for (int[] arrow : arrowMoves) {
                    Map<String, Object> move = new HashMap<>();
                    move.put("queen-position-current", new int[]{queen[0], queen[1]});
                    move.put("queen-position-next", new int[]{newPos[0], newPos[1]});
                    move.put("arrow-position", new int[]{arrow[0], arrow[1]});
                    moves.add(move);
                }
            }
        }
       // System.out.println("From MoveGenerating: preparing to return moves"); 
        return moves;
    }

    public static int[][] simulateMove(int[][] boardState, Map<String, Object> move) {
        int[][] newBoard = cloneBoard(boardState);
        int[] qcurr = (int[]) move.get("queen-position-current");
        int[] qnew = (int[]) move.get("queen-position-next");
        int[] arrow = (int[]) move.get("arrow-position");
    
        int queenType = newBoard[qcurr[0]][qcurr[1]];
        newBoard[qcurr[0]][qcurr[1]] = 0;
        newBoard[qnew[0]][qnew[1]] = queenType;
        newBoard[arrow[0]][arrow[1]] = 3; // Arrow mark
        return newBoard; // Fix: Return new board instead of modifying existing one
    }
    

    private static List<int[]> findQueens(int[][] boardState, boolean isBlack) {
        List<int[]> queens = new ArrayList<>();
        int queenValue = isBlack ? 1 : 2;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (boardState[i][j] == queenValue) {
                    queens.add(new int[]{i, j});
                }
            }
        }
        return queens;
    }

    public static List<int[]> getValidMoves(int[][] boardState, int row, int col) {
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    
        for (int[] dir : directions) {
            int r = row + dir[0], c = col + dir[1];
    
            while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE) {
                if (boardState[r][c] != 0) {
                    break;
                }
                if (r >= BOARD_SIZE || c >= BOARD_SIZE || r < 0 || c < 0) { 
                    break; // Fix: Ensure out-of-bounds is checked **before adding**
                }
                moves.add(new int[]{r, c});
                r += dir[0];
                c += dir[1];
            }
        }
        return moves;
    }
    

    public static int evaluateBoard(int[][] boardState, boolean isMaximizing) {
        int blackMobility = 0, whiteMobility = 0;
        int blackCount = 0, whiteCount = 0;

        for (int[] queen : findQueens(boardState, true)) {
            blackMobility += getValidMoves(boardState, queen[0], queen[1]).size();
            blackCount++;
        }
        for (int[] queen : findQueens(boardState, false)) {
            whiteMobility += getValidMoves(boardState, queen[0], queen[1]).size();
            whiteCount++;
        }

        int mobilityScore = blackMobility - whiteMobility;
        int queenAdvantage = 10 * (blackCount - whiteCount);

        return mobilityScore != 0 ? mobilityScore : queenAdvantage;
    }

    public static boolean isGameOver(int[][] boardState) {
        return generateAllMoves(boardState, true).isEmpty() || generateAllMoves(boardState, false).isEmpty();
    }

    public static int[][] cloneBoard(int[][] board) {
        int[][] copy = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, BOARD_SIZE);
        }
        return copy;
    }
}
