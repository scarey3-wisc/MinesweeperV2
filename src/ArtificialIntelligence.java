import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

public class ArtificialIntelligence extends Player {
	private Game myGame;
	private Map myMap;

	private LinkedList<Point> bad;
	private LinkedList<Point> good;
	private boolean analyzed;

	public ArtificialIntelligence(Game theGame) {
		bad = new LinkedList<Point>();
		good = new LinkedList<Point>();
		myGame = theGame;
		analyzed = false;
	}

	public void reset(Game theGame) {
		myGame = theGame;
		bad = new LinkedList<Point>();
		good = new LinkedList<Point>();
		myMap = null;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Sweepin.delay();
			Sweepin.lockProcess();
			if (good.size() > 0) {
				analyzed = false;
				for (int i = 0; i < good.size(); i++) {
					if (!myGame.isUnknown(good.get(i))) {
						good.remove(i);
						i--;
					}
				}
				if (good.size() > 0) {
					Point move = good.remove((int) (Math.random() * good.size()));
					if (myGame.clickSpot(move.x, move.y) == -1) {
						System.out.println("THAT'S A PROBLEM");
					}
					;
				}
			} else {
				ArrayList<ArrayList<LinkedPoint>> partitionList = segregatePoints();

				myMap = new Map(myGame.getWidth(), myGame.getHeight());
				for (int i = 0; i < myGame.getWidth(); i++) {
					for (int j = 0; j < myGame.getHeight(); j++) {
						myMap.setAt(i, j, myGame.getKnowledge(i, j));
					}
				}
				for (Point p : bad) {
					myMap.setAt(p.x, p.y, 'B');
				}

				ArrayList<ArrayList<Long>> counts = new ArrayList<ArrayList<Long>>();
				counts.add(null);
				for (int i = 1; i < partitionList.size(); i++) {
					ArrayList<Long> test = analyzePartition(partitionList.get(i));
					counts.add(test);
				}

				if (analyzed) {
					if (counts.size() == 1 || good.size() == 0 && partitionList.get(0).size() > 0) {
						LinkedPoint randomlyChosen = partitionList.get(0)
								.get((int) (Math.random() * partitionList.get(0).size()));
						System.out.println("Guess");
						myGame.clickSpot(randomlyChosen.x, randomlyChosen.y);
					} else {
						int i = 0;
						while (i < partitionList.size() && partitionList.get(i).size() == 0) {
							i++;
						}
						if (i < partitionList.size()) {
							LinkedPoint randomlyChosen = partitionList.get(i)
									.get((int) (Math.random() * partitionList.get(i).size()));
							System.out.println("Guess");
							myGame.clickSpot(randomlyChosen.x, randomlyChosen.y);

						}
					}
					analyzed = false;
				}else {
					analyzed = true;
				}
				
			}
			Sweepin.releaseProcess();
		}
	}

	/*
	 * partition: a list of unknown cells that we're exploring theory: an array,
	 * where 0 means 'no bomb', 1 means 'bomb', and -1 means 'idk' counter: records
	 * how many bombs have appeared at a particular index in valid hypothesis in
	 * each of these three arrays, the same index refers to the same point - except
	 * that counter has an extra element for the total number of valid hypothesis.
	 * 
	 * order: a list of indexes; the order in which to build theories depth: which
	 * index in the order to do next
	 */
	private int testHypothesis(ArrayList<LinkedPoint> partition, int[] theory, ArrayList<Long> counter,
			ArrayList<Integer> order, int depth) {
		// Step one: check if this theory is valid in and of itself
		for (int i = 0; i < theory.length; i++) {
			if (theory[i] == 1) {
				myMap.setAt(partition.get(i).x, partition.get(i).y, 'B');
			} else if (theory[i] == 0) {
				myMap.setAt(partition.get(i).x, partition.get(i).y, 'N');
			}
		}

		boolean valid = true;

		for (int alph = 0; alph < theory.length; alph++) {
			LinkedPoint center = partition.get(alph);
			for (int i = center.x - 1; i <= center.x + 1; i++) {
				for (int j = center.y - 1; j <= center.y + 1; j++) {
					if (i == center.x && j == center.y) {
						continue;
					}
					if (i < 0 || j < 0 || i >= myGame.getWidth() || j >= myGame.getHeight()) {
						continue;
					}
					if (myMap.getAt(i, j) - '0' >= 0 && myMap.getAt(i, j) - '0' <= 8) {
						valid = valid && myMap.plausibleNumBombs(i, j);
					}
				}
			}
		}

		// Step two: reset the territory
		for (int i = 0; i < theory.length; i++) {
			myMap.setAt(partition.get(i).x, partition.get(i).y, '?');
		}

		// Step three: if its not valid, return -1 for invalid
		if (!valid) {
			return -1;
		}
		// Step four: check whether we've reached the bottom; if we have, update Counter
		// and then return a 1 for 'valid'
		if (depth == order.size()) {
			for (int i = 0; i < theory.length; i++) {
				if (theory[i] == 1) {
					counter.set(i, counter.get(i) + 1);
				}
			}
			counter.set(counter.size() - 1, counter.get(counter.size() - 1) + 1);
			return 1;
		}
		// Step five: if we haven't reached the bottom, recurse with a bomb in and not
		// in
		// the next slot.
		int index = order.get(depth);
		theory[index] = 1;
		int r1 = testHypothesis(partition, theory, counter, order, depth + 1);
		theory[index] = 0;
		int r2 = testHypothesis(partition, theory, counter, order, depth + 1);
		theory[index] = -1;
		// Step six: if both children were invalid, return -1 for invalid, otherwise
		// return 1 for valid
		if (r1 == -1 && r2 == -1) {
			return -1;
		} else {
			return 1;
		}
	}

	private ArrayList<Long> analyzePartition(ArrayList<LinkedPoint> partition) {
		int[] theory = new int[partition.size()];
		for (int i = 0; i < theory.length; i++) {
			theory[i] = -1;
		}
		ArrayList<Long> counter = new ArrayList<Long>();
		for (int i = 0; i < theory.length + 1; i++) {
			counter.add(new Long(0));
		}
		ArrayList<Integer> order = new ArrayList<Integer>();
		for (int i = 0; i < theory.length; i++) {
			order.add(i);
		}
		int result = testHypothesis(partition, theory, counter, order, 0);
		if (result == -1) {
			System.out.println("THAT'S A HUGE PROBLEM");
			return null;
		}

		long totalOptions = counter.get(counter.size() - 1);

		for (int i = 0; i < theory.length; i++) {
			if (counter.get(i) == 0) {
				good.add(new Point(partition.get(i).x, partition.get(i).y));
			} else if (counter.get(i) == totalOptions) {
				bad.add(new Point(partition.get(i).x, partition.get(i).y));
			}
		}

		return counter;
	}

	private ArrayList<ArrayList<LinkedPoint>> segregatePoints() {
		ArrayList<Point> unknowns = myGame.getAllUnknown();
		LinkedPoint[][] mapClone = new LinkedPoint[myGame.getWidth()][myGame.getHeight()];
		for (Point p : unknowns) {
			mapClone[p.x][p.y] = new LinkedPoint(p.x, p.y);
		}
		for (Point p : bad) {
			mapClone[p.x][p.y] = null;
		}

		for (int i = 0; i < mapClone.length; i++) {
			for (int j = 0; j < mapClone[i].length; j++) {
				if (mapClone[i][j] == null) {
					continue;
				}
				if (myGame.hasInfo(new Point(i, j))) {
					mapClone[i][j].informed = true;
				}
			}
		}

		ArrayList<ArrayList<LinkedPoint>> partitions = new ArrayList<ArrayList<LinkedPoint>>();
		partitions.add(new ArrayList<LinkedPoint>());
		for (int i = 0; i < mapClone.length; i++) {
			for (int j = 0; j < mapClone[i].length; j++) {
				if (mapClone[i][j] == null) {
					continue;
				}
				if (!mapClone[i][j].informed) {
					partitions.get(0).add(mapClone[i][j]);
					mapClone[i][j] = null;
					continue;
				}
				ArrayList<LinkedPoint> onePart = new ArrayList<LinkedPoint>();
				LinkedList<LinkedPoint> queue = new LinkedList<LinkedPoint>();
				queue.add(mapClone[i][j]);
				mapClone[i][j] = null;
				while (!queue.isEmpty()) {
					LinkedPoint next = queue.removeFirst();
					onePart.add(next);

					ArrayList<Point> adjacentCommentary = new ArrayList<Point>();
					for (int ii = next.x - 1; ii <= next.x + 1; ii++) {
						for (int jj = next.y - 1; jj <= next.y + 1; jj++) {
							if (ii == next.x && jj == next.y) {
								continue;
							}
							if (ii < 0 || ii >= mapClone.length || jj < 0 || jj >= mapClone[ii].length) {
								continue;
							}
							if (myGame.isNumber(new Point(ii, jj))) {
								adjacentCommentary.add(new Point(ii, jj));
							}
						}
					}

					for (Point p : adjacentCommentary) {
						for (int ii = p.x - 1; ii <= p.x + 1; ii++) {
							for (int jj = p.y - 1; jj <= p.y + 1; jj++) {
								if (ii == p.x && jj == p.y) {
									continue;
								}
								if (ii < 0 || ii >= mapClone.length || jj < 0 || jj >= mapClone[ii].length) {
									continue;
								}
								if (mapClone[ii][jj] != null) {
									queue.addLast(mapClone[ii][jj]);
									mapClone[ii][jj] = null;
								}
							}
						}
					}

				}
				partitions.add(onePart);
			}
		}

		return partitions;
	}

	public ArrayList<Thought> generateThoughts(int size) {
		ArrayList<Thought> myThoughts = new ArrayList<Thought>();
		for (Point p : bad) {
			BufferedImage nova = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) nova.getGraphics();

			g.translate(size / 2, size / 2);
			g.rotate(Math.PI / 4);
			g.setColor(Color.red);
			g.fillRect((int) (-0.1 * size), (int) (-0.4 * size), (int) (0.2 * size), (int) (0.8 * size));
			g.fillRect((int) (-0.4 * size), (int) (-0.1 * size), (int) (0.8 * size), (int) (0.2 * size));

			myThoughts.add(new Thought(p, nova));
		}

		for (Point p : good) {
			BufferedImage nova = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) nova.getGraphics();

			g.translate(size / 2, size / 2);
			g.setColor(Color.GREEN);
			g.fillRect((int) (-0.1 * size), (int) (-0.35 * size), (int) (0.2 * size), (int) (0.7 * size));
			g.fillRect((int) (-0.35 * size), (int) (-0.1 * size), (int) (0.7 * size), (int) (0.2 * size));

			myThoughts.add(new Thought(p, nova));
		}
		return myThoughts;
	}

	private class HypothesisCounter {
		private ArrayList<ArrayList<Long>> contents;
		private int size;

		public HypothesisCounter(int size) {
			this.size = size;
			contents = new ArrayList<ArrayList<Long>>();
			for (int i = 0; i < size; i++) {
				ArrayList<Long> nova = new ArrayList<Long>();
				for (int j = 0; j < size + 1; j++) {
					nova.add(new Long(0));
				}
				contents.add(nova);
			}
		}

		public void logHypothesis(int[] hypothesis) {
			int totalBombs = 0;
			for (int i : hypothesis) {
				if (i == 1) {
					totalBombs++;
				}
			}
			ArrayList<Long> atNum = contents.get(totalBombs);
			for (int i = 0; i < hypothesis.length; i++) {
				if (hypothesis[i] == 1) {
					atNum.set(i, atNum.get(i) + 1);
				}
			}
			atNum.set(hypothesis.length, atNum.get(hypothesis.length) + 1);
		}

		public int[] giveCertainties(int withNumBombs) {
			int[] result = new int[size];
			ArrayList<Long> thatNum = contents.get(withNumBombs);
			if (thatNum.get(size) == 0) {
				return null;
			}
			for (int i = 0; i < size; i++) {
				if (thatNum.get(i) == 0) {
					result[i] = 0;
				} else if (thatNum.get(i) == thatNum.get(size)) {
					result[i] = 1;
				} else {
					result[i] = -1;
				}
			}
			return result;
		}

		public int[] giveCertainties() {
			int[] result = new int[size];
			for (int i = 0; i < size; i++) {
				result[i] = -2;
			}
			for (int numBombs = 0; numBombs < size; numBombs++) {
				int[] levelResults = giveCertainties(numBombs);
				if (levelResults == null) {
					continue;
				}
				for (int i = 0; i < size; i++) {
					if (result[i] == -2) {
						result[i] = levelResults[i];
					}
					if (result[i] != levelResults[i]) {
						result[i] = -1;
					}
				}
			}
			return result;
		}
	}

	private class LinkedPoint {
		public int x;
		public int y;
		public boolean informed;

		public LinkedPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Sweepin.delay();
		Sweepin.lockProcess();
		int x = e.getX();
		int y = e.getY();
		Point p = myGame.convertScreenCoordinates(x, y);
		if (e.getButton() == 1) {
			myGame.clickSpot(p.x, p.y);
		}
		Sweepin.releaseProcess();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

}