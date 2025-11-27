package primitives;

import java.awt.*;
import java.applet.*;
import scene.Camera;
import scene.SpaceObject;
import scene.Scene;
import render.RenderedElement;

/**
 * This primitive represents point volume in 3D space.
 * 
 * @author Igor Khotin
 *
 */
public class Trixel extends Primitive {
	Applet applet;
	Graphics canvas;
	private boolean isInScreen;
	public final int cntPoints = 1;
	primitives.Point P1;
	public boolean isBitmapped = false;

	public Trixel(Color TrixelColor, double x, double y, double z) {
		PrimitiveColor = new RColor(TrixelColor);
		P1 = new primitives.Point(x, y, z);
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

	public RenderedElement getRenderedElement(SpaceObject spaceObject) {
		RenderedElement element;
		element = new RenderedElement();
		element.PrimitiveColor = this.PrimitiveColor;
		element.cntPoints = 1;
		element.ix1 = P1.ipx;
		element.iy1 = P1.ipy;
		element.d1 = P1.z_buffer;
		element.calcAverageDistance();
		element.isBitmapped = this.isBitmapped;
		if (this.isBitmapped) element.imgElement = spaceObject.model.imgModel;
		int vdist = (int)(element.distance / 4000);
		element.volume = this.volume - vdist;
		if (element.volume < 1) element.volume = 1;
		return element;
	}
}