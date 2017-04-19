import entities.Directory;
import entities.Node;

/**
 * This class creates a sample graph for testing.
 */
// Feel free to modify the coordinates; juts leave the edges in place.
class SampleGraph
{
	Node[] nodes;
	Node a;
	Node b;
	Node c;
	Node d;
	Node e;
	Node f;
	Node g;
	/* (All lines have length 1)
	   -22     0     10    28     55
		g------a------b-----e------f
			   |\    /|
			   | \  / |
			   |  \/  |
			   |  /\  |
			   | /  \ |
			   |/    \|
			24 c      d
	d is at integer distance from all other edges
	 a=26, b=24, c=10, e=30, f=51, g=40
	 */
	SampleGraph() {
		Directory dir = new Directory();
		this.a = dir.addNewNode(0, 0, 0);
		this.b = dir.addNewNode(10, 0, 0);
		this.c = dir.addNewNode(0, 24, 0);
		this.d = dir.addNewNode(10, 24, 0);
		this.e = dir.addNewNode(28, 0, 0);
		this.f = dir.addNewNode(55, 0, 0);
		this.g = dir.addNewNode(-15, 0, 0);

		dir.connectNodes(this.a, this.b);
		dir.connectNodes(this.a, this.c);
		dir.connectNodes(this.b, this.a); // no duplicates should occur
		dir.connectNodes(this.b, this.c);
		dir.connectNodes(this.a, this.d);
		dir.connectNodes(this.b, this.d);
		dir.connectNodes(this.a, this.g);
		dir.connectNodes(this.f, this.e);
		dir.connectNodes(this.b, this.e);


		this.nodes = new Node[] {
			dir.addNewNode(0, 0, 0),
			dir.addNewNode(0, 9, 0),
			dir.addNewNode(2.5, 4, 0),
			dir.addNewNode(3, 17, 0),
			dir.addNewNode(4, 19, 0),
			dir.addNewNode(5, 8, 0),
			dir.addNewNode(5, 13, 0),
			dir.addNewNode(5, 23, 0),
			dir.addNewNode(6.3, 21, 0),
			dir.addNewNode(7, 0, 0),
			dir.addNewNode(7, 2, 0),
			dir.addNewNode(7, 4, 0),
			dir.addNewNode(7, 6, 0),
			dir.addNewNode(7, 23, 0),
			dir.addNewNode(8, 3, 0),
			dir.addNewNode(8, 19, 0),
			dir.addNewNode(9, 6, 0),
			dir.addNewNode(10, 9, 0),
			dir.addNewNode(10, 14.5, 0),
			dir.addNewNode(11, 12, 0),
			dir.addNewNode(12, 15, 0),
			dir.addNewNode(12, 19.5, 0),
			dir.addNewNode(13, 18, 0),
			dir.addNewNode(14, 12, 0),
			dir.addNewNode(14, 18., 0)
		};

		Node[] n = this.nodes;
		dir.connectNodes(n[1], n[3]);
		dir.connectNodes(n[2], n[5]);
		dir.connectNodes(n[2], n[9]);
		dir.connectNodes(n[3], n[4]);
		dir.connectNodes(n[3], n[6]);
		dir.connectNodes(n[4], n[8]);
		dir.connectNodes(n[5], n[6]);
		dir.connectNodes(n[5], n[12]);
		dir.connectNodes(n[5], n[18]);
		dir.connectNodes(n[6], n[15]);
		dir.connectNodes(n[7], n[9]);
		dir.connectNodes(n[8], n[13]);
		dir.connectNodes(n[8], n[15]);
		dir.connectNodes(n[9], n[10]);
		dir.connectNodes(n[9], n[14]);
		dir.connectNodes(n[10], n[11]);
		dir.connectNodes(n[11], n[12]);
		dir.connectNodes(n[13], n[15]);
		dir.connectNodes(n[14], n[16]);
		dir.connectNodes(n[16], n[17]);
		dir.connectNodes(n[17], n[19]);
		dir.connectNodes(n[17], n[23]);
		dir.connectNodes(n[18], n[21]);
		dir.connectNodes(n[18], n[23]);
		dir.connectNodes(n[19], n[20]);
		dir.connectNodes(n[21], n[24]);
		dir.connectNodes(n[22], n[24]);
	}

	/*
	Paths of potential interest:
	n[9] -> n[13]: 9, 10, 11, 12, 5, 6, 15, 13
	n[9] -> n[22]: 9, 10, 11, 12, 5, 18, 21, 24, 22
	n[19] -> n[22]: 19, 17, 23, 18, 21, 24, 22
	n[1] -> n[13]: 1, 3, 4, 8, 13
	n[7] -> n[23]: 7, 9, 14, 16, 17, 23
	 */
}
