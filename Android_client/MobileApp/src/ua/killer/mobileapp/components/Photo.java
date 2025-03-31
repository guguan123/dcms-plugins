package ua.killer.mobileapp.components;

import org.json.JSONException;
import org.json.JSONObject;

import ua.killer.mobileapp.application.Constants;

public class Photo {
	private boolean exists = false;
	private int id;
	private String extension;
	
	public Photo(JSONObject json) throws JSONException {
		exists = json.getBoolean("exists");
		
		if (exists()) {
			id = json.getInt("id");
			extension = json.getString("extension");
		}
	}
	
	public boolean exists() {
		return exists;
	}
	
	public int getId() {
		return id;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public String getHash() {
		return "PHOTO." + (exists() ? id : 0);
	}
	
	public String getUrl() {
		String link = exists() ? "/foto/foto128/" + getId() + "." + getExtension() : "/style/user/avatar.gif";
		return Constants.SERVER_HOST + link;
	}
	
	public static Photo parse(JSONObject json) throws JSONException {
		return new Photo(json);
	}
}
