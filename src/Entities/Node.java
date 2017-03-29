package Entities;

/**
 * Created by jtgaulin on 3/29/17.
 */
public class Node {
    private int x;
    private int y;
    private String name;
    private String desc;

    public Node(int x, int y, String name, String desc) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.desc = desc;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
