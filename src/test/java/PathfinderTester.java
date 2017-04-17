import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import main.algorithms.Pathfinder;
import org.junit.Test;
import org.junit.Assert;

import entities.Node;

/**
 * Created by Michael on 4/2/2017.
 * Tests functions in the Pathfinder
 */
public class PathfinderTester
{
	@Test
	public void pathfindTester() {
	/* Does a simple test on the pathfinder algorithm
	 * Nodes are organized in a square as shown below
	 *   n2-----d
	 *   |    / |
	 *   |  n1  |
	 *   | /    |
	 *   o------n3
	 *   Correct path is to go through the center
	 */
		Node origin = new Node(0, 0, 0); //Create a new node
		Node dest = new Node(10, 10, 0); //Create a new node
		//Create the nodes
		Node n1 = new Node (5, 5, 0);
		Node n2 = new Node (10, 0, 0);
		Node n3 = new Node (0, 10, 0);

		//Link Adjacencies
		origin.connect(n1);
		origin.connect(n2);
		origin.connect(n3);
		dest.connect(n1);
		dest.connect(n2);
		dest.connect(n3);

		List<Node> shortestDist = new ArrayList<>();
		shortestDist.add(origin);
		shortestDist.add(n1);
		shortestDist.add(dest);
		Assert.assertEquals(Pathfinder.findPath(origin, dest), shortestDist); //Make sure the node has been
	}

	/*
	Test a scenario where the destination node is unreachable (no adjacent nodes)
	Should return an empty list
	 */
	@Test
	public void unreachableNodeTest() {
		Node origin = new Node(0, 0, 0); //Create a new node
		Node dest = new Node(10, 10, 0); //Create a new node
		//Create the nodes
		Node n1 = new Node (5, 5, 0);
		Node n2 = new Node (10, 0, 0);
		Node n3 = new Node (0, 10, 0);

		//Link Adjacencies
		origin.connect(n1); 
		origin.connect(n2);
		origin.connect(n3);

		List<Node> shortestDist = new ArrayList<>(); //The empty list
		Assert.assertEquals(Pathfinder.findPath(origin, dest), shortestDist);
	}

	// 	n[9] -> n[13]: 9, 10, 11, 12, 5, 6, 15, 13
	@Test
	public void complexPath9to13() {
		SampleGraph G = new SampleGraph();
		Node[] n = G.nodes;

		List<Node> result = Pathfinder.findPath(n[9], n[13]);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
// uncomment in case of error
		/* also, add this to build.gradle:
		   test {
		       testLogging.showStandardStreams = true
		   }
		*/
//		for (Node k : result) {
//			for (int i=0; i < n.length; i++) {
//				if (k == n[i]) {
//					System.out.println(i);
//				}
//			}
//		}
		Node[] expect = {n[9], n[10], n[11], n[12], n[5], n[6], n[15], n[13]};
		Assert.assertArrayEquals(expect, resultAsArray);
	}

	//	n[9] -> n[22]: 9, 10, 11, 12, 5, 18, 21, 24, 22
	@Test
	public void complexPath9to22() {
		SampleGraph G = new SampleGraph();
		Node[] n = G.nodes;
		List<Node> result = Pathfinder.findPath(n[9], n[22]);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		Node[] expect = {n[9], n[10], n[11], n[12], n[5], n[18], n[21], n[24], n[22]};
		Assert.assertArrayEquals(expect, resultAsArray);
	}

	//	n[19] -> n[22]: 19, 17, 23, 18, 21, 24, 22
	@Test
	public void complexPath19to22() {
		SampleGraph G = new SampleGraph();
		Node[] n = G.nodes;
		List<Node> result = Pathfinder.findPath(n[19], n[22]);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		Node[] expect = {n[19], n[17], n[23], n[18], n[21], n[24], n[22]};
		Assert.assertArrayEquals(expect, resultAsArray);
	}

	//	n[1] -> n[13]: 1, 3, 4, 8, 13
	@Test
	public void complexPath1to13() {
		SampleGraph G = new SampleGraph();
		Node[] n = G.nodes;
		List<Node> result = Pathfinder.findPath(n[1], n[13]);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		Node[] expect = {n[1], n[3], n[4], n[8], n[13]};
		Assert.assertArrayEquals(expect, resultAsArray);
	}

	//	n[7] -> n[23]: 7, 9, 14, 16, 17, 23
	@Test
	public void complexPath7to23() {
		SampleGraph G = new SampleGraph();
		Node[] n = G.nodes;
		List<Node> result = Pathfinder.findPath(n[7], n[23]);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		Node[] expect = {n[7], n[9], n[14], n[16], n[17], n[23]};
		Assert.assertArrayEquals(expect, resultAsArray);
	}

	//Tests the basic functionality of travelling to different floors
	@Test
	public void simpleMultiFloorPath() {
/*
  X = floor 1
  O = floor 2
  Z = both floors
  E = elevator

   01234567890
  0X          
  1           
  2  Z        
  3           
  4           
  5     E     
  6           
  7           
  8           
  9           
  0          O

 */
		Node origin = new Node(0, 0, 1); //Create a start node
		Node dest = new Node(10, 10, 2); //Create a end node
		Node n1 = new Node(2, 2, 1); //Node between origin and elev1
		Node n2 = new Node(2, 2, 2); //Node between elev2 and dest
		Node elev1 = new Node(5, 5, 1);
		Node elev2 = new Node(5, 5, 2);

		//Connect nodes on same floor
		origin.connect(n1);
		n1.connect(elev1);
		n2.connect(elev2);
		dest.connect(n2);

		//Connect elevator nodes to each other
		elev1.connect(elev2);
		System.out.println(origin.getNeighbors());
		System.out.println(n1.getNeighbors());
		System.out.println(elev1.getNeighbors());

		//Create the expected node path array
		Node[] expect = {origin, n1, elev1, elev2, n2, dest};
		//Find the shortest path
		List<Node> result = Pathfinder.findPath(origin, dest);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		System.out.println(Arrays.asList(expect));
		System.out.println(result);
		Assert.assertArrayEquals(expect, resultAsArray);
	}

	@Test
	public void testElevatorPenalty() {
		//If destination is on the same floor, Pathfinder should use the path on the same
		//floor rather than try to find a shorter route via an elevator
	/*
	 * Floor 1                      Floor 2
	 * _______________________      ________________________
	 * |O ------------------N1|     |                      |
	 * | \E11                ||     |  E21                 |
	 * |                     ||     |     \_               |
	 * |                     ||     |       \_             |
	 * |                     ||     |         \_           |
	 * |                     ||     |           \_         |
	 * |                     ||     |             \_       |
	 * |                 E12 ||     |               \_E22  |
	 * |                    \D|     |                      |
	 * |______________________|     |______________________|
	 * Path should be [O, N1, D] not [O, E11, E21, E22, E12, D] even though it may appear
	 * shorter
	 */

		//Create the nodes
		Node O = new Node(0, 20, 1);
		Node D = new Node(20, 0, 1);
		Node N1 = new Node(20, 20, 1);
		Node E11 = new Node(1, 19, 1);
		Node E21 = new Node(1, 19, 2);
		Node E12 = new Node(19, 1, 1);
		Node E22 = new Node(19, 1, 2);

		//Connect the nodes
		O.connect(N1);
		N1.connect(D);
		O.connect(E11);
		E11.connect(E21);
		E21.connect(E22);
		E22.connect(E12);
		E12.connect(D);

		//Expected shortest path
		Node[] expect = {O, N1, D};
		//Find the shortest path
		List<Node> result = Pathfinder.findPath(O, D);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		Assert.assertArrayEquals(expect, resultAsArray);
	}
}
