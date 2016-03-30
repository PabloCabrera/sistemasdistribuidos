package ar.edu.unlu.pcabrera.remotecontrol

public interface TVDisplay {
	public void setChannel (int channel);
	public void setVolume (int volume);
	public void setMuted (boolean muted);
	public void setPower (boolean power);
	public void writeChannel (String str);
}
