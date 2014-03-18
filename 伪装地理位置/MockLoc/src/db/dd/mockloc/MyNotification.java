package db.dd.mockloc;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MyNotification {
	public NotificationManager mNotificationManager;
	public Notification mNotification;
	public Context mContext;
	private int mNotificationID;

	public MyNotification(Context context, int notifyId) {
		mContext = context;

		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotification = new Notification(R.drawable.icon, context
				.getString(R.string.notifyTitle), System.currentTimeMillis());
		mNotification.contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.notify);
		mNotification.contentView.setTextViewText(R.id.txtTitle, context
				.getString(R.string.notifyTitle));
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				new Intent(mContext, TestGPSActivity.class), 0);
		mNotification.contentIntent = contentIntent;
		
		mNotificationID = notifyId;
		
		mNotification.flags = Notification.FLAG_NO_CLEAR;
	}

	public void startNotify(String msg) {
		mNotification.contentView.setTextViewText(R.id.txtCurrentLocation, msg);
		mNotificationManager.notify(mNotificationID, mNotification);
	}

	public void updateMessage(String msg) {
		mNotification.contentView.setTextViewText(R.id.txtCurrentLocation, msg);
		mNotificationManager.notify(mNotificationID, mNotification);
	}

	public void cancelNotify() {
		mNotificationManager.cancel(mNotificationID);
	}
}
