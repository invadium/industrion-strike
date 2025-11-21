package parser;

public class ParserException extends Exception {
	public static final int ILL_STRING = 1;

	public int iType;
	public String strDescr;

	public ParserException (int iType, String descr) {
		this.iType = iType;
		this.strDescr = descr;
	}

	public String toString() {
		return "Parser Error #" + iType + ": " + strDescr;
	}
}