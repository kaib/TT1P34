//Main zum starten der Tests. Konfiguration über static parameter

package schiffeversenken;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import de.uniba.wiai.lspi.util.logging.Logger;

public class Game implements NotifyCallback {
	ChordImpl chord;
	Board board;
	Strategie strategie;
	Logger logger;
	private static int numberOfPlayers = 3;
	private static int INIT_TIME = 1000;
	private static int NETWORK_TIME = 10000;

	// public static final Logger logger =
	// Logger.getLogger(Game.class.getName());

	private Game(int port, int netPort, boolean init) {

		logger = Logger.getLogger("lal");
		this.logger.debug("Logger initialized.");

		String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		String ip = "";
		try {
			ip = (InetAddress.getLocalHost().getHostAddress());
			ip = "localhost";
		} catch (UnknownHostException e2) {
			System.out.println("failed get IP");
			e2.printStackTrace();
		}
		URL bootstrapURL = null;
		try {
			bootstrapURL = new URL(protocol + "://" + ip + ":" + netPort + "/");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		URL localURL = null;
		try {
			localURL = new URL(protocol + "://" + ip + ":" + port + "/");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		chord = new ChordImpl();
		chord.setCallback(this);
		if (init) {
			try {

				chord.create(bootstrapURL);
				System.out.println("Create Network: " + chord.getID());
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {

				chord.join(localURL, bootstrapURL);
				System.out.println("Join Network: " + chord.getID());
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.error("Game initialized!");

	}

	public static void main(String[] args) {
		int startPort = 8380;
		de.uniba.wiai.lspi.chord.service.PropertiesLoader.loadPropertyFile();
		Game game = new Game(startPort, startPort, true);
		List<Game> players = new LinkedList<Game>();
		players.add(game);
		for (int i = 1; i <= numberOfPlayers - 1; i++) {
			players.add(new Game(startPort + i, startPort, false));
		}

		try {
			System.out.println("now sleep");
			Thread.sleep(NETWORK_TIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Init players");
		for (Game player : players) {
			player.init();
		}
		try {
			System.out.println("now sleep");
			Thread.sleep(INIT_TIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("run game");
		for (Game player : players) {
			player.start();
		}

		// Eigenes Spielfeld berechnen
		// TODO: SPielfeldüberischt basteln
		// TODO megacoole strategie anwenden

	}

	public void init() {
		board = new Board(chord.getID(), getOpponents());
		strategie = new Strategie(board);
		System.out.println("Inited: " + chord.getID() + " Board: " + board);
	}

	public void won() {
		System.out.println("WE WON");
		while (true) {

		}
	}

	public void start() {

		if (checkStart()) {
			System.out.println("i Start: " + chord.getID());
			this.fire();
		}
	}

	private boolean checkStart() {
		boolean result = false;
		byte[] b = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
		ID maxID = new ID(b);
		if (maxID.isInInterval(chord.getPredecessorID(), chord.getID())) {
			result = true;
		}
		return result;
	}

	private void fire() {
		final ID target = strategie.nextTarget();

		ID targetNode;
		try {
			targetNode = chord.getFingerTable().get(0).findSuccessor(target)
					.getNodeID();
			System.out.println("[Game] ID: " + chord.getID().toBigInteger()
					+ " schießt auf ID: " + targetNode.toBigInteger()
					+ " Feld: " + target.toBigInteger());
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				chord.retrieve(target);
			}
		}).start();
	}

	private List<ID> getOpponents() {
		List<ID> opponents = new LinkedList<ID>();
		ID node = chord.getFingerTable().get(0).getNodeID();

		while (!opponents.contains(node)) {
			opponents.add(node);

			try {

				node = chord.getFingerTable().get(0)
						.findSuccessor(node.addPowerOfTwo(0)).getNodeID();
			} catch (CommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return opponents;
	}

	@Override
	public void retrieved(ID target) {
		boolean hit = board.isHit(target);
		logger.debug("RetrievedCallback with Target: " + target);
		this.chord.broadcast(target, hit);
		if (board.haveWon()) {
			System.out.println("###############Gewonnen Retrieved"
					+ board.haveWon() + " ID:" + chord.getID());
		} else {
			fire();
		}
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		logger.debug("Broadcast from: " + source + " to: " + target
				+ "with hit: " + hit);
		if (board != null) {
			board.update(source, target, hit);
			System.out.println("[Game] Board from " + chord.getID() + " B: "
					+ board);
			if (board.haveWon()) {
				System.out.println("###############Gewonnen " + board.haveWon()
						+ " ID:" + chord.getID());
			}
		}

	}

}
