import org.junit.Test;
import org.junit.Assert;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import entities.Node;
import entities.Directory;
import main.Pathfinder;

/**
 * Created by Michael on 4/2/2017.
 * Tests functions in the Pathfinder
 */
public class PathfinderTester
{
	/* Does a simple test on the pathfinder algorithm
	 * Nodes are organized in a square as shown below
	 *   n2-----d
	 *   |    / |
	 *   |  n1  |
	 *   | /    |
	 *   o------n3
	 *   Correct path is to go through the center
	 */
	@Test
	public void pathfindTester() {
		Node origin = new Node(0, 0); //Create a new node
		Node dest = new Node(10, 10); //Create a new node
		//Create the nodes
		Node n1 = new Node (5, 5);
		Node n2 = new Node (10, 0);
		Node n3 = new Node (0, 10);

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
		Node origin = new Node(0, 0); //Create a new node
		Node dest = new Node(10, 10); //Create a new node
		//Create the nodes
		Node n1 = new Node (5, 5);
		Node n2 = new Node (10, 0);
		Node n3 = new Node (0, 10);

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
}
