package ubc.cosc322;

import java.util.List;
import java.util.Map;

public class queenLocationBoardPair {
	private int[][] board;
	private List<int[]> queenLocations;
	public queenLocationBoardPair(int[][] board, List<int[]> queenLocations) {
		this.board=board;
		this.queenLocations=queenLocations;
	}
	public int[][] getBoard(){
		return board;
	}
	public List<int[]> getQueenLocations() {
		return queenLocations;
	}
	
	public void setBoard(int [][] board) {
		this.board=board;
	}
	public void setQueenLocation(List<int[]> queenLocations) {
		this.queenLocations=queenLocations;
	}
	public List<int[]> getQueenLocations(boolean isBlack) {
		if (isBlack) {
			return queenLocations.subList(0,  4);
		}else {
			return queenLocations.subList(4,  8);
		}
	}
}
