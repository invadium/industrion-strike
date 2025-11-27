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
	private Applet applet;

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

	public Media(Applet applet) {
		this.applet = applet;

		try {
			imgResult = applet.getImage(applet.getCodeBase(), "data/images/result.jpg");
			MediaTracker all = new MediaTracker(applet);

			auNoise = applet.getAudioClip(applet.getCodeBase(), "data/audio/rambling-noise.wav");
			auPing = applet.getAudioClip(applet.getCodeBase(), "data/audio/select.wav");
			auMsg = applet.getAudioClip(applet.getCodeBase(), "data/audio/ping.wav");
			auAlarm = applet.getAudioClip(applet.getCodeBase(), "data/audio/digital-alarm.wav");
			auSignal = applet.getAudioClip(applet.getCodeBase(), "data/audio/alarm-2-9.wav");
			auTeleport = applet.getAudioClip(applet.getCodeBase(), "data/audio/machine.wav");

			auWeapon[0] = applet.getAudioClip(applet.getCodeBase(), "data/audio/blast-04.wav");
			auWeapon[1] = applet.getAudioClip(applet.getCodeBase(), "data/audio/cool-lil-laser-1.wav");
			auWeapon[2] = applet.getAudioClip(applet.getCodeBase(), "data/audio/dusty-laser-wav");
			auWeapon[3] = applet.getAudioClip(applet.getCodeBase(), "data/audio/rayo-laser.wav");

			auExplosion = applet.getAudioClip(applet.getCodeBase(), "data/audio/explosion-1.wav");
			auBlow = applet.getAudioClip(applet.getCodeBase(), "data/audio/explosion-medium.wav");
			auHit = applet.getAudioClip(applet.getCodeBase(), "data/audio/impact-095.wav");

			all.addImage(imgResult, 0);
			all.waitForAll();
		} catch (InterruptedException e) {}		
	}
}
