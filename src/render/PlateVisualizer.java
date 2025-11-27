package render;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.applet.*;
import media.Media;

public class PlateVisualizer extends Visualizer {

	public PlateVisualizer(Applet applet, Graphics canvas, Media media) {
		this.applet = applet;
		this.canvas = canvas;
		this.canvas2D = (Graphics2D) canvas;
		this.media =  media;
	}
	
	public void updateCanvas(Graphics canvas) {
		this.canvas = canvas;
		this.canvas2D = (Graphics2D) canvas;
	}

	public void drawRenderedElement(RenderedElement element) {
		canvas.setColor(element.PrimitiveColor.getColor());
		switch(element.cntPoints) {
			case -13:
					Font cFnt = new Font("Courier", Font.PLAIN, element.volume);
					canvas.setFont(cFnt);
					canvas.drawString(element.txtNote, element.ix1, element.iy1);
					break;
			case 1: if (element.isBitmapped && element.imgElement != null) {
						canvas.drawImage(element.imgElement,
								element.ix1 - (element.imgElement.getWidth(applet) >> 1),
								element.iy1 - (element.imgElement.getHeight(applet) >> 1),
								applet);
					} else if (element.volume > 1) {
						canvas.fillOval(element.ix1-element.volume/2, element.iy1-element.volume/2,
							(element.volume), (element.volume));
					} else canvas.drawLine(element.ix1, element.iy1, element.ix1, element.iy1);
					break;
			case 2: if (element.volume > 1) {
						canvas2D.setStroke(new BasicStroke((float)element.volume));
						canvas2D.setColor(element.PrimitiveColor.getColor());
						canvas2D.draw(new Line2D.Double(element.ix1, element.iy1, element.ix2, element.iy2));
						canvas2D.setStroke(new BasicStroke(1.0f));
					} else
						canvas.drawLine(element.ix1, element.iy1, element.ix2, element.iy2);
					break;
			case 3: int xpoints[] = new int[3];
					int ypoints[] = new int[3];
					xpoints[0] = element.ix1;
					xpoints[1] = element.ix2;
					xpoints[2] = element.ix3; 
					ypoints[0] = element.iy1;
					ypoints[1] = element.iy2;
					ypoints[2] = element.iy3;
					//canvas.drawPolyline(xpoints, ypoints, 3);
					canvas.fillPolygon(xpoints, ypoints, 3);
					break;
			case 4:	int xpoints4[] = new int[4];
					int ypoints4[] = new int[4];
					xpoints4[0] = element.ix1;
					xpoints4[1] = element.ix2;
					xpoints4[2] = element.ix3;
					xpoints4[3] = element.ix4; 
					ypoints4[0] = element.iy1;
					ypoints4[1] = element.iy2;
					ypoints4[2] = element.iy3;
					ypoints4[3] = element.iy4;
					//canvas.drawPolyline(xpoints4, ypoints4, 4);
					if (element.isWareframed) canvas.drawPolygon(xpoints4, ypoints4, 4);
					else canvas.fillPolygon(xpoints4, ypoints4, 4);
					break;
		}
	}

}