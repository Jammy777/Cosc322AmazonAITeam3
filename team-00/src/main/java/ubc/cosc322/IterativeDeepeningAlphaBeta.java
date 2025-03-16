package ubc.cosc322;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Date;
import java.util.Map;

public class IterativeDeepeningAlphaBeta {
	public static Map<String, Object> iterativeDeepeningSearch(int [][] boardState, boolean isBlack, int timeLimitSec){
		 
		AtomicBoolean timeUp = new AtomicBoolean(false); // Thread-safe flag
	    Map<String, Object> bestResult = null; // Store best result found

        // Scheduled executor to stop search after time limit
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
        	timeUp.set(true);
            System.out.println("Time's up! Returning best result found...");
        }, timeLimitSec, TimeUnit.SECONDS);

        // Start the iterative deepening search
        int depth = 1; // Start with depth 1
        while (!timeUp.get()) { // Check flag atomically
            Map<String, Object> result = iterativeDeepeningSearch(depth);
            bestResult = result; // Update best result
            System.out.println("Depth: " + depth + ", Found: " + result);
            depth++; // Increase depth
        }

        // Shutdown scheduler
        scheduler.shutdown();

        // Print final best result
        System.out.println("Best result found from Iterative Deepening: " + bestResult);
		    
		    
		
		
	}
}
