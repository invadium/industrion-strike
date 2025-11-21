package media;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;

/**
 * Incapsulates media needed in the game
 * 
 * @author Igor Khotin
 *
 */
public class Media {
	public static final int maxImages = 16;
	private Applet theApplet;

	public Image imgResult;

	public AudioClip auNoise;
	public AudioClip auPing;
	public AudioClip auMsg;
	public AudioClip auSignal;
	public AudioClip auAlarm;
	public AudioClip auTeleport;

	public AudioClip auExplosion;
	public AudioClip auBlow;
	public AudioClip auHit;
	public AudioClip auWeapon[] = new AudioClip[4];

	public Media(Applet theApplet) {
		this.theApplet = theApplet;

		try {
			imgResult = theApplet.getImage(theApplet.getCodeBase(), "data/images/result.jpg");
			MediaTracker all = new MediaTracker(theApplet);

			//auNoise = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/noise.au");
			auNoise = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/rambling-noise.wav");
			//auPing = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/ping.au");
			auPing = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/select.wav");
			//auMsg = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/msg.au");
			auMsg = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/ping.wav");
			//auAlarm = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/alarm.au");
			auAlarm = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/digital-alarm.wav");
			//auSignal = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/signal.au");
			auSignal = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/alarm-2-9.wav");
			//auTeleport = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/teleport.au");
			auTeleport = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/machine.wav");

			//auWeapon[0] = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/flak.au");
			auWeapon[0] = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/blast-04.wav");
			//auWeapon[1] = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/medium_laser.au");
			//auWeapon[1] = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/quick-laser.wav");
			auWeapon[1] = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/cool-lil-laser-1.wav");
			auWeapon[2] = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/dusty-laser-wav");
			auWeapon[3] = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/rayo-laser.wav");

			//auExplosion = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/explosion.au");
			auExplosion = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/explosion-1.wav");
			//auBlow = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/blow.au");
			auBlow = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/explosion-medium.wav");
			//auHit = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/hit.au");
			auHit = theApplet.getAudioClip(theApplet.getCodeBase(), "data/audio/impact-095.wav");

			all.addImage(imgResult, 0);
			all.waitForAll();
		} catch (InterruptedException e) {}		
	}
}
