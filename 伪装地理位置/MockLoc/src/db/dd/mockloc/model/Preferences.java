package db.dd.mockloc.model;


import db.dd.mockloc.constaints.Constants;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	private static SharedPreferences prefs;

	public static final String PREF_LAST_ZOOM_LEVEL = "pref_last_zoom_level";
	public static final String PREF_LAUNCH_NUM = "pref_launch_num";
	public static final String PREF_SHORTCUT = "pref_short_cut";
	public static final String PREF_BEARING = "pref_bearing";
	public static final String PREF_ALTITUDE = "pref_altitude";
	public static final String PREF_SPEED = "pref_speed";
	public static final String PREF_ACCURACY = "pref_accuracy";

	public static void init(Application app) {
		prefs = app.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE);
	}

	public static float getAccuracy() {
		return prefs.getFloat(Preferences.PREF_ACCURACY, Constants.DEFAULT_ACCURACY);
	}
	
	public static void putAccuracy(float accuracy) {
		put(PREF_ACCURACY, accuracy);
	}
	
	public static float getSpeed() {
		return prefs.getFloat(Preferences.PREF_SPEED, Constants.DEFAULT_SPEED);
	}
	
	public static void putSpeed(float speed) {
		put(PREF_SPEED, speed);
	}
	
	public static float getBearing() {
		return prefs.getFloat(Preferences.PREF_BEARING, 0f);
	}
	
	public static void putBearing(float bearing) {
		put(PREF_BEARING, bearing);
	}
	
	public static long getAltitude() {
		return prefs.getLong(Preferences.PREF_ALTITUDE, 0);
	}
	
	public static void putAltitude(long altitude) {
		put(PREF_ALTITUDE, altitude);
	}
	
	public static boolean getShortCut() {
		return prefs.getBoolean(Preferences.PREF_SHORTCUT, false);
	}
	
	public static void putShortCut(boolean flag) {
		put(PREF_SHORTCUT, flag);
	}
	
	public static int getLaunchNum() {
		return prefs.getInt(Preferences.PREF_LAUNCH_NUM, 0);
	}
	
	public static void putLaunchNum(int num) {
		put(PREF_LAUNCH_NUM, num);
	}
	
	public static int getLastZoomLevel() {
		return prefs.getInt(Preferences.PREF_LAST_ZOOM_LEVEL, 3);
	}

	public static void putLastZoomLevel(int zoomLevel) {
		put(PREF_LAST_ZOOM_LEVEL, zoomLevel);
	}

	public static long getLongValue(String name){
		return prefs.getLong(name, 0);
	}
	
	public static float getFloatValue(String name){
		return prefs.getFloat(name, 0f);
	}
	
	public static String getStringValue(String name){
		return prefs.getString(name, "");
	}
	
	private static void put(String name, Object value) {
		SharedPreferences.Editor editor = prefs.edit();
		if (value.getClass() == Boolean.class) {
			editor.putBoolean(name, (Boolean) value);
		}
		if (value.getClass() == String.class) {
			editor.putString(name, (String) value);
		}
		if (value.getClass() == Integer.class) {
			editor.putInt(name, ((Integer) value).intValue());
		}
		if (value.getClass() == Float.class) {
			editor.putFloat(name, ((Float) value).intValue());
		}
		if (value.getClass() == Long.class) {
			editor.putLong(name, ((Long) value).intValue());
		}

		editor.commit();

	}

}
