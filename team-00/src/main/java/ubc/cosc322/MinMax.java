package ubc.cosc322;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MinMax {
    private static final int MAX_DEPTH = 0;
    private static final int INF = Integer.MAX_VALUE;
    private static final HeuristicEvaluator evaluator = new HeuristicEvaluator(); 


    public static Map<String, Object> findBestMove(int[][] board, boolean isBlack) {
        System.out.println("From MinMax: Finding best move for " + (isBlack ? "Black" : "White"));

        List<Map<String, Object>> possibleMoves = MoveGenerator.generateAllMoves(board, isBlack);
        System.err.println("From MinMax: MoveGenerator exited, possible moves returned.");
        if (possibleMoves == null || possibleMoves.isEmpty()) {
            System.out.println("From MinMax: No valid moves available.");
            return null;
        }

        System.out.println("From MinMax: Possible Moves Count: " + possibleMoves.size());

        int bestScore = isBlack ? -INF : INF;
        Map<String, Object> bestMove = null;

        for (Map<String, Object> move : possibleMoves) {
            
            int[][] newBoard = MoveGenerator.simulateMove(board, move); // Updated to return a new board
            int score = minimax(newBoard, MAX_DEPTH, -INF, INF, !isBlack);

           // System.out.println("From MinMax: Move " + move + " has score " + score);

            if ((isBlack && score > bestScore) || (!isBlack && score < bestScore)) {
                bestScore = score;
                bestMove = move;
            }
        }

        if (bestMove == null) {
            System.out.println("From MinMax: WARNING! No best move found, selecting random move...");
            bestMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
        }

        System.out.println("From MinMax: Best move chosen: " + bestMove);
        return bestMove;
    }

    private static int minimax(int[][] board, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0 || MoveGenerator.isGameOver(board)) {
            return MoveGenerator.evaluateBoard(board, isMaximizing);
        }

        //System.err.println("From MinMax: Calling generateAllMoves from minmax function:");
        List<Map<String, Object>> moves = MoveGenerator.generateAllMoves(board, isMaximizing);

        if (isMaximizing) {
            int maxEval = -INF;
            for (Map<String, Object> move : moves) {
                int[][] newBoard = MoveGenerator.simulateMove(board, move); // Updated to return a new board
                int eval = minimax(newBoard, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Alpha-Beta Pruning
            }
            return maxEval;
        } else {
            int minEval = INF;
            for (Map<String, Object> move : moves) {
                int[][] newBoard = MoveGenerator.simulateMove(board, move); // Updated to return a new board
                int eval = minimax(newBoard, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Alpha-Beta Pruning
            }
            return minEval;
        }
    }
}
