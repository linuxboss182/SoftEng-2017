package main;

import entities.Node;

public class Pathfinder
{


	/**A method to translate a series of nodes into text directions
	 *
	 */
	public static String getTextDirections(Node[] path) {
		int rightMin1 = 345;
		int rightMax1 = 360;
		int rightMin2 = 0;
		int rightMax2 = 15;

		int softRightMax = 75;
		int straightMax = 105;
		int softLeftMax = 165;
		int leftMax = 195;
		int hardLeftMax = 265;
		int backWardsMax = 275;
		int hardRightMax = 345;

		String directions = "First, ";

		for(int i = 1; i < path.length - 1; i++) {
			double turnAngle = path[i].angle(path[i-1], path[i+1]);
			int leftTurns = 0, rightTurns = 0;

			// Determine the direction of the turn through a bunch of if statements
			if(turnAngle >= rightMin1 && turnAngle < rightMax1 || turnAngle >= rightMin2 && turnAngle < rightMax2) {
				// Right Turn
				if(rightTurns == 0) {
					directions += "take a right turn,\nThen ";
				} else {
					directions += "take the " + rightTurns + "rd Right,\n Then";
					rightTurns = 0;
				}
			} else if(turnAngle >= rightMax2 && turnAngle < softRightMax) {
				// Soft Right Turn
				directions += "take a soft right turn,\nThen ";
			} else if(turnAngle >= softRightMax && turnAngle < straightMax) {
				// Straight (NO TURN!!!)
				directions += "continue straight,\nThen ";
				// Figure out if there is a left or right turn available as well, then increment the counters
				Node[] forks = path[i].getAdjacencies();
				for(int j = 0; j < forks.length; j++) {
					double forkAngle = path[i].angle(path[i-1], forks[j]);
					// Only checks for normal right and left turns (not including soft or hard variants)
					if(forkAngle >= rightMin1 && forkAngle < rightMax1 || forkAngle >= rightMin2 && turnAngle < rightMax2) {
						rightTurns++;
					}
					if(forkAngle >= softLeftMax && forkAngle < leftMax) {
						leftTurns++;
					}
				}


			} else if(turnAngle >= straightMax && turnAngle < softLeftMax) {
				// Soft Left Turn
				directions += "take a soft left turn\nThen ";
			} else if(turnAngle >= softLeftMax && turnAngle < leftMax) {
				// Left Turn
				if(leftTurns == 0) {
					directions += "take a left turn,\nThen ";
				} else {
					directions += "take the " + leftTurns + "rd Left,\n Then";
					leftTurns = 0;
				}
			} else if(turnAngle >= leftMax && turnAngle < hardLeftMax) {
				// Hard Left Turn
				directions += "take a hard left turn\nThen ";
			} else if(turnAngle >= hardLeftMax && turnAngle < backWardsMax) {
				// Turn Around
				directions += "turn around\nThen ";
			} else if(turnAngle >= backWardsMax && turnAngle < hardRightMax) {
				// Hard Right Turn
				directions += "take a hard right turn\nThen ";
			}
		}
		directions += "you are at your destination.";
		return directions;
	}

	public static void main(String[] args) {
		Node[] path = {new Node(1,1), new Node(1, 2), new Node(2,3), new Node(2, 4), new Node(1, 3), new Node(0, 4), new Node(-1,5), new Node(-2, 6), new Node(-3, 7), new Node(-2, 8)};
		System.out.println(getTextDirections(path));
	}
}
