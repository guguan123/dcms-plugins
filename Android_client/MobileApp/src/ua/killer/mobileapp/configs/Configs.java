package ua.killer.mobileapp.configs;

import ua.killer.mobileapp.application.Utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class Configs implements OnSharedPreferenceChangeListener {
	private Context mContext;
	private SharedPreferences sPrefs;
	public int userID;
	public boolean showMail, showDiscussions, showNotification, showTape, showFriends, showGuests, playSound, playVibration, onlyWiFi;
	public int lastCountMail, lastCountDiscussions, lastCountNotification, lastCountTape;
	public int lastMessageID, lastNewFriendID, lastNewGuestID;
	public int refresh;
	public String userToken;
	
	public Configs(Context _mContext) {
		this.mContext = _mContext;
		sPrefs = Utils.getDefaultSPrefs(mContext);
		_init();
	}
	
	private void _init() {
		userToken = sPrefs.getString("user_token", "");
		userID = sPrefs.getInt("user_id", 0);
		showMail = sPrefs.getBoolean("mail", false);
		showDiscussions = sPrefs.getBoolean("discussions", false);
		showNotification = sPrefs.getBoolean("notification", false);
		showTape = sPrefs.getBoolean("tape", false);
		showFriends = sPrefs.getBoolean("friends", false);
		showGuests = sPrefs.getBoolean("guests", false);
		playSound = sPrefs.getBoolean("sound", false);
		playVibration = sPrefs.getBoolean("vibration", false);
		onlyWiFi = sPrefs.getBoolean("onlyWiFi", false);
		refresh = Integer.valueOf(sPrefs.getString("refresh", "5"));
		
		lastCountMail = sPrefs.getInt("lastCountMail", 0);
		lastCountDiscussions = sPrefs.getInt("lastCountDiscussions", 0);
		lastCountNotification = sPrefs.getInt("lastCountNotification", 0);
		lastCountTape = sPrefs.getInt("lastCountTape", 0);
		lastMessageID = sPrefs.getInt("lastMessageID", 0);
		lastNewFriendID = sPrefs.getInt("lastNewFriendID", 0);
		lastNewGuestID = sPrefs.getInt("lastNewGuestID", 0);
		
		sPrefs.registerOnSharedPreferenceChangeListener(this);
	}
	
	public SharedPreferences getSPrefs() {
		return sPrefs;
	}
	
	public Editor getEditor() {
		return getSPrefs().edit();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String field) {
		_init();
	}

	public boolean isAuth() {
		return !userToken.isEmpty();
	}

}
