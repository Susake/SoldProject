package db.dd.mockloc.utils;

import db.dd.mockloc.constaints.Constants;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class DialogClick extends DialogClickLisener {
	private Context ctx;

	public DialogClick(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public void onClick() {
		try {
			// ctx.startActivity(new Intent(Settings.ACTION_SETTINGS));
			ctx.sendBroadcast(new Intent(Constants.FINISH_BROADCAST));
		} catch (Exception e) {
			Log.e("DialogClick", "" + e.getMessage());
		}
	}

}
