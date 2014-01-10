
// Diese Klasse enthällt das Spielfeld. Der aktuelle Spielstand wird mit der Update Methode festgehalten.



package schiffeversenken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uniba.wiai.lspi.chord.data.ID;

public class Board {
	private Map<ID, Set<Integer>> hittedFields;
	private Map<ID, Integer> lifes;
	private ID me;
	private List<ID> opponents;
	public static final int MAX_FIELDS =15;
	private int numberOfShips = 1;
	private List<Integer> ownShips;

	public Board(ID me, List<ID> players) {
		hittedFields = new HashMap<ID, Set<Integer>>();
		lifes = new HashMap<ID, Integer>();
		this.opponents = players;
		this.me = me;
		for (ID opponent : opponents) {
			hittedFields.put(opponent, new HashSet<Integer>());
			lifes.put(opponent, numberOfShips);
		}
		// TODO setzstrategie
//		ownShips = Arrays.asList(0,7,8,49,55,56,70,82,83,99);
		ownShips = Arrays.asList(2);
	}

	public List<Integer> getRemainingTargets(ID opponent) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < MAX_FIELDS; i++) {
			result.add(i);
		}
		result.removeAll(hittedFields.get(opponent));
		return result;
	}

	//Returns Null if no oppenend left
	public ID getLowestOpponent() {
		int shipCount = Integer.MAX_VALUE;
		ID target =null;
		for (Map.Entry<ID, Integer> entry : lifes.entrySet()) {
			if ((entry.getValue() < shipCount) && !(entry.getKey().equals(me)) && (entry.getValue() > 0)) {
				shipCount = entry.getValue();
				target = entry.getKey();
			}
		}
		return target;
	}

	public ID getPredecessorNodeID(ID node) {
		List<ID> succNodes = opponents;
		int index = succNodes.indexOf(node);
		if (index == 0) {
			return succNodes.get(succNodes.size() - 1);
		} else {
			return succNodes.get(index - 1);
		}
	}

	public void update(ID opponentID, ID targetID, boolean hit) {
		int field = IdConverter.IDtoField(getPredecessorNodeID(opponentID),
				opponentID, targetID, MAX_FIELDS);
		hittedFields.get(opponentID).add(field);
		if (hit) {
			if (lifes.get(opponentID) > 0) {
				lifes.put(opponentID, lifes.get(opponentID) - 1);
			}
		}
	}

	public Map<ID, Set<Integer>> getHittedFields() {
		return hittedFields;
	}

	public Set<ID> getLivingOpponents() {
		Set<ID> result = new HashSet<ID>();
		for (Map.Entry<ID, Integer> entry : lifes.entrySet()) {
			if (entry.getValue() > 0) {
				result.add(entry.getKey());
			}
		}
		result.remove(me);
		return result;
	}

	public boolean isHit(ID targetID) {
		boolean result = false;
		Integer hitField = Integer.valueOf(IdConverter.IDtoField(getPredecessorNodeID(me), me,
				targetID,MAX_FIELDS));
		
		if (ownShips.contains(hitField)) {
			List<Integer> newShipList = new ArrayList<Integer>();
			result = true;
			for(Integer ship:ownShips) {
				if(ship != hitField) {
					newShipList.add(ship);
				}
			}
			ownShips = newShipList;
		}
		return result;
	}

	public boolean areWeDeadYet() {
		return ownShips.isEmpty();
	}

	public boolean haveWon() {
		boolean result = true;
		for (Map.Entry<ID, Integer> entry : lifes.entrySet()) {
			if (entry.getValue() > 0 && entry.getKey() != me){
				result = false;
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "Board [hittedFields=" + hittedFields + ", lifes=" + lifes
				+ ", me=" + me + ", opponents=" + opponents
				+ ", numberOfShips=" + numberOfShips + ", ownShips=" + ownShips
				+ "]";
	}
	
	

}
