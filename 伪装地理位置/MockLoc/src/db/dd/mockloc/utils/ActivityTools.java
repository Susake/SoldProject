package db.dd.mockloc.utils;

import db.dd.mockloc.MyNotification;
import db.dd.mockloc.R;
import db.dd.mockloc.constaints.Constants;
import db.dd.mockloc.model.DataAdapter;
import db.dd.mockloc.model.DataModel;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.util.Log;



public class ActivityTools {

	public static void deleteFav(Context ctx, DataModel dm) {
		DataAdapter db = new DataAdapter(ctx);
		db.deleteFavByName(dm);

		Intent it = new Intent(Constants.DELETE_BROADCAST);
		it.putExtra(Constants.EXTRA_NAME, dm.name);
		it.putExtra(Constants.EXTRA_LAT, dm.lat);
		it.putExtra(Constants.EXTRA_LNG, dm.lng);
		it.putExtra(Constants.EXTRA_TYPE, dm.type);
		ctx.sendBroadcast(it);
	}

	public static void addFav(Context ctx, DataModel dm) {
		DataAdapter db = new DataAdapter(ctx);

		dm.type = Constants.TYPE_FAVORITE;
		db.insertFav(dm);

		Intent it = new Intent(Constants.ADD_BROADCAST);
		it.putExtra(Constants.EXTRA_NAME, dm.name);
		it.putExtra(Constants.EXTRA_LAT, dm.lat);
		it.putExtra(Constants.EXTRA_LNG, dm.lng);
		it.putExtra(Constants.EXTRA_TYPE, dm.type);
		ctx.sendBroadcast(it);

	}

	public static void setLocation(Context ctx, DataModel dm) {
		Intent it = new Intent(Constants.SHOW_ON_MAP_BROADCAST);
		it.putExtra(Constants.EXTRA_NAME, dm.name);
		it.putExtra(Constants.EXTRA_LAT, dm.lat);
		it.putExtra(Constants.EXTRA_LNG, dm.lng);
		it.putExtra(Constants.EXTRA_TYPE, dm.type);
		ctx.sendBroadcast(it);
	}
	
	public static LocationManager getTestLocationManager(Context ctx) {
		try {
			LocationManager locMgr = (LocationManager) ctx
					.getSystemService(Context.LOCATION_SERVICE);
			locMgr.addTestProvider(Constants.GPS_LABEL, false, false, false,
					false, true, true, true, 0, 5);
			
			// Fix the issue about can't access gps after remove the test provider
//			locMgr.setTestProviderStatus(Constants.GPS_LABEL,
//					LocationProvider.AVAILABLE, null, System
//							.currentTimeMillis());
//			locMgr.setTestProviderEnabled(Constants.GPS_LABEL, true);
			return locMgr;
		} catch (Exception e) {
			Log.e("dd", e.getMessage());
		}

		return null;
	}

	public static void disableNetworkLocation(Context ctx) {
		try {
			LocationManager locMgr = (LocationManager) ctx
					.getSystemService(Context.LOCATION_SERVICE);
			locMgr.addTestProvider(Constants.NETWORK_LABEL, false, false,
					false, false, true, true, true, 0, 5);
			locMgr.setTestProviderStatus(Constants.NETWORK_LABEL,
					LocationProvider.TEMPORARILY_UNAVAILABLE, null, System
							.currentTimeMillis());
			locMgr.setTestProviderEnabled(Constants.NETWORK_LABEL, true);
		} catch (Exception e) {
			//e.printStackTrace();
			Log.e("daodao", e.getMessage());
			
			Utils.dialogCreate(ctx, ctx
					.getString(R.string.errorMockSettingDisabled),
					new DialogClick(ctx), false);
					
		}
	}

	public static void enableNetworkLocation(Context ctx) {
		try {
			LocationManager locMgr = (LocationManager) ctx
					.getSystemService(Context.LOCATION_SERVICE);
			locMgr.clearTestProviderEnabled(Constants.NETWORK_LABEL);
			locMgr.removeTestProvider(Constants.NETWORK_LABEL);
			locMgr.setTestProviderEnabled(Constants.NETWORK_LABEL, false);
		} catch (Exception e) {
			Utils.dialogCreate(ctx, ctx
					.getString(R.string.errorMockSettingDisabled),
					new DialogClick(ctx), false);
		}
	}

	public static MyNotification getMyNotification(Context ctx) {
		return new MyNotification(ctx, Constants.NOTIFICATION_ID);
	}

}
