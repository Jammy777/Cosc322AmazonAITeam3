package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
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

        int score = Heuristics.mobilityTesting(board, true);

        assertEquals(0, score, "Expected evaluation score to be 0 since both players have equal mobility.");
    }
    @Test
    public void testGenerateAllMoves() {
    	boolean isBlack=true;
    	int[][] testBoard= {{1,0},{0,3}};
    	List<Map<String, Object>> movesGenerated=MoveGenerator.generateAllMoves(testBoard, isBlack);
    	
    	Map<String, Object> move1 = new HashMap<>();
        move1.put("queen-position-current", new ArrayList<>(Arrays.asList(0,0)));
        move1.put("queen-position-next", new ArrayList<>(Arrays.asList(0,1)));
        move1.put("arrow-position", new ArrayList<>(Arrays.asList(1,0)));
        
        Map<String, Object> move2 = new HashMap<>();
        move2.put("queen-position-current", new ArrayList<>(Arrays.asList(0,0)));
        move2.put("queen-position-next", new ArrayList<>(Arrays.asList(1,0)));
        move2.put("arrow-position", new ArrayList<>(Arrays.asList(0,1)));
        
        Map<String, Object> move3 = new HashMap<>();
        move3.put("queen-position-current", new ArrayList<>(Arrays.asList(0,0)));
        move3.put("queen-position-next", new ArrayList<>(Arrays.asList(0,1)));
        move3.put("arrow-position", new ArrayList<>(Arrays.asList(0,0)));
        
        Map<String, Object> move4 = new HashMap<>();
        move4.put("queen-position-current", new ArrayList<>(Arrays.asList(0,0)));
        move4.put("queen-position-next", new ArrayList<>(Arrays.asList(1,0)));
        move4.put("arrow-position", new ArrayList<>(Arrays.asList(0,0)));
        
        assertTrue(movesGenerated.contains(move1)
        		&movesGenerated.contains(move2)
        		&movesGenerated.contains(move3)
        		&movesGenerated.contains(move4)
        		&movesGenerated.size()==4);
    }
   
    


    @Test
    void testUpdateQueenLocationStatic() {
        // Initialize queen locations
        List<int[]> queenLocations = new ArrayList<>();
        queenLocations.add(new int[]{2, 3});
        queenLocations.add(new int[]{5, 6});
        queenLocations.add(new int[]{8, 9});
        queenLocations.add(new int[]{9, 9});
        
        // Define the move
        Map<String, Object> move = new HashMap<>();
        move.put("queen-position-current", new ArrayList<>(Arrays.asList(5, 6)));
        move.put("queen-position-next", new ArrayList<>(Arrays.asList(6, 7)));

        // Call the method
        List<int[]> updatedQueens = MoveGenerator.updateQueenLocationStatic(move, queenLocations);

        // Check if the queen moved correctly
        assertArrayEquals(new int[]{2, 3}, updatedQueens.get(0)); // Unchanged
        assertArrayEquals(new int[]{6, 7}, updatedQueens.get(1)); // Updated
        assertArrayEquals(new int[]{8, 9}, updatedQueens.get(2)); // Unchanged
        assertArrayEquals(new int[]{9, 9}, updatedQueens.get(3));
    }
    @Test
    public void testSimulateMove() {
        // Initial board setup
        int[][] board = new int[10][10];
        for (int i = 0; i < 10; i++) {
            Arrays.fill(board[i], 0);
        }
        
        
        board[2][2] = 1;
        board[3][2] = 1;
        board[4][2] = 1;
        board[5][2] = 1;
        board[2][3] = 2;
        board[2][4] = 2;
        board[2][5] = 2;
        board[2][6] = 2;
        
        // Initial queen locations
        List<int[]> queenLocations = new ArrayList<>();
        queenLocations.add(new int[]{2, 2});
        queenLocations.add(new int[]{3, 2});
        queenLocations.add(new int[]{4, 2});
        queenLocations.add(new int[]{5, 2});
        queenLocations.add(new int[]{2, 3});
        queenLocations.add(new int[]{2, 4});
        queenLocations.add(new int[]{2, 5});
        queenLocations.add(new int[]{2, 6});
        
        queenLocationBoardPair qlbp = new queenLocationBoardPair(board, queenLocations);
        
        // Define a move: move the queen from (2,2) to (1,1) and shoot an arrow at (1,2)
        Map<String, Object> move = new HashMap<>();
        move.put("queen-position-current", new ArrayList<>(Arrays.asList(2, 2)));
        move.put("queen-position-next", new ArrayList<>(Arrays.asList(1, 1)));
        move.put("arrow-position", new ArrayList<>(Arrays.asList(1, 2)));
        
        // Simulate the move
        queenLocationBoardPair newQLBP = MoveGenerator.simulateMove(qlbp, move);
        int[][] newBoard = newQLBP.getBoard();
        
        // Assertions to check the correctness of the move
        assertEquals(0, newBoard[2][2], "The old queen position should be empty.");
        assertEquals(1, newBoard[1][1], "The new queen position should contain the queen.");
        assertEquals(3, newBoard[1][2], "The arrow position should be marked.");
        
        // Check updated queen locations
        List<int[]> newQueenLocations = newQLBP.getQueenLocations();
        assertEquals(8, newQueenLocations.size());
        assertArrayEquals(new int[]{1, 1}, newQueenLocations.get(0));
        assertArrayEquals(new int[]{3,2}, newQueenLocations.get(1));
        assertArrayEquals(new int[]{4,2}, newQueenLocations.get(2));
        assertArrayEquals(new int[]{5,2}, newQueenLocations.get(3));
        assertArrayEquals(new int[]{2,3}, newQueenLocations.get(4));
        assertArrayEquals(new int[]{2,4}, newQueenLocations.get(5));
        assertArrayEquals(new int[]{2,5}, newQueenLocations.get(6));
        assertArrayEquals(new int[]{2,6}, newQueenLocations.get(7));
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
