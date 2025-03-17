package ubc.cosc322;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class IsTerminalTest {

    /**
     * Test when black queen is completely blocked on black's turn.
     * The board is filled with arrows (3) except for the black queen.
     */
    @Test
    public void testBlackBlockedOnBlackTurn() {
        int size = 5;
        int[][] board = new int[size][size];
        
        // Fill entire board with arrows (3)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 3;
            }
        }
        // Place black queen at (2,2)
        board[2][2] = 1;
        // Place white queen somewhere (e.g., (0,0)); its status doesn't affect black's turn
        board[0][0] = 2;
        
        // Black queen has no free adjacent cells.
        assertTrue(IterativeDeepening.isTerminal(board, true), 
            "Expected terminal state on black's turn because the black queen is blocked.");
    }
    
    /**
     * Test when white queen is completely blocked on white's turn.
     * The board is filled with arrows (3) except for the white queen.
     */
    @Test
    public void testWhiteBlockedOnWhiteTurn() {
        int size = 5;
        int[][] board = new int[size][5];
        board = new int[size][size];
        
        // Fill entire board with arrows (3)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 3;
            }
        }
        // Place white queen at (2,2)
        board[2][2] = 2;
        // Place black queen somewhere (e.g., (4,4)); its status doesn't affect white's turn
        board[4][4] = 1;
        
        // White queen has no free adjacent cells.
        assertTrue(IterativeDeepening.isTerminal(board, false), 
            "Expected terminal state on white's turn because the white queen is blocked.");
    }
    
    /**
     * Test when black queen has a free move on black's turn.
     * One adjacent cell is empty so black is not terminal.
     */
    @Test
    public void testBlackNotBlockedOnBlackTurn() {
        int size = 5;
        int[][] board = new int[size][size];
        
        // Fill board with arrows (3)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 3;
            }
        }
        // Place black queen at (2,2)
        board[2][2] = 1;
        // Free up an adjacent cell for black (e.g., (2,3))
        board[2][3] = 0;
        // Place white queen (status irrelevant on black's turn)
        board[0][0] = 2;
        
        // Black queen has at least one move.
        assertFalse(IterativeDeepening.isTerminal(board, true), 
            "Expected non-terminal state on black's turn because the black queen has an available move.");
    }
    
    /**
     * Test when white queen has a free move on white's turn.
     * One adjacent cell is empty so white is not terminal.
     */
    @Test
    public void testWhiteNotBlockedOnWhiteTurn() {
        int size = 5;
        int[][] board = new int[size][size];
        
        // Fill board with arrows (3)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 3;
            }
        }
        // Place white queen at (2,2)
        board[2][2] = 2;
        // Free up an adjacent cell for white (e.g., (2,1))
        board[2][1] = 0;
        // Place black queen (status irrelevant on white's turn)
        board[4][4] = 1;
        
        // White queen has at least one move.
        assertFalse(IterativeDeepening.isTerminal(board, false), 
            "Expected non-terminal state on white's turn because the white queen has an available move.");
    }
}

