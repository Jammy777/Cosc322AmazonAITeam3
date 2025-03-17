package ubc.cosc322;

import java.util.Map;

public class valueMovePair {
	private int value;
	private Map<String, Object> move;
	public valueMovePair(int value, Map<String, Object> move) {
		this.value=value;
		this.move=move;
	}
	public int getValue(){
		return value;
	}
	public Map<String, Object> getMove() {
		return move;
	}
	public void setValue(int val) {
		value=val;
	}
	public void setMove(Map<String, Object> move) {
		this.move=move;
	}
}
