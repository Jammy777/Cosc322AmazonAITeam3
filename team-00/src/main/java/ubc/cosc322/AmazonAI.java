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
    List<int[]> queenLocations;
    

    public static void main(String[] args) {
        /* Thread player1 = new Thread(() -> {
            AmazonAI player = new AmazonAI("AI_Player_1", "");
            if (player.getGameGUI() == null) {
                player.Go();
            } else {
                BaseGameGUI.sys_setup();
                java.awt.EventQueue.invokeLater(player::Go);
            }
        });

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
        this.gameClient = new GameClient(userName, passwd, this);
        queenLocations=new ArrayList<int[]>();
        
    }

    @Override
    public void onLogin() {
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
            ArrayList<Integer> gameState = (ArrayList<Integer>) msgDetails.get("game-state");

            if (gameState != null) {
                boardState = new int[BOARD_SIZE][BOARD_SIZE];
                for (int i = 0; i < BOARD_SIZE; i++) {
                    for (int j = 0; j < BOARD_SIZE; j++) {
                        boardState[i][j] = gameState.get(11+i * 11 + j + 1);
                    }
                }
                printBoard();
                if (gameGUI != null) {
                    gameGUI.setGameState(gameState);
                }
                initializeQueenLocations();
            }
            

        } else if (GameMessage.GAME_ACTION_START.equals(messageType)) {
            isBlack = msgDetails.get("player-black").equals(userName);
            
            if (isBlack) {
                Map<String, Object> move = makeMove(new queenLocationBoardPair(boardState, queenLocations), null);

                if (move != null) {
                    gameGUI.updateGameState(shiftPosUpByOne(move));
                }
            }
    

        } else if (GameMessage.GAME_ACTION_MOVE.equals(messageType)) {
            System.out.println("Server acknowledged the move: " + msgDetails);
            updateBoardState(msgDetails, true);
            gameGUI.updateGameState(msgDetails);
            Map<String, Object> prevMove=shiftPosDownByOne(msgDetails);
            queenLocations=MoveGenerator.updateQueenLocationStatic(prevMove, queenLocations);

           

            Map<String, Object> move = makeMove(new queenLocationBoardPair(boardState, queenLocations), prevMove);

            if (move != null) {
                gameGUI.updateGameState(shiftPosUpByOne(move));
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
            System.out.println("\n");
        }
    }

    private Map<String, Object> makeMove(queenLocationBoardPair qlbp, Map<String, Object> incomingMove) {
        System.out.println("AI is making a move...");

        Map<String, Object> move = IterativeDeepening.iterativeDeepeningSearch(qlbp, isBlack, 28, incomingMove).getMove();
        if (move == null) {
            System.out.println("No valid move found, game over.");
            return null;
        }

        ArrayList<Integer> adjustedQueenCurPos = new ArrayList<>(Arrays.asList(
                ((ArrayList<Integer>) move.get("queen-position-current")).get(0) + 1,
                ((ArrayList<Integer>) move.get("queen-position-current")).get(1) + 1));

        ArrayList<Integer> adjustedQueenNewPos = new ArrayList<>(Arrays.asList(
                ((ArrayList<Integer>) move.get("queen-position-next")).get(0) + 1,
                ((ArrayList<Integer>) move.get("queen-position-next")).get(1) + 1));

        ArrayList<Integer> adjustedArrowNewPos = new ArrayList<>(Arrays.asList(
                ((ArrayList<Integer>) move.get("arrow-position")).get(0) + 1,
                ((ArrayList<Integer>) move.get("arrow-position")).get(1) + 1));

        gameClient.sendMoveMessage(adjustedQueenCurPos, adjustedQueenNewPos, adjustedArrowNewPos);

        updateBoardState(move, false);
        updateQueenLocation(move, queenLocations);
        
        printBoard();
        return move;
    }

    private void updateBoardState(Map<String, Object> move, boolean incomingMove) {
        ArrayList<Integer> qcurr = (ArrayList<Integer>) move.get("queen-position-current");
        ArrayList<Integer> qnew = (ArrayList<Integer>) move.get("queen-position-next");
        ArrayList<Integer> arrow = (ArrayList<Integer>) move.get("arrow-position");

        if (incomingMove) {
            boardState[qcurr.get(0) - 1][qcurr.get(1) - 1] = 0;
            boardState[qnew.get(0) - 1][qnew.get(1) - 1] = isBlack ? 2 : 1;
            boardState[arrow.get(0) - 1][arrow.get(1) - 1] = 3;
        } else {
            boardState[qcurr.get(0)][qcurr.get(1)] = 0;
            boardState[qnew.get(0)][qnew.get(1)] = isBlack ? 1 : 2;
            boardState[arrow.get(0)][arrow.get(1)] = 3;
        }
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
    private Map<String, Object> shiftPosDownByOne(Map<String, Object> move) {
        ArrayList<Integer> adjustedQueenCurPos = new ArrayList<>(Arrays.asList(((ArrayList<Integer>) (move.get("queen-position-current"))).get(0) - 1, ((ArrayList<Integer>) (move.get("queen-position-current"))).get(1) - 1));
        ArrayList<Integer> adjustedQueenNewPos = new ArrayList<>(Arrays.asList(((ArrayList<Integer>) (move.get("queen-position-next"))).get(0) - 1, ((ArrayList<Integer>) (move.get("queen-position-next"))).get(1) - 1));
        ArrayList<Integer> adjustedArrowNewPos = new ArrayList<>(Arrays.asList(((ArrayList<Integer>) (move.get("arrow-position"))).get(0) - 1, ((ArrayList<Integer>) (move.get("arrow-position"))).get(1) - 1));
        Map<String, Object> adjustedMap = new HashMap<String, Object>();
        adjustedMap.put("queen-position-current", adjustedQueenCurPos);
        adjustedMap.put("queen-position-next", adjustedQueenNewPos);
        adjustedMap.put("arrow-position", adjustedArrowNewPos);
        return adjustedMap;
    }
    public void initializeQueenLocations() {
    	queenLocations.addAll(MoveGenerator.findQueens(boardState, true));
    	queenLocations.addAll(MoveGenerator.findQueens(boardState, false));
    	
    }
    public void updateQueenLocation(Map<String, Object> move, List<int[]> queenLocations) {
    	ArrayList<Integer> qcurr = (ArrayList<Integer>) move.get("queen-position-current");
        ArrayList<Integer> qnew = (ArrayList<Integer>) move.get("queen-position-next");
        int i=0;
        List<int[]> newQueenLocations= new ArrayList<int[]>(queenLocations);
        for(int[] queen : queenLocations) {
        	if (qcurr.get(0)==queen[0]&&qcurr.get(1)==queen[1]) {
        		newQueenLocations.get(i)[0]=qnew.get(0);
        		newQueenLocations.get(i)[1]=qnew.get(1);
        		
        	}
        	i++;
        }
        this.queenLocations=newQueenLocations;
        
        
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
