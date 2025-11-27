package primitives;

import java.awt.*;
import java.applet.*;
import math.*;
import scene.Camera;
import scene.SpaceObject;
import scene.Scene;
import render.RenderedElement;

/**
 * Line primitive
 * 
 * @author Igor Khotin
 *
 */
public class Line extends Primitive {
	Applet applet;
	Graphics canvas;
	private boolean isInScreen;
	public final int cntPoints = 2;
	primitives.Point P1, P2;

	public Line(Color LineColor,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		PrimitiveColor = new RColor(LineColor);
		P1 = new primitives.Point(x1, y1, z1);
		P2 = new primitives.Point(x2, y2, z2);
	}

	public void shift(double sx, double sy, double sz) {
		P1.x += sx;
		P1.y += sy;
		P1.z += sz;
		P2.x += sx;
		P2.y += sy;
		P2.z += sz;
	}

	public void reduceScale(double factor) {
		P1.x /= factor;
		P1.y /= factor;
		P1.z /= factor;
		P2.x /= factor;
		P2.y /= factor;
		P2.z /= factor;
	}

	public void increaseScale(double factor) {
		P1.x *= factor;
		P1.y *= factor;
		P1.z *= factor;
		P2.x *= factor;
		P2.y *= factor;
		P2.z *= factor;
	}


	public void renderProection(Scene sce, Camera cam, SpaceObject obj) {
		P1.renderPoint(cam, obj);
		P2.renderPoint(cam, obj);

		if (!P1.isInScreen && !P2.isInScreen) {
			this.isInScreen = false;
			return;
		}
		this.isInScreen = true;
	}

	public RenderedElement getRenderedElement(SpaceObject spaceObject) {
		RenderedElement element;
		element = new RenderedElement();
		if (spaceObject.colorExtension == false) element.PrimitiveColor = this.PrimitiveColor;
		    else element.PrimitiveColor = spaceObject.ownColor;
		element.cntPoints = 2;
		element.ix1 = P1.ipx;
		element.iy1 = P1.ipy;
		element.d1 = P1.z_buffer;
		element.ix2 = P2.ipx;
		element.iy2 = P2.ipy;
		element.d2 = P2.z_buffer;
		element.calcAverageDistance();
		int vdist = (int)(element.distance / 4000);
		element.volume = this.volume - vdist;
		if (element.volume < 1) element.volume = 1;
		return element;
	}
}