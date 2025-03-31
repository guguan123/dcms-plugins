package ua.killer.mobileapp.activities;

import ua.killer.mobileapp.R;
import ua.killer.mobileapp.application.Utils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	private Context mContext;
	private SharedPreferences sPrefs;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		mContext = this;
		sPrefs = Utils.getDefaultSPrefs(mContext);
		
		Preference exitPref = (Preference) findPreference("exit");
		ListPreference refreshPref = (ListPreference) findPreference("refresh");

		exitPref.setSummary(sPrefs.getString("user_nick", ""));
		exitPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle(getResources().getString(R.string.exit)).setMessage(getResources().getString(R.string.dialog_continue) + "?").setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Utils.logout(mContext);
						finish();
					}
				}).setNegativeButton(getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
				return false;
			}
		});

		refreshPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference pref, Object value) {
				String lastValue = sPrefs.getString("refresh", "0");
				sPrefs.edit().putString("refresh", value.toString()).commit();
				if (lastValue.equals("0") && !value.toString().equals("0"))
					Utils.addServiceToAlarm(mContext);
				else if (value.toString().equals("0") && !lastValue.equals("0"))
					Utils.removeServiceFromAlarm(mContext);
				return true;
			}
		});
	}
}
