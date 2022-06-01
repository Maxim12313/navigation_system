import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class ViewCanvas extends Canvas implements MouseListener, MouseMotionListener {
    private Graph graph;
    private Node startNode = null, endNode = null;
    private java.util.List<Link> path = null;
    private boolean clickSetsStart = true;
    private ArrayList<Link> locatedLinks = new ArrayList<>();
    private boolean first = true;

    ViewCanvas(Graph graph, int width, int height) {
        this.graph = graph;
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(new CanvasComponentListener());


        setBackground(Color.WHITE);
    }

    public ArrayList<Link> getLocatedLinks() {
        return locatedLinks;
    }

    public Graph getGraph() {
        return graph;
    }

    @Override
    public void paint(Graphics pen) {
        Graphics2D linkPen = (Graphics2D) pen.create();
        Graphics2D nodePen = (Graphics2D) pen.create();
        linkPen.setColor(Color.DARK_GRAY);
        //nodePen.setColor(Color.DARK_GRAY);

        int nodeThickness = 4; //set thickness here
        //pen2.setStroke(new BasicStroke(lineThickness));

        //HashMap<Integer, Node> nodeList = graph.getNodeList();
        for (Link link : graph.getLinkList().values()) {
            drawLinks(link, linkPen);

            //draw nodes

//            int startID = link.getStartNodeID();
//            int endID = link.getEndNodeID();
//            if (startID != 0) {
//                Node node1 = nodeList.get(startID);
//                nodePen.fillOval(node1.getX() - nodeThickness / 2, node1.getY() - nodeThickness / 2, nodeThickness, nodeThickness);
//            }
//            if (endID != 0) {
//                Node node2 = nodeList.get(endID);
//                nodePen.fillOval(node2.getX() - nodeThickness / 2, node2.getY() - nodeThickness / 2, nodeThickness, nodeThickness);
//            }
        }

        if (!locatedLinks.isEmpty()) {
            linkPen.setColor(Color.BLUE);
            linkPen.setStroke(new BasicStroke(3));
            for (Link link : locatedLinks) {
                drawLinks(link, linkPen);
            }
        }


        if (path != null) {
            linkPen.setColor(Color.ORANGE);
            linkPen.setStroke(new BasicStroke(2));
            System.out.println("Directions from red to green: ");
            Node prevNode = startNode;
            for (Link link : path) {
                drawLinks(link, linkPen);
                Double angle;
                Node thisNode = graph.getNodeList().get(link.getEndNodeID());

                if (prevNode != null) {
                    double xChange = thisNode.getX() - prevNode.getX();
                    double yChange = -(thisNode.getY() - prevNode.getY());
                    angle = Math.toDegrees(Math.atan(yChange / xChange));

                    if (xChange == 0) {
                        if (yChange > 0) {
                            angle = 90.0;
                        } else if (yChange < 0) {
                            angle = 270.0;
                        }
                    } else if (yChange == 0) {
                        if (xChange > 0) {
                            angle = 0.0;
                        } else if (xChange < 0) {
                            angle = 180.0;
                        }
                    } else if (xChange < 0) {
                        if (yChange < 0) {
                            angle += 180;
                        } else {
                            angle = 180 - angle;
                        }
                    } else if (yChange < 0) {
                        angle += 360;
                    }
                    //System.out.println("Angle:" + angle);
                    giveDirection(angle);
                }
                prevNode = thisNode;
            }
            System.out.println("");
            System.out.println("--------------------------------------------------");
        }


        nodeThickness = 8;
        if (startNode != null) {
            nodePen.setColor(Color.RED);
            //System.out.println("Start node (x,y) = (" + startNode.getX() + "," + startNode.getY() + ")");
            nodePen.fillOval(startNode.getX() - nodeThickness / 2, startNode.getY() - nodeThickness / 2, nodeThickness, nodeThickness);
        }
        if (endNode != null) {
            nodePen.setColor(Color.GREEN);
            //System.out.println("End node (x,y) = (" + endNode.getX() + "," + endNode.getY() + ")");
            nodePen.fillOval(endNode.getX() - nodeThickness / 2, endNode.getY() - nodeThickness / 2, nodeThickness, nodeThickness);
        }
    }


    private void giveDirection(double angle) {

        String direction = "";
        double margin = 20;
        if (angle > 0 + margin && angle < 180 - margin) {
            direction += ("N");
        } else if (angle > 180 + margin && angle < 360 - margin) {
            direction += ("S");
        }
        if (angle > 270 + margin || angle < 90 - margin) {
            direction += ("E");
        } else if (angle > 90 + margin && angle < 270 - margin) {
            direction += ("W");
        }
        if (direction.length() > 0) { //for detailed maps where some nodes have same coordinates and no direction
            System.out.print(direction + ", ");
        }

    }


    public void drawLinks(Link link, Graphics2D pen) {
        int prevX = 0;
        int prevY = 0;
        for (Waypoint waypoint : link.getWaypointList()) {

            int x = waypoint.x;
            int y = waypoint.y;
            //won't draw the first xy, but that won't matter because always more than 1 waypoint
            if (prevX != 0 && prevY != 0) {
                pen.drawLine(prevX, prevY, x, y);
            }
            prevX = x;
            prevY = y;
        }
    }

    private void printNodeInfo(Node node) {
        String intersects = "";
        intersects += node.getNodeID() + " - ";
        for (Link road : node.getLinkList().values()) {
            intersects += road.getName() + ", ";
        }
        System.out.println(intersects);
    }

    public void mousePressed(MouseEvent event) {

        int mouseX = event.getX();
        int mouseY = event.getY();
        //System.out.println("Actual: (" + mouseX + "," + mouseY + ")");
        Node node = graph.findClosestNode(mouseX, mouseY);


        if (clickSetsStart) {
            startNode = node;
            clickSetsStart = false;
            System.out.print("Start Red: ");
            printNodeInfo(startNode);
        } else {
            endNode = node;
            clickSetsStart = true;
            System.out.print("End Green: ");
            printNodeInfo(endNode);


        }
        if (startNode != null && endNode != null) {

            locatedLinks = new ArrayList<>();
            if (clickSetsStart && !first) {
                System.out.print("Start Red: ");
                printNodeInfo(startNode);
            } else if (!clickSetsStart) {
                System.out.print("End Green: ");
                printNodeInfo(endNode);
            }
            first = false;
            path = graph.findPath(startNode, endNode);

        }
        repaint();
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseDragged(MouseEvent event) {
    }

    public void mouseMoved(MouseEvent event) {
    }


    class CanvasComponentListener extends ComponentAdapter {
        public void componentResized(ComponentEvent event) {
            resized();
        }

    }

    public void resized() {
    }


}
