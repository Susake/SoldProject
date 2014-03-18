package db.dd.mockloc.service;

import com.apsalar.sdk.Apsalar;

import db.dd.mockloc.TestGPSActivity;
import db.dd.mockloc.constaints.Constants;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EndTestGPSReceiver extends BroadcastReceiver {
	public static Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		TestGPSActivity.endTestGPS(context);
		
		try {
			// track
			Apsalar.startSession((Activity) context, "jacksun", "pyhffNv1");
			Apsalar.event(Constants.TRACK_EVENT_BROADCAST_END_MOCK);
		} catch (Exception e) {

		}
	}
}