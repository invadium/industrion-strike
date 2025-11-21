package primitives;

import java.awt.*;
import java.applet.*;
import math.*;
import scene.Camera;
import scene.SpaceObject;
import scene.Scene;
import render.RenderedElement;

/**
 * Triangle primitive - the basic 3D primitive in our engine.
 * 
 * @author Igor Khotin
 *
 */
public class Triangle extends Primitive {
	Applet theApplet;
	Graphics theCanvas;
	public final int cntPoints = 3;
	public primitives.Point P1, P2, P3;
	private RColor renderedColor;
	private boolean isInScreen;
	//material properties
	public boolean Solid = true;
	public boolean Wareframe = false;
	public boolean matIlluminated = false;
	public int matMetal = 150;
	
	public Triangle(Color TriangleColor,
			double x1, double y1, double z1, 
			double x2, double y2, double z2, 
			double x3, double y3, double z3) {
		PrimitiveColor = new RColor(TriangleColor);
		this.P1 = new primitives.Point(x1, y1, z1);
		this.P2 = new primitives.Point(x2, y2, z2);
		this.P3 = new primitives.Point(x3, y3, z3);
	}

	public void shift(double sx, double sy, double sz) {
		P1.x += sx;
		P1.y += sy;
		P1.z += sz;
		P2.x += sx;
		P2.y += sy;
		P2.z += sz;
		P3.x += sx;
		P3.y += sy;
		P3.z += sz;
	}

	public void reduceScale(double factor) {
		P1.x /= factor;
		P1.y /= factor;
		P1.z /= factor;
		P2.x /= factor;
		P2.y /= factor;
		P2.z /= factor;
		P3.x /= factor;
		P3.y /= factor;
		P3.z /= factor;
	}

	public void increaseScale(double factor) {
		P1.x *= factor;
		P1.y *= factor;
		P1.z *= factor;
		P2.x *= factor;
		P2.y *= factor;
		P2.z *= factor;
		P3.x *= factor;
		P3.y *= factor;
		P3.z *= factor;
	}


	public void renderProection(Scene sce, Camera cam, SpaceObject obj) {
		P1.renderPoint(cam, obj);
		P2.renderPoint(cam, obj);
		P3.renderPoint(cam, obj);

		//if (P1.z_buffer < 0 || P2.z_buffer < 0 || P3.z_buffer < 0) return;

		/*if (!P1.isInScreen && !P2.isInScreen && !P3.isInScreen) {
			this.isInScreen = false;
			return;
		}*/
		this.isInScreen = true;


		renderedColor = new RColor(PrimitiveColor);
		if (matIlluminated == true) return;
			//find normal vector
			double i, k, j;
			i = (P2.BP.y - P1.BP.y) * (P3.BP.z - P1.BP.z) - (P2.BP.z - P1.BP.z) * (P3.BP.y - P1.BP.y);
			j = (P2.BP.z - P1.BP.z) * (P3.BP.x - P1.BP.x) - (P2.BP.x - P1.BP.x) * (P3.BP.z - P1.BP.z);
			k = (P2.BP.x - P1.BP.x) * (P3.BP.y - P1.BP.y) - (P2.BP.y - P1.BP.y) * (P3.BP.x - P1.BP.x);
			Vector normal = new Vector(i, j, k);
			Vector out = cam.getOutVector();
	
			double cosFi = out.getCosFi(normal); //calc angle between Out and Normal
			double Fi = CMath.M.acos(cosFi);
			if (Fi < CMath.PIn2 && Fi > -CMath.PIn2) {
				normal.negative();
				cosFi = normal.getCosFiN(out);
				Fi = CMath.M.acos(cosFi);
			}
		
			Vector sunVector = new Vector(P1.BP.x - sce.theSun.fire.x,
									P1.BP.y - sce.theSun.fire.y,
									P1.BP.z - sce.theSun.fire.z);
			double cosPsi = normal.getCosFi(sunVector);
			double Psi = CMath.M.acos(cosPsi);

			int colorVal = -(int)(cosPsi * 200.0);

			//if colorVal negative, sun does'nt fall on verge
			if (cosPsi > 0) {
				renderedColor.darkDiffuse(sce.theSun.fonLight - cosPsi/10);
			} else {
				//shadding (for diffuse model)
				renderedColor.diffuse((-cosPsi) + sce.theSun.fonLight);
				//metal blik
				if (matMetal != 0) renderedColor.increase(
					(int)(cosPsi*cosPsi * (double)matMetal));
			}
	}

	public RenderedElement getRenderedElement(SpaceObject theSpaceObject) {
		RenderedElement theElement;
		theElement = new RenderedElement();
		theElement.PrimitiveColor = this.renderedColor;
		theElement.cntPoints = 3;
		theElement.ix1 = P1.ipx;
		theElement.iy1 = P1.ipy;
		theElement.d1 = P1.z_buffer;
		theElement.ix2 = P2.ipx;
		theElement.iy2 = P2.ipy;
		theElement.d2 = P2.z_buffer;
		theElement.ix3 = P3.ipx;
		theElement.iy3 = P3.ipy;
		theElement.d3 = P3.z_buffer;
		theElement.isInScreen = this.isInScreen;
		theElement.calcAverageDistance();
		return theElement;
	}
}