package render;

import java.awt.*;

class Message {
	String strMsg;
	Color cMsg;

	Message(String strMsg) {
		this.strMsg = strMsg;
		this.cMsg = Color.green;
	}

	Message(String strMsg, Color cMsg) {
		this.strMsg = strMsg;
		this.cMsg = cMsg;
	}
}