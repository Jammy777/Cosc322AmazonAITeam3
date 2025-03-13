package ubc.cosc322;

public class AmazonAITest {
    public static void main(String[] args) {
        // Create two instances of the AI bot to play against each other
        Thread player1 = new Thread(() -> {
            AmazonAI player = new AmazonAI("AI_Player_1", "");
            player.Go();
        });
        /// can make this one player by removing thread two - make it a comment
        Thread player2 = new Thread(() -> {
            AmazonAI player = new AmazonAI("AI_Player_2", "");
            player.Go();
        });

        player1.start();
        player2.start();
    }
}
