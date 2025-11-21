package primitives;

import math.*;
import scene.Camera;
import scene.SpaceObject;

/**
 * Point primitive
 * 
 * @author Igor Khotin
 *
 */
public class Point {
	public double x, y, z;
	public Vector P;
	public Vector BP;
	public double px, py, z_buffer;
	public int ipx, ipy;
	public boolean isInScreen;
	
	public Point() {
		this.x = 0;
		this.y = 0;
		this.x = 0;
	}

	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void renderPoint(Camera cam, SpaceObject obj) {	
		//point rotating
		P = new Vector(x, y, z);
		P.mul(obj.objRotate);

		//shift
		//BP = new Vector(P.x + obj.x, P.y + obj.y, P.z + obj.z);  //vector for painting
		//P.shift(obj.x - cam.x, obj.y - cam.y, obj.z - cam.z + cam.d);
		BP = new Vector(P.x + obj.x, P.y + obj.y, P.z + obj.z);  //vector for painting
		P.shift(obj.x - cam.x, obj.y - cam.y, obj.z - cam.z);

		//camera rotating
		P.mul(cam.matRotate);
		
        	if (P.z <= 0) z_buffer = -1;
			else z_buffer = CMath.M.sqrt(P.x*P.x + P.y*P.y + P.z*P.z);

		//P.z = P.z - cam.d;	
		//proection
		px = P.x * cam.d / (cam.d + P.z);
		py = P.y * cam.d / (cam.d + P.z);
		ipx = (int)px + cam.ScreenShiftX;
		ipy = (int)py + cam.ScreenShiftY;		

		
		if (ipx <= 0 || ipx >= cam.ScreenWidth 
			|| ipy <= 0 || ipy >= cam.ScreenHeight) isInScreen = false;
		else isInScreen = true;
	}

}