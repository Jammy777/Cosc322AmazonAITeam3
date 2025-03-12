package ubc.cosc322;

import java.util.*;

public class MoveGenerator {
    private static final int BOARD_SIZE = 10;

    public static Map<String, Object> generateMove(int[][] boardState, boolean isBlack) {
        Random rand = new Random();

        // Find all available queens for the player
        List<int[]> queens = findQueens(boardState, isBlack);
        if (queens.isEmpty()) {
            System.out.println("Game Over: No more queens left to move.");
            return null;
        }

        // Remove any queen that has already moved (i.e., is no longer in its expected position)
        queens.removeIf(q -> boardState[q[0]][q[1]] == 0);

        // Check if any remaining queen has valid moves
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

        // Select a random queen that has valid moves
        int[] queen;
        List<int[]> validMoves;
        do {
            queen = queens.get(rand.nextInt(queens.size()));
            validMoves = getValidMoves(boardState, queen[0], queen[1]);
        } while (validMoves.isEmpty());

        // Select a random valid move
        int[] move = validMoves.get(rand.nextInt(validMoves.size()));

        // Get valid arrow placements from the new queen position
        List<int[]> arrowMoves = getValidMoves(boardState, move[0], move[1]);
        if (arrowMoves.isEmpty()) {
            return null;
        }

        // Select a random arrow position
        int[] arrow = arrowMoves.get(rand.nextInt(arrowMoves.size()));

        // Store move information
        ArrayList<Integer> qcurr = new ArrayList<>(Arrays.asList(queen[0], queen[1]));
        ArrayList<Integer> qnew = new ArrayList<>(Arrays.asList(move[0], move[1]));
        ArrayList<Integer> arrowPos = new ArrayList<>(Arrays.asList(arrow[0], arrow[1]));

        System.out.println("Sending move: Queen from " + qcurr + " to " + qnew + " with arrow at " + arrowPos);

        // Create move message
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
        int BOARD_SIZE = boardState.length;
        int[] directions = {-1, 0, 1};

        System.out.println("Generating valid moves for Queen at (" + row + ", " + col + ")");

        for (int dr : directions) {
            for (int dc : directions) {
                if (dr == 0 && dc == 0) continue; // Skip staying in place

                int r = row + dr;
                int c = col + dc;

                System.out.println("Checking direction: (" + dr + ", " + dc + ")");

                while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE) {
                    if (boardState[r][c] != 0) {
                        System.out.println("Blocked at (" + r + ", " + c + ") by " + boardState[r][c]);
                        break; // Stop if a queen or an obstacle is found
                    }

                    moves.add(new int[]{r, c});
                    System.out.println("Valid move added: (" + r + ", " + c + ")");

                    r += dr;
                    c += dc;
                }
            }
        }

        System.out.println("Total valid moves found: " + moves.size());
        return moves;
    }
}
