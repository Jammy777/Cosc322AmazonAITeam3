package ubc.cosc322;

import java.util.*;

public class MoveGenerator {
    private static final int BOARD_SIZE = 10;

    public static Map<String, Object> generateMove(int[][] boardState, boolean isBlack) {
        Random rand = new Random();

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

        int[] queen = queens.get(rand.nextInt(queens.size()));
        List<int[]> validMoves = getValidMoves(boardState, queen[0], queen[1]);
        if (validMoves.isEmpty()) {
            return null;
        }

        int[] move = validMoves.get(rand.nextInt(validMoves.size()));
        List<int[]> arrowMoves = getValidMoves(boardState, move[0], move[1]);
        if (arrowMoves.isEmpty()) {
            return null;
        }

        int[] arrow = arrowMoves.get(rand.nextInt(arrowMoves.size()));

        ArrayList<Integer> qcurr = new ArrayList<>(Arrays.asList(queen[0], queen[1]));
        ArrayList<Integer> qnew = new ArrayList<>(Arrays.asList(move[0], move[1]));
        ArrayList<Integer> arrowPos = new ArrayList<>(Arrays.asList(arrow[0], arrow[1]));

        System.out.println("Sending move: Queen from " + qcurr + " to " + qnew + " with arrow at " + arrowPos);

        Map<String, Object> moveMessage = new HashMap<>();
        moveMessage.put("queen-position-current", qcurr);
        moveMessage.put("queen-position-new", qnew);
        moveMessage.put("arrow-position", arrowPos);

        return moveMessage;
    }

    private static List<int[]> findQueens(int[][] boardState, boolean isBlack) {
        List<int[]> queens = new ArrayList<>();
        int queenValue = isBlack ? 2 : 1; // Assuming black = 2, white = 1
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
                int r = row + dr;
                int c = col + dc;
                while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && boardState[r][c] == 0) {
                    moves.add(new int[]{r, c});
                    r += dr;
                    c += dc;
                }
            }
        }
        return moves;
    }
}
