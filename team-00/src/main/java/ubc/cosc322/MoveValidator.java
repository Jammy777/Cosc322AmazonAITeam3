package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;

public class MoveValidator {

    private static final int BOARD_SIZE = 10;
    private static final int EMPTY = 0;
    private static final int ARROW = 3;

    /**
     * Finds all queens of the specified color on the board.
     *
     * @param boardState The current board state.
     * @param isBlack    Whether the player is black.
     * @param BOARD_SIZE The size of the board (set server-side).
     * @return A list of queen positions as {row, col} pairs.
     */
    static List<int[]> findQueens(int[][] boardState, boolean isBlack, int BOARD_SIZE) {
        List<int[]> queens = new ArrayList<>();
        int queenValue = isBlack ? 2 : 1; // 2 for black, 1 for white

        System.out.println("\n=== Searching for " + (isBlack ? "Black" : "White") + " Queens ===");

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (boardState[i][j] == queenValue) {
                    queens.add(new int[]{i, j});
                    System.out.println("  Found queen at: (" + i + ", " + j + ")");
                }
            }
        }

        System.out.println("Total Queens Found: " + queens.size());
        return queens;
    }

    /**
     * Generates all valid moves for a given queen position.
     *
     * @param boardState The current board state.
     * @param row        The row index of the queen.
     * @param col        The column index of the queen.
     * @param BOARD_SIZE The size of the board (set server-side).
     * @return A list of valid moves as {row, col} pairs.
     */

    /**
     * Checks if a given arrow shot is valid.
     * @param board The current board state.
     * @param queenX The X coordinate of the queen after moving.
     * @param queenY The Y coordinate of the queen after moving.
     * @param arrowX The X coordinate where the arrow is shot.
     * @param arrowY The Y coordinate where the arrow is shot.
     * @return True if the arrow shot is valid, False otherwise.
     */
    public static boolean isValidArrow(int[][] board, int queenX, int queenY, int arrowX, int arrowY) {
        if (!inBounds(arrowX, arrowY) || board[arrowX][arrowY] != EMPTY) return false;

        int dx = Integer.compare(arrowX, queenX);
        int dy = Integer.compare(arrowY, queenY);

        int x = queenX + dx;
        int y = queenY + dy;

        while (x != arrowX || y != arrowY) {
            if (board[x][y] != EMPTY) return false;
            x += dx;
            y += dy;
        }
        return true;
    }

    /**
     * Finds all legal moves for a queen at a given position.
     * @param board The current board state.
     * @param x The X coordinate of the queen.
     * @param y The Y coordinate of the queen.
     * @return A list of valid moves [x, y].
     */
    public static List<int[]> getValidMoves(int[][] board, int x, int y) {
        List<int[]> validMoves = new ArrayList<>();

        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // Vertical and Horizontal
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonal
        };

        for (int[] dir : directions) {
            int dx = dir[0], dy = dir[1];
            int nx = x + dx, ny = y + dy;

            while (inBounds(nx, ny) && board[nx][ny] == EMPTY) {
                validMoves.add(new int[]{nx, ny});
                nx += dx;
                ny += dy;
            }
        }
        return validMoves;
    }

    /**
     * Checks if a coordinate is within bounds.
     */
    private static boolean inBounds(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }


}
