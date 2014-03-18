package db.dd.mockloc.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apsalar.sdk.Apsalar;

import db.dd.mockloc.TestGPSActivity;
import db.dd.mockloc.constaints.Constants;

public class StartTestGPSReceiver extends BroadcastReceiver {
	public static Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		String name = intent.getStringExtra(Constants.EXTRA_NAME);
		Double lat = intent.getDoubleExtra(Constants.EXTRA_LAT, 0);
		Double lng = intent.getDoubleExtra(Constants.EXTRA_LNG, 0);
		float speed = intent.getFloatExtra(Constants.EXTRA_SPEED, 0f);
		float bearing = intent.getFloatExtra(Constants.EXTRA_BEARING, 0f);
		float accuracy = intent.getFloatExtra(Constants.EXTRA_ACCURACY, 0f);
		
		TestGPSActivity.startTestGPS(context, name, lat, lng, speed, accuracy, bearing, false);

		try {
			// track
			Apsalar.startSession((Activity) context, "jacksun", "pyhffNv1");
			Apsalar.event(Constants.TRACK_EVENT_BROADCAST_START_MOCK);
		} catch (Exception e) {

		}
	}
}