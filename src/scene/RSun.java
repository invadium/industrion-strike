package scene;

import math.*;

public class RSun {
	public Vector fire;
	public int R;
	public int G;
	public int B;

	public double fonLight = 0.5;

	public RSun(double x, double y, double z, int R, int G, int B) {
		fire = new Vector(x, y, z);
		this.R = R;
		this.G = G;
		this.B = B;
	}
}