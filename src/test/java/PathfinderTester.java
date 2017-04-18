import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import entities.Directory;
import main.algorithms.PathNotFoundException;
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
	/** use this to find paths without worrying about missing paths */
	private static List<Node> findPathSafely(Node n1, Node n2) {
		List<Node> path = null;
		try {
			 path = Pathfinder.findPath(n1, n2);
		} catch (PathNotFoundException e) {
			Assert.fail("Path not found, but expected");
		}
		return path;
	}

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
		Directory dir = new Directory();
		Node origin = dir.addNewNode(0, 0, 0); //Create a dir.addNewNode
		Node dest = dir.addNewNode(10, 10, 0); //Create a dir.addNewNode
		//Create the nodes
		Node n1 = dir.addNewNode (5, 5, 0);
		Node n2 = dir.addNewNode (10, 0, 0);
		Node n3 = dir.addNewNode (0, 10, 0);

		//Link Adjacencies
		dir.connectNodes(origin, n1);
		dir.connectNodes(origin, n2);
		dir.connectNodes(origin, n3);
		dir.connectNodes(dest, n1);
		dir.connectNodes(dest, n2);
		dir.connectNodes(dest, n3);

		List<Node> shortestDist = new ArrayList<>();
		shortestDist.add(origin);
		shortestDist.add(n1);
		shortestDist.add(dest);
		Assert.assertEquals(findPathSafely(origin, dest), shortestDist); //Make sure the node has been
	}

	/*
	Test a scenario where the destination node is unreachable (no adjacent nodes)
	Should return an empty list
	 */
	@Test(expected = PathNotFoundException.class)
	public void unreachableNodeTest() throws PathNotFoundException {
		Directory dir = new Directory();
		Node origin = dir.addNewNode(0, 0, 0); //Create a new node
		Node dest = dir.addNewNode(10, 10, 0); //Create a new node
		//Create the nodes
		Node n1 = dir.addNewNode (5, 5, 0);
		Node n2 = dir.addNewNode (10, 0, 0);
		Node n3 = dir.addNewNode (0, 10, 0);

		//Link Adjacencies
		dir.connectNodes(origin, n1);
		dir.connectNodes(origin, n2);
		dir.connectNodes(origin, n3);

		List<Node> shortestDist = new ArrayList<>(); //The empty list
		Assert.assertEquals(Pathfinder.findPath(origin, dest), shortestDist);
	}

	// 	n[9] -> n[13]: 9, 10, 11, 12, 5, 6, 15, 13
	@Test
	public void complexPath9to13() {
		Directory dir = new Directory();
		SampleGraph G = new SampleGraph();
		Node[] n = G.nodes;

		List<Node> result = findPathSafely(n[9], n[13]);

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
		Directory dir = new Directory();
		SampleGraph G = new SampleGraph();
		Node[] n = G.nodes;
		List<Node> result = findPathSafely(n[9], n[22]);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		Node[] expect = {n[9], n[10], n[11], n[12], n[5], n[18], n[21], n[24], n[22]};
		Assert.assertArrayEquals(expect, resultAsArray);
	}

	//	n[19] -> n[22]: 19, 17, 23, 18, 21, 24, 22
	@Test
	public void complexPath19to22() {
		Directory dir = new Directory();
		SampleGraph G = new SampleGraph();
		Node[] n = G.nodes;
		List<Node> result = findPathSafely(n[19], n[22]);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		Node[] expect = {n[19], n[17], n[23], n[18], n[21], n[24], n[22]};
		Assert.assertArrayEquals(expect, resultAsArray);
	}

	//	n[1] -> n[13]: 1, 3, 4, 8, 13
	@Test
	public void complexPath1to13() {
		Directory dir = new Directory();
		SampleGraph G = new SampleGraph();
		Node[] n = G.nodes;
		List<Node> result = findPathSafely(n[1], n[13]);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		Node[] expect = {n[1], n[3], n[4], n[8], n[13]};
		Assert.assertArrayEquals(expect, resultAsArray);
	}

	//	n[7] -> n[23]: 7, 9, 14, 16, 17, 23
	@Test
	public void complexPath7to23() {
		Directory dir = new Directory();
		SampleGraph G = new SampleGraph();
		Node[] n = G.nodes;
		List<Node> result = findPathSafely(n[7], n[23]);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		Node[] expect = {n[7], n[9], n[14], n[16], n[17], n[23]};
		Assert.assertArrayEquals(expect, resultAsArray);
	}

	//Tests the basic functionality of travelling to different floors
	@Test
	public void simpleMultiFloorPath() {
		Directory dir = new Directory();
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
		Node origin = dir.addNewNode(0, 0, 1); //Create a start node
		Node dest = dir.addNewNode(10, 10, 2); //Create a end node
		Node n1 = dir.addNewNode(2, 2, 1); //Node between origin and elev1
		Node n2 = dir.addNewNode(2, 2, 2); //Node between elev2 and dest
		Node elev1 = dir.addNewNode(5, 5, 1);
		Node elev2 = dir.addNewNode(5, 5, 2);

		//Connect nodes on same floor
		dir.connectNodes(origin, n1);
		dir.connectNodes(n1, elev1);
		dir.connectNodes(n2, elev2);
		dir.connectNodes(dest, n2);

		//Connect elevator nodes to each other
		dir.connectNodes(elev1, elev2);
		System.out.println(origin.getNeighbors());
		System.out.println(n1.getNeighbors());
		System.out.println(elev1.getNeighbors());

		//Create the expected node path array
		Node[] expect = {origin, n1, elev1, elev2, n2, dest};
		//Find the shortest path
		List<Node> result = findPathSafely(origin, dest);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		System.out.println(Arrays.asList(expect));
		System.out.println(result);
		Assert.assertArrayEquals(expect, resultAsArray);
	}

	@Test
	public void testElevatorPenalty() {
		Directory dir = new Directory();
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
		Node O = dir.addNewNode(0, 20, 1);
		Node D = dir.addNewNode(20, 0, 1);
		Node N1 = dir.addNewNode(20, 20, 1);
		Node E11 = dir.addNewNode(1, 19, 1);
		Node E21 = dir.addNewNode(1, 19, 2);
		Node E12 = dir.addNewNode(19, 1, 1);
		Node E22 = dir.addNewNode(19, 1, 2);

		//Connect the nodes
		dir.connectNodes(O, N1);
		dir.connectNodes(N1, D);
		dir.connectNodes(O, E11);
		dir.connectNodes(E11, E21);
		dir.connectNodes(E21, E22);
		dir.connectNodes(E22, E12);
		dir.connectNodes(E12, D);

		//Expected shortest path
		Node[] expect = {O, N1, D};
		//Find the shortest path
		List<Node> result = findPathSafely(O, D);
		Node[] resultAsArray = result.toArray(new Node[result.size()]);
		Assert.assertArrayEquals(expect, resultAsArray);
	}
}
