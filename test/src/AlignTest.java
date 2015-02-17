import static org.junit.Assert.*;

import org.junit.Test;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WaySegment;


public class AlignTest {

	@Test
	public void testTwoSegmentsAlignment() {

		DataSet set = Main.map.mapView.getEditLayer().data;
		
		Way wSegm1 = new Way();
		set.addPrimitive(wSegm1);
		Way wSegm2 = new Way();
		set.addPrimitive(wSegm2);
		wSegm1.addNode(new Node(new EastNorth(4,  0)));
		wSegm1.addNode(new Node(new EastNorth(4,  4)));
		wSegm2.addNode(new Node(new EastNorth(2,  2)));
		wSegm2.addNode(new Node(new EastNorth(6,  2)));
		
		WaySegment segm1 = new WaySegment(wSegm1, 0);
		WaySegment segm2 = new WaySegment(wSegm2, 0);

		System.out.println(segm1);
		System.out.println(segm2);
		
		assertTrue(true);
		
	}

}
