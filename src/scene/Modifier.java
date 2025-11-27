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

	public void setValues(Triangle triangle) {
		triangle.matIlluminated = this.Illuminated;
		triangle.Solid = this.Solid;
		triangle.Wareframe = this.Wareframe;
		if (!Solid && !Wareframe) triangle.Solid = true;
		triangle.matMetal = this.iMetal;
		triangle.sFrame = this.sFrame;
		triangle.eFrame = this.eFrame;
	}

	public void setValues(Rectangle rectangle) {
		rectangle.matIlluminated = this.Illuminated;
		rectangle.Solid = this.Solid;
		rectangle.Wareframe = this.Wareframe;
		if (!Solid && !Wareframe) rectangle.Solid = true;
		rectangle.matMetal = this.iMetal;
		rectangle.sFrame = this.sFrame;
		rectangle.eFrame = this.eFrame;
	}

	public void setValues(Line line) {
		line.sFrame = this.sFrame;
		line.eFrame = this.eFrame;
		line.volume = this.iVolume;
	}

	public void setValues(Trixel trixel) {
		trixel.sFrame = this.sFrame;
		trixel.eFrame = this.eFrame;
		trixel.isBitmapped = this.Bitmapped;
		trixel.volume = this.iVolume;
	}

	public void setValues(Text text) {
		text.volume = this.iVolume;
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