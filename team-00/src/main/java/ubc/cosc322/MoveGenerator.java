package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveGenerator {
    private static final int BOARD_SIZE = 10;

    public static Map<String, Object> generateMove(int[][] boardState, boolean isBlack) {
        return MinMax.findBestMove(boardState, isBlack);  // Calls MinMax instead of selecting randomly
    }

    public static List<Map<String, Object>> generateAllMoves(int[][] boardState, boolean isBlack) {
        List<Map<String, Object>> moves = new ArrayList<>();
        //queenRemovedBoardState allows un-obstructed search of possible arrow moves for each queen move
        int[][] queenRemovedBoardState=cloneBoard(boardState);
        List<int[]> queens = findQueens(boardState, isBlack);

        for (int[] queen : queens) {
        	queenRemovedBoardState[queen[0]][queen[1]]=0;
            List<int[]> validMoves = getValidMoves(queenRemovedBoardState, queen[0], queen[1]);
            for (int[] newPos : validMoves) {
                List<int[]> arrowMoves = getValidMoves(queenRemovedBoardState, newPos[0], newPos[1]);
                for (int[] arrow : arrowMoves) {
                    Map<String, Object> move = new HashMap<>();
                    move.put("queen-position-current", new ArrayList<>(Arrays.asList(queen[0], queen[1])));
                    move.put("queen-position-next", new ArrayList<>(Arrays.asList(newPos[0], newPos[1])));
                    move.put("arrow-position", new ArrayList<>(Arrays.asList(arrow[0], arrow[1])));
                    moves.add(move);
                }
            }
            queenRemovedBoardState[queen[0]][queen[1]]=isBlack? 1:2;
        }
        return moves;
    }

    public static int[][] simulateMove(int[][] boardState, Map<String, Object> move) {
        int[][] newBoard = cloneBoard(boardState);
        ArrayList<Integer> qcurr = (ArrayList<Integer>) move.get("queen-position-current");
        ArrayList<Integer> qnew = (ArrayList<Integer>) move.get("queen-position-next");
        ArrayList<Integer> arrow = (ArrayList<Integer>) move.get("arrow-position");

        int queenType = newBoard[qcurr.get(0)][qcurr.get(1)];
        newBoard[qcurr.get(0)][qcurr.get(1)] = 0;
        newBoard[qnew.get(0)][qnew.get(1)] = queenType;
        newBoard[arrow.get(0)][arrow.get(1)] = 3; // Arrow mark

        return newBoard;
    }

    private static List<int[]> findQueens(int[][] boardState, boolean isBlack) {
        List<int[]> queens = new ArrayList<>();
        int queenValue = isBlack ? 1 : 2;
        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState.length; j++) {
                if (boardState[i][j] == queenValue) {
                    queens.add(new int[]{i, j});
                }
            }
        }
        return queens;
    }

    static List<int[]> getValidMoves(int[][] boardState, int row, int col) {
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] dir : directions) {
            int r = row + dir[0], c = col + dir[1];
            while (r >= 0 && r < boardState.length && c >= 0 && c < boardState[0].length && boardState[r][c] == 0) {
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
        int[][] copy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, board.length);
        }
        return copy;
    }
}
