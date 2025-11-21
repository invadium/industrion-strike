package scene;

import primitives.Text;
import primitives.Trixel;
import primitives.Line;
import primitives.Triangle;
import primitives.Rectangle;

class Modifier {
	public boolean isParsed;
	public boolean Solid, Wareframe, Illuminated, Bitmapped;
	public final int iMetalDef = 150;
	public int iMetal;
	public int iVolume;
	public int sFrame, eFrame;

	Modifier() {
		resetModifiers();
	}

	public void resetModifiers() {
		isParsed = true;
		Solid = false;
		Wareframe = false;
		Illuminated = false;
		Bitmapped = false;
		iMetal = iMetalDef;
		iVolume = 1;
		sFrame = 0;
		eFrame = 0;
	}

	public void setValues(Triangle theTriangle) {
		theTriangle.matIlluminated = this.Illuminated;
		theTriangle.Solid = this.Solid;
		theTriangle.Wareframe = this.Wareframe;
		if (!Solid && !Wareframe) theTriangle.Solid = true;
		theTriangle.matMetal = this.iMetal;
		theTriangle.sFrame = this.sFrame;
		theTriangle.eFrame = this.eFrame;
	}

	public void setValues(Rectangle theRectangle) {
		theRectangle.matIlluminated = this.Illuminated;
		theRectangle.Solid = this.Solid;
		theRectangle.Wareframe = this.Wareframe;
		if (!Solid && !Wareframe) theRectangle.Solid = true;
		theRectangle.matMetal = this.iMetal;
		theRectangle.sFrame = this.sFrame;
		theRectangle.eFrame = this.eFrame;
	}

	public void setValues(Line theLine) {
		theLine.sFrame = this.sFrame;
		theLine.eFrame = this.eFrame;
		theLine.volume = this.iVolume;
	}

	public void setValues(Trixel theTrixel) {
		theTrixel.sFrame = this.sFrame;
		theTrixel.eFrame = this.eFrame;
		theTrixel.isBitmapped = this.Bitmapped;
		theTrixel.volume = this.iVolume;
	}

	public void setValues(Text theText) {
		theText.volume = this.iVolume;
	}



	public boolean isModified() {
		if (sFrame!=0 || eFrame!=0 || iMetal!=iMetalDef
			|| Solid || Wareframe || Illuminated || Bitmapped) return true;
		return false;
	}

	public boolean isModified2() {
		if (iMetal!=iMetalDef || Solid || Wareframe || Illuminated) return true;
		return false;
	}
}