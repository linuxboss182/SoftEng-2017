import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import entities.Directory;
import main.ApplicationController;
import main.algorithms.Algorithm;
import main.algorithms.PathNotFoundException;
import main.algorithms.Pathfinder;
import org.junit.Test;
import org.junit.Assert;

import entities.Node;

/**
 * Created by Michael on 4/18/2017.
 * Tests to insure the Breadth First Algorithm behaves like Breadth First
 * Breadth first does not take distance in account so mosts tests will test functionality
 */
public class BreadthFirstTester
{
	/** use this to find paths without worrying about missing paths */
	private static List<Node> findPathSafely(Node n1, Node n2) {
		List<Node> path = null;
		Algorithm alg = Arrays.stream(Pathfinder.getAlgorithmList())
				.filter(a -> "Breadth First".compareToIgnoreCase(a.getName())==0)
				.findAny().orElse(null);
		if (alg == null) Assert.fail("BFS not found");
		Pathfinder.setStrategy(alg);
		try {
			path = Pathfinder.findPath(n1, n2);
		} catch (PathNotFoundException e) {
			Assert.fail("Path not found, but expected");
		}
		return path;
	}


	/* Does a simple test on the Breadth First pathfinder algorithm
	* Nodes are organized in a square as shown below
	*   n2-----d
	*   |    / |
	*   |  n1  |
	*   | /    |
	*   o------n3
	*   No matter which way the pathfinder chooses the number of nodes
	*   traversed should be 3
	*/
	@Test
	public void simpleBreadthFirstTest() {

		//ApplicationController appCont = new ApplicationController();
		Directory dir = new Directory();
		ApplicationController.setDirectory(dir);
		Node origin = dir.addNewNode(0, 0, 0, "heyyeyaaeyaaaeyaeyaa", false); //Create a dir.addNewNode
		Node dest = dir.addNewNode(10, 10, 0, "heyyeyaaeyaaaeyaeyaa", false); //Create a dir.addNewNode
		//Create the nodes
		Node n1 = dir.addNewNode (5, 5, 0, "heyyeyaaeyaaaeyaeyaa", false);
		Node n2 = dir.addNewNode (10, 0, 0, "heyyeyaaeyaaaeyaeyaa", false);
		Node n3 = dir.addNewNode (0, 10, 0, "heyyeyaaeyaaaeyaeyaa", false);

		//Link Adjacencies
		dir.connectNodes(origin, n1);
		dir.connectNodes(origin, n2);
		dir.connectNodes(origin, n3);
		dir.connectNodes(dest, n1);
		dir.connectNodes(dest, n2);
		dir.connectNodes(dest, n3);

		Assert.assertEquals(3, findPathSafely(origin, dest).size());
	}

	/* Does a dead end test on the Breadth First pathfinder algorithm
	* Nodes are organized in a square as shown below
	*          d
	*        /
	*      n1
	*     /
	*   o------n2------n3
	*/
	@Test
	public void DeadEndBreadthFirstTest() {
		Directory dir = new Directory();
		ApplicationController.setDirectory(dir);
		Node origin = dir.addNewNode(0, 0, 0, "heyyeyaaeyaaaeyaeyaa", false); //Create a dir.addNewNode
		Node dest = dir.addNewNode(10, 10, 0, "heyyeyaaeyaaaeyaeyaa", false); //Create a dir.addNewNode
		//Create the nodes
		Node n1 = dir.addNewNode(5, 5, 0, "heyyeyaaeyaaaeyaeyaa", false);
		Node n2 = dir.addNewNode(10, 0, 0, "heyyeyaaeyaaaeyaeyaa", false);
		Node n3 = dir.addNewNode(20, 0, 0, "heyyeyaaeyaaaeyaeyaa", false);

		//Link Adjacencies
		dir.connectNodes(origin, n1);
		dir.connectNodes(origin, n2);
		dir.connectNodes(dest, n1);
		dir.connectNodes(n2, n3);

		//System.out.println(dest);
		//System.out.println(Arrays.asList(findPathSafely(origin, dest)));
		Assert.assertEquals(3, findPathSafely(origin, dest).size());
	}

}
