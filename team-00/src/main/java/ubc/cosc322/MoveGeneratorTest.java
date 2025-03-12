package ubc.cosc322;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
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
        int row = 4, col = 4;
        List<int[]> moves = MoveGenerator.getValidMoves(board, row, col);

        // Should have 27 moves in all 8 directions until the edge of the board
        assertEquals(35, moves.size());
    }

    @Test
    public void testValidMoves_BlockedByOtherQueens() {
        board[4][4] = 1; // Queen's position
        board[4][6] = 1; // Block in the right direction
        board[6][4] = 1; // Block downward
        
        List<int[]> moves = MoveGenerator.getValidMoves(board, 4, 4);

        // Should exclude positions beyond (4,6) and (6,4)
        assertFalse(containsMove(moves, 4, 7));
        assertFalse(containsMove(moves, 7, 4));
    }

    @Test
    public void testValidMoves_QueenAtCorner() {
        board[0][0] = 1; // Queen at top-left corner
        List<int[]> moves = MoveGenerator.getValidMoves(board, 0, 0);

        // Queen should have only 3 possible move directions
        assertEquals(27, moves.size());
    }

    @Test
    public void testValidMoves_BlockedByArrows() {
        board[5][5] = 1; // Queen's position
        board[6][6] = -1; // Arrow blocks diagonal
        board[5][6] = -1; // Arrow blocks right move
        
        List<int[]> moves = MoveGenerator.getValidMoves(board, 5, 5);

        assertFalse(containsMove(moves, 6, 6));
        assertFalse(containsMove(moves, 5, 6));
    }

    // Helper method to check if a move exists in the list
    private boolean containsMove(List<int[]> moves, int row, int col) {
        for (int[] move : moves) {
            if (move[0] == row && move[1] == col) {
                return true;
            }
        }
        return false;
    }
}
