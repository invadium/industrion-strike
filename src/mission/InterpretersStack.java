package mission;

class InterpretersStack {
	final int maxElements = 1000;

	int top = 0;
	DataElement Data[] = new DataElement[maxElements];
	
	public InterpretersStack () {
	}

	public void push(DataElement dt) throws InterpreterException {
		if (top + 1 >= maxElements)
			throw new InterpreterException("interpreters command stack is full");
		Data[top] = dt;
		top++;
	}

	public void push(int iValue) throws InterpreterException {
		if (top + 1 >= maxElements)
			throw new InterpreterException("interpreters command stack is full");
		DataElement dt = new DataElement(iValue);
		Data[top] = dt;
		top++;
	}

	public void push(String strValue) throws InterpreterException {
		if (top + 1 >= maxElements)
			throw new InterpreterException("interpreters command stack is full");
		DataElement dt = new DataElement(strValue);
		Data[top] = dt;
		top++;
	}

	public DataElement pop() throws InterpreterException {
		if (top <= 0)
			throw new InterpreterException("interpreters command stack is empty");
		top--;
		return Data[top];
	}

	public int topType() {
		if (top > 0) return Data[top-1].type;
		else return -1;
	}

	public int popi() throws InterpreterException {
		if (top <= 0)
			throw new InterpreterException("interpreters command stack is empty");
		if (Data[top-1].type != DataElement.intElement) {
			this.dumpValues(10);
			throw new InterpreterException("an integer value in the stack expected but '"
				+ Data[top-1].strValue + "' has been found");
		}
		top--;
		return Data[top].iValue;
	}

	public String pops() throws InterpreterException {
		if (top <= 0)
			throw new InterpreterException("interpreters command stack is empty");
		if (Data[top-1].type != DataElement.strElement)
			throw new InterpreterException("a string value in the stack expected");
		top--;
		return Data[top].strValue;
	}


	public void clear() {
		top = 0;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public void dumpValues(int iCount) {
		System.out.println("============================================");
		System.out.println("Values in stack: " + this.top);
		System.out.println("STACK DUMP:");
		for (int i = 0; i < iCount; i++) {
			if (top > 0) {
				top--;
				if (Data[top].type != DataElement.strElement)
					System.out.println(Data[top].strValue);
				if (Data[top].type != DataElement.intElement)
					System.out.println(Data[top].iValue);
				else System.out.println("empty element");
			} else break;
		}
		System.out.println("============================================");
	}
}