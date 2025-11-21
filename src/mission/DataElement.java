package mission;

public class DataElement {
	public final static int nulElement = 0;
	public final static int strElement = 1;
	public final static int intElement = 2;
	public int type = 0;
	public int iValue = 0;
	public String strValue;

	DataElement() {
		type = nulElement;
	}

	DataElement(DataElement d) {
		this.type = d.type;
		this.strValue = d.strValue;
		this.iValue = d.iValue;
	}

	DataElement(String strValue) {
		this.type = strElement;
		this.strValue = strValue;
	}

	DataElement(int iValue) {
		this.type = intElement;
		this.iValue = iValue;
	}
}