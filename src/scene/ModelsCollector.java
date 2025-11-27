package scene;

import java.awt.*;
import java.applet.*;
import parser.*;
import primitives.*;

public class ModelsCollector {
	Applet applet;
	Graphics canvas;
	Parser prs;
	public boolean isError = false;

	public final int maxModels = 256;

	public int cntModels = 0;
	public Model models[];

	public void updateCanvas(Graphics canvas) {
		this.canvas = canvas;
	}
	
	public ModelsCollector(Applet applet, Graphics canvas, String strFileName)
			throws ModelsException, ParserException {
		this.applet = applet;
		this.canvas = canvas;		
		prs = new Parser(applet);
		String strNames[] = new String [maxModels];
		int declModels = 0;
		
	try {
		Token tok;
		//load models list
        System.out.println("loading '" + strFileName + "' models declaration");
		if (prs.openStream(strFileName)) {
			do {
				tok = prs.getToken();
                
				if (tok!=null) {
                    System.out.println("token: " + tok.strValue);
					isMatch(tok, Token.STRING);
					if (declModels >= maxModels) throw new ModelsException(4, "too many models files",
							prs.strFileName, prs.iLine, prs.iLineChar);
					strNames[declModels] = tok.strValue;
					declModels++;
				}
			} while (tok!=null);
		} else throw new ModelsException(5, "can't open models declaration file", prs.strFileName, 0, 0);
        System.out.println("declared models: " + declModels);
        
		//create rooms for all declarated models
		models = new Model[declModels];
		Model curModel;
		//modifiers
		Modifier mod = new Modifier();	
		primitives.Text text;
		primitives.Trixel trixel;
		primitives.Line line;
		primitives.Triangle triangle;
		primitives.Rectangle rectangle;

		//loading all declarated models
		for (int i=0; i<declModels; i++)
		if (prs.openStream(strNames[i])) {
			//get header MODEL
			tok = prs.getToken(); isMatch(tok, Token.STRING, "MODEL");
			System.out.println("loading '" + prs.strFileName + "'...");
			curModel = new Model();
			Color curColor = Color.blue; //set default color

			//load properties loop
			// <MODIFIER> = <VALUE>; | <MODIFIER> <VALUES LIST>;
			while (tok!=null) {
				tok = prs.getToken(); if (tok == null && mod.isParsed == true) break;
				isMatch(tok, Token.STRING);
				mod.isParsed = false;
					
				if (tok.strValue.equals("SOLID")) mod.Solid = true;
				else if (tok.strValue.equals("WAREFRAME")) mod.Wareframe = true;
				else if (tok.strValue.equals("ILLUMINATED")) mod.Illuminated = true;
				else if (tok.strValue.equals("BITMAPPED")) mod.Bitmapped = true;
				else if (tok.strValue.equals("METAL")) {
					tok = prs.getToken(); isMatch(tok, Token.NUMBER);
					mod.iMetal = tok.iValue;
				}
				else if (tok.strValue.equals("VOLUME")) {
					tok = prs.getToken(); isMatch(tok, Token.NUMBER);
					mod.iVolume = tok.iValue;
				}
				else if (tok.strValue.equals("FRAMES")) {
					tok = prs.getToken(); isMatch(tok, Token.NUMBER);
					mod.sFrame = tok.iValue;
					tok = prs.getToken(); isMatch(tok, Token.NUMBER);
					mod.eFrame = tok.iValue;
				}
				else if (tok.strValue.equals("SHIP")) {
					if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
					tok = prs.getToken(); isMatch(tok, Token.SEMI);
					curModel.isShip = true;
				}
				else if (tok.strValue.equals("FIGHTER")) {
					if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
					tok = prs.getToken(); isMatch(tok, Token.SEMI);
					curModel.isFighter = true;
				}
				else if (tok.strValue.equals("WEAPON")) {
					if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
					tok = prs.getToken(); isMatch(tok, Token.SEMI);
					curModel.isWeapon = true;
				}
				else if (tok.strValue.equals("SPACE")) {
					if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
					tok = prs.getToken(); isMatch(tok, Token.SEMI);
					curModel.isSpace = true;
				}
				else if (tok.strValue.equals("STATIC")) {
					if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
					tok = prs.getToken(); isMatch(tok, Token.SEMI);
					curModel.isStatic = true;
				}
				else if (tok.strValue.equals("MISSILE")) {
					if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
					tok = prs.getToken(); isMatch(tok, Token.SEMI);
					curModel.isMissile = true;
				}
				else if (tok.strValue.equals("NAME")) {
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						tok = prs.getToken(); isMatch(tok, Token.EQ);
						tok = prs.getToken(); isMatch(tok, Token.STRING);
						curModel.strModelName = tok.strValue;
						tok = prs.getToken(); isMatch(tok, Token.SEMI);
						
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("IDNAME")) {
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						tok = prs.getToken(); isMatch(tok, Token.EQ);
						tok = prs.getToken(); isMatch(tok, Token.STRING);
						curModel.strIDName = tok.strValue;						
						tok = prs.getToken(); isMatch(tok, Token.SEMI);						
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("IMAGE")) {
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						tok = prs.getToken(); isMatch(tok, Token.EQ);
						tok = prs.getToken(); isMatch(tok, Token.STRING);
						curModel.imgModel = applet.getImage(applet.getCodeBase(), tok.strValue);
						canvas.drawImage(curModel.imgModel, 0, 0, applet);
						tok = prs.getToken(); isMatch(tok, Token.SEMI);
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("SOUND")) {
						//if (!curModel.isWeapon && !curModel.isMissile) raiseException("dirrective apply only to weapon or missile models");
						curModel.fx = getFixedValue(mod);
				}
				else if (tok.strValue.equals("COLOR")) {
						curColor = takeColor();
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("FARCOLOR")) {
						curModel.farColor = new RColor(takeColor());
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("TEXT")) {
						if (mod.isModified2()) raiseException("prefix modifiers are not allowed here");
						text = takeText(curColor);
						mod.setValues(text);
						curModel.addPrimitive(text);
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("TRIXEL")) {
						if (mod.isModified2()) raiseException("prefix modifiers are not allowed here");
						trixel = takeTrixel(curColor);
						mod.setValues(trixel);
						curModel.addPrimitive(trixel);
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("LINE")) {
						if (mod.isModified2()) raiseException("prefix modifiers are not allowed here");
						line = takeLine(curColor);
						mod.setValues(line);
						curModel.addPrimitive(line);
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("TRIANGLE")) {
						triangle = takeTriangle(curColor);
						mod.setValues(triangle);
						curModel.addPrimitive(triangle);
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("RECTANGLE")) {
						rectangle = takeRectangle(curColor);
						mod.setValues(rectangle);
						curModel.addPrimitive(rectangle);
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("SHIFT_X")) {
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						shiftX(curModel);
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("SHIFT_Y")) {
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						shiftY(curModel);
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("SHIFT_Z")) {
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						shiftZ(curModel);
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("REDUCE_SCALE")) {
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						reduceScale(curModel);
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("INCREASE_SCALE")) {
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						increaseScale(curModel);
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("FRAGMENT")) {
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						tok = prs.getToken(); isMatch(tok, Token.STRING);
						int modIndex = this.getIndexByName(tok.strValue);
						if (modIndex == -1) raiseException("can't mount unexistent model " + tok.strValue);
						tok = prs.getToken(); isMatch(tok, Token.SEMI);

						curModel.fragment = models[modIndex];
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("BLOW")) {
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						tok = prs.getToken(); isMatch(tok, Token.STRING);
						int modIndex = this.getIndexByName(tok.strValue);
						if (modIndex == -1) raiseException("can't mount unexistent model " + tok.strValue);
						tok = prs.getToken(); isMatch(tok, Token.SEMI);

						curModel.blow = models[modIndex];
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("MOUNT")) {
						if (!curModel.isFighter) raiseException("can't mount fron laser to non-fighter model");
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						if (curModel.cntWeapons >= curModel.maxWeapons) raiseException("to many weapons have been mounted");
						tok = prs.getToken(); isMatch(tok, Token.STRING);
						int modIndex = this.getIndexByName(tok.strValue);
						if (modIndex == -1) raiseException("can't mount unexistent model " + tok.strValue);
						tok = prs.getToken(); isMatch(tok, Token.STRING, "TO");
						curModel.rackCoord[curModel.cntWeapons] = new Coord3D();
						tok = prs.getToken(); isMatch(tok, Token.NUMBER);
						curModel.rackCoord[curModel.cntWeapons].x = tok.iValue;
						tok = prs.getToken(); isMatch(tok, Token.NUMBER);
						curModel.rackCoord[curModel.cntWeapons].y = tok.iValue;
						tok = prs.getToken(); isMatch(tok, Token.NUMBER);
						curModel.rackCoord[curModel.cntWeapons].z = tok.iValue;
						tok = prs.getToken(); isMatch(tok, Token.SEMI);

						curModel.weaponRack[curModel.cntWeapons] = models[modIndex];
						curModel.cntWeapons++;
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("MOUNT_TURRET")) {
						if (!curModel.isShip) raiseException("turret can be mounted only on capital ships");
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						if (curModel.cntWeapons >= curModel.maxWeapons) raiseException("to many weapons have been mounted");
						tok = prs.getToken(); isMatch(tok, Token.STRING);
						int modIndex = this.getIndexByName(tok.strValue);
						if (modIndex == -1) raiseException("can't mount unexistent model " + tok.strValue);
						tok = prs.getToken(); isMatch(tok, Token.STRING, "TO");
						curModel.rackCoord[curModel.cntWeapons] = new Coord3D();
						tok = prs.getToken(); isMatch(tok, Token.NUMBER);
						curModel.rackCoord[curModel.cntWeapons].x = tok.iValue;
						tok = prs.getToken(); isMatch(tok, Token.NUMBER);
						curModel.rackCoord[curModel.cntWeapons].y = tok.iValue;
						tok = prs.getToken(); isMatch(tok, Token.NUMBER);
						curModel.rackCoord[curModel.cntWeapons].z = tok.iValue;

						//get sector
						tok = prs.getToken(); isMatch(tok, Token.STRING, "WITH");
						tok = prs.getToken(); isMatch(tok, Token.STRING, "SECTOR");
						curModel.weaponSector[curModel.cntWeapons] = 0;
						byte bValue = 1;
						for (int j=0; j<8; j++) {
							tok = prs.getToken(); isMatch(tok, Token.NUMBER);
							if (tok.iValue!=0 && tok.iValue!=1) raiseException("must be binary values here");
							if (tok.iValue==1) curModel.weaponSector[curModel.cntWeapons] |= bValue;
							bValue <<= 1;
						}

						tok = prs.getToken(); isMatch(tok, Token.SEMI);

						curModel.weaponRack[curModel.cntWeapons] = models[modIndex];
						curModel.cntWeapons++;
						mod.resetModifiers();
				}
				else if (tok.strValue.equals("MOUNT_LAUNCHER")) {
						if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
						if (curModel.cntLaunchers >= Model.maxLaunchers) raiseException("too many launchers");
						//get model name and missile capacity
						tok = prs.getToken(); isMatch(tok, Token.STRING);
						int modIndex = this.getIndexByName(tok.strValue);
						if (modIndex == -1) raiseException("can't mount unexistent model " + tok.strValue);
						tok = prs.getToken(); isMatch(tok, Token.STRING, "CAPACITY");
						tok = prs.getToken(); isMatch(tok, Token.NUMBER);
						curModel.missileCapacity[curModel.cntLaunchers] = tok.iValue;
						curModel.missilesRack[curModel.cntLaunchers] = models[modIndex];
						//get coordinates
						tok = prs.getToken(); isMatch(tok, Token.STRING, "TO");
						curModel.launchersCoord[curModel.cntLaunchers] = new Coord3D();
						tok = prs.getToken(); isMatch(tok, Token.NUMBER);
						curModel.launchersCoord[curModel.cntLaunchers].x = tok.iValue;
						tok = prs.getToken(); isMatch(tok, Token.NUMBER);
						curModel.launchersCoord[curModel.cntLaunchers].y = tok.iValue;
						tok = prs.getToken(); isMatch(tok, Token.NUMBER);
						curModel.launchersCoord[curModel.cntLaunchers].z = tok.iValue;
						tok = prs.getToken(); isMatch(tok, Token.SEMI);
						curModel.cntLaunchers++;
				}
				else if (tok.strValue.equals("TARGET_FACTOR")) {
						curModel.targetFactor = getFixedValue(mod);
				}
				else if (tok.strValue.equals("FRAMES_COUNT")) {
						curModel.cntFrames = getFixedValue(mod);
				}
				else if (tok.strValue.equals("FRAGMENTS_COUNT")) {
						curModel.fragments = getFixedValue(mod);
				}
				else if (tok.strValue.equals("BLOWS_COUNT")) {
						curModel.blows = getFixedValue(mod);
				}
				else if (tok.strValue.equals("REACH_RANGE")) {
						curModel.reachRange = (double)getFixedValue(mod);
				}
				else if (tok.strValue.equals("COST")) {
						curModel.Cost = getFixedValue(mod);
				}
				else if (tok.strValue.equals("RADIUS")) {
						curModel.radius = (double)getFixedValue(mod);
				}
				else if (tok.strValue.equals("RANGE")) {
						curModel.range = (double)getFixedValue(mod);
				}
				else if (tok.strValue.equals("STARTSPEED")) {
						curModel.startSpeed = getFixedValue(mod);
				}
				else if (tok.strValue.equals("MAXSPEED")) {
						curModel.maxSpeed = getFixedValue(mod);
				}
				else if (tok.strValue.equals("CRUISESPEED")) {
						curModel.cruiseSpeed = getFixedValue(mod);
				}
				else if (tok.strValue.equals("ACCELERATION")) {	
						curModel.acceleration = getFixedValue(mod);
				}
				else if (tok.strValue.equals("FUEL_DEPLETION")) {	
						curModel.fuelDepletion = getFixedValue(mod);
				}
				else if (tok.strValue.equals("MAXYAW")) {
						curModel.maxYaw = getFixedValue(mod);
				}
				else if (tok.strValue.equals("MAXPITCH")) {
						curModel.maxPitch = getFixedValue(mod);
				}	
				else if (tok.strValue.equals("MAXROLL")) {
						curModel.maxRoll = getFixedValue(mod);
				}
				else if (tok.strValue.equals("YAW_ACCELERATION")) {
						curModel.yawAcceleration = getFixedValue(mod);
				}
				else if (tok.strValue.equals("PITCH_ACCELERATION")) {
						curModel.pitchAcceleration = getFixedValue(mod);
				}
				else if (tok.strValue.equals("ROLL_ACCELERATION")) {
						curModel.rollAcceleration = getFixedValue(mod);
				}
				else if (tok.strValue.equals("YAW_FUEL_DEPLETION")) {
						curModel.yawFuelDepletion = getFixedValue(mod);
				}
				else if (tok.strValue.equals("PITCH_FUEL_DEPLETION")) {
						curModel.pitchFuelDepletion = getFixedValue(mod);
				}
				else if (tok.strValue.equals("ROLL_FUEL_DEPLETION")) {
						curModel.rollFuelDepletion = getFixedValue(mod);
				}
				else if (tok.strValue.equals("FUELTANK")) {
						curModel.FuelTank = getFixedValue(mod);
				}
				else if (tok.strValue.equals("ENERGY")) {
						curModel.Energy = getFixedValue(mod);
				}
				else if (tok.strValue.equals("RECHARGE")) {
						curModel.energyRecharge = getFixedValue(mod);
				}
				else if (tok.strValue.equals("SHIELD")) {
						curModel.Shield = getFixedValue(mod);
				}
				else if (tok.strValue.equals("SHRECHARGE")) {
						curModel.shieldRecharge = getFixedValue(mod);
				}
				else if (tok.strValue.equals("HULL")) {
						curModel.Hull = getFixedValue(mod);
				}
				else if (tok.strValue.equals("MARK")) {
						curModel.radarMark = getFixedValue(mod);
				}
				else if (tok.strValue.equals("FARSIZE")) {
						curModel.farSize = getFixedValue(mod);
				}
				else if (tok.strValue.equals("LIFETIME")) {
						curModel.lifeTime = getFixedValue(mod);
				}
				else if (tok.strValue.equals("RECHARGETIME")) {
						curModel.rechargeTime = getFixedValue(mod);
				}
				else {
                    System.out.println("incorrect modifier: " + tok.strValue);
                    raiseException("incorrect modifier: " + tok.strValue);
                }
			}

			//check on correct end of the file
			if (mod.isParsed == false)
				throw new ModelsException(7, "unexpected end of the file occured",
								prs.strFileName, prs.iLine, prs.iLineChar);

			models[i] = curModel;
			System.out.println("succesfull");
			cntModels++;
		} else throw new ModelsException(7, "can't open models file", prs.strFileName, 0, 0);

	} catch(ModelsException e) {
        e.printStackTrace();
		throw(e);
	} catch(ParserException e) {
        e.printStackTrace();
		throw(e);
	}
	}

	private int getFixedValue(Modifier mod)
					throws ModelsException, ParserException {
		Token tok;
		if (mod.isModified()) raiseException("prefix modifiers are not allowed here");
		tok = prs.getToken(); isMatch(tok, Token.EQ);
		tok = prs.getToken(); isMatch(tok, Token.NUMBER);
		int fixedValue = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.SEMI);						
		mod.resetModifiers();
		return fixedValue;
	}

	private Color takeColor() throws ModelsException, ParserException {
		int r, g, b;
		Token tok;

		tok = prs.getToken(); isMatch(tok, Token.NUMBER); r = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); g = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); b = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		
		Color newColor = new Color(r, g, b);
		return newColor;
	}

	private Text takeText(Color curColor) throws ModelsException, ParserException {
		int x, y, z;
		Token tok;

		tok = prs.getToken(); isMatch(tok, Token.STRING); String txtNote = tok.strValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); x = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); y = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); z = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		
		Text text = new Text(curColor, (double)x, (double)y, (double)z, txtNote);
		return text;
	}

	private Trixel takeTrixel(Color curColor) throws ModelsException, ParserException {
		int x, y, z;
		Token tok;

		tok = prs.getToken(); isMatch(tok, Token.NUMBER); x = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); y = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); z = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		
		Trixel trixel = new Trixel(curColor, (double)x, (double)y, (double)z);
		return trixel;
	}

	private Line takeLine(Color curColor) throws ModelsException, ParserException {
		int x1, y1, z1, x2, y2, z2;
		Token tok;

		tok = prs.getToken(); isMatch(tok, Token.NUMBER); x1 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); y1 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); z1 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); x2 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); y2 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); z2 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		
		Line line = new Line(curColor, (double)x1, (double)y1, (double)z1,
								(double)x2, (double)y2, (double)z2);
		return line;
	}

	private Triangle takeTriangle(Color curColor) throws ModelsException, ParserException {
		int x1, y1, z1, x2, y2, z2, x3, y3, z3;
		Token tok;

		tok = prs.getToken(); isMatch(tok, Token.NUMBER); x1 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); y1 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); z1 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); x2 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); y2 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); z2 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); x3 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); y3 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); z3 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		
		Triangle triangle = new Triangle(curColor,
								(double)x1, (double)y1, (double)z1,
								(double)x2, (double)y2, (double)z2,
								(double)x3, (double)y3, (double)z3);
		return triangle;
	}

	private primitives.Rectangle takeRectangle(Color curColor)
							throws ModelsException, ParserException {
		int x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4;
		Token tok;

		tok = prs.getToken(); isMatch(tok, Token.NUMBER); x1 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); y1 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); z1 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); x2 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); y2 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); z2 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); x3 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); y3 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); z3 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); x4 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); y4 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER); z4 = tok.iValue;
		tok = prs.getToken(); isMatch(tok, Token.SEMI);
		
		primitives.Rectangle rectangle = new primitives.Rectangle(curColor,
								(double)x1, (double)y1, (double)z1,
								(double)x2, (double)y2, (double)z2,
								(double)x3, (double)y3, (double)z3,
								(double)x4, (double)y4, (double)z4);
		return rectangle;
	}

	private void shiftX(Model M) throws ModelsException, ParserException {
		Token tok;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER);
		double factor = (double)tok.iValue;
		for (int i = 0; i<M.cntPrimitives; i++) {
			M.primitives[i].shift(factor, 0.0, 0.0);
		}
		for (int i = 0; i<M.cntWeapons; i++) {
			M.shiftWeapon(i, factor, 0.0, 0.0);
		}

		tok = prs.getToken(); isMatch(tok, Token.SEMI);
	}

	private void shiftY(Model M) throws ModelsException, ParserException {
		Token tok;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER);
		double factor = (double)tok.iValue;
		for (int i = 0; i<M.cntPrimitives; i++) {
			M.primitives[i].shift(0.0, factor, 0.0);
		}
		for (int i = 0; i<M.cntWeapons; i++) {
			M.shiftWeapon(i, 0.0, factor, 0.0);
		}

		tok = prs.getToken(); isMatch(tok, Token.SEMI);
	}

	private void shiftZ(Model M) throws ModelsException, ParserException {
		Token tok;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER);
		double factor = (double)tok.iValue;
		for (int i = 0; i<M.cntPrimitives; i++) {
			M.primitives[i].shift(0.0, 0.0, factor);
		}
		for (int i = 0; i<M.cntWeapons; i++) {
			M.shiftWeapon(i, 0.0, 0.0, factor);
		}

		tok = prs.getToken(); isMatch(tok, Token.SEMI);
	}

	private void reduceScale(Model M) throws ModelsException, ParserException {
		Token tok;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER);
		double factor = (double)tok.iValue;
		for (int i = 0; i<M.cntPrimitives; i++) {
			M.primitives[i].reduceScale(factor);
		}
		for (int i = 0; i<M.cntWeapons; i++) {
			M.reduceWeaponScale(i, factor);
		}

		tok = prs.getToken(); isMatch(tok, Token.SEMI);
	}

	private void increaseScale(Model M) throws ModelsException, ParserException {
		Token tok;
		tok = prs.getToken(); isMatch(tok, Token.NUMBER);
		double factor = (double)tok.iValue;
		for (int i = 0; i<M.cntPrimitives; i++) {
			M.primitives[i].increaseScale(factor);
		}
		for (int i = 0; i<M.cntWeapons; i++) {
			M.increaseWeaponScale(i, factor);
		}

		tok = prs.getToken(); isMatch(tok, Token.SEMI);
	}

	private void raiseException(String descr) throws ModelsException {
		throw new ModelsException(13, descr,
							prs.strFileName, prs.iLine, prs.iLineChar);
	}

	private void isMatch(Token tok, int type) throws ModelsException {
		if (tok == null)
			throw new ModelsException(1, "unexpected end of the file",
								prs.strFileName, prs.iLine, prs.iLineChar);
		if (tok.type!=type)
			throw new ModelsException(2, "'" + Token.Match[type] + "' expected but "
					+ Token.Match[tok.type] + " has been found",
					prs.strFileName, prs.iLine, prs.iLineChar);
	}

	private void isMatch(Token tok, int type, String strValue) throws ModelsException {
		if (tok == null)
			throw new ModelsException(1, "unexpected end of the file",
								prs.strFileName, prs.iLine, prs.iLineChar);
		if (tok.type!=type || (tok.strValue.equals(strValue) == false))
			throw new ModelsException(3, "modifier '" + strValue + "' expected",
					prs.strFileName, prs.iLine, prs.iLineChar);
	}

	public int getIndexByName(String strModelName) {
		int index = -1;
		for (int i=0; i<cntModels; i++)
			if (strModelName.equals(models[i].strModelName)) index = i;
		return index;
	}
}