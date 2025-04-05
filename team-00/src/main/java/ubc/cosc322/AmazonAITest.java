package ubc.cosc322;

import ygraph.ai.smartfox.games.BaseGameGUI;

public class AmazonAITest {
    public static void main(String[] args) {
        // Create two instances of the AI bot to play against each other
        Thread player1 = new Thread(() -> {
            AmazonAI player = new AmazonAI("Team_3", "", "mixed");
            if (player.getGameGUI() == null) {
                player.Go();
            } else {
                BaseGameGUI.sys_setup();
                java.awt.EventQueue.invokeLater(player::Go);
            }
        });
        
		
		
		 Thread player2 = new Thread(() -> { AmazonAI player = new
		 AmazonAI("AI_Player_2", "", "mobileAndKingTerritoryMixed"); if (player.getGameGUI() == null) {
		 player.Go(); } else { BaseGameGUI.sys_setup();
		 java.awt.EventQueue.invokeLater(player::Go); } });
		 
        player1.start();
		
		  
		  
		player2.start();
		 
    }
}
