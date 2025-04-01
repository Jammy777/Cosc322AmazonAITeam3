package ubc.cosc322;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
class updateQueenLocationTest {

    @Test
    void testUpdateQueenLocationStatic() {
        // Initialize queen locations
        List<int[]> queenLocations = new ArrayList<>();
        queenLocations.add(new int[]{2, 3});
        queenLocations.add(new int[]{5, 6});
        queenLocations.add(new int[]{8, 9});
        
        // Define the move
        Map<String, Object> move = new HashMap<>();
        move.put("queen-position-current", new ArrayList<>(Arrays.asList(5, 6)));
        move.put("queen-position-next", new ArrayList<>(Arrays.asList(6, 7)));

        // Call the method
        List<int[]> updatedQueens = MoveGenerator.updateQueenLocationStatic(move, queenLocations);

        // Check if the queen moved correctly
        assertArrayEquals(new int[]{2, 3}, updatedQueens.get(0)); // Unchanged
        assertArrayEquals(new int[]{7, 7}, updatedQueens.get(1)); // Updated
        assertArrayEquals(new int[]{8, 9}, updatedQueens.get(2)); // Unchanged
    }
}

