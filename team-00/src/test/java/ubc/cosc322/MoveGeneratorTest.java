package ubc.cosc322;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MoveGeneratorTest {

    private static final int BOARD_SIZE = 10;
    private int[][] board;

    @BeforeEach
    public void setUp() {
        board = new int[BOARD_SIZE][BOARD_SIZE]; // Reset board before each test
    }

    @Test
    public void testValidMoves_EmptyBoard() {
        board[4][4] = 1; // Place queen in the center
        List<int[]> moves = MoveGenerator.getValidMoves(board, 4, 4);

        assertEquals(35, moves.size(), "Queen in center should have exactly 35 moves.");
    }

    @Test
    public void testValidMoves_BlockedByOtherQueens() {
        board[4][4] = 1; // Queen's position
        board[4][6] = 1; // Block in the right direction
        board[6][4] = 1; // Block downward

        List<int[]> moves = MoveGenerator.getValidMoves(board, 4, 4);

        assertFalse(containsMove(moves, 4, 7)); // Blocked beyond (4,6)
        assertFalse(containsMove(moves, 7, 4)); // Blocked beyond (6,4)
    }

    @Test
    public void testValidMoves_QueenAtCorner() {
        board[0][0] = 1; // Queen in the top-left corner
        List<int[]> moves = MoveGenerator.getValidMoves(board, 0, 0);

        assertEquals(27, moves.size(), "Queen at (0,0) should have exactly 27 moves.");
    }

    @Test
    public void testValidMoves_BlockedByArrows() {
        board[5][5] = 1; // Queen's position
        board[6][6] = 3; // Arrow blocks diagonal
        board[5][6] = 3; // Arrow blocks right move

        List<int[]> moves = MoveGenerator.getValidMoves(board, 5, 5);

        assertFalse(containsMove(moves, 6, 6)); // Blocked by arrow
        assertFalse(containsMove(moves, 5, 6)); // Blocked by arrow
    }

    @Test
    public void testGenerateAllMoves_WithQueens() {
        board[3][3] = 1; // Queen 1
        board[7][7] = 1; // Queen 2

        List<Map<String, Object>> moves = MoveGenerator.generateAllMoves(board, true);

        assertFalse(moves.isEmpty(), "Move generation should not be empty.");
    }

    @Test
    public void testEvaluateBoard_InitialState() {
        board[3][3] = 1; // White Queen
        board[6][6] = 2; // Black Queen

        int score = MoveGenerator.evaluateBoard(board, true);

        assertEquals(0, score, "Expected evaluation score to be 0 since both players have equal mobility.");
    }

    private boolean containsMove(List<int[]> moves, int row, int col) {
        for (int[] move : moves) {
            if (move[0] == row && move[1] == col) {
                return true;
            }
        }
        return false;
    }
}
