package primitives;

import java.awt.*;
import scene.Scene;
import scene.Camera;
import scene.SpaceObject;
import render.RenderedElement;

/**
 * Abstract primitive
 * 
 * @author Igor Khotin
 *
 */
abstract public class Primitive {
	public RColor PrimitiveColor;
	public int cntPoints;
	public int volume = 1;
	public boolean isBitmaped = false;
	public int sFrame = 0;
	public int eFrame = 0;

	abstract public void renderProection(Scene sce, Camera cam, SpaceObject obj);
	abstract public RenderedElement getRenderedElement(SpaceObject theSpaceObject);
	abstract public void reduceScale(double factor);
	abstract public void increaseScale(double factor);
	abstract public void shift(double sx, double sy, double sz);
}