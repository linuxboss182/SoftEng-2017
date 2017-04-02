import entities.Node;
import entities.Directory;
import algorithms.Pathfinder;
import org.junit.Test;
import org.junit.Assert;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Michael on 4/2/2017.
 * Tests functions in the Pathfinder
 */
public class PathfinderTester
{
	/* Does a simple test on the pathfinder algorithm
	 * Nodes are organized in a square as shown below
	 *   n2     d
	 *      n1
	 *   o      n3
	 *   Correct path is to go through the center
	 */
	/*@Test
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

		List<Node> shortestDist = new ArrayList<Node>();
		shortestDist.add(origin);
		shortestDist.add(n1);
		shortestDist.add(dest);
		Assert.assertEquals(findPath(origin, dest), shortestDist); //Make sure the node has been
	}*/
}
