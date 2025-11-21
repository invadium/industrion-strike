package engine;

public interface AreaListener {	
	public void areaClick(Area area, int x, int y, int clicks);
	
	public void areaRightClick(Area area, int x, int y, int clicks);
	
	public void areaWheel(Area area, int value);
}
