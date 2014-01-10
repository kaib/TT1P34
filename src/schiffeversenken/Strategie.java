package schiffeversenken;

import java.util.List;

import de.uniba.wiai.lspi.chord.data.ID;

public class Strategie {
	Board board;

	public Strategie(Board board) {
		this.board = board;
	}

	public ID nextTarget() {
		// strategie: SChwächster gegner, random Schuss auf nicht beschossene
		ID opponent = board.getLowestOpponent();
		
		List<Integer> remainingTargets = board.getRemainingTargets(opponent);
		
		int random = (int)( Math.random() * (remainingTargets.size() -1));
		return IdConverter.FieldToId(opponent, board.getPredecessorNodeID(opponent), remainingTargets.get(random),board.MAX_FIELDS);
	}
}
