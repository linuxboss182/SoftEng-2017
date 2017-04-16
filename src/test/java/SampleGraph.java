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
		this.a = new Node(0, 0, 0);
		this.b = new Node(10, 0, 0);
		this.c = new Node(0, 24, 0);
		this.d = new Node(10, 24, 0);
		this.e = new Node(28, 0, 0);
		this.f = new Node(55, 0, 0);
		this.g = new Node(-15, 0, 0);

		this.a.connect(this.b);
		this.a.connect(this.c);
		this.b.connect(this.a); // no duplicates should occur
		this.b.connect(this.c);
		this.a.connect(this.d);
		this.b.connect(this.d);
		this.a.connect(this.g);
		this.f.connect(this.e);
		this.b.connect(this.e);


		this.nodes = new Node[] {
			new Node(0, 0, 0),
			new Node(0, 9, 0),
			new Node(2.5, 4, 0),
			new Node(3, 17, 0),
			new Node(4, 19, 0),
			new Node(5, 8, 0),
			new Node(5, 13, 0),
			new Node(5, 23, 0),
			new Node(6.3, 21, 0),
			new Node(7, 0, 0),
			new Node(7, 2, 0),
			new Node(7, 4, 0),
			new Node(7, 6, 0),
			new Node(7, 23, 0),
			new Node(8, 3, 0),
			new Node(8, 19, 0),
			new Node(9, 6, 0),
			new Node(10, 9, 0),
			new Node(10, 14.5, 0),
			new Node(11, 12, 0),
			new Node(12, 15, 0),
			new Node(12, 19.5, 0),
			new Node(13, 18, 0),
			new Node(14, 12, 0),
			new Node(14, 18., 05)
		};

		Node[] n = this.nodes;
		n[1].connect(n[3]);
		n[2].connect(n[5]);
		n[2].connect(n[9]);
		n[3].connect(n[4]);
		n[3].connect(n[6]);
		n[4].connect(n[8]);
		n[5].connect(n[6]);
		n[5].connect(n[12]);
		n[5].connect(n[18]);
		n[6].connect(n[15]);
		n[7].connect(n[9]);
		n[8].connect(n[13]);
		n[8].connect(n[15]);
		n[9].connect(n[10]);
		n[9].connect(n[14]);
		n[10].connect(n[11]);
		n[11].connect(n[12]);
		n[13].connect(n[15]);
		n[14].connect(n[16]);
		n[16].connect(n[17]);
		n[17].connect(n[19]);
		n[17].connect(n[23]);
		n[18].connect(n[21]);
		n[18].connect(n[23]);
		n[19].connect(n[20]);
		n[21].connect(n[24]);
		n[22].connect(n[24]);
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
