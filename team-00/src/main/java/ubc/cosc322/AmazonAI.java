package ubc.cosc322;

import java.util.ArrayList;
import java.util.Map;

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
    private boolean isBlack;

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
                }
            }
        } else if (GameMessage.GAME_ACTION_START.equals(messageType)) {
            isBlack = msgDetails.get("player-black").equals(userName);
        } else if (GameMessage.GAME_ACTION_MOVE.equals(messageType)) {
            System.out.println("Server acknowledged the move: " + msgDetails);
            if (gameGUI != null && msgDetails.containsKey("move")) {
                gameGUI.updateGameState(msgDetails);
            }
            makeMove();
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
        Map<String, Object> move = MoveGenerator.generateMove(boardState, isBlack);
        if (move == null) {
            System.out.println("No valid move found, game over.");
            return;
        }

        gameClient.sendMoveMessage(
                (ArrayList<Integer>) move.get("queen-position-current"),
                (ArrayList<Integer>) move.get("queen-position-new"),
                (ArrayList<Integer>) move.get("arrow-position")
        );

        updateBoardState(move);
        printBoard();
    }

    private void updateBoardState(Map<String, Object> move) {
        ArrayList<Integer> qcurr = (ArrayList<Integer>) move.get("queen-position-current");
        ArrayList<Integer> qnew = (ArrayList<Integer>) move.get("queen-position-new");
        ArrayList<Integer> arrow = (ArrayList<Integer>) move.get("arrow-position");

        boardState[qcurr.get(0)][qcurr.get(1)] = 0;
        boardState[qnew.get(0)][qnew.get(1)] = isBlack ? 2 : 1;
        boardState[arrow.get(0)][arrow.get(1)] = 3;
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
