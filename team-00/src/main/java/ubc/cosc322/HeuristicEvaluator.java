package ubc.cosc322;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

// Represents a board cell with row and column coordinates.
class Cell {
    int row, col;
    
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell other = (Cell) o;
        return row == other.row && col == other.col;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
    
    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}

// Represents a game piece belonging to a player.
class Amazon {
    private Cell position;
    private int player;
    
    public Amazon(Cell position, int player) {
        this.position = position;
        this.player = player;
    }
    
    public Cell getPosition() {
        return position;
    }
    
    public int getPlayer() {
        return player;
    }
}

// The board is a 2D grid where: 
// 0 = empty, 1 = player 1's amazon, 2 = player 2's amazon, -1 = blocked.
class Board {
    int rows, cols;
    int[][] grid;
    
    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            Arrays.fill(grid[i], 0);
        }
    }
    
    // New constructor to build a board from an existing grid.
    public Board(int[][] grid) {
        this.rows = grid.length;
        this.cols = grid[0].length;
        this.grid = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(grid[i], 0, this.grid[i], 0, cols);
        }
    }
    
    public void setCell(int row, int col, int value) {
        grid[row][col] = value;
    }
    
    public int getCell(int row, int col) {
        return grid[row][col];
    }
    
    public List<Amazon> getAmazons(int player) {
        List<Amazon> amazons = new ArrayList<>();
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                if (grid[i][j] == player) {
                    amazons.add(new Amazon(new Cell(i, j), player));
                }
            }
        }
        return amazons;
    }
    
    private static final int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
    
    public List<Cell> getLegalMoves(Amazon amazon) {
        List<Cell> moves = new ArrayList<>();
        Cell pos = amazon.getPosition();
        int x = pos.row, y = pos.col;
        System.out.println("Checking legal moves for Amazon at: " + pos);
    
        for (int d = 0; d < 8; d++) {
            int nx = x + dx[d], ny = y + dy[d];
            while (isWithinBounds(nx, ny) && grid[nx][ny] == 0) {
                moves.add(new Cell(nx, ny));
                nx += dx[d];
                ny += dy[d];
            }
        }
    
        System.out.println("Legal moves found: " + moves);
        return moves;
    }
    
    public Set<Cell> floodFill(Cell start, int player) {
        Set<Cell> visited = new HashSet<>();
        Queue<Cell> queue = new LinkedList<>();
    
        if (!isWithinBounds(start.row, start.col)) {
            System.out.println("Flood-fill start position (" + start + ") is out of bounds.");
            return visited;
        }
    
        // Start position must be empty or occupied by the player
        if (grid[start.row][start.col] != 0 && grid[start.row][start.col] != player) {
            System.out.println("Flood-fill start position (" + start + ") is blocked or belongs to the opponent.");
            return visited;
        }
    
        queue.add(start);
        visited.add(start);
    
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            
            // Explore in all 8 directions
            for (int d = 0; d < 8; d++) {
                int nx = current.row, ny = current.col;
                
                // Move in a straight line until hitting an obstacle
                while (true) {
                    nx += dx[d];
                    ny += dy[d];
                    
                    Cell neighbor = new Cell(nx, ny);
                    
                    if (!isWithinBounds(nx, ny) || grid[nx][ny] != 0) break; // Stop at obstacles or other players
                    
                    if (visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
    
        System.out.println("Flood-fill completed for Player " + player + ". Total reachable cells: " + visited.size());
        return visited;
    }
    
    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    
    public boolean isFree(int row, int col) {
        return isWithinBounds(row, col) && grid[row][col] == 0;
    }
    
    public void printBoard() {
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                System.out.print(grid[i][j] + "\t");
            }
            System.out.println();
        }
    }
}

// Improved heuristic evaluator that considers mobility, territory, connectivity, and queen distances.
class HeuristicEvaluator {
    
    public int evaluate(Board board, int player) {
        int opponent = (player == 1) ? 2 : 1;
    
        int mobilityScore = countLegalMoves(board, player) - countLegalMoves(board, opponent);
        int territoryScore = evaluateTerritory(board, player) - evaluateTerritory(board, opponent);
        int connectivityScore = evaluateConnectivity(board, player) - evaluateConnectivity(board, opponent);
        int queenDistanceScore = evaluateQueenDistances(board, opponent) - evaluateQueenDistances(board, player);
    
        int weightMobility = 3;
        int weightTerritory = 2;
        int weightConnectivity = 1;
        int weightDistance = 1;
    
        int finalScore = (weightMobility * mobilityScore) +
                         (weightTerritory * territoryScore) +
                         (weightConnectivity * connectivityScore) +
                         (weightDistance * queenDistanceScore);
    
        // Debugging Output
        System.out.println("Player " + player + " Scores:");
        System.out.println("  Mobility Score: " + mobilityScore);
        System.out.println("  Territory Score: " + territoryScore);
        System.out.println("  Connectivity Score: " + connectivityScore);
        System.out.println("  Queen Distance Score: " + queenDistanceScore);
        System.out.println("  Final Heuristic Score: " + finalScore);
        
        return finalScore;
    }
    
    private int countLegalMoves(Board board, int player) {
        int total = 0;
        for (Amazon amazon : board.getAmazons(player)) {
            List<Cell> moves = board.getLegalMoves(amazon);
            System.out.println("Player " + player + " Amazon at " + amazon.getPosition() + " has " + moves.size() + " moves.");
            total += moves.size();
        }
        return total;
    }
    
    private int evaluateTerritory(Board board, int player) {
        Set<Cell> reachable = new HashSet<>();
    
        for (Amazon amazon : board.getAmazons(player)) {
            Set<Cell> filledCells = board.floodFill(amazon.getPosition(), player);
            System.out.println("Player " + player + " flood-fill from " + amazon.getPosition() + " covers: " + filledCells.size());
            reachable.addAll(filledCells);
        }
    
        System.out.println("Total territory for Player " + player + ": " + reachable.size());
        return reachable.size();
    }
    
    // Counts pairs of queens that are within a threshold (here, Manhattan distance <= 3)
    private int evaluateConnectivity(Board board, int player) {
        List<Amazon> queens = board.getAmazons(player);
        int connectivity = 0;
        for (int i = 0; i < queens.size(); i++) {
            for (int j = i + 1; j < queens.size(); j++) {
                int dist = Math.abs(queens.get(i).getPosition().row - queens.get(j).getPosition().row)
                         + Math.abs(queens.get(i).getPosition().col - queens.get(j).getPosition().col);
                if (dist <= 3) connectivity++;
            }
        }
        return connectivity;
    }
    
    // Calculates the average Manhattan distance between queens.
    private int evaluateQueenDistances(Board board, int player) {
        List<Amazon> queens = board.getAmazons(player);
        if (queens.size() < 2) return 0;
        int totalDistance = 0;
        int count = 0;
        for (int i = 0; i < queens.size(); i++) {
            for (int j = i + 1; j < queens.size(); j++) {
                int dist = Math.abs(queens.get(i).getPosition().row - queens.get(j).getPosition().row)
                         + Math.abs(queens.get(i).getPosition().col - queens.get(j).getPosition().col);
                totalDistance += dist;
                count++;
            }
        }
        return totalDistance / count;
    }
}