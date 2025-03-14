package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MoveGenerator {
    private static final int BOARD_SIZE = 10;

    public static Map<String, Object> generateMove(int[][] boardState, boolean isBlack) {
        Random rand = new Random();

        List<int[]> queens = findQueens(boardState, isBlack);
        List<int[]> movableQueens = queens.stream()
                .filter(q -> !getValidMoves(boardState, q[0], q[1]).isEmpty())
                .toList();

        if (movableQueens.isEmpty()) {
            System.out.println("Game Over: No more queens left to move.");
            return null;
        }

        int[] queen = movableQueens.get(rand.nextInt(movableQueens.size()));
        List<int[]> validMoves = getValidMoves(boardState, queen[0], queen[1]);
        int[] move = validMoves.get(rand.nextInt(validMoves.size()));
        
        boardState[queen[0]][queen[1]] = 0;
        boardState[move[0]][move[1]] = isBlack ? 1 : 2;

        List<int[]> arrowMoves = getValidMoves(boardState, move[0], move[1]);
        if (arrowMoves.isEmpty()) {
        	boardState[queen[0]][queen[1]] = isBlack ? 1 : 2;
            boardState[move[0]][move[1]] = 0;
            return null;
        }
        int[] arrow = arrowMoves.get(rand.nextInt(arrowMoves.size()));

        
        boardState[arrow[0]][arrow[1]] = 3; // Arrow mark

        ArrayList<Integer> qcurr = new ArrayList<>(Arrays.asList(queen[0], queen[1]));
        ArrayList<Integer> qnew = new ArrayList<>(Arrays.asList(move[0], move[1]));
        ArrayList<Integer> arrowPos = new ArrayList<>(Arrays.asList(arrow[0], arrow[1]));

        System.out.println("Sending move: Queen from " + qcurr + " to " + qnew + " with arrow at " + arrowPos);

        Map<String, Object> moveMessage = new HashMap<>();
        moveMessage.put("queen-position-current", qcurr);
        moveMessage.put("queen-position-next", qnew);
        moveMessage.put("arrow-position", arrowPos);
       
        return moveMessage;
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

    static List<int[]> getValidMoves(int[][] boardState, int row, int col) {
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir : directions) {
            int r = row + dir[0], c = col + dir[1];
            while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && boardState[r][c] == 0) {
                moves.add(new int[]{r, c});
                r += dir[0];
                c += dir[1];
            }
        }
        return moves;
    }
}

