package primitives;

/**
 * Represents point in 3D space.
 * 
 * @author Igor Khotin
 *
 */
public class Coord3D {
	public double x, y, z;

	public Coord3D() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Coord3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}