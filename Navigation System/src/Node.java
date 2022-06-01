import java.util.HashMap;

class Node implements Comparable<Node> {
    private int x;
    private int y;
    private int nodeID;
    private HashMap<Integer, Link> linkList = new HashMap<>();
    private double shortestLength = Double.POSITIVE_INFINITY;
    private int shortestLinkID = 0;


    public Node(int id, int x1, int y1) {
        x = x1;
        y = y1;
        nodeID = id;
        // TOD: Write the Node constructor
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getNodeID() {
        return nodeID;
    }

    public HashMap<Integer, Link> getLinkList() {
        return linkList;
    }

    public int compareTo(Node other) {
        if (shortestLength < other.getShortestLength()) {
            return -1;
        }
        if (shortestLength > other.getShortestLength()) {
            return 1;
        } else {
            return 0;
        }


        // TOD: Write the Node compareTo function
    }

    public double getShortestLength() {
        return shortestLength;
    }

    public int getShortestLinkID() {
        return shortestLinkID;
    }

    public void setShortestLength(double length) {
        shortestLength = length;
    }

    public void setShortestLinkID(int linkID) {
        shortestLinkID = linkID;
    }

}

