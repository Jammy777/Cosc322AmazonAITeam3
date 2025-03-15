package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;

public class AmazonAI extends GamePlayer {

    private GameClient gameClient = null;
    private BaseGameGUI gameGUI = null;
    private String userName = null;
    private String passwd = null;
    private int[][] boardState;
    private final int BOARD_SIZE = 10;
    private boolean isBlack;

    public static void main(String[] args) {
        // Create two instances of the AI bot to play against each other
        /* Thread player1 = new Thread(() -> {
            AmazonAI player = new AmazonAI("AI_Player_1", "");
            if (player.getGameGUI() == null) {
                player.Go();
            } else {
                BaseGameGUI.sys_setup();
                java.awt.EventQueue.invokeLater(player::Go);
            }
            
        });
/// can make this one player by removing thread two - make it a comment
        
        Thread player2 = new Thread(() -> {
            AmazonAI player = new AmazonAI("AI_Player_2", "");
            if (player.getGameGUI() == null) {
                player.Go();
            } else {
                BaseGameGUI.sys_setup();
                java.awt.EventQueue.invokeLater(player::Go);
            }
       });
        

        player1.start();
        player2.start(); */
    }

    public AmazonAI(String userName, String passwd) {
        this.userName = userName;
        this.passwd = passwd;
        this.gameGUI = new BaseGameGUI(this);
        this.gameClient = new GameClient(userName, passwd, (GamePlayer) this);
    }

    @Override
    public void onLogin() {

        if (gameGUI != null) {
            gameGUI.setRoomInformation(gameClient.getRoomList());
        }

        gameClient.joinRoom("Oyama Lake");
        System.out.println(userName + " joined the room.");

    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        System.out.println("Game message received ; Type: " + messageType);
        System.out.println("Message Details: " + msgDetails);

        if (GameMessage.GAME_STATE_BOARD.equals(messageType)) {

            ArrayList<Integer> gameState = (ArrayList<Integer>) msgDetails.get("game-state");

            if (gameState != null) {
                boardState = new int[BOARD_SIZE][BOARD_SIZE];
                for (int i = 0; i < BOARD_SIZE; i++) {
                    for (int j = 0; j < BOARD_SIZE; j++) {
                        boardState[i][j] = gameState.get((10 - i) * 11 + j + 1); 
                    }
                }                
                printBoard();
                if (gameGUI != null) {
                    gameGUI.setGameState(gameState);
                }

            }

        } else if (GameMessage.GAME_ACTION_START.equals(messageType)) {
            isBlack = msgDetails.get("player-black").equals(userName);
            if (!isBlack) {

                makeMove();

            }
        } else if (GameMessage.GAME_ACTION_MOVE.equals(messageType)) {
            System.out.println("Server acknowledged the move: " + msgDetails);
            updateBoardState(msgDetails, true);
            gameGUI.updateGameState(msgDetails);

            System.out.println("Move from server: " + (ArrayList<Integer>) msgDetails.get("queen-position-current") + "\n"
                    + (ArrayList<Integer>) msgDetails.get("queen-position-next") + "\n"
                    + (ArrayList<Integer>) msgDetails.get("arrow-position"));

            Map<String, Object> move = makeMove();

            if (move != null) {
                gameGUI.updateGameState(shiftPosUpByOne(move));
            }

        }
        return true;
    }

    private Map<String, Object> makeMove() {
        System.out.println("From AmazonAI: AI is making a move...");
        System.out.println("From AmazonAI: Checking initial board state: ");

        Map<String, Object> move = MinMax.findBestMove(boardState, isBlack);
        System.out.println("From AmazonAI: Chosen Move: " + move);

        if (move == null) {
            System.out.println("From AmazonAI: No valid move found, game over.");
            return null;
        }

        // Convert move data from int[] to ArrayList<Integer>
        move = convertMove(move);

        if (gameClient != null) { // Null check for safety
            gameClient.sendMoveMessage(
                    (ArrayList<Integer>) move.get("queen-position-current"),
                    (ArrayList<Integer>) move.get("queen-position-next"),
                    (ArrayList<Integer>) move.get("arrow-position")
            );
        } else {
            System.out.println("From AmazonAI: ERROR - gameClient is null, cannot send move.");
        }

        updateBoardState(move, false);
        printBoard();
        return move;
    }

    /**
     * Converts int[] arrays in the move map to ArrayList<Integer> (for
     * SmartFoxServer compatibility).
     */
    private Map<String, Object> convertMove(Map<String, Object> move) {
        Map<String, Object> convertedMove = new HashMap<>();
    
        convertedMove.put("queen-position-current", convertToArrayList((int[]) move.get("queen-position-current")));
        convertedMove.put("queen-position-next", convertToArrayList((int[]) move.get("queen-position-next")));
        convertedMove.put("arrow-position", convertToArrayList((int[]) move.get("arrow-position")));
    
        return convertedMove;
    }
    
    private ArrayList<Integer> convertToArrayList(int[] arr) {
        if (arr[0] < 0 || arr[0] >= 10 || arr[1] < 0 || arr[1] >= 10) {
            System.err.println("ERROR: Invalid coordinate conversion! " + Arrays.toString(arr));
        }
        return new ArrayList<>(Arrays.asList(arr[0] + 1, arr[1] + 1));
    }    

    private void updateBoardState(Map<String, Object> move, boolean incomingMove) {
        ArrayList<Integer> qcurr = (ArrayList<Integer>) move.get("queen-position-current");
        ArrayList<Integer> qnew = (ArrayList<Integer>) move.get("queen-position-next");
        ArrayList<Integer> arrow = (ArrayList<Integer>) move.get("arrow-position");
    
        int qcurrX = qcurr.get(0) - 1;
        int qcurrY = qcurr.get(1) - 1;
        int qnewX = qnew.get(0) - 1;
        int qnewY = qnew.get(1) - 1;
        int arrowX = arrow.get(0) - 1;
        int arrowY = arrow.get(1) - 1;
    
        // Ensure all indices are within the valid 0-9 range
        if (qcurrX < 0 || qcurrX >= 10 || qcurrY < 0 || qcurrY >= 10 ||
            qnewX < 0 || qnewX >= 10 || qnewY < 0 || qnewY >= 10 ||
            arrowX < 0 || arrowX >= 10 || arrowY < 0 || arrowY >= 10) {
            System.err.println("ERROR: Move contains invalid indices! " + move);
            return; // Skip the update to avoid crashing
        }
    
        boardState[qcurrX][qcurrY] = 0;
        boardState[qnewX][qnewY] = isBlack ? 1 : 2;
        boardState[arrowX][arrowY] = 3;
    }

    private void printBoard() {
        System.out.println("Current Board State:");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(boardState[i][j] + " ");
            }
            System.out.println("\n");
            System.out.println("\n");
        }
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

    private Map<String, Object> shiftPosUpByOne(Map<String, Object> move) {
        ArrayList<Integer> adjustedQueenCurPos = new ArrayList<>(Arrays.asList(((ArrayList<Integer>) (move.get("queen-position-current"))).get(0) + 1, ((ArrayList<Integer>) (move.get("queen-position-current"))).get(1) + 1));
        ArrayList<Integer> adjustedQueenNewPos = new ArrayList<>(Arrays.asList(((ArrayList<Integer>) (move.get("queen-position-next"))).get(0) + 1, ((ArrayList<Integer>) (move.get("queen-position-next"))).get(1) + 1));
        ArrayList<Integer> adjustedArrowNewPos = new ArrayList<>(Arrays.asList(((ArrayList<Integer>) (move.get("arrow-position"))).get(0) + 1, ((ArrayList<Integer>) (move.get("arrow-position"))).get(1) + 1));
        Map<String, Object> adjustedMap = new HashMap<String, Object>();
        adjustedMap.put("queen-position-current", adjustedQueenCurPos);
        adjustedMap.put("queen-position-next", adjustedQueenNewPos);
        adjustedMap.put("arrow-position", adjustedArrowNewPos);
        return adjustedMap;
    }
}
