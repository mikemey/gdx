package uk.mmi.gaming.net.server;

public interface ServerCommListener {
	public void serverStarted();

	public void serverStopped();

	public void log(String string, Object... params);
}
