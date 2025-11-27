package parser;

import java.applet.*;
import java.io.*;
import java.net.URL;

public class Parser {
	Applet applet;
	InputStream IS;

	public int iLine, iLineChar;
	public String strFileName;
	private int iBuffer;
    private boolean	isEmpty;
	public  boolean takeNegative = true;

	private Token bufToken;
	private boolean isTokenInBuffer = false;

	public Parser(Applet applet) {
		this.applet = applet;
	}

	public boolean openStream(String strFileName) {
		try {
			 IS = new URL(applet.getCodeBase(), strFileName).openStream();
			 isEmpty = true;
		} catch (Exception e) {
			String err = e.toString();
			System.out.println(err);
			return false;
		}
		iLine = 0;
		iLineChar = 0;
		this.strFileName = strFileName;
		return true;
	}

	private int getNext() {
		try {
			if (isEmpty) {
				int c = IS.read();
				if (c == 0x0A) {
					iLine++;
					iLineChar = 0;
				} else iLineChar++;
				return c;
			}
		} catch (Exception e) {
			String err = e.toString();
			System.out.println(err);
		}

		isEmpty = true;
		return iBuffer;
	}

	private void retChar(int iVal) {
		iBuffer = iVal;
		isEmpty = false;
	}

	public void retToken(Token tok) {
		isTokenInBuffer = true;
		bufToken = tok;
	}

	public Token getToken() throws ParserException {
		if (isTokenInBuffer) {
			isTokenInBuffer = false;
			return bufToken;
		}
		//skip tabs, spaces, eols and comments
		int i = 0;
		boolean NA = true;

		while(NA){
			do i = getNext();
			while ((i==0x20 || i==0x09 || i==0xA || i==0xD) && i!=-1);
			if (i == 0x23) {
				//skip comment (starting from hash #)
				while (i!=0x0A && i!=-1) i = getNext(); 
			} else NA = false;
		}
		if (i == -1) return null; //eof


		int itok = 0;
		int sign = 1;
		if (takeNegative) {
			if (i=='-') {sign = -1; i = getNext();}
		} else takeNegative = true;
		if (i>=0x30 && i<=0x39) {
			//token is a number
			do {
				itok = itok * 10 + (i - 0x30);
				i = getNext();
			} while (i>=0x30 && i<=0x39 && i!=-1);
			retChar(i);
			itok *= sign;
			Token iToken = new Token(itok);
			return iToken;
		}
		       
		//check on special symbol
		//mov || cmp
		if (i==0x3D) {
			i = getNext();
			if (i==0x3D) {
				Token eqToken = new Token(Token.CMP, "==");
				return eqToken;
			} else {
				retChar(i);
				Token eqToken = new Token(Token.EQ, "=");
				return eqToken;
			}
		}
		//semicolon
		if (i==0x3B) {
			Token semiToken = new Token(Token.SEMI, ";");
			return semiToken;
		}
		//dot
		if (i==0x2E) {
			Token semiToken = new Token(Token.DOT, ".");
			return semiToken;
		}
		//(
		if (i==0x28) {
			Token semiToken = new Token(Token.ORD, "(");
			return semiToken;
		}
		//)
		if (i==0x29) {
			Token semiToken = new Token(Token.CRD, ")");
			return semiToken;
		}

		//operators
		if (i==0x26) {
			Token opToken = new Token(Token.AND, "&"); return opToken;
		}
		if (i==0x7C) {
			Token opToken = new Token(Token.OR, "|"); return opToken;
		}
		if (i==0x2B) {
			Token opToken = new Token(Token.ADD, "+"); return opToken;
		}
		if (i==0x2D) {
			Token opToken = new Token(Token.SUB, "-"); return opToken;
		}
		if (i==0x2F) {
			Token opToken = new Token(Token.DIV, "/"); return opToken;
		}
		if (i==0x2A) {
			Token opToken = new Token(Token.MUL, "*"); return opToken;
		}
		if (i==0x25) {
			Token opToken = new Token(Token.SUB, "%"); return opToken;
		}
		if (i==0x2C) {
			Token opToken = new Token(Token.COMMA, ","); return opToken;
		}
		if (i==0x21) {
			i = getNext();
			if (i==0x3D) {
				Token eqToken = new Token(Token.NEQ, "!="); return eqToken;
			} else {
				retChar(i);
				Token eqToken = new Token(Token.NOT, "!");
				return eqToken;
			}
		}
		if (i==0x24) {
			Token opToken = new Token(Token.USD, "$"); return opToken;
		}
		if (i==0x3C) {
			i = getNext();
			if (i==0x3D) {
				Token eqToken = new Token(Token.LSQ, "<="); return eqToken;
			} else {
				retChar(i);
				Token eqToken = new Token(Token.LS, "<");
				return eqToken;
			}
		}
		if (i==0x3E) {
			i = getNext();
			if (i==0x3D) {
				Token eqToken = new Token(Token.GRQ, ">="); return eqToken;
			} else {
				retChar(i);
				Token eqToken = new Token(Token.GR, ">");
				return eqToken;
			}
		}

		

		//token is a string
		StringBuffer tok = new StringBuffer(64); //token buffer
		if (i==0x22) do {
			i = getNext();
			if (i!=0x22) tok.append((char)i);
			if (i==0x0A || i==0x0D || i==-1) {
				throw new ParserException(ParserException.ILL_STRING, "unexpected end of the string literal");
			}
		} while(i!=0x22 && i!=-1);
		else { 
			tok.append((char)i);
			do {
				i = getNext(); 
				if (!isBreak(i))
					tok.append((char)i);
			} while(!isBreak(i));
			retChar(i);
		}

		Token strToken = new Token(tok.toString());
		return strToken;	
	}

	private boolean isBreak(int i) {
		if (i==0x20 || i==0x09 || i==0x0A || i==0x0D || i==-1) return true;
		if (i == 0x3E) return true; //>
		if (i == 0x3C) return true; //<
		if (i == 0x21) return true; //!

		if (i==0x26) return true; //&
		if (i==0x7C) return true; //|
		if (i==0x2B) return true; //+
		if (i==0x2D) return true; //-
		if (i==0x2F) return true; ///
		if (i==0x2A) return true; //*
		if (i==0x25) return true; //%
		if (i==0x3D) return true; //=

		if (i==0x22) return true; //"
		if (i==0x3B) return true; //;
		if (i==0x2E) return true; //.
		if (i==0x28) return true; //(
		if (i==0x29) return true; //)
		return false;
	}
}