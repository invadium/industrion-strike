package render;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.applet.*;
import media.Media;

abstract public class Visualizer {
	Applet applet;
	Graphics canvas;
	Graphics2D canvas2D;
	Media media;
	//int xpoints[];
	//int ypoints[];

	abstract public void drawRenderedElement(RenderedElement element);
	
	abstract public void updateCanvas(Graphics canvas);
}