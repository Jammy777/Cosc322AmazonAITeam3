package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;

public class AmazonAI extends GamePlayer {
    private GameClient gameClient;
    private BaseGameGUI gameGUI;
    private String userName;
    private String passwd;
    private int[][] boardState;
    private final int BOARD_SIZE = 10;

    public AmazonAI(String userName, String passwd) {
        this.userName = userName;
        this.passwd = passwd;
        this.gameClient = new GameClient(userName, passwd, this);
        this.gameGUI = new BaseGameGUI(this);
    }

    public static void main(String[] args) {
        AmazonAI player = new AmazonAI("AI_Player", "");

        if (player.getGameGUI() == null) {
            player.Go();
        } else {
            BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(player::Go);
        }
    }

    @Override
    public void onLogin() {
        userName = gameClient.getUserName();
        if (gameGUI != null) {
            gameGUI.setRoomInformation(gameClient.getRoomList());
        }
        gameClient.joinRoom("Oyama Lake");
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        System.out.println("Game message received ; Type: " + messageType);
        System.out.println("Message Details: " + msgDetails);

        if (GameMessage.GAME_STATE_BOARD.equals(messageType)) {
            if (msgDetails.containsKey("game-state")) {
                ArrayList<Integer> gameState = (ArrayList<Integer>) msgDetails.get("game-state");

                if (gameState != null) {
                    boardState = new int[BOARD_SIZE][BOARD_SIZE];
                    for (int i = 0; i < BOARD_SIZE; i++) {
                        for (int j = 0; j < BOARD_SIZE; j++) {
                            boardState[i][j] = gameState.get(i * BOARD_SIZE + j);
                        }
                    }
                    printBoard();
                    if (gameGUI != null) {
                        gameGUI.setGameState(gameState);
                    }
                    makeMove();
                } else {
                    System.err.println("Error: Missing or invalid game state.");
                }
            }
        } else if (GameMessage.GAME_ACTION_MOVE.equals(messageType)) {
            System.out.println("Server acknowledged the move: " + msgDetails);
            if (gameGUI != null && msgDetails.containsKey("move")) {
                gameGUI.updateGameState(msgDetails);
            } else {
                System.err.println("Error: Missing or invalid move data in msg.");
            }
        }
        return true;
    }

    private void printBoard() {
        System.out.println("Current Board State:");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(boardState[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void makeMove() {
        System.out.println("AI is making a move...");
        Random rand = new Random();

        List<int[]> queens = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (boardState[i][j] == 1) {
                    queens.add(new int[]{i, j});
                }
            }
        }

        if (queens.isEmpty()) {
            System.out.println("No queens found for AI to move.");
            return;
        }

        int[] queen = queens.get(rand.nextInt(queens.size()));
        List<int[]> validMoves = getValidMoves(queen[0], queen[1]);
        if (validMoves.isEmpty()) {
            System.out.println("No valid moves available for the selected queen.");
            return;
        }

        int[] move = validMoves.get(rand.nextInt(validMoves.size()));
        List<int[]> arrowMoves = getValidMoves(move[0], move[1]);
        if (arrowMoves.isEmpty()) {
            System.out.println("No valid arrow shots available.");
            return;
        }

        int[] arrow = arrowMoves.get(rand.nextInt(arrowMoves.size()));

        ArrayList<Integer> qcurr = new ArrayList<>(Arrays.asList(queen[0], queen[1]));
        ArrayList<Integer> qnew = new ArrayList<>(Arrays.asList(move[0], move[1]));
        ArrayList<Integer> arrowPos = new ArrayList<>(Arrays.asList(arrow[0], arrow[1]));

        System.out.println("Sending move: Queen from " + qcurr + " to " + qnew + " with arrow at " + arrowPos);

        if (qnew == null || qnew.isEmpty()) {
            System.err.println("Error: qnew (queen's new position) is NULL or empty!");
            return;
        }

        gameClient.sendMoveMessage(qcurr, qnew, arrowPos);

        boardState[queen[0]][queen[1]] = 0;
        boardState[move[0]][move[1]] = 1;
        boardState[arrow[0]][arrow[1]] = 3;

        System.out.println("Updated Board After Move:");
        printBoard();
    }

    private List<int[]> getValidMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();
        int[] directions = {-1, 0, 1};
        for (int dr : directions) {
            for (int dc : directions) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr;
                int c = col + dc;
                while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && boardState[r][c] == 0) {
                    moves.add(new int[]{r, c});
                    r += dr;
                    c += dc;
                }
            }
        }
        return moves;
    }

    @Override
    public String userName() {
        return this.userName;
    }

    @Override
    public GameClient getGameClient() {
        return this.gameClient;
    }

    @Override
    public BaseGameGUI getGameGUI() {
        return this.gameGUI;
    }

    @Override
    public void connect() {
        gameClient = new GameClient(userName, passwd, this);
    }
}