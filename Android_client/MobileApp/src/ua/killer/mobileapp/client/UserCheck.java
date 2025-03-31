package ua.killer.mobileapp.client;

import java.util.HashMap;

import org.json.JSONObject;

import android.util.Log;
import ua.killer.mobileapp.application.Constants;
import ua.killer.mobileapp.listeners.OnLoginListener;
import ua.killer.mobileapp.listeners.OnServerConnectListener;

public class UserCheck {
	private OnLoginListener listener;
	private String loginNick, loginPassword;
	private ServerConnector connector;
	private boolean isLoading;
	
	public UserCheck(String loginNick, String loginPassword, OnLoginListener listener) {
		this.listener = listener;
		this.loginNick = loginNick;
		this.loginPassword = loginPassword;
	}
	
	public void setOnLoginListener(OnLoginListener listener) {
		this.listener = listener;
	}
	
	public void connect() {
		HashMap<Object, Object> data = new HashMap<Object, Object>();
		data.put("nick", loginNick);
		data.put("password", loginPassword);
		connector = new ServerConnector(data, new OnServerConnectListener() {
			@Override
			public void onStartConnecting() {
				listener.onCheckStart(loginNick, loginPassword);
				isLoading = true;
			}

			@Override
			public void onFinishConnecting(String responseText) {
				JSONObject json = null;
				try {
					json = new JSONObject(responseText);
				} catch (Exception e) {
					Log.v("myLogs", e.toString());
				} finally {
					listener.onCheckFinish(loginNick, loginPassword, json);
				}
				isLoading = false;
			}
		});
		connector.send(Constants.SERVER_AUTH_URL);
	}

	public boolean isLoading() {
		return isLoading;
	}
}
