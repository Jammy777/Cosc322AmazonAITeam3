package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;

public class AmazonAI extends GamePlayer {
    private GameClient gameClient=null;
    private BaseGameGUI gameGUI=null;
    private String userName=null;
    private String passwd=null;
    private int[][] boardState;
    private final int BOARD_SIZE = 10;
    private boolean isBlack;


    public AmazonAI(String userName, String passwd) {
        this.userName = userName;
        this.passwd = passwd;
        this.gameGUI = new BaseGameGUI(this);
        this.gameClient = new GameClient(userName, passwd, (GamePlayer)this);
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
                        // Correct index mapping from 11x11 to 10x10
                        int index = (10 - i) * 11 + (j + 1); // Adjusted mapping
                        boardState[i][j] = gameState.get(index);
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

            gameGUI.updateGameState(msgDetails);
            //big problem for TA here!!!!! (output)
            System.out.println("Move from server: "+(ArrayList<Integer>) msgDetails.get("queen-position-current")+"\n"+
                    (ArrayList<Integer>) msgDetails.get("queen-position-new")+"\n"+
                    (ArrayList<Integer>) msgDetails.get("arrow-position"));



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
            System.out.println("\n");
        }
    }

    private void makeMove() {
        System.out.println("AI is making a move...");

        // Debugging: Print moves for all queens
        List<int[]> queens = MoveValidator.findQueens(boardState, isBlack, BOARD_SIZE);
        int totalMoves = 0;

        for (int[] queen : queens) {
            List<int[]> validMoves = MoveValidator.getValidMoves(boardState, queen[0], queen[1]);
            totalMoves += validMoves.size();
            System.out.println("Queen at (" + queen[0] + "," + queen[1] + ") has " + validMoves.size() + " moves.");
        }

        System.out.println("Total possible moves across all queens: " + totalMoves);

        // Continue with normal move generation
        Map<String, Object> move = MoveGenerator.generateMove(boardState, isBlack);
        if (move == null) {
            System.out.println("No valid move found, game over.");
            return;
        }

        ArrayList<Integer> adjustedQueenCurPos = new ArrayList<>(Arrays.asList(((ArrayList<Integer>) (move.get("queen-position-current"))).get(0) + 1, ((ArrayList<Integer>) (move.get("queen-position-current"))).get(1) + 1));
        ArrayList<Integer> adjustedQueenNewPos = new ArrayList<>(Arrays.asList(((ArrayList<Integer>) (move.get("queen-position-new"))).get(0) + 1, ((ArrayList<Integer>) (move.get("queen-position-new"))).get(1) + 1));
        ArrayList<Integer> adjustedArrowNewPos = new ArrayList<>(Arrays.asList(((ArrayList<Integer>) (move.get("arrow-position"))).get(0) + 1, ((ArrayList<Integer>) (move.get("arrow-position"))).get(1) + 1));

        gameClient.sendMoveMessage(
                adjustedQueenCurPos,
                adjustedQueenNewPos,
                adjustedArrowNewPos
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