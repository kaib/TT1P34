//Diese Klasse enthält die Spielstrategie. Durch den Aufruf von nextTarget, wird der nächste Gegner inklusive Angriffsfeld berechnet
//Strategie: Schwächster Gegner, random Schuss auf nicht beschossene

package schiffeversenken;

import java.util.List;

import de.uniba.wiai.lspi.chord.data.ID;

public class Strategie {
	Board board;

	public Strategie(Board board) {
		this.board = board;
	}

	public ID nextTarget() {
		
		ID opponent = board.getLowestOpponent();
		
		List<Integer> remainingTargets = board.getRemainingTargets(opponent);
		
		int random = (int)( Math.random() * (remainingTargets.size() -1));
		System.out.println("TargetFiel: " + remainingTargets.get(random));
		return IdConverter.FieldToId(opponent, board.getPredecessorNodeID(opponent), remainingTargets.get(random),board.MAX_FIELDS);
	}
}
