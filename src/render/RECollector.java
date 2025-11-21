package render;

import java.awt.*;

public class RECollector {
	public final int maxElements = 1024;
	public int currentSet = 512;
	public int cntElements;

	public int iHead, iTail;
	public RenderedElement Elements[];	

	public RECollector() {
		int i;
		iHead = -1;
		iTail = -1;
		Elements = new RenderedElement[maxElements];
		for (i=0; i<maxElements; i++) {
			Elements[i] = new RenderedElement();
		}
	}
	
	public void clearList() {
		int i;
		cntElements = 0;
		for (i=0; i<maxElements; i++) {
			Elements[i].inList = false;
		}
	}

	private void findPlace(RenderedElement theElement, int i) {
			//find the elements place in the list
			boolean isFindPlace = false;
			int k = -1;
			int j = iHead;
			while (isFindPlace == false && j != -1) {
				////System.out.println("OK:" + Elements[j].distance + " > " + Elements[i].distance);
				if (theElement.test(Elements[j])) {
					isFindPlace = true;
					k = Elements[j].prevElement;
				} else {
					j = Elements[j].nextElement;
					if (j == -1) k = iTail;
				}
			}
			//insert the element into the list
			Elements[i].prevElement = k;
			Elements[i].nextElement = j;
			if (k != -1) Elements[k].nextElement = i;
				else iHead = i;
			if (j != -1) Elements[j].prevElement = i;
				else iTail = i;
	}

	public boolean addElement(RenderedElement theElement) {
		if (theElement.distance <= 0) return false;
		if (!theElement.isInScreen) return false;
		if (cntElements == 0) {
			Elements[0] = theElement;
			iHead = 0;
			iTail = 0;
			Elements[0].prevElement = -1;
			Elements[0].nextElement = -1;
			Elements[0].inList = true;
			cntElements = 1;
		} else if (cntElements < currentSet) {
			//find the room for this new element
			int i = 0;
			while (Elements[i].inList == true) {i++;}

			//insert the element in the array
			Elements[i] = theElement;
			theElement.inList = true;
			cntElements++;

			findPlace(theElement, i);

		} else {	
			//there is no room for this new element
			//...
			//here we need to find farest element end remove it from list
			//and insert new element instead
			if (Elements[iTail].distance > theElement.distance) {
				Elements[iTail].inList = false;
				iTail = Elements[iTail].prevElement;
				Elements[iTail].nextElement = -1;
				cntElements--;
				this.addElement(theElement);
				return true;
			}
			return false;
		};

		return true;
	}

	public boolean removeElement() {
		return true;
	}
}