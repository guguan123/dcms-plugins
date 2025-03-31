package ua.killer.mobileapp.listeners;

import org.json.JSONObject;

public interface OnLoginListener {
	public void onCheckStart(String nick, String password);
	public void onCheckFinish(String nick, String password, JSONObject result);
}
