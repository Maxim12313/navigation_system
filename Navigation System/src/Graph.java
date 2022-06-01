import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

class Graph {
    private HashMap<Integer, Node> nodeList = new HashMap<>();
    private HashMap<Integer, Link> linkList = new HashMap<>();
    private int links;
    private String dataFolder;


    private ArrayList<Node> alteredNodes = new ArrayList<>();

    Graph(String dataFolder) {
        readNodes(dataFolder);
        readLinks(dataFolder);
        readWaypoints(dataFolder);
    }

    public HashMap<Integer, Link> getLinkList() {
        return linkList;
    }

    public HashMap<Integer, Node> getNodeList() {
        return nodeList;
    }

    public void printInfo() {
        System.out.println("######################################################");
        System.out.println("Data: " + dataFolder);
        System.out.println("Nodes: " + nodeList.values().size());
        System.out.println("Links: " + links + " (counting two-way as 2 links: " + linkList.values().size() + ")");
        System.out.println("######################################################");
    }


    void readNodes(String dataFolder) {
        try {
            DataInputStream inStream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataFolder + "/nodes.bin")));
            this.dataFolder = dataFolder;
            int numNodes = inStream.readInt();
            //System.out.println("Nodes: " + numNodes);
            for (int i = 0; i < numNodes; i++) {
                int nodeID = inStream.readInt();
                int x = inStream.readInt();
                int y = inStream.readInt();

                Node node = new Node(nodeID, x, y);
                nodeList.put(nodeID, node);


            }
            //System.out.println("Actual Nodes: " + nodeList.values().size());
            inStream.close();
        } catch (IOException e) {
            System.err.println("error on nodes: " + e.getMessage());
        }
    }


    void readLinks(String dataFolder) {
        try {
            DataInputStream linksStream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataFolder + "/links.bin")));
            int numLinks = linksStream.readInt();
            this.links = numLinks;
            //System.out.println("Links: " + numLinks);
            for (int i = 0; i < numLinks; i++) {
                int linkID = linksStream.readInt();
                int startNodeID = linksStream.readInt();
                int endNodeID = linksStream.readInt();
                String name = linksStream.readUTF();
                double length = linksStream.readDouble();
                byte oneway = linksStream.readByte();


                // TOD: make a link, add it to your data structures
                Link link = new Link(linkID, startNodeID, endNodeID, name, length, oneway);
                linkList.put(linkID, link);
                nodeList.get(startNodeID).getLinkList().put(linkID, link);
                if (oneway == 2) {
                    Link link2 = new Link(-linkID, endNodeID, startNodeID, name, length, oneway);
                    linkList.put(-linkID, link2);
                    nodeList.get(endNodeID).getLinkList().put(-linkID, link2);
                }

            }
            linksStream.close();
        } catch (IOException e) {
            System.err.println("error on links: " + e.getMessage());
        }
    }

    void readWaypoints(String dataFolder) {
        try {
            DataInputStream waypointsStream = new DataInputStream(new BufferedInputStream(new FileInputStream(dataFolder + "/links-waypoints.bin")));

            int numLinks = waypointsStream.readInt();
            for (int i = 0; i < numLinks; i++) {
                int linkID = waypointsStream.readInt();
                int numWaypoints = waypointsStream.readInt();
                Link link = linkList.get(linkID);
                ArrayList<Waypoint> waypointList = link.getWaypointList();
                for (int p = 0; p < numWaypoints; p++) {
                    int x = waypointsStream.readInt();
                    int y = waypointsStream.readInt();
                    Waypoint waypoint = new Waypoint(x, y);
                    waypointList.add(waypoint);
                }
                if (link.getOneway() == 2) {
                    linkList.get(-linkID).setWaypointList(waypointList);
                }
            }
            waypointsStream.close();
        } catch (IOException e) {
            System.err.println("error on waypoints: " + e.getMessage());
        }
    }

    // returns the Node closest to the given X and Y coordinates (OK to be O(N))
    Node findClosestNode(int targetX, int targetY) {
        double closestTotalDist = Double.POSITIVE_INFINITY;
        Node closestNode = null;
        for (Node node : nodeList.values()) {
            double xDist = targetX - node.getX();
            double yDist = targetY - node.getY();
            double nodeTotalDist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
            if (nodeTotalDist < closestTotalDist) {
                closestTotalDist = nodeTotalDist;
                closestNode = node;
            }
        }

        return closestNode;
    }

    // given two nodes, finds the shortest path between then using Dijkstra's Algorithm
    List<Link> findPath(Node startNode, Node endNode) {
        long startTime = System.currentTimeMillis();
        if (!alteredNodes.isEmpty()) {
            for (Node n : alteredNodes) {
                n.setShortestLength(Double.POSITIVE_INFINITY);
                n.setShortestLinkID(0);
            }
        }

        alteredNodes = new ArrayList<>(); //make empty
        alteredNodes.add(startNode);
        alteredNodes.add(endNode);
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.naturalOrder());

        startNode.setShortestLength(0);
        frontier.add(startNode);
        while (frontier.size() > 0) {
            Node node = frontier.poll();
            double midLength = node.getShortestLength();

            if (node.equals(endNode)) {
                System.out.println("Distance in Miles: " + midLength + " miles");
                break;
            }

            for (Link link : node.getLinkList().values()) { //go through links and check each node
                Node neighborNode = nodeList.get(link.getEndNodeID()); //neighbor node
                double totalLength = midLength + link.getLength();
                if (totalLength < neighborNode.getShortestLength()) { //updates and finds shortest length
                    neighborNode.setShortestLength(totalLength);
                    neighborNode.setShortestLinkID(link.getLinkID());
                    frontier.add(neighborNode);
                    alteredNodes.add(neighborNode);
                }
            }
        }
        ArrayList<Link> path = new ArrayList<>();
        while (endNode != startNode) {
            Link link = linkList.get(endNode.getShortestLinkID());
            if (link == null) {
                System.out.println("Error: Unreachable (no links connect the nodes or a node is isolated by oneway links)");
                System.out.println("--------------------------------------------------");
                return null;
            }
            endNode = nodeList.get(link.getStartNodeID());
            path.add(0, link);
        }
        long stopTime = System.currentTimeMillis();
        double duration = (stopTime - startTime) / 1000.0;
        System.out.println("Distance in Nodes: " + path.size());
        System.out.println("Search Time : " + duration + " seconds (only Dijkstra's Algorithm)");
        return path;
    }

}
