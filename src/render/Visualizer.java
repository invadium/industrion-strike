package render;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.applet.*;
import media.Media;

abstract public class Visualizer {
	Applet theApplet;
	Graphics theCanvas;
	Graphics2D theCanvas2D;
	Media theMedia;
	//int xpoints[];
	//int ypoints[];

	abstract public void drawRenderedElement(RenderedElement theElement);
	
	abstract public void updateCanvas(Graphics theCanvas);
}