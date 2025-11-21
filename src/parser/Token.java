package parser;

public class Token {
	public static final int ERROR = -1;
	public static final int NUMBER = 0;
	public static final int STRING = 1;
 	public static final int EQ = 2;
 	public static final int SEMI = 3;
 	public static final int ORD = 4;
 	public static final int CRD = 5;
 	public static final int DOT = 6;
	//operators
	public static final int CMP = 7;	// ==
	public static final int ADD = 8;	// +
	public static final int SUB = 9;	// -
	public static final int DIV = 10;	// /
	public static final int MUL = 11;	// *
	public static final int MOD = 12;	// %
	public static final int NOT = 13;	// !
	public static final int USD = 14;	// $
	public static final int LS = 15;	// <
	public static final int GR = 16;	// >
	public static final int LSQ = 17;	// <=
	public static final int GRQ = 18;	// >=
	public static final int NEQ = 19;	// !=
	public static final int AND = 20;	// &
	public static final int OR  = 21;	// |
	public static final int COMMA = 22; // ,

	public static final String Match[] = {"number", "string literal",
			"=", ";", "(", ")", ".",
			"==", "+", "-", "/", "*", "%", "!", "$", "<", ">", ">=", "<=", "!=", "&", "|", ","};

	public int type;
	public int iValue;
	public String strValue;
	private final String nullString = "";

	public Token() {
		this.type = ERROR; //error in input stream
		this.strValue = nullString;
		this.iValue = 0;
	}

	Token(int iValue) {
		this.type = NUMBER;
		this.iValue = iValue;
		this.strValue = nullString;
	}

	Token(String strValue) {
		this.type = STRING;
		this.strValue = strValue;
		this.iValue = 0;
	}

	Token(int iType, String strValue) {
		this.type = iType;
		this.strValue = strValue;
		this.iValue = 0;
	}
}
