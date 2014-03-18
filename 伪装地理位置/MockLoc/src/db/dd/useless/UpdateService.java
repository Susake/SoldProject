package db.dd.useless;

import java.net.URLEncoder;

import org.json.JSONObject;

import db.dd.mockloc.R;
import db.dd.mockloc.constaints.Constants;
import db.dd.mockloc.service.LatestApplicationVersionInfo;
import db.dd.mockloc.utils.HttpHelper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;


public class UpdateService extends Service {
	public static final String TAG = "UpdateService";
	public static final boolean debug = false;
	public static final int UPDATE_NOTIFICATION_ID = 1;
	public static boolean isSetUpdateAlarm = false;

	private static final String DEVICE_NAME = android.os.Build.MODEL;
	public static final String APPLICATION_NAME = "APPLICATION_NAME";
	public static final String APPLICATION_VERSION = "APPLICATION_VERSION";

	/*
	 * This method is needed for 1.6 devices.
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		Bundle extras = intent.getExtras();
		final String applicationName = extras.getString(APPLICATION_NAME);
		final int applicationVersion = extras.getInt(APPLICATION_VERSION);

		final String thisAppUrl = Constants.UPDATE_URL + "?device="
				+ URLEncoder.encode(DEVICE_NAME) + "&app="
				+ URLEncoder.encode(applicationName);

		new Thread(new GetApplicationInfoThread(applicationVersion,
				applicationName, thisAppUrl)).start();
	}

	private class GetApplicationInfoThread implements Runnable {
		private LatestApplicationVersionInfo latestAppInfo;
		private int applicationVersion;
		private String applicationName;
		private String url;

		public GetApplicationInfoThread(int applicationVersion,
				String applicationName, String url) {
			this.applicationVersion = applicationVersion;
			this.applicationName = applicationName;
			this.url = url;
		}

		@Override
		public void run() {
			try {
				latestAppInfo = getApplicationInfo(url);
				if (!isAppValid(latestAppInfo)) {
					return;
				}
				if (Integer.parseInt(latestAppInfo.getVersion()) > applicationVersion) {

					final String tickertext = applicationName + " update "
							+ latestAppInfo.getVersion() + " available";
					final String title = applicationName + " update "
							+ latestAppInfo.getVersion() + " available";
					final String description = latestAppInfo.getMessage();
					showNotification(UPDATE_NOTIFICATION_ID,
							R.drawable.icon,
							// au.com.optus.android.framework.R.drawable.icn_status_update,
							tickertext, title, description, latestAppInfo
									.getUrl());
				}
			} catch (Exception e) {

			} finally {
				UpdateService.this.stopSelf();
			}
		}

	}
	
	private LatestApplicationVersionInfo getApplicationInfo(String url) {
		LatestApplicationVersionInfo latestAppInfo = new LatestApplicationVersionInfo();
		try {
			HttpHelper httpHelper = new HttpHelper();

			String s = httpHelper.performGet(url);
			if (!HttpHelper.UNABLE_TO_RETRIEVE_INFO.equals(s)
					&& !HttpHelper.NO_DATA_CONNECTION.equals(s)) {
				if (s == null || "".equals(s)) {
					return latestAppInfo;
				}

				JSONObject json = new JSONObject(s);

				latestAppInfo.setApp(json.optString("app", ""));
				latestAppInfo.setVersion(json.optString("version", ""));
				latestAppInfo.setUrl(json.optString("url", ""));
				latestAppInfo.setDevice(json.optString("device", ""));
				latestAppInfo.setMessage(json.optString("message", ""));

			}
		} catch (Exception e) {
		}
		return latestAppInfo;
	}

	private boolean isAppValid(LatestApplicationVersionInfo appInfo) {
		if (appInfo == null) {
			return false;
		}
		if (appInfo.getApp() == null || appInfo.getApp().trim().length() == 0) {
			return false;
		}
		if (appInfo.getVersion() == null
				|| appInfo.getVersion().trim().length() == 0) {
			return false;
		}
		try {
			Integer.parseInt(appInfo.getVersion());
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Not applicable
		return null;
	}

	protected void showNotification(int ID, int icon, String tickerText,
			String title, String desc, String url) {
		final Context context = this;
		final NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		final long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		Intent notificationIntent = null;
		if (url != null) {
			notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		} else {
			notificationIntent = new Intent(Settings.ACTION_SETTINGS);
		}

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, desc, contentIntent);
		mNotificationManager.notify(ID, notification);
	}

	public static void scheduleUpdateService(Context context, String appName,
			float currentVersion) {
		try {
			Intent intent = new Intent(context, UpdateService.class);
			intent.putExtra(APPLICATION_NAME, appName);
			intent.putExtra(APPLICATION_VERSION, currentVersion);
			PendingIntent pendingIntent = PendingIntent.getService(context, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			long firstTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;

			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					firstTime, 24 * 60 * 60 * 1000, pendingIntent);
			// kick first time
			context.startService(intent);
			isSetUpdateAlarm = true;
		} catch (Exception e) {

		}
	}
}
