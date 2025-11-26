package render;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.applet.*;
import media.Media;

public class FrameVisualizer extends Visualizer {

	public FrameVisualizer(Applet theApplet, Graphics theCanvas, Media theMedia) {
		this.applet = theApplet;
		this.canvas = theCanvas;
		this.canvas2D = (Graphics2D) theCanvas;
		this.media =  theMedia;
	}
	
	public void updateCanvas(Graphics theCanvas) {
		this.canvas = theCanvas;
		this.canvas2D = (Graphics2D) theCanvas;
	}

	public void drawRenderedElement(RenderedElement theElement) {
		//set color and width of wareframe
		canvas.setColor(Color.white);
		canvas2D.setStroke(new BasicStroke((float)1));

		switch(theElement.cntPoints) {
			case -13:
					Font cFnt = new Font("Courier", Font.PLAIN, theElement.volume);
					canvas.setFont(cFnt);
					canvas.drawString(theElement.txtNote, theElement.ix1, theElement.iy1);
					break;
			case 1: if (theElement.isBitmapped && theElement.imgElement != null) {
						canvas.drawImage(theElement.imgElement,
								theElement.ix1 - (theElement.imgElement.getWidth(applet) >> 1),
								theElement.iy1 - (theElement.imgElement.getHeight(applet) >> 1),
								applet);
					} else if (theElement.volume > 1) {
						canvas.fillOval(theElement.ix1-theElement.volume/2, theElement.iy1-theElement.volume/2,
							(theElement.volume), (theElement.volume));
					} else canvas.drawLine(theElement.ix1, theElement.iy1, theElement.ix1, theElement.iy1);
					break;
			case 2: if (theElement.volume > 1) {
						canvas2D.setStroke(new BasicStroke((float)theElement.volume));
						//theCanvas2D.setColor(theElement.PrimitiveColor.getColor());
						canvas2D.draw(new Line2D.Double(theElement.ix1, theElement.iy1, theElement.ix2, theElement.iy2));
						canvas2D.setStroke(new BasicStroke(1.0f));
					} else
						canvas.drawLine(theElement.ix1, theElement.iy1, theElement.ix2, theElement.iy2);
					break;
			case 3: int xpoints[] = new int[3];
					int ypoints[] = new int[3];
					xpoints[0] = theElement.ix1;
					xpoints[1] = theElement.ix2;
					xpoints[2] = theElement.ix3; 
					ypoints[0] = theElement.iy1;
					ypoints[1] = theElement.iy2;
					ypoints[2] = theElement.iy3;
					canvas.drawPolygon(xpoints, ypoints, 3);
					break;
			case 4:	int xpoints4[] = new int[4];
					int ypoints4[] = new int[4];
					xpoints4[0] = theElement.ix1;
					xpoints4[1] = theElement.ix2;
					xpoints4[2] = theElement.ix3;
					xpoints4[3] = theElement.ix4; 
					ypoints4[0] = theElement.iy1;
					ypoints4[1] = theElement.iy2;
					ypoints4[2] = theElement.iy3;
					ypoints4[3] = theElement.iy4;
					//theCanvas.drawPolyline(xpoints4, ypoints4, 4);
					canvas.drawPolygon(xpoints4, ypoints4, 4);
					break;
		}
	}

}