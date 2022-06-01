import java.util.ArrayList;

class Link {
    // TOD: Declare fields to store a Link
    private ArrayList<Waypoint> waypointList = new ArrayList<>();
    private int linkID;
    private int startNodeID;
    private int endNodeID;
    private String name;
    private double length;
    private byte oneway;

    Link(int linkID1, int startNodeID1, int endNodeID1, String name1, double length1, byte oneway1) {
        // TOD: Write the Link constructor
        startNodeID = startNodeID1;
        endNodeID = endNodeID1;
        name = name1;
        length = length1;
        linkID = linkID1;
        oneway = oneway1;


    }

    public byte getOneway() {
        return oneway;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public int getLinkID() {
        return linkID;
    }

    public int getStartNodeID() {
        return startNodeID;
    }

    public int getEndNodeID() {
        return endNodeID;
    }

    public ArrayList<Waypoint> getWaypointList() {
        return waypointList;
    }

    public void setWaypointList(ArrayList<Waypoint> list) {
        waypointList = list;
    }

}
