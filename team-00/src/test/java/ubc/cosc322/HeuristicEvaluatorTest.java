package ubc.cosc322;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class HeuristicEvaluatorTest {

    @Test
    void testEvaluateEmptyBoard() {
        int[][] emptyGrid = new int[10][10]; // Empty board
        Board board = new Board(emptyGrid);
        HeuristicEvaluator evaluator = new HeuristicEvaluator();

        int scorePlayer1 = evaluator.evaluate(board, 1);
        int scorePlayer2 = evaluator.evaluate(board, 2);

        assertEquals(0, scorePlayer1, "Player 1 score should be neutral on an empty board.");
        assertEquals(0, scorePlayer2, "Player 2 score should be neutral on an empty board.");
    }

    @Test
    void testEvaluateSingleAmazon() {
        int[][] grid = new int[10][10];
        grid[0][0] = 1; // Player 1 Amazon
        Board board = new Board(grid);
        HeuristicEvaluator evaluator = new HeuristicEvaluator();

        int score = evaluator.evaluate(board, 1);
        // With one Amazon and no opponent, mobility and territory should yield a positive overall score.
        assertTrue(score > 0, "Player 1 should have a positive score with one Amazon on board.");
    }

    @Test
    void testEvaluateTwoAmazonsWithObstacles() {
        // Define a 10x10 board
        int[][] grid = new int[10][10];

        // Place Amazons
        grid[0][0] = 1;  // Player 1's Amazon at (0,0)
        grid[9][9] = 2;  // Player 2's Amazon at (9,9)

        // Place Obstacles in non-symmetric positions to create differences in mobility/territory.
        grid[4][4] = -1;
        grid[5][6] = -1;
        grid[2][7] = -1;

        // Create Board & Evaluator
        Board board = new Board(grid);
        HeuristicEvaluator evaluator = new HeuristicEvaluator();

        // Verify Amazon Positions
        List<Amazon> p1Amazons = board.getAmazons(1);
        List<Amazon> p2Amazons = board.getAmazons(2);

        assertEquals(1, p1Amazons.size(), "Player 1 should have exactly 1 Amazon.");
        assertEquals(1, p2Amazons.size(), "Player 2 should have exactly 1 Amazon.");

        // Print possible moves for debugging
        System.out.println("Player 1 Possible Moves:");
        for (Amazon amazon : p1Amazons) {
            List<Cell> moves = board.getLegalMoves(amazon);
            System.out.println("  Amazon at " + amazon.getPosition() + " -> " + moves);
            assertFalse(moves.isEmpty(), "Amazon should have possible moves.");
        }

        System.out.println("Player 2 Possible Moves:");
        for (Amazon amazon : p2Amazons) {
            List<Cell> moves = board.getLegalMoves(amazon);
            System.out.println("  Amazon at " + amazon.getPosition() + " -> " + moves);
            assertFalse(moves.isEmpty(), "Amazon should have possible moves.");
        }

        // Evaluate Board for Both Players
        int scoreP1 = evaluator.evaluate(board, 1);
        int scoreP2 = evaluator.evaluate(board, 2);

        System.out.println("Score Player 1: " + scoreP1);
        System.out.println("Score Player 2: " + scoreP2);

        // Expect that due to differences in mobility, the evaluation for the two players should be different.
        assertTrue(scoreP1 < scoreP2, "Player 1 should have a lower score than Player 2 due to lower mobility.");
    }

    @Test
    void testConnectivityEvaluation() {
        // This test uses a 2x2 block of Amazons for player 1.
        int[][] grid = new int[10][10];
        grid[0][0] = 1;
        grid[0][1] = 1;
        grid[1][0] = 1;
        grid[1][1] = 1;

        Board board = new Board(grid);
        HeuristicEvaluator evaluator = new HeuristicEvaluator();

        int score = evaluator.evaluate(board, 1);
        System.out.println("Connectivity Evaluation (2x2 block) Score: " + score);
        // We expect a positive contribution from connectivity.
        assertTrue(score > 0, "Connectivity should contribute positively in a tight formation.");
    }

    @Test
    void testMobilityComparison() {
        // Test that an Amazon in the center has a higher evaluation than one in the corner, since a central position should allow more legal moves and a larger territory.
        int[][] gridCenter = new int[10][10];
        gridCenter[5][5] = 1; // Amazon in center
        Board boardCenter = new Board(gridCenter);
        HeuristicEvaluator evaluator = new HeuristicEvaluator();
        int scoreCenter = evaluator.evaluate(boardCenter, 1);

        int[][] gridCorner = new int[10][10];
        gridCorner[0][0] = 1; // Amazon in corner
        Board boardCorner = new Board(gridCorner);
        int scoreCorner = evaluator.evaluate(boardCorner, 1);

        System.out.println("Score Center: " + scoreCenter + ", Score Corner: " + scoreCorner);
        assertTrue(scoreCenter > scoreCorner, "An Amazon in the center should have a higher evaluation than one in the corner.");
    }

    @Test
    void testConnectivityComparison() {
        // Compare two board setups for player 1:
        // Board A: Two Amazons placed close together.
        // Board B: Two Amazons placed far apart.
        int[][] gridA = new int[10][10];
        gridA[4][4] = 1;
        gridA[4][5] = 1;
        Board boardA = new Board(gridA);
        HeuristicEvaluator evaluator = new HeuristicEvaluator();
        int scoreA = evaluator.evaluate(boardA, 1);

        // Board B: Two Amazons far apart.
        int[][] gridB = new int[10][10];
        gridB[0][0] = 1;
        gridB[9][9] = 1;
        Board boardB = new Board(gridB);
        int scoreB = evaluator.evaluate(boardB, 1);

        System.out.println("Score for close Amazons: " + scoreA + ", Score for far apart Amazons: " + scoreB);
        assertTrue(scoreA > scoreB, "Two closely placed Amazons should yield a higher evaluation than two far apart Amazons due to better connectivity and lower queen distance penalty.");
    }

    @Test
    void testMoveGeneration() {
        int[][] grid = new int[10][10];
        grid[3][3] = 1; // Player 1 Amazon

        Map<String, Object> move = MoveGenerator.generateMove(grid, false); // Player 1 move

        assertNotNull(move, "Move should not be null.");
        System.out.println("Generated Move: " + move);

        assertTrue(move.containsKey("queen-position-current"), "Move should include current queen position.");
        assertTrue(move.containsKey("queen-position-new"), "Move should include new queen position.");
        assertTrue(move.containsKey("arrow-position"), "Move should include arrow position.");
    }

    @Test
    void testMoveGenerationWithObstacles() {
        int[][] grid = new int[10][10];
        grid[3][3] = 1; // Player 1 Amazon
        grid[4][4] = -1; // Obstacle
        grid[5][5] = -1; // Obstacle

        Map<String, Object> move = MoveGenerator.generateMove(grid, false); // Player 1 move

        assertNotNull(move, "Move should not be null.");
        System.out.println("Generated Move with Obstacles: " + move);
    }

    @Test
    void testEdgeCaseNearlyFullBoard() {
        // Create a nearly full board where almost all cells are blocked.
        int[][] grid = new int[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = -1; // Fill with obstacles
            }
        }

        grid[5][5] = 1; // Player 1's Amazon at (5,5).
        grid[5][4] = 2; // Player 2's Amazon at (5,4)
    
        // Clear a few cells so each Amazon has minimal but equal mobility.
        grid[5][6] = 0;
        grid[4][5] = 0;
        grid[6][5] = 0;
        grid[4][4] = 0;
        grid[6][4] = 0;
    
        Board board = new Board(grid);
        HeuristicEvaluator evaluator = new HeuristicEvaluator();
    
        int score = evaluator.evaluate(board, 1);
        System.out.println("Score on nearly full board: " + score);
    
        // The score should be near zero.
        assertTrue(Math.abs(score) < 5, "Score on nearly full board should be near 0 if both players have similarly limited mobility.");
    }

    @Test
    void testDynamicScenario() {
        // Simulate a move that improves the board's evaluation.
        int[][] gridInitial = new int[10][10];
        gridInitial[0][0] = 1;

        // Add a couple of obstacles to further restrict movement.
        gridInitial[0][1] = -1;
        gridInitial[1][0] = -1;
        Board boardInitial = new Board(gridInitial);
        HeuristicEvaluator evaluator = new HeuristicEvaluator();
        int scoreInitial = evaluator.evaluate(boardInitial, 1);
        System.out.println("Initial score (corner): " + scoreInitial);

        // Simulate a move that relocates the Amazon to the center for improved mobility.
        int[][] gridAfterMove = new int[10][10];

        gridAfterMove[5][5] = 1; // Amazon moved to center
        //Preserve obstacles
        gridAfterMove[0][1] = -1;
        gridAfterMove[1][0] = -1;

        Board boardAfterMove = new Board(gridAfterMove);
        int scoreAfterMove = evaluator.evaluate(boardAfterMove, 1);
        System.out.println("Score after moving to center: " + scoreAfterMove);

        assertTrue(scoreAfterMove > scoreInitial, "Relocating the Amazon from the corner to the center should increase the evaluation score due to improved mobility.");
    }
}