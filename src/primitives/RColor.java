package primitives;

import java.awt.*;

/**
 * Represents color.
 * Utilises basic operation over the color (needed for lights).
 * 
 * @author Igor Khotin
 *
 */
public class RColor {
	final int minVal = 25;
	final int maxVal = 255;
	public int R, G, B;

	public RColor(int R, int G, int B) {
		this.R = R;
		this.G = G;
		this.B = B;
	}

	public RColor(RColor C) {
		this.R = C.R;
		this.G = C.G;
		this.B = C.B;
	}

	public RColor(Color C) {
		this.R = C.getRed();
		this.G = C.getGreen();
		this.B = C.getBlue();
	}

	public Color getColor() {
		Color C = new Color(this.R, this.G, this.B);
		return C;
	}

	public void setDarkSide() {
		//set dark side of the Force :)))
		this.G = 255;
		this.B = 0;
		this.R = 0;
	}

	public void darkDiffuse(double val) {
		this.R = (int)((double)this.R * val);
		this.G = (int)((double)this.G * val);
		this.B = (int)((double)this.B * val);

		if (this.R > maxVal) this.R = maxVal;
		if (this.G > maxVal) this.G = maxVal;
		if (this.B > maxVal) this.B = maxVal;
		if (this.R < 0) this.R = 0;
		if (this.G < 0) this.G = 0;
		if (this.B < 0) this.B = 0;
	}

	public void diffuse(double val) {
		this.R = (int)((double)this.R * val);
		this.G = (int)((double)this.G * val);
		this.B = (int)((double)this.B * val);

		if (this.R > maxVal) this.R = maxVal;
		if (this.G > maxVal) this.G = maxVal;
		if (this.B > maxVal) this.B = maxVal;
	}

	public void increase(int val) {
		this.R += val; if (this.R > maxVal) this.R = maxVal;
		this.G += val; if (this.G > maxVal) this.G = maxVal;
		this.B += val; if (this.B > maxVal) this.B = maxVal;

		if (this.R < minVal) this.R = minVal;
		if (this.G < minVal) this.G = minVal;
		if (this.B < minVal) this.B = minVal;
	}

	public void reduce(int val) {
		this.R -= val; if (this.R < minVal) this.R = minVal;
		this.G -= val; if (this.G < minVal) this.G = minVal;
		this.B -= val; if (this.B < minVal) this.B = minVal;
	}
}