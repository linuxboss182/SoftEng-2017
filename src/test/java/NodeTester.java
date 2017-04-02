import entities.Node;
import org.junit.Test;
import org.junit.Assert;
/**
 * This is a class to test the Node class.
 * It is more of an example than a necessity.
 * Created by Ted on 3/30/2017.
 *
 * Basically what you do is write a function for each test you want to run
 * Once you have all of the tests, you can run it

 In these examples, I only use assertTrue, but there are others:
 *      Asserts: Note:: Each of these methods also can take in a String as their first parameter as a fail message. Ex: fail() can be called as fail("") for the same result, or you could do fail("I don't want this to succeed")
 *      assertTrue(boolean) -- asserts that a condition is true
 *      assertFalse(boolean) -- asserts that a condition is false
 *      fail() -- fails a test with no message
 *      assertEquals(Object obj1, Object obj2) -- asserts that two objects are equal
 *      assertNotEquals(Object expected, Object actual) -- asserts that two objects are not equal
 *      assertArrayEquals(Object[] expecteds, Object[] actuals) -- asserts that the two arrays are equal
 *  there are more for LITERALLY EVERY TYPE....
 *  floats and doubles also have a delta parameter(it's a float or a double) that controls the accuracy of the comparison
 *      assertNull(Object actual) -- asserts that an Object is null
 *      assertNotNull(Object atual) -- asserts that an Object is not null
 *      assertSame(Object obj1, Object obj2) -- asserts that two objects refer to the same object
 *      assertNotSame(Object obj1, Object obj2) -- opposite of above
 *
 * These can all be found in
 * External Libraries: Gradle:junitLjunit:4.1/junit-4.12.jar/org.junit/Assert.java
 */

public class NodeTester {

    /* These two tests be needed later.
    @Test
    public void testGetX() {
        Node n = new Node(20, 30);
        Assert.assertTrue(n.getX() == 20);
    }

    @Test
    public void testGetY() {
        Node n = new Node(20, 30);
        Assert.assertTrue(n.getY() == 30);
    }
    */

    @Test
    public void testAngleZero() {
        Node a = new Node(0, 0);
        Node b = new Node(0, 1);
        Node c = new Node(1, 1);
        double delta = 0.001;
        Assert.assertEquals((double) 0 , b.angle(a, c), delta);
    }

    @Test
    public void testAngleOneEighty() {
        Node a = new Node(0, 0);
        Node b = new Node(1, 0);
        Node c = new Node(1, 1);
        double delta = 0.001;
        Assert.assertEquals((double) 180 , b.angle(a, c), delta);
    }

    @Test
    public void testAngleNinety() {
        Node a = new Node(0, 0);
        Node b = new Node(0, 1);
        Node c = new Node(0, 2);
        double delta = 0.001;
        Assert.assertEquals((double) 90 , b.angle(a, c), delta);
    }

    @Test
    public void testAngleTwoSeventy() {
        Node a = new Node(0, 0);
        Node b = new Node(0, 1);
        Node c = new Node(0, 0);
        double delta = 0.001;
        Assert.assertEquals((double) 270 , b.angle(a, c), delta);
    }

    @Test
    public void testAngleSlightRight() {
        Node a = new Node(0, 0);
        Node b = new Node(0, 1);
        Node c = new Node(1, 2);
        double delta = 0.001;
        Assert.assertEquals((double) 45 , b.angle(a, c), delta);
    }
    @Test
    public void testAngleOddCase() {
        // fixed the code from this one
        // turns out java does modulo operations before addition
        // sooo a + 450 % 360 is equivalent to a + 90;
        Node a = new Node(0, 0);
        Node b = new Node(1, -1);
        Node c = new Node(1, 1);
        double delta = 0.001;
        Assert.assertEquals((double) 225, b.angle(a, c), delta);
    }

    @Test
    public void testAngleOppositeOddCase() {
        // fixed the code from this one
        // turns out java does modulo operations before addition
        // sooo a + 450 % 360 is equivalent to a + 90;
        Node a = new Node(0, 0);
        Node b = new Node(1, -1);
        Node c = new Node(1, 1);
        double delta = 0.001;
        Assert.assertEquals((double) 315, c.angle(a, b), delta);
    }

    @Test
    public void testAngleNoMove() {
        Node a = new Node(0,0);
        double delta = 0.001;
        Assert.assertEquals((double) Double.NaN, a.angle(a, a), delta);
    }
    // I think it's safe to say that Node.angle works
    // The logic is sound and the code has been fixed for the odd cases

}
