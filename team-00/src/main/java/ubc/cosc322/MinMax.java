package ubc.cosc322;

import java.util.*;

public class MinMax {
    private static final int MAX_DEPTH = 3; // Change for deeper searches
    private static final int INF = Integer.MAX_VALUE;

    public static Map<String, Object> findBestMove(int[][] board, boolean isBlack) {
        int bestScore = isBlack ? -INF : INF; // Minimax perspective
        Map<String, Object> bestMove = null;

        List<Map<String, Object>> possibleMoves = generateAllMoves(board, isBlack);

        for (Map<String, Object> move : possibleMoves) {
            int[][] newBoard = simulateMove(board, move);
            int score = minimax(newBoard, MAX_DEPTH, -INF, INF, !isBlack);

            if ((isBlack && score > bestScore) || (!isBlack && score < bestScore)) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private static int minimax(int[][] board, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0 || isGameOver(board)) {
            return evaluateBoard(board, isMaximizing);
        }

        List<Map<String, Object>> moves = generateAllMoves(board, isMaximizing);

        if (isMaximizing) {
            int maxEval = -INF;
            for (Map<String, Object> move : moves) {
                int[][] newBoard = simulateMove(board, move);
                int eval = minimax(newBoard, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // Alpha-Beta Pruning
            }
            return maxEval;
        } else {
            int minEval = INF;
            for (Map<String, Object> move : moves) {
                int[][] newBoard = simulateMove(board, move);
                int eval = minimax(newBoard, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // Alpha-Beta Pruning
            }
            return minEval;
        }
    }

    private static int evaluateBoard(int[][] board, boolean isMaximizing) {
        int blackMobility = 0, whiteMobility = 0;
        for (int[] queen : MoveValidator.findQueens(board, true, 10)) {
            blackMobility += MoveValidator.getValidMoves(board, queen[0], queen[1]).size();
        }
        for (int[] queen : MoveValidator.findQueens(board, false, 10)) {
            whiteMobility += MoveValidator.getValidMoves(board, queen[0], queen[1]).size();
        }
        return isMaximizing ? (blackMobility - whiteMobility) : (whiteMobility - blackMobility);
    }

    private static List<Map<String, Object>> generateAllMoves(int[][] board, boolean isBlack) {
        List<Map<String, Object>> moves = new ArrayList<>();
        for (int[] queen : MoveValidator.findQueens(board, isBlack, 10)) {
            for (int[] newPos : MoveValidator.getValidMoves(board, queen[0], queen[1])) {
                for (int[] arrow : MoveValidator.getValidMoves(board, newPos[0], newPos[1])) {
                    Map<String, Object> move = new HashMap<>();
                    move.put("queen-position-current", Arrays.asList(queen[0], queen[1]));
                    move.put("queen-position-new", Arrays.asList(newPos[0], newPos[1]));
                    move.put("arrow-position", Arrays.asList(arrow[0], arrow[1]));
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    private static int[][] simulateMove(int[][] board, Map<String, Object> move) {
        int[][] newBoard = cloneBoard(board);
        List<Integer> qcurr = (List<Integer>) move.get("queen-position-current");
        List<Integer> qnew = (List<Integer>) move.get("queen-position-new");
        List<Integer> arrow = (List<Integer>) move.get("arrow-position");

        int queenType = newBoard[qcurr.get(0)][qcurr.get(1)];

        newBoard[qcurr.get(0)][qcurr.get(1)] = 0;
        newBoard[qnew.get(0)][qnew.get(1)] = queenType;
        newBoard[arrow.get(0)][arrow.get(1)] = 3;

        return newBoard;
    }

    private static boolean isGameOver(int[][] board) {
        return generateAllMoves(board, true).isEmpty() || generateAllMoves(board, false).isEmpty();
    }

    private static int[][] cloneBoard(int[][] board) {
        int[][] copy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        }
        return copy;
    }
}
