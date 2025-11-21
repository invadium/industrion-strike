package engine;

import java.util.ArrayList;



public class AreaControl {
	public static final int ANY = 0;	
	public static final int BUTTON = 1;
	public static final int RADAR = 2;
	public static final int MESSAGE_LOG = 3;	
	public static final int AUTOPILOT = 4;
	public static final int TIMER = 5;
	
	ArrayList zone = new ArrayList();	
	
	public AreaControl() {};
	
	public void addArea(AreaListener listener, String id, int type, int x, int y, int w, int h) {
		Area area = new Area(listener, id, type, x, y, w, h);
		zone.add(area);		
	}
	public void addAreaOnBackground(AreaListener listener, String id, int type, int x, int y, int w, int h) {
		Area area = new Area(listener, id, type, x, y, w, h);
		zone.add(0, area);
	}
	
	public void updateAreaCoords(String id, int x, int y, int w, int h) {
		for (int i = 0; i < zone.size(); i++) {
			Area area = (Area)zone.get(i);
			if (area.id.equals(id)) {
                /*
				System.out.println("updating " + id
						+ " " + x + ":" + y
						+ " " + w + ":" + h);
                */
				area.setCoords(x, y, w, h);
			}
		}
	}
	
	public boolean removeArea(String id) {
		for (int i = 0; i < zone.size(); i++)
			if (((Area)zone.get(i)).id.equals(id)) {
				zone.remove(i);
				return true;				
			}
		return false;
	}
	
	public Area probe(int x, int y) {
		for (int i = this.zone.size(); i > 0; i--) {
			Area area = (Area)this.zone.get(i - 1);
			if (area.probe(x, y)) return area;			
		}
		
		return null;		
	}
}
