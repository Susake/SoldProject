package db.dd.mockloc.constaints;

import android.location.LocationManager;

public class Constants {
	/** Name of the shared preferences */
	public static final String PREFS = "testgpsprefs";
	
	public static final String GPS_LABEL = LocationManager.GPS_PROVIDER;
	public static final String NETWORK_LABEL = LocationManager.NETWORK_PROVIDER;
	
	public static final int NOTIFICATION_ID = 1000;
	
	public static final String DELETE_BROADCAST = "overlay_delete";
	public static final String ADD_BROADCAST = "overlay_add";
	public static final String REFRESH_BROADCAST = "overlay_refresh";
	public static final String SHOW_ON_MAP_BROADCAST = "show_on_map";
	public static final String FINISH_BROADCAST = "finish";

	public static final String UPDATE_URL = "http://apps.lydfit.com/ws/checkupdate/";
	public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	
	public static final String TYPE_FAVORITE = "favorite";
	public static final String TYPE_SEARCH = "search";
	public static final String TYPE_COMMON = "common";

	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_LAT = "lat";
	public static final String EXTRA_LNG = "lng";
	public static final String EXTRA_SPEED = "speed";
	public static final String EXTRA_ACCURACY = "accuracy";
	public static final String EXTRA_ALTITUDE = "altitude";
	public static final String EXTRA_BEARING = "bearing";
	public static final String EXTRA_TYPE = "type";
	
	public static final long DEFAULT_ACCURACY = 5;
	public static final long DEFAULT_SPEED = 10;

	public static final String IMPORT_DIR = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/mockgps/";
	public static final String IMPORT_FILENAME = "import.csv";
	public static final String IMPORT_DIR_LABEL = "/sdcard/mockgps/import.csv";
	
	public static final String TRACK_EVENT_LAUNCH = "launch";
	public static final String TRACK_EVENT_LAUNCH_NUM = "launch_number";
	
	public static final String TRACK_EVENT_SET_LOCATION = "click_set_location";
	public static final String TRACK_EVENT_ADD_FAVORITE = "click_add_favorite";
	public static final String TRACK_EVENT_TAP_OVERLAY = "tap_overlay";
	public static final String TRACK_EVENT_SETTINGS_FAV = "settings_favorites";
	public static final String TRACK_EVENT_SETTINGS_HISTORY = "settings_history";
	public static final String TRACK_EVENT_SETTINGS_SEARCH = "settings_search";
	public static final String TRACK_EVENT_SETTINGS_IMPORT = "settings_import";
	public static final String TRACK_EVENT_SETTINGS_ABOUT = "settings_about";
	public static final String TRACK_EVENT_SETTINGS_TIP = "settings_tip";
	public static final String TRACK_EVENT_SETTINGS_SPEED = "settings_speed";
	public static final String TRACK_EVENT_SETTINGS_BEARING = "settings_bearing";
	public static final String TRACK_EVENT_SETTINGS_ACCURACY = "settings_accuracy";
	public static final String TRACK_EVENT_SETTINGS_AUTITUDE = "settings_autitude";
	public static final String TRACK_EVENT_BROADCAST_START_MOCK = "broadcase_start_mock";
	public static final String TRACK_EVENT_BROADCAST_END_MOCK = "broadcase_end_mock";
	
}
