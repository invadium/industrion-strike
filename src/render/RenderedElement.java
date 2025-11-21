package render;

import java.awt.*;
import math.CMath;
import primitives.RColor;

public class RenderedElement {
	int prevElement, nextElement;
	public boolean inList;
	public boolean isInScreen;
	public boolean isWareframed = false;
	public boolean isBitmapped = false;
	public Image imgElement;

	public RColor PrimitiveColor;
	public int cntPoints;
	public double d1, d2, d3, d4;
	public int ix1, ix2, ix3, ix4, iy1, iy2, iy3, iy4;
	public double distance;
	public int volume = 1;
	public String txtNote;

	public RenderedElement() {
		isInScreen = true;
		inList = false;
		prevElement = -1;
		nextElement = -1;
	}

	public void calcAverageDistance() {
		switch(cntPoints) {
		case -13: distance = d1; break;
		case 1:	distance = d1; break;
		case 2:	if (d1<0 || d2<0)  distance = -1;
				else distance = (d1 + d2) / 2;
				break;
		case 3:	if (d1<0 || d2<0 || d3<0) distance = -1;
				else distance = (d1 + d2 + d3) / 3;
				break;
		case 4: if (d1<0 || d2<0 || d3<0 || d4<0) distance = -1;
				else distance = (d1 + d2 + d3 + d4) / 4;
				break;
		}
	}

	private double getND(int x, int y) {
		int p1, p2, p3, p4;

		switch(this.cntPoints) {
		case -13: return d1;
		case 1:	return d1;
		case 2:	p1 = (int)CMath.M.sqrt((x - ix1)*(x - ix1) + (y - iy1)*(y - iy1));
				p2 = (int)CMath.M.sqrt((x - ix2)*(x - ix2) + (y - iy2)*(y - iy2));
				if (p1 <= p2) return d1;
					else return d2;
		case 3:	p1 = (int)CMath.M.sqrt((x - ix1)*(x - ix1) + (y - iy1)*(y - iy1));
				p2 = (int)CMath.M.sqrt((x - ix2)*(x - ix2) + (y - iy2)*(y - iy2));
				p3 = (int)CMath.M.sqrt((x - ix3)*(x - ix3) + (y - iy3)*(y - iy3));
				if (p1 <= p2) {
					if (p1 <= p3) return d1;
						else return d3;
				}
				else {
					if (p2 <= p3) return d2;
						else return d3;
				}
		case 4: p1 = (int)CMath.M.sqrt((x - ix1)*(x - ix1) + (y - iy1)*(y - iy1));
				p2 = (int)CMath.M.sqrt((x - ix2)*(x - ix2) + (y - iy2)*(y - iy2));
				p3 = (int)CMath.M.sqrt((x - ix3)*(x - ix3) + (y - iy3)*(y - iy3));
				p4 = (int)CMath.M.sqrt((x - ix4)*(x - ix4) + (y - iy4)*(y - iy4));
				if (p1 <= p2) {
					if (p1 <= p3) {
						if (p1 <= p4) return d1; else return d4;
					} else {
						if (p3 <= p4) return d3; else return d4;
					}
				}
				else {
					if (p2 <= p3) {
						if (p2 <= p4) return d2; else return d4;
					} else {
						if (p3 <= p4) return d3; else return d4;
					}
				}
		}
		return 0;
	}

	private double getPD(RenderedElement EL) {
		double distancer = 0;

		switch(EL.cntPoints) {
		case -13: distancer = getND(EL.ix1, EL.iy1); break;
		case 1:	distancer = getND(EL.ix1, EL.iy1); break;
		case 2:	distancer = (getND(EL.ix1, EL.iy1) + getND(EL.ix2, EL.iy2) + distance) / 3; 
				break;
		case 3:	distancer = (getND(EL.ix1, EL.iy1) + getND(EL.ix2, EL.iy2)
					+ getND(EL.ix3, EL.iy3) + distance) / 4; 
				break;
		case 4: distancer = (getND(EL.ix1, EL.iy1) + getND(EL.ix2, EL.iy2)
					+ getND(EL.ix3, EL.iy3) + getND(EL.ix4, EL.iy4) + distance) / 5; 
				break;
		}
		return distancer;
	}

	public boolean test(RenderedElement EL) {
		double distancer = this.getPD(EL);
		double distancer2 = EL.getPD(this);

		if (distancer < distancer2) return true;
		return false;
	}

}