package ua.killer.mobileapp.receivers;

import ua.killer.mobileapp.application.Utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent arg1) {
		Utils.addServiceToAlarm(ctx);
	}

}
