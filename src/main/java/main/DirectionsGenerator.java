package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import entities.Direction;
import entities.Node;
import entities.RoomType;
import entities.icons.IconType;
import main.algorithms.Pathfinder;

//TODO: Clean up getTextDirections (see notes)
/*
Notes:

fromPath should probably return a List<String>, which can be displayed as desired.

getTextDirections could use some dramatic cleanup; if IntelliJ says it is too complex to
  analyze, it's probably too complex

The actual directions "left", "right", etc. should probably be an Enum.

 */

public class DirectionsGenerator
{

	private static int rightMin1 = 345;
	private static int rightMax1 = 360;
	private static int rightMin2 = 0;
	private static int rightMax2 = 15;

	private static int softRightMax = 75;
	private static int straightMax = 105;
	private static int softLeftMax = 165;
	private static int leftMax = 195;
	private static int hardLeftMax = 265;
	private static int backWardsMax = 275;
	private static int hardRightMax = 345;

	/**
	 * Generate text directions based on the given path.
	 *
	 * @param path A list of nodes representing the path.
	 *
	 * @return Directions for the path, as a string.
	 */
	public static List<Direction> fromPath(List<Node> path) {
		LinkedList<Node> asArray = new LinkedList<>();
		int i = 0;
		for (Node n : path) {
			asArray.add(n);
			i++;
		}
		return DirectionsGenerator.getTextDirections(asArray);
	}

	// TODO: Use StringBuilder instead of String concatenation
	/** Returns a string that tells how to get from start to end
	 *
	 * @param path the nodes along the path
	 * @return String directions that tell how to reach a destination
	 */
	private static List<Direction> getTextDirections(LinkedList<Node> path) {
		List<Direction>  directions = new ArrayList<Direction>();
		directions.add(new Direction("First, ", IconType.PORTAL, path.get(0)));

		int leftTurns = 0, rightTurns = 0;
		// redo text directions with switch cases based on types of nodes
		for(int i = 1; i < path.size() - 1; i++) {
			switch (path.get(i).getType()) {
				// if PORTAL is read, check to see what type of next node is
				case PORTAL:
					// check case where portal leads outside
					if (path.get(i+1).getBuildingName().equals("Outside")) {
						directions.add(new Direction("Go outside", IconType.PORTAL, path.get(i)));
						i++;
					} else {
						i++;
						directions.add(new Direction("Go into "+path.get(i).getBuildingName(), IconType.PORTAL, path.get(i)));
					}
					// reset num of right and left turns after entering / exiting building
					rightTurns = 0;
					leftTurns = 0;
					break;
				case STAIRS:
					while (path.get(i + 1).getType() == RoomType.STAIRS) {
						i++;
					}
					directions.add(new Direction("Take the stairs to the " +
							path.get(i).getFloor() + getTurnPostfix(path.get(i).getFloor())
							+ " floor", IconType.STAIRS, path.get(i)));
					rightTurns = 0;
					leftTurns = 0;
					break;
				case ELEVATOR:
					while ((i + 1 != path.size()) && (path.get(i + 1).getType() == RoomType.ELEVATOR)) {
						i++;
					}
					directions.add(new Direction("Take the elevator to the " +
							path.get(i).getFloor() + getTurnPostfix(path.get(i).getFloor())
							+ " floor", IconType.ELEVATOR, path.get(i)));
					rightTurns = 0;
					leftTurns = 0;
					break;
				default:
					double turnAngle = path.get(i).angle(path.get(i + 1), path.get(i - 1));
					if (isRightTurn(turnAngle)) {
						// Right Turn
						if (rightTurns == 0) {
							directions.add(new Direction("Take a right turn", IconType.RIGHT, path.get(i)));
						} else {
							rightTurns++;
							directions.add(new Direction("Take the " + rightTurns
									+ getTurnPostfix(rightTurns) + " right", IconType.RIGHT, path.get(i)));

						}
						// if you take a turn, then the count for turns should be reset
						// to 0

						rightTurns = 0;
						leftTurns = 0;
					} else if (isSoftRightTurn(turnAngle)) {
						if (Pathfinder.getStrategy() != Pathfinder.getAlgorithmList()[0])

							continue;
						// Soft Right Turn
						directions.add(new Direction("Take a soft right turn", IconType.SRIGHT, path.get(i)));
						// if you take a turn, then the count for turns should be reset to 0
						leftTurns = 0;
						rightTurns = 0;
					} else if (isStraight(turnAngle)) {
						directions.add(new Direction("Continue straight", IconType.STRAIGHT, path.get(i)));
						while(isStraight(turnAngle)) {
							// Straight (NO TURN!!!)
							//				directions += "continue straight,\nThen "; // we don't want to spam them with this
							// Figure out if there is a left or right turn available as well, then increment the counters
							Set<Node> forks = path.get(i).getNeighbors();
							for (Node fork : forks) {
								int forkAngle = (int) path.get(i).angle(fork, path.get(i - 1));

								if (isRightTurn(forkAngle) || isSoftRightTurn(forkAngle) || isHardRightTurn(forkAngle)) {
									rightTurns++;
								}
								if (isLeftTurn(forkAngle) || isSoftLeftTurn(forkAngle) || isHardLeftTurn(forkAngle)) {
									leftTurns++;
								}
							}
							i++;
							if(path.size() == i+1){
								break;
							}
							turnAngle = path.get(i).angle(path.get(i + 1), path.get(i - 1));
						}
						i--;
					} else if (isSoftLeftTurn(turnAngle)) {
						if (Pathfinder.getStrategy() != Pathfinder.getAlgorithmList
								()[0])
							continue;
						// Soft Left Turn
						directions.add(new Direction("Take a soft left turn", IconType.SLEFT, path.get(i)));
						// if you take a turn, then the count for turns should be reset to 0
						leftTurns = 0;
						rightTurns = 0;
					} else if (isLeftTurn(turnAngle)) {
						// Left Turn
						if (leftTurns == 0) {
							directions.add(new Direction("Take a left turn", IconType.LEFT, path.get(i)));
						} else {
							leftTurns++;
							directions.add(new Direction("Take the "+ leftTurns +
									getTurnPostfix(leftTurns) + " left", IconType.LEFT, path.get(i)));
						}
						// if you take a turn, then the count for turns should be reset to 0
						leftTurns = 0;
						rightTurns = 0;
					} else if (isHardLeftTurn(turnAngle)) {
						// Hard Left Turn
						directions.add(new Direction("Take a hard left turn", IconType.HLEFT, path.get(i)));
						// if you take a turn, then the count for turns should be reset to 0
						leftTurns = 0;
						rightTurns = 0;
					} else if (isBackwards(turnAngle)) {
						// Turn Around
						//directions.add(new Direction("Turn around", IconType.PORTAL));
						//actually don't
						// if you take a turn, then the count for turns should be reset to 0
						leftTurns = 0;
						rightTurns = 0;
					} else if (isHardRightTurn(turnAngle)) {
						// Hard Right Turn
						directions.add(new Direction("Take a hard right turn", IconType.HRIGHT, path.get(i)));
						// if you take a turn, then the count for turns should be reset to 0
						leftTurns = 0;
						rightTurns = 0;
					}
					break;
			}
		}
		directions.add(new Direction("You have arrived at your destination.", IconType.PORTAL, path.get(path.size() - 1)));
		return directions;
	}

	/** Determines if the angle given corresponds to a right turn
	 *
	 * @param angle the turn angle
	 * @return true: if it's a right turn
	 *         false: otherwise
	 */
	private static boolean isRightTurn(double angle) {
		return isBetween(rightMin1, angle, rightMax1) || isBetween(rightMin2, angle, rightMax2);
	}

	/** Determines if the angle given corresponds to a left turn
	 *
	 * @param angle the turn angle
	 * @return true: if it's a left turn
	 *         false: otherwise
	 */
	private static boolean isLeftTurn(double angle) {
		return isBetween(softLeftMax, angle, leftMax);
	}

	/** Determines if the angle given corresponds to a soft right turn
	 *
	 * @param angle the turn angle
	 * @return true: if it's a soft right turn
	 *         false: otherwise
	 */
	private static boolean isSoftRightTurn(double angle) {
		return isBetween(rightMax2, angle, softRightMax);
	}

	/** Determines if the angle given corresponds to a soft left turn
	 *
	 * @param angle the turn angle
	 * @return true: if it's a soft left turn
	 *         false: otherwise
	 */
	private static boolean isSoftLeftTurn(double angle) {
		return isBetween(straightMax, angle, softLeftMax);
	}

	/** Determines if the angle given corresponds to a hard right turn
	 *
	 * @param angle the turn angle
	 * @return true: if it's a hard right turn
	 *         false: otherwise
	 */
	private static boolean isHardRightTurn(double angle) {
		return isBetween(backWardsMax, angle, hardRightMax);
	}

	/** Determines if the angle given corresponds to a hard left turn
	 *
	 * @param angle the turn angle
	 * @return true: it it's a hard left turn
	 *         false: otherwise
	 */
	private static boolean isHardLeftTurn(double angle) {
		return isBetween(leftMax, angle, hardLeftMax);
	}

	/** Determines if the angle given corresponds to moving straight
	 *
	 * @param angle the turn angle
	 * @return true: if it's a straight
	 *         false: otherwise
	 */
	private static boolean isStraight(double angle) {
		return isBetween(softRightMax, angle, straightMax);
	}

	/** Determines if the angle given corresponds to turning around
	 *
	 * @param angle the turn angle
	 * @return true: if it's backwards
	 *         false: otherwise
	 */
	private static boolean isBackwards(double angle) {
		return isBetween(hardLeftMax, angle, backWardsMax);
	}

	/** Determines is B is numerically between A and C
	 * A should be less than C
	 * @param A the lesser bound
	 * @param B the number you want to check to be in between A and C
	 * @param C the greater bound
	 * @return
	 */
	private static boolean isBetween(double A, double B, double C) {
		return B >= A && B < C;
	}

	/** Determines whether the user is going to use an elevator of not
	 * @param node1 the start node
	 * @param node2 the end node
	 * @return true: if they are on the same floor
	 *         false: if they are on the different floor
	 */
	private static boolean isElevator(Node node1, Node node2){
		return node1.getFloor() != node2.getFloor();
	}

	private static boolean isPortal(Node node1, Node node2) {
		return ! node1.getBuildingName().equalsIgnoreCase(node2.getBuildingName());
	}

	/** Gives the postfix for a number
	 *
	 * @param turns the number that needs a postfix
	 * @return "st" if turns == 1, "nd" if turns == 2, "rd" if turns == 3, "th" if turns == 4, 5, 6, 7, 8,9, 10, 11, 12, 13, 14, 15, 16, 17, 18,19,20, and potentially so on...
	 */
	private static String getTurnPostfix(int turns) {
		// This does not account for numbers greater than 20
		switch(turns) {
			case 1:
				return "st";
			case 2:
				return "nd";
			case 3:
				return "rd";
			default:
				return "th";
		}
	}
}
