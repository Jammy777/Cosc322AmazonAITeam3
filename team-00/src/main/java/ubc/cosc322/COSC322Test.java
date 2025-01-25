
package ubc.cosc322;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class COSC322Test extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
	
    private String userName = null;
    private String passwd = null;
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {				 
    	COSC322Test player = new COSC322Test("args[0]", "args[1]");
    	
    	if(player.getGameGUI() == null) {
    		player.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	player.Go();
                }
            });
    	}

		
    }
	
    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public COSC322Test(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gamegui = new BaseGameGUI(this);
    }
 


    @Override
    public void onLogin() {
		userName=gameClient.getUserName();
		if (gamegui!=null){
			gamegui.setRoomInformation(gameClient.getRoomList());
		}

    	//System.out.println("Congratualations!!! "
    	//		+ "I am called because the server indicated that the login is successfully");
    	//System.out.println("The next step is to find a room and join it: "
    	//		+ "the gameClient instance created in my constructor knows how!"); 
		//System.out.println(gameClient.getRoomList());
		gameClient.joinRoom("Oyama Lake");
		
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	  //This method will be called by the GameClient when it receives a game-related message
        //from the server.
        System.out.println("Game message recieved ; Type: " + messageType);
        //For a detailed description of the message types and format, 
        //see the method GamePlayer.handleGameMessage() in the game-client-api document. 
        if (GameMessage.GAME_STATE_BOARD.equals(messageType)) {
            if (gamegui != null&&msgDetails.containsKey("game-state")) {
                ArrayList<Integer> gameState=(ArrayList<Integer>) msgDetails.get("game-state");
				if (gameState!=null){
					gamegui.setGameState(gameState);
				}else{
					System.err.println("Missing or invalid game state file");
				}
            }
        }     else if (GameMessage.GAME_ACTION_MOVE.equals(messageType)) {
			if (gamegui != null&&msgDetails.containsKey("move")&&msgDetails != null) {
				gamegui.updateGameState(msgDetails);
            
        } else{
			System.err.println("Error: Missing or invalid move data in msg");
		}
	}  return true;	
    }
    
    
    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		// TODO Auto-generated method stub
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		// TODO Auto-generated method stub
		return  this.gamegui;
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
    	gameClient = new GameClient(userName, passwd, this);			
	}

 
}//end of class
