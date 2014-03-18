package db.dd.mockloc.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;

import com.csvreader.CsvReader;

import db.dd.mockloc.R;
import db.dd.mockloc.constaints.Constants;
import db.dd.mockloc.model.DataAdapter;
import db.dd.mockloc.model.DataModel;



public class Utils {
	private static final String LOG_TAG = "Utils";

	public static int getVersionCode(Context ctx) {
		int versionCode = 101;

		try {

			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(ctx.getPackageName(), 0);
			if (pi != null) {
				versionCode = pi.versionCode;
			} else {
			}

		} catch (NameNotFoundException e) {
		}

		return versionCode;
	}

	public static String getVersionName(Context ctx) {
		String versionName = "";

		try {

			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(ctx.getPackageName(), 0);
			if (pi != null) {
				versionName = pi.versionName;
			} else {
			}

		} catch (NameNotFoundException e) {
		}

		return versionName;
	}

	public static boolean isNetWorkExist(Context ctx) {
		try {
			ConnectivityManager conManager = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (conManager.getActiveNetworkInfo() == null
					|| !conManager.getActiveNetworkInfo().isAvailable()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public static String buildUrl(HashMap<String, Object> parameters)
			throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		Set<String> keys = parameters.keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();

			builder.append(key).append("=").append(
					URLEncoder.encode(parameters.get(key).toString(), "UTF-8"));
			if (iterator.hasNext()) {
				builder.append("&");
			}
		}
		return builder.toString();
	}

	public static void dialogCreate(Context ctx, String errMsg,
			final DialogClickLisener dcl, boolean showCancel) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setCancelable(true);
			builder.setMessage(errMsg).setCancelable(true).setPositiveButton(
					R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if (dcl != null) {
								dcl.onClick();
							}

						}
					});

			if (showCancel) {
				builder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
			}

			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			Log.e(LOG_TAG, "dialogCreate--error--" + e.getMessage());
		}
	}

	public static void dialogCreate(Context ctx, String title, String errMsg,
			final DialogClickLisener dcl, boolean showCancel) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setCancelable(true);
			builder.setTitle(title).setMessage(errMsg).setCancelable(true)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									if (dcl != null) {
										dcl.onClick();
									}

								}
							});

			if (showCancel) {
				builder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
			}

			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			Log.e("Error", "Show Dialog Error");
		}
	}

	public static String getLocaleTime(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		SimpleDateFormat tempDate = new SimpleDateFormat("MM/dd HH:mm:ss");
		return tempDate.format(calendar.getTime());
	}

	public static String checkSdcard(String path) {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String result = "";
		String state = Environment.getExternalStorageState();

		File fs = new File(path);

		try {
			if (!fs.exists()) {
				fs.mkdirs();
			}
		} catch (Exception e) {
			if (!fs.canWrite()) {
				result = "SD Card is available for read";
			} else {

			}
		}

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
			result = "SD Card is available for read "
					+ mExternalStorageAvailable;
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
			result = "Please insert a SD Card to save your data "
					+ mExternalStorageAvailable + mExternalStorageWriteable;
		}

		return result;
	}

	public static String saveToSDCard(byte[] data, String path, String name) {
		String sdcardStatus = checkSdcard(path);
		if ("".equals(sdcardStatus)) {
			return sdcardStatus;
		}

		saveFile(data, path, name);

		return "";
	}

	private static boolean saveFile(byte[] data, String path, String name) {
		String filename = name;
		File sdImageMainDirectory = new File(path);

		if (!sdImageMainDirectory.exists()) {
			sdImageMainDirectory.mkdirs();
		}

		File outputFile = new File(sdImageMainDirectory, filename);

		try {
			FileOutputStream fos2 = new FileOutputStream(outputFile);
			fos2.write(data);
			fos2.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean createDirIfNotExist(String path) {
		File sdImageMainDirectory = new File(path);

		if (!sdImageMainDirectory.exists()) {
			sdImageMainDirectory.mkdirs();
		}
		return false;
	}

	public static String getSafeString() {

		return "citybustour";
	}

	/**
	 ** 閼惧嘲褰嘢dCard鐠侯垰绶�
	 * 
	 *閵嗭拷
	 */
	public static String getExternalStoragePath() {
		// 閼惧嘲褰嘢dCard閻樿埖锟�
		String state = android.os.Environment.getExternalStorageState();
		// 閸掋倖鏌嘢dCard閺勵垰鎯佺�妯烘躬楠炴湹绗栭弰顖氬讲閻劎娈�
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
				return android.os.Environment.getExternalStorageDirectory()
						.getPath();
			}
		}
		return null;
	}

	/**
	 * 
	 * 閼惧嘲褰囩�妯哄亶閸楋紕娈戦崜鈺�稇鐎瑰綊鍣洪敍灞藉礋娴ｅ秳璐熺�妤勫Ν
	 * 
	 * @param filePath
	 * 
	 * @return availableSpare
	 */
	public static long getAvailableStore(String filePath) {
		// 閸欐牕绶眘dcard閺傚洣娆㈢捄顖氱窞
		StatFs statFs = new StatFs(filePath);
		// 閼惧嘲褰嘼lock閻ㄥ嚪IZE
		long blocSize = statFs.getBlockSize();
		long availaBlock = 1;
		// 閼惧嘲褰嘊LOCK閺佷即鍣�
		// long totalBlocks = statFs.getBlockCount();
		// 閸欘垯濞囬悽銊ф畱Block閻ㄥ嫭鏆熼柌锟�		long availaBlock = statFs.getAvailableBlocks();
		// long total = totalBlocks * blocSize;
		long availableSpare = availaBlock  * blocSize;
		return availableSpare;
	}

	public static boolean isServiceRunning(Context context, String className) {

		boolean isRunning = false;

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {

				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	public static void doMapSearch(Context ctx, final String name,
			final Handler handler) {

		Log.d(LOG_TAG, "doMapSearch start: name=" + name);
		final String url = ctx.getString(R.string.mapSearchUrl, name);
		Log.d(LOG_TAG, "doMapSearch start: url=" + url);

		new Thread(new Runnable() {

			@Override
			public void run() {
				DataModel dm = new DataModel();
				dm.name = name;
				try {

					HttpHelper httpHelper = new HttpHelper();
					String s = httpHelper.performGet(url);

					JSONObject json = new JSONObject(s);

					if (json != null && "OK".equals(json.optString("status"))
							&& json.optJSONArray("results") != null) {

						JSONArray data = json.optJSONArray("results");

						if (data.length() > 0) {
							JSONObject geometry = data.optJSONObject(0)
									.optJSONObject("geometry");
							if (geometry != null) {
								JSONObject location = geometry
										.optJSONObject("location");
								if (location != null) {
									dm.lat = location.optString("lat");
									dm.lng = location.optString("lng");
									Log.d(LOG_TAG, "doMapSearch: result="
											+ dm.lat + "/" + dm.lng);
								}
							}

						}
					}

				} catch (Exception e) {
					Log.e(LOG_TAG, "" + e.getMessage());
				} finally {
					Message msg = new Message();
					msg.obj = dm;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	public static void doImport(final Context ctx, final String fileName,
			final Handler handler) {

		Log.d(LOG_TAG, "doImport start: fileName=" + fileName);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Integer length = 0;
				try {
					CsvReader cr = new CsvReader(fileName, ',', Charset
							.forName("UTF-8"));
					while (cr.readRecord()) {
						DataModel dm = new DataModel();
						dm.name = cr.get(0);

						try {
							Double.parseDouble(cr.get(1));
							Double.parseDouble(cr.get(2));
						} catch (Exception e1) {
							continue;
						}

						length++;

						dm.lat = cr.get(1);
						dm.lng = cr.get(2);

						DataAdapter db = new DataAdapter(ctx);
						dm.type = Constants.TYPE_FAVORITE;
						db.insertFav(dm);
					}
					cr.close();
				} catch (Exception e) {
					Log.e(LOG_TAG, "" + e.getMessage());
				} finally {
					Message msg = new Message();
					msg.obj = length;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}
}
