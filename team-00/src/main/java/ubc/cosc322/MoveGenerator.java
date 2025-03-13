package ubc.cosc322;

import java.util.*;

import java.awt.Point;
import java.util.*;

import static ubc.cosc322.MoveValidator.findQueens;
import static ubc.cosc322.MoveValidator.getValidMoves;

public class MoveGenerator {
    private static final int BOARD_SIZE = 10;

    public static Map<String, Object> generateMove(int[][] boardState, boolean isBlack) {
        Random rand = new Random();
        int queenValue = isBlack ? 2 : 1;

        // Find all queens
        List<int[]> queens = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (boardState[i][j] == queenValue) {
                    queens.add(new int[]{i, j});
                }
            }
        }

        // Filter queens that have valid moves
        List<int[]> movableQueens = new ArrayList<>();
        for (int[] q : queens) {
            if (!MoveValidator.getValidMoves(boardState, q[0], q[1]).isEmpty()) {
                movableQueens.add(q);
            }
        }

        if (movableQueens.isEmpty()) {
            System.out.println("Game Over: No more queens left to move.");
            return null;
        }

        // Pick a random queen
        int[] queen = movableQueens.get(rand.nextInt(movableQueens.size()));

        // Get its valid moves
        List<int[]> validMoves = MoveValidator.getValidMoves(boardState, queen[0], queen[1]);
        int[] move = validMoves.get(rand.nextInt(validMoves.size()));

        // Move queen
        boardState[queen[0]][queen[1]] = 0;
        boardState[move[0]][move[1]] = queenValue;

        // Get valid arrow moves
        List<int[]> arrowMoves = MoveValidator.getValidMoves(boardState, move[0], move[1]);
        int[] arrow = arrowMoves.get(rand.nextInt(arrowMoves.size()));

        // Shoot arrow
        boardState[arrow[0]][arrow[1]] = 3;

        ArrayList<Integer> qcurr = new ArrayList<>(Arrays.asList(queen[0], queen[1]));
        ArrayList<Integer> qnew = new ArrayList<>(Arrays.asList(move[0], move[1]));
        ArrayList<Integer> arrowPos = new ArrayList<>(Arrays.asList(arrow[0], arrow[1]));

        System.out.println("Final Move: Queen from " + qcurr + " → " + qnew + " | Arrow at " + arrowPos);

        Map<String, Object> moveMessage = new HashMap<>();
        moveMessage.put("queen-position-current", qcurr);
        moveMessage.put("queen-position-new", qnew);
        moveMessage.put("arrow-position", arrowPos);

        return moveMessage;
    }
}