package render;

import java.awt.*;
import java.util.*;

import engine.Area;
import engine.AreaControl;
import engine.AreaListener;
import engine.Strike;

import media.Media;

public class MessageScreen implements AreaListener{
	private static final String MESSAGELOG = "MessageLog";
	
	Strike applet;
	Graphics canvas;
	Media media;
	Font screenFontL = new Font("Courier", Font.PLAIN, 12);

	static final int cntMessages = 7;
	boolean isExpired = true;
	int lastMsg = 0;
	int curMsg = -1;
	Vector Msg = new Vector(256);

	// position
	public int x = 10;
	public int y = 400;
	public int w = 350;
	public int h = 110;

	public MessageScreen(Strike applet, Graphics canvas, Media media) {
		this.applet = applet;
		this.applet.areaControl.addArea(this, MESSAGELOG, AreaControl.MESSAGE_LOG, this.x,this.y, this.w, this.h);
		this.canvas = canvas;
		this.media = media;
	}

	public void push(String strMessage) {
		Msg.addElement(new Message(strMessage));
		if (curMsg == Msg.size() - 2) curMsg++;
	}

	public void push(String strMessage, Color msgColor) {
		Msg.addElement(new Message(strMessage, msgColor));
		if (curMsg == Msg.size() - 2) curMsg++;
	}

	public void spush(String strMessage) {
		Msg.addElement(new Message(strMessage));
		if (curMsg == Msg.size() - 2) curMsg++;
		media.auMsg.play();
	}

	public void spush(String strMessage, Color msgColor) {
		Msg.addElement(new Message(strMessage, msgColor));
		if (curMsg == Msg.size() - 2) curMsg++;
		media.auMsg.play();
	}

	public void expaired() {
		isExpired = true;
	}

	public void draw() {
	  Message ms;
	  if (isExpired || lastMsg != curMsg) {
		try {
			canvas.setColor(Color.black);
			canvas.fillRect(x, y, w, h);
			canvas.setFont(screenFontL);
			int yc = y + h - 5;
			int xc = x + 5;
			int cnt = 0;
			int i = curMsg;
			while (i >= 0 && cnt < cntMessages) {
				ms = (Message)Msg.elementAt(i);
				canvas.setColor(ms.cMsg);
				canvas.drawString(ms.strMsg, xc, yc);
				i--;
				cnt++;
				yc -= 15;
			}
			lastMsg = curMsg;
			isExpired = false;
		} catch (NoSuchElementException e) {
		}
	  }
	}

	public void pageUp() {
		curMsg--;
		if (curMsg < cntMessages) {
			if (Msg.size() > cntMessages) curMsg = cntMessages - 1;
			else curMsg = Msg.size() - 1;
		}
	}

	public void pageDown() {
		curMsg ++;
		if (curMsg > Msg.size() - 1) curMsg = Msg.size() - 1;
	}

	public void home() {
		if (Msg.size() > cntMessages) curMsg = cntMessages - 1;
			else curMsg = Msg.size() - 1;
	}

	public void end() {
		if (Msg.size() > 0) curMsg = Msg.size() - 1;
	}
	
	public void areaClick(Area area, int cx, int cy, int clicks) {}
	
	public void areaRightClick(Area area, int cx, int cy, int clicks) {}
	
	public void areaWheel(Area area, int value) {
		if (value > 0) {
			this.pageDown();
		} else if (value < 0) {
			this.pageUp();			
		}	
	}
	
	public void clearifyPosition(int w, int h) {
		this.x = 10;
		this.y = h - this.h - 10;
		this.w = applet.scene.indicatorScreen.x - this.x - 10;
		
		// update active areas
		applet.areaControl.updateAreaCoords(
				MESSAGELOG, this.x,this.y, this.w, this.h);
	}
}