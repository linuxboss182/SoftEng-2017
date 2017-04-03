import entities.Node;

/**
 * This class creates a sample graph for testing.
 */
// Feel free to modify the coordinates; juts leave the edges in place.
class SampleGraph
{
	Node a = new Node(0, 0);
	Node b = new Node(10, 0);
	Node c = new Node(0, 24);
	Node d = new Node(10, 24);
	Node e = new Node(28, 0);
	Node f = new Node(55, 0);
	Node g = new Node(-15, 0);
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
	public SampleGraph() {
		this.a.connect(this.b);
		this.a.connect(this.c);
		this.b.connect(this.a); // no duplicates should occur
		this.b.connect(this.c);
		this.a.connect(this.d);
		this.b.connect(this.d);
		this.a.connect(this.g);
		this.f.connect(this.e);
		this.b.connect(this.e);
	}
}
