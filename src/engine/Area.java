package engine;

/**
 * Represents activate rectangular area on the screen.
 * 
 * @author Igor Khotin 
 * 
 */
public class Area {
	String id;
	AreaListener areaListener;	
	int type;
	int x, y, dx, dy, w, h;
	
	int x_pos = 0;
	int y_pos = 0;
	
	public Area(String id, int type, int x, int y, int w, int h) {
		this.id = id; 
		this.type = type;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.dx = x + w;
		this.dy = y + h;
	}
	
	public Area(AreaListener areaListener, String id, int type, int x, int y, int w, int h) {
		this.id = id;
		this.areaListener = areaListener; 
		this.type = type;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.dx = x + w;
		this.dy = y + h;
	}
	
	public void setCoords(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.dx = x + w;
		this.dy = y + h;
	}
	
	public boolean probe(int px, int py) {
		if (px >= x && px < dx && py >= y && py < dy) {
			// inside
			// calculate internal coordinates
			this.x_pos = px - x;
			this.y_pos = py - y;
			return true;
		}
		return false;
	}
	
	public int get_x() {
		return this.x_pos;
	}
	
	public int get_y() {
		return this.y_pos;
	}
	
	public int getType() {
		return this.type;
	}
	
	public String getId() {
		return this.id;
	}
	
	/**
	 * Must be executed in case of area click 
	 */
	public void areaClick(int clicks) {
		if (this.areaListener != null)
			this.areaListener.areaClick(this, this.x_pos, this.y_pos, clicks);
	}
	
	/**
	 * Must be executed in case of area right click
	 */
	public void areaRightClick(int clicks) {
		if (this.areaListener != null)
			this.areaListener.areaRightClick(this, this.x_pos, this.y_pos, clicks);
	}
	
	public void areaWheel(int value) {
		this.areaListener.areaWheel(this, value);
	}
}
