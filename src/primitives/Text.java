package primitives;

import java.awt.*;
import java.applet.*;
import math.*;	
import scene.Camera;
import scene.SpaceObject;
import scene.Scene;
import render.RenderedElement;

/**
 * Text in space primitive
 * 
 * @author Igor Khotin
 *
 */
public class Text extends Primitive {
	Applet theApplet;
	Graphics theCanvas;
	private boolean isInScreen;
	public final int cntPoints = 1;
	primitives.Point P1;
	String txtNote;
	public int volume = 12;

	public Text(Color TextColor, double x, double y, double z, String txtNote) {
		PrimitiveColor = new RColor(TextColor);
		P1 = new primitives.Point(x, y, z);
		this.txtNote = txtNote;
	}

	public void shift(double sx, double sy, double sz) {
		P1.x += sx;
		P1.y += sy;
		P1.z += sz;
	}

	public void reduceScale(double factor) {
		P1.x /= factor;
		P1.y /= factor;
		P1.z /= factor;
	}

	public void increaseScale(double factor) {
		P1.x *= factor;
		P1.y *= factor;
		P1.z *= factor;
	}

	public void renderProection(Scene sce, Camera cam, SpaceObject obj) {
		this.P1.renderPoint(cam, obj);
		if (!P1.isInScreen) {
			this.isInScreen = false;
			return;
		}
		this.isInScreen = true;
	}

	public RenderedElement getRenderedElement(SpaceObject theSpaceObject) {
		RenderedElement theElement;
		theElement = new RenderedElement();
		theElement.PrimitiveColor = this.PrimitiveColor;
		theElement.cntPoints = -13;
		theElement.ix1 = P1.ipx;
		theElement.iy1 = P1.ipy;
		theElement.d1 = P1.z_buffer;
		theElement.calcAverageDistance();
		theElement.volume = this.volume;
		theElement.txtNote = this.txtNote;
		return theElement;
	}
}