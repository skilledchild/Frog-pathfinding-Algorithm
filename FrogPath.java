import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class responsible for computing the frog's optimal path through a hexagonal pond.
 * Uses a DFS-like approach combined with a custom priority queue.
 */
public class FrogPath {
	private Pond pond;

	/**
	 * Constructs a FrogPath object by loading a pond map from file.
	 */
	public FrogPath(String filename) {
		try {
			pond = new Pond(filename);
		} catch (InvalidMapException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Determines the best next hexagon to jump to from the current cell.
	 * Uses a priority queue to evaluate surrounding cells and their terrain types.
	 */
	public Hexagon findBest(Hexagon currCell) {
		ArrayUniquePriorityQueue<Hexagon> pq = new ArrayUniquePriorityQueue<>();

		// Check immediate neighbors
		for(int i = 0; i < 6; i++) {
			Hexagon neighbour = currCell.getNeighbour(i);
			currCell.markOutStack(); // Mark current cell as processed

			if(neighbour != null) { // Avoid null at edges
				if(!pq.contains(neighbour) && !neighbour.isMarked()) {
					
					// Skip impassable terrain
					if(neighbour.isMudCell() || neighbour.isAlligator()) {}

					// Water cell, not near alligator
					if(neighbour.isWaterCell() && !nearAligator(neighbour)) {
						pq.add(neighbour, 6.0);
					}

					// Reeds cell
					if(neighbour.isReedsCell()) {
						if(!nearAligator(neighbour)) {
							pq.add(neighbour, 5.0);
						}
						else{
							pq.add(neighbour, 10.0);
						}
					}

					// Lilypad cell
					if(neighbour.isLilyPadCell() && !nearAligator(neighbour)) {
						pq.add(neighbour, 4.0);
					}

					// Food cell with flies
					if(neighbour instanceof FoodHexagon) {
						int num = ((FoodHexagon) neighbour).getNumFlies();
						if(num == 1 && !nearAligator(neighbour)) {
							pq.add(neighbour, 2.0);
						}
						if(num == 2 && !nearAligator(neighbour)) {
							pq.add(neighbour, 1.0);
						}
						if(num == 3 && !nearAligator(neighbour)) {
							pq.add(neighbour, 0.0);
						}
					}
				}
			}
		}

		// Check 2-cells-away neighbors if current cell is a lilypad
		if(currCell.isLilyPadCell()) {
			for(int i = 0; i < 6; i++) {
				Hexagon neighbour = currCell.getNeighbour(i);
				if(neighbour != null) {
					for(int j = 0; j < 6; j++) {
						Hexagon cellAway = neighbour.getNeighbour(j); // neighbor of neighbor
						if(cellAway != null) {
							if(!pq.contains(cellAway) && !cellAway.isMarked()) {
								if(cellAway.isMudCell() || cellAway.isAlligator()) {}

								// Water cell two steps away
								if(cellAway.isWaterCell() && !nearAligator(cellAway)) {
									if(i == j) {
										pq.add(cellAway, 6.5);
									}
									else {
										pq.add(cellAway, 7.0);
									}
								}

								// Reeds cell two steps away
								if(cellAway.isReedsCell()) {
									if(!nearAligator(cellAway)) {
										if( i == j ) {
											pq.add(cellAway, 5.5);
										}
										else {
											pq.add(cellAway, 6.0);
										}
									}
									else{
										if( i == j ) {
											pq.add(cellAway, 10.5);
										}
										else {
											pq.add(cellAway, 11.0);
										}
									}
								}

								// Lilypad cell two steps away
								if(cellAway.isLilyPadCell() && !nearAligator(cellAway)) {
									if(i == j) {
										pq.add(cellAway, 4.5);
									}
									else {
										pq.add(cellAway, 5.0);
									}
								}

								// Food cell two steps away
								if(cellAway instanceof FoodHexagon) {
									int num = ((FoodHexagon) cellAway).getNumFlies();
									if(num == 1 && !nearAligator(cellAway)) {
										if(i == j) {
											pq.add(cellAway, 2.5);
										}
										else {
											pq.add(cellAway, 3.0);
										}
									}
									if(num == 2 && !nearAligator(cellAway)) {
										if(i == j) {
											pq.add(cellAway, 1.5);
										}
										else {
											pq.add(cellAway, 2.0);
										}
									}
									if(num == 3 && !nearAligator(cellAway)) {
										if(i == j) {
											pq.add(cellAway, 0.5);
										}
										else {
											pq.add(cellAway, 0.0);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// Return the neighbor with the highest priority (lowest score)
		if(pq.isEmpty()) return null;
		return pq.peek();
	}

	/**
	 * Uses DFS to find a complete path from the start to end, 
	 * consuming flies and backtracking when stuck.
	 */
	public String findPath() {
		ArrayStack<Hexagon> s = new ArrayStack<Hexagon>();
		int filesEaten = 0;
		String str = "";

		s.push(pond.getStart());
		pond.getStart().markInStack();

		while(!s.isEmpty()) {
			Hexagon curr = s.peek();
			str = str + curr.getID() + " ";

			if(curr.isEnd()) {
				break;
			}

			// Consume flies
			if(curr instanceof FoodHexagon) {
				filesEaten += ((FoodHexagon) curr).getNumFlies();
				((FoodHexagon) curr).removeFlies();
			}

			// Try next best move
			Hexagon next = findBest(curr);
			if(next == null) {
				s.pop(); // Backtrack
				curr.markOutStack();
			}
			else {
				s.push(next);
				next.markInStack();
			}
		}

		if(s.isEmpty()) return "No solution";
		return str + "ate " + filesEaten + " flies";
	}

	/**
	 * Checks if the given hexagon is adjacent to an alligator cell.
	 */
	private boolean nearAligator(Hexagon neighbour) {
		boolean nearAligator = false;
		for(int i = 0; i < 6; i++) {
			if(neighbour.getNeighbour(i) != null) {
				if(neighbour.getNeighbour(i).isAlligator()) nearAligator = true;
			}
		}
		return nearAligator;
	}
}
