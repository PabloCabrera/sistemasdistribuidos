package ar.edu.unlu.pcabrera.remotecontrol;

public interface DVDDisplay {
	
	public void notifyVolume (int volume);
	public void notifyTime (long millisecs);
	public void notifyTray (boolean inside);
	public void notifyTrack (int number);
	public void notifyStatus (byte status);
	
}
