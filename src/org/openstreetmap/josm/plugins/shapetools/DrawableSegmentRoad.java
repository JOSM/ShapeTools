// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import java.awt.Color;

import org.openstreetmap.josm.data.osm.WaySegment;
/**
 * Adding this as a temporary layer on mapview will color the selected segment yellow
 * @author Adrian_antochi
 *
 */
public class DrawableSegmentRoad extends DrawableSegment {

    public DrawableSegmentRoad(WaySegment segment) {
        super(segment);
        this.color = Color.YELLOW;
    }

}
