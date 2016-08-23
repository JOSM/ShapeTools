// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.ChangeCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WaySegment;
/**
 * Different utilitarian functions
 * @author Adrian_antochi
 */
public final class ShapeMath {

    private ShapeMath() {
        // Hide default constructor for utilities classes
    }

    /**
     * @return closest segment contained in the way, regarding the segment
     */
    public static WaySegment getClosestSegment(Way fromThisway, WaySegment regardingThisSegment) {
        double maxDistance = Double.MAX_VALUE;
        WaySegment closestSegment = null;
        for (int i = 0; i < fromThisway.getNodesCount() - 1; i++) {

            WaySegment currentSegment = new WaySegment(fromThisway, i);
            EastNorth currentSegmentCentroid = getCentroid(currentSegment);
            Point p = new Point(currentSegmentCentroid.getX(), currentSegmentCentroid.getY());
            Point x1 = new Point(regardingThisSegment.getFirstNode().getEastNorth().getX(),
                                 regardingThisSegment.getFirstNode().getEastNorth().getY());
            Point x2 = new Point(regardingThisSegment.getSecondNode().getEastNorth().getX(),
                                 regardingThisSegment.getSecondNode().getEastNorth().getY());
            double distance = Point.distance(x1, x2, p);

            if (distance < maxDistance) {
                maxDistance = distance;
                closestSegment = new WaySegment(fromThisway, i);
            }
        }
        return closestSegment;
    }

    /**
     * The same as getClosestSegment(), only that it's limited by an epsilon
     * @return closest segment contained in the way,
     *         regarding the segment if the distance between the two is smaller than epsilon, otherwise returns null
     */
    public static WaySegment getClosestSegmentUsingEpsilon(Way fromThisway, WaySegment regardingThisSegment, double epsilon) {
        double maxDistance = Double.MAX_VALUE;
        WaySegment closestSegment = null;
        System.out.println("input epsilon: " + epsilon);
        for (int i = 0; i < fromThisway.getNodesCount() - 1; i++) {

            WaySegment currentSegment = new WaySegment(fromThisway, i);
            EastNorth currentSegmentCentroid = getCentroid(currentSegment);
            Point p = new Point(currentSegmentCentroid.getX(), currentSegmentCentroid.getY());
            Point x1 = new Point(regardingThisSegment.getFirstNode().getEastNorth().getX(),
                                 regardingThisSegment.getFirstNode().getEastNorth().getY());
            Point x2 = new Point(regardingThisSegment.getSecondNode().getEastNorth().getX(),
                                 regardingThisSegment.getSecondNode().getEastNorth().getY());
            double distance = Point.distance(x1, x2, p);
            System.out.println("Calculated distance: " + distance);
            if (distance < maxDistance && distance < epsilon) {
                maxDistance = distance;
                closestSegment = new WaySegment(fromThisway, i);
            }
        }
        System.out.println("closest segment using epsilon returned: " + closestSegment);
        return closestSegment;
    }

    /**
     * Command for rotating a certain node regarding a center point, by a specified angle
     * @param node to be roated
     * @param angle in degrees
     * @param center EastNorth coordinates of the center-point
     * @return a ChangeCommand used for rotating of the node
     */
    private static Command rotate(Node node, double angle, EastNorth center) {
        Node newNode = new Node(node);
        EastNorth oldCoor = node.getEastNorth();
        newNode.setEastNorth(getRotation(oldCoor, angle, center));
        return new ChangeCommand(node, newNode);
    }

    /**
     * Command for rotating a way regarding a center point, by a specified angle
     * @param way to be rotated
     * @param angle in degrees
     * @param center EastNort coordinates of the center-point
     * @return a Collection of Commands
     */
    private static Collection<Command> rotate(Way way, double angle, EastNorth center) {
        Set<Node> nodesSet = new HashSet<>();
        List<Node> wayNodes = way.getNodes();
        for (Node node : wayNodes) {
            nodesSet.add(node);
        }
        EastNorth allNodesCenter = ShapeMath.getCentroid(nodesSet);
        Iterator<Node> it = nodesSet.iterator();
        Collection<Command> totalCommands = new ArrayList<>();
        while (it.hasNext()) {
            totalCommands.add(rotate(it.next(), angle, allNodesCenter));
        }
        return totalCommands;
    }

    /**
     * @return EastNorth coordinates after rotation is applied
     */
    public static EastNorth getRotation(EastNorth originalPoint, double angle, EastNorth center) {
        double x = Math.cos(angle) * (originalPoint.getX() - center.getX())
                 - Math.sin(angle) * (originalPoint.getY() - center.getY()) + center.getX();
        double y = Math.sin(angle) * (originalPoint.getX() - center.getX())
                 + Math.cos(angle) * (originalPoint.getY() - center.getY()) + center.getY();
        return new EastNorth(x, y);
    }

    /**
     * @return the center-point of a way, calculated by taking the averange of all east and north coordinates of a way
     */
    public static EastNorth getCentroid(Way way) {
        double x = 0, y = 0;
        List<Node> wayNodes = way.getNodes();

        if (way.isClosed()) {
            for (int i = 0; i < wayNodes.size() - 1; i++) {
                x += wayNodes.get(i).getEastNorth().getX();
                y += wayNodes.get(i).getEastNorth().getY();
            }
            x = x / (way.getNodesCount() - 1);
            y = y / (way.getNodesCount() - 1);
            return new EastNorth(x, y);
        }

        for (int i = 0; i < wayNodes.size(); i++) {
            x += wayNodes.get(i).getEastNorth().getX();
            y += wayNodes.get(i).getEastNorth().getY();
        }
        x = x / way.getNodesCount();
        y = y / way.getNodesCount();

        return new EastNorth(x, y);
    }

    /**
     * @return the center-point of a way, calculated by taking the averange of all east and north coordinates of a way
     */
    public static EastNorth getCentroid(WaySegment segment) {
        double x = 0, y = 0;

        x = x + segment.getFirstNode().getEastNorth().getX() + segment.getSecondNode().getEastNorth().getX();
        y = y + segment.getFirstNode().getEastNorth().getY() + segment.getSecondNode().getEastNorth().getY();

        x = x / 2.0;
        y = y / 2.0;

        return new EastNorth(x, y);
    }

    /**
     * @return center-point of a list of ways
     */
    public static EastNorth getCentroid(List<Way> wayList) {
        double x = 0, y = 0;
        for (int i = 0; i < wayList.size(); i++) {
            EastNorth currentCenter = getCentroid(wayList.get(i));
            x += currentCenter.getX();
            y += currentCenter.getY();
        }
        x = x / wayList.size();
        y = y / wayList.size();
        return new EastNorth(x, y);
    }

    /**
     * @return center-point of a set of nodes
     */
    public static EastNorth getCentroid(Set<Node> nodes) {
        Iterator<Node> i = nodes.iterator();
        double x = 0, y = 0;
        while (i.hasNext()) {
            Node currentNode = i.next();
            x += currentNode.getEastNorth().getX();
            y += currentNode.getEastNorth().getY();
        }
        x = x / nodes.size();
        y = y / nodes.size();
        return new EastNorth(x, y);
    }

    /**
     * Performs rotation of a collection of nodes and ways, regarding their node's center and an angle
     * @param angle in degrees
     */
    public static void doRotate(Collection<Way> ways, Collection<Node> nodes, double angle) {
        SequenceCommand commands = new SequenceCommand("rotateCommand", avoidDuplicateNodesRotation(ways, nodes, angle));
        Main.main.undoRedo.add(commands);
        Main.map.repaint();
    }

    private static Collection<Command> avoidDuplicateNodesRotation(Collection<Way> ways, Collection<Node> nodes, double angle) {
        System.out.println("doRotate() called: rotating shapes by: " + angle);
        Set<Node> nodesSet = new HashSet<>();
        for (Way way : ways) {
            List<Node> wayNodes = way.getNodes();
            for (Node node : wayNodes) {
                nodesSet.add(node);
            }
        }
        for (Node node : nodes) {
            nodesSet.add(node);
        }
        EastNorth allNodesCenter = ShapeMath.getCentroid(nodesSet);
        Iterator<Node> i = nodesSet.iterator();
        Collection<Command> commands = new ArrayList<>();
        while (i.hasNext()) {
            commands.add(rotate(i.next(), angle, allNodesCenter));
        }
        return commands;
    }

    /**
     * Aligns second way to the first one
     */
    public static void align(Way firstWay, Way secondWay) {
        double x1 = firstWay.getNode(0).getEastNorth().getX();
        double x2 = firstWay.getNode(1).getEastNorth().getX();
        double x3 = secondWay.getNode(0).getEastNorth().getX();
        double x4 = secondWay.getNode(1).getEastNorth().getX();
        double y1 = firstWay.getNode(0).getEastNorth().getY();
        double y2 = firstWay.getNode(1).getEastNorth().getY();
        double y3 = secondWay.getNode(0).getEastNorth().getY();
        double y4 = secondWay.getNode(1).getEastNorth().getY();
        double requiredAngle = Math.atan2(y2 - y1, x2 - x1) - Math.atan2(y4 - y3, x4 - x3);
        System.out.println("Angle calculated from align() " + requiredAngle);
        SequenceCommand commands = new SequenceCommand("alignCommand", rotate(secondWay, requiredAngle, getCentroid(secondWay)));
        Main.main.undoRedo.add(commands);
        Main.map.repaint();
    }

    /**
     * Aligns a way to a road, by using their segments
     * @param roadSegment segment of the road
     * @param toRotateSegment segment(wall) of the building that needs to be rotated
     */
    public static void align(WaySegment roadSegment, WaySegment toRotateSegment) {
        double x1 = roadSegment.getFirstNode().getEastNorth().getX();
        double x2 = roadSegment.getSecondNode().getEastNorth().getX();
        double x3 = toRotateSegment.getFirstNode().getEastNorth().getX();
        double x4 = toRotateSegment.getSecondNode().getEastNorth().getX();
        double y1 = roadSegment.getFirstNode().getEastNorth().getY();
        double y2 = roadSegment.getSecondNode().getEastNorth().getY();
        double y3 = toRotateSegment.getFirstNode().getEastNorth().getY();
        double y4 = toRotateSegment.getSecondNode().getEastNorth().getY();

        double requiredAngle = Math.atan2(y2 - y1, x2 - x1) - Math.atan2(y4 - y3, x4 - x3);
        System.out.println("Angle calculated from align() " + requiredAngle);

        requiredAngle = normalise(requiredAngle);
        SequenceCommand commands = new SequenceCommand("AlignCommand",
                rotate(toRotateSegment.way, requiredAngle, getCentroid(toRotateSegment.way)));
        Main.main.undoRedo.add(commands);
        Main.map.repaint();
    }

    /**
     * Aligns a way to a segment, mostly used to align a building to a road segment
     */
    public static void align(WaySegment roadSegment, Way building) {
        WaySegment closestSegment = ShapeMath.getClosestSegment(building, roadSegment);
        double x1 = roadSegment.getFirstNode().getEastNorth().getX();
        double x2 = roadSegment.getSecondNode().getEastNorth().getX();
        double x3 = closestSegment.getFirstNode().getEastNorth().getX();
        double x4 = closestSegment.getSecondNode().getEastNorth().getX();
        double y1 = roadSegment.getFirstNode().getEastNorth().getY();
        double y2 = roadSegment.getSecondNode().getEastNorth().getY();
        double y3 = closestSegment.getFirstNode().getEastNorth().getY();
        double y4 = closestSegment.getSecondNode().getEastNorth().getY();

        double requiredAngle = Math.atan2(y2 - y1, x2 - x1) - Math.atan2(y4 - y3, x4 - x3);
        System.out.println("Angle calculated from align() " + requiredAngle);

        requiredAngle = normalise(requiredAngle);

        SequenceCommand commands = new SequenceCommand("aligningBuilding", rotate(building, requiredAngle, getCentroid(building)));
        Main.main.undoRedo.add(commands);
        Main.map.repaint();
    }

    /**
     * Aligns the building only if the distance between the closest wall of the building and it's closest road-segment is smaller than epsilon
     */
    public static void alignUsingEpsilon(WaySegment roadSegment, Way building, double epsilon) {
        WaySegment closestSegment = ShapeMath.getClosestSegmentUsingEpsilon(building, roadSegment, epsilon);
        if (closestSegment != null) {
            double x1 = roadSegment.getFirstNode().getEastNorth().getX();
            double x2 = roadSegment.getSecondNode().getEastNorth().getX();
            double x3 = closestSegment.getFirstNode().getEastNorth().getX();
            double x4 = closestSegment.getSecondNode().getEastNorth().getX();
            double y1 = roadSegment.getFirstNode().getEastNorth().getY();
            double y2 = roadSegment.getSecondNode().getEastNorth().getY();
            double y3 = closestSegment.getFirstNode().getEastNorth().getY();
            double y4 = closestSegment.getSecondNode().getEastNorth().getY();

            double requiredAngle = Math.atan2(y2 - y1, x2 - x1) - Math.atan2(y4 - y3, x4 - x3);
            System.out.println("Angle calculated from align() " + requiredAngle);

            requiredAngle = normalise(requiredAngle);

            SequenceCommand commands = new SequenceCommand("aligningUsingEpislon", rotate(building, requiredAngle, getCentroid(building)));
            Main.main.undoRedo.add(commands);
            Main.map.repaint();
        } else {
            System.out.println("NOPE, EPSILON TOO SMALL: " + epsilon);
        }
    }

    /**
     * Used internally to normalise an angle, if it's greater than Math.PI
     * @return the normalized angle
     */
    public static double normalise(double a) {
        while (a > Math.PI) {
            a -= 2 * Math.PI;
        }
        while (a <= -Math.PI) {
            a += 2 * Math.PI;
        }
        if (a > Math.PI / 2) {
            a -= Math.PI;
        } else if (a < -Math.PI / 2) {
            a += Math.PI;
        }
        return a;
    }
}
