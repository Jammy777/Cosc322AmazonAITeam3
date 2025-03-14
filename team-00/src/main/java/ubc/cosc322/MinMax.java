package ubc.cosc322;

import java.util.List;
import java.util.Map;

public class MinMax {

    private static final int MAX_DEPTH = 3;
    private static final int INF = Integer.MAX_VALUE;

    public static Map<String, Object> findBestMove(int[][] board, boolean isBlack) {
        int bestScore = isBlack ? -INF : INF;
        Map<String, Object> bestMove = null;

        System.out.println("From MinMax: Finding best move for " + (isBlack ? "Black" : "White"));

        List<Map<String, Object>> possibleMoves = MoveGenerator.generateAllMoves(board, isBlack);
        System.out.println("From MinMax: Possible Moves Count: " + possibleMoves.size());

        if (possibleMoves.isEmpty()) {
            System.out.println("From MinMax: No valid moves found.");
            return null;
        }

        for (Map<String, Object> move : possibleMoves) {
            System.out.println("Best move loop executed"); 
            int[][] newBoard = cloneBoard(board);
            MoveGenerator.simulateMove(newBoard, move);
            int score = minimax(newBoard, MAX_DEPTH, -INF, INF, !isBlack);

            if ((isBlack && score > bestScore) || (!isBlack && score < bestScore)) {
                System.out.println("Move assignment executed.");
                bestScore = score;
                bestMove = move;
            }
        }
        System.out.println("From MinMax: Best move chosen: " + bestMove + " with score " + bestScore);
        return bestMove;
    }

    private static int minimax(int[][] board, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0 || MoveGenerator.isGameOver(board)) {
            return MoveGenerator.evaluateBoard(board, isMaximizing);
        }

        List<Map<String, Object>> moves = MoveGenerator.generateAllMoves(board, isMaximizing);

        if (isMaximizing) {
            int maxEval = -INF;
            for (Map<String, Object> move : moves) {
                int[][] newBoard = cloneBoard(board);
                MoveGenerator.simulateMove(newBoard, move);
                int eval = minimax(newBoard, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Alpha-Beta Pruning

                            }}
            return maxEval;
        } else {
            int minEval = INF;
            for (Map<String, Object> move : moves) {
                int[][] newBoard = cloneBoard(board);
                MoveGenerator.simulateMove(newBoard, move);
                int eval = minimax(newBoard, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha-Beta Pruning

                            }}
            return minEval;
        }
    }

    private static int[][] cloneBoard(int[][] board) {
        int[][] copy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        }
        return copy;
    }
}
