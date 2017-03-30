import entities.Node;
import org.junit.Test;
import org.junit.Assert;
/** This is a class to test the Node class.
 * It is more of an example than a necessity.
 * Created by Ted on 3/30/2017.
 */

/** Basically what you do is write a function for each test you want to run
 * Once you have all of the tests, you can run it
 */

/** In these examples, I only use assertTrue, but there are others:
 *      Asserts:
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

    @Test
    public void testGetX() {
        Node n = new Node(20, 30, "Forty", "Fifty");
        Assert.assertTrue(n.getX() == 20);
    }

    @Test
    public void testGetY() {
        Node n = new Node(20, 30, "Forty", "Fifty");
        Assert.assertTrue(n.getY() == 30);
    }

    @Test
    public void testGetName() {
        Node n = new Node(20, 30, "Forty", "Fifty");
        Assert.assertTrue(n.getName().equals("Forty"));
    }

    @Test
    public void testGetDesc() {
        Node n = new Node(20, 30, "Forty", "Fifty");
        Assert.assertTrue(n.getDesc().equals("Fifty"));
    }

}
