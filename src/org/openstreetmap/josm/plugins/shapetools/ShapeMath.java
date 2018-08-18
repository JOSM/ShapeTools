// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.command.ChangeCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WaySegment;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.tools.Geometry;
import org.openstreetmap.josm.tools.Logging;

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
            EastNorth en1 = regardingThisSegment.getFirstNode().getEastNorth();
            Point x1 = new Point(en1.getX(), en1.getY());
            EastNorth en2 = regardingThisSegment.getSecondNode().getEastNorth();
            Point x2 = new Point(en2.getX(), en2.getY());
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

        for (int i = 0; i < fromThisway.getNodesCount() - 1; i++) {

            WaySegment currentSegment = new WaySegment(fromThisway, i);
            EastNorth currentSegmentCentroid = getCentroid(currentSegment);
            Point p = new Point(currentSegmentCentroid.getX(), currentSegmentCentroid.getY());
            EastNorth en1 = regardingThisSegment.getFirstNode().getEastNorth();
            Point x1 = new Point(en1.getX(), en1.getY());
            EastNorth en2 = regardingThisSegment.getSecondNode().getEastNorth();
            Point x2 = new Point(en2.getX(), en2.getY());
            double distance = Point.distance(x1, x2, p);

            if (distance < maxDistance && distance < epsilon) {
                maxDistance = distance;
                closestSegment = new WaySegment(fromThisway, i);
            }
        }
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
        EastNorth allNodesCenter = getCentroid(nodesSet);
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

    private static EastNorth getCentroid(List<Node> wayNodes) {
        EastNorth center = Geometry.getCenter(wayNodes);
        return center != null ? center : Geometry.getCentroid(wayNodes);
    }

    /**
     * @return the center-point of a way, calculated by taking the average of all east and north coordinates of a way
     */
    public static EastNorth getCentroid(Way way) {
        return getCentroid(way.getNodes());
    }

    /**
     * @return the center-point of a way, calculated by taking the averange of all east and north coordinates of a way
     */
    public static EastNorth getCentroid(WaySegment segment) {
        return getCentroid(Arrays.asList(segment.getFirstNode(), segment.getSecondNode()));
    }

    /**
     * @return center-point of a set of nodes
     */
    public static EastNorth getCentroid(Set<Node> nodes) {
        return getCentroid(new ArrayList<>(nodes));
    }

    /**
     * Performs rotation of a collection of nodes and ways, regarding their node's center and an angle
     * @param angle in degrees
     */
    public static void doRotate(Collection<Way> ways, Collection<Node> nodes, double angle) {
        commitCommands("rotateCommand", avoidDuplicateNodesRotation(ways, nodes, angle));
    }

    private static Collection<Command> avoidDuplicateNodesRotation(Collection<Way> ways, Collection<Node> nodes, double angle) {
        Logging.info("rotating shapes by: " + angle);
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
        EastNorth allNodesCenter = getCentroid(nodesSet);
        Iterator<Node> i = nodesSet.iterator();
        Collection<Command> commands = new ArrayList<>();
        while (i.hasNext()) {
            commands.add(rotate(i.next(), angle, allNodesCenter));
        }
        return commands;
    }

    private static double computeAngle(Node n1, Node n2, Node n3, Node n4) {
        EastNorth en1 = n1.getEastNorth();
        EastNorth en2 = n2.getEastNorth();
        EastNorth en3 = n3.getEastNorth();
        EastNorth en4 = n4.getEastNorth();
        double x1 = en1.getX();
        double x2 = en2.getX();
        double x3 = en3.getX();
        double x4 = en4.getX();
        double y1 = en1.getY();
        double y2 = en2.getY();
        double y3 = en3.getY();
        double y4 = en4.getY();
        return Math.atan2(y2 - y1, x2 - x1) - Math.atan2(y4 - y3, x4 - x3);
    }

    /**
     * Aligns second way to the first one
     */
    public static void align(Way firstWay, Way secondWay) {
        double requiredAngle = computeAngle(
                firstWay.getNode(0), firstWay.getNode(1),
                secondWay.getNode(0), secondWay.getNode(1));
        commitCommands("alignCommand", rotate(secondWay, requiredAngle, getCentroid(secondWay)));
    }

    /**
     * Aligns a way to a road, by using their segments
     * @param roadSegment segment of the road
     * @param toRotateSegment segment(wall) of the building that needs to be rotated
     */
    public static void align(WaySegment roadSegment, WaySegment toRotateSegment) {
        double requiredAngle = normalise(computeAngle(
                roadSegment.getFirstNode(), roadSegment.getSecondNode(),
                toRotateSegment.getFirstNode(), toRotateSegment.getSecondNode()));
        commitCommands("AlignCommand", rotate(toRotateSegment.way, requiredAngle, getCentroid(toRotateSegment.way)));
    }

    /**
     * Aligns the building only if the distance between the closest wall of the building and it's closest road-segment is smaller than epsilon
     */
    public static void alignUsingEpsilon(WaySegment roadSegment, Way building, double epsilon) {
        WaySegment closestSegment = getClosestSegmentUsingEpsilon(building, roadSegment, epsilon);
        if (closestSegment != null) {
            double requiredAngle = normalise(computeAngle(
                    roadSegment.getFirstNode(), roadSegment.getSecondNode(),
                    closestSegment.getFirstNode(), closestSegment.getSecondNode()));
            commitCommands("aligningUsingEpsilon", rotate(building, requiredAngle, getCentroid(building)));
        } else {
            Logging.warn("NOPE, EPSILON TOO SMALL: " + epsilon);
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

    private static void commitCommands(String sequenceName, Collection<Command> commands) {
        if (!commands.isEmpty()) {
            UndoRedoHandler.getInstance().add(new SequenceCommand(sequenceName, commands));
            MainApplication.getMap().repaint(); // FIXME avoid complete repaint
        }
    }
}
