package ua.killer.mobileapp.listeners;

public interface OnServerConnectListener {
	public void onStartConnecting();
	public void onFinishConnecting(String responseText);
}
