package math;

import java.math.*;
import java.util.*;

/**
 * Math support class
 * 
 * @author Igor Khotin
 *
 */
public class CMath {
	static public Math M;
	static public Random R;
	static public double dFactor;
	static public double PI;
	static public double PI2;
	static public double PIn2;
	static public double tsin[];
	static public double tcos[];

	static {
		R = new Random();
		tsin = new double[360 * 60];
		tcos = new double[360 * 60];
		PI = M.PI;
		PI2 = M.PI * 2;
		PIn2 = M.PI / 2;
		dFactor = (M.PI / 180) / 60;
		int i;
		for (i=0; i<(360 * 60); i++) {
			tsin[i] = M.sin(i * dFactor);
			tcos[i] = M.cos(i * dFactor);
		}
	}

	static public double sin(double r) {
		int i;
		r = r / dFactor;
		i = (int)r;
		i = i % (360 * 60);
		return tsin[i];
	}

	static public double sin(int d) {
		if (d<0) d += (360 * 60);
		return tsin[d];
	}

	static public double cos(double r) {
		int i;
		r = r / dFactor;
		i = (int)r;
		i = i % (360 * 60);
		return tcos[i];
	}

	static public double cos(int d) {
		if (d<0) d += (360*60);
		return tcos[d];
	}
}