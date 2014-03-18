package db.dd.mockloc;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import android.R.integer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.apsalar.sdk.Apsalar;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import db.dd.mockloc.constaints.Constants;
import db.dd.mockloc.model.DataAdapter;
import db.dd.mockloc.model.DataModel;
import db.dd.mockloc.model.Preferences;
import db.dd.mockloc.service.MockLocationService;
import db.dd.mockloc.utils.ActivityTools;
import db.dd.mockloc.utils.DialogClickLisener;
import db.dd.mockloc.utils.Utils;



public class TestGPSActivity extends MapActivity implements OnClickListener,
		View.OnCreateContextMenuListener {
	private static final String LOG_TAG = "TestGPSActivity";

	private Context mContext;
	private MapView mMapView;
	private MyLocationOverlay mMyLocationOverlay;
	private MapController mMapController;

	private Button mBtnSet;
	private Button mBtnSetAndFav;
	private Button mBtnStop;

	private PopupWindow popMenu;
	private ProgressDialog searchLoading = null;

	private List<Overlay> mapOverlays;
	private MapItemizedOverlay searchOverlay;
	private OverlayItem centerOverlay;

	private static LocationManager mLocationManager;
	private static MyNotification mNotification;

	private HashMap<String, OverlayItem> tempOverlays = new HashMap<String, OverlayItem>();
	private static String currentMockLoction = "";
	
	
	int seconds = 30; //闂撮殧鏃堕棿,榛樿30绉�
	EditText timeEdit;
	MoveThread moveThread = null;
	List<DataModel> list;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preferences.init(getApplication());
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.main);

		mContext = this;

		Preferences.putLaunchNum(Preferences.getLaunchNum() + 1);

		initView();
		initData();

		if (Preferences.getLaunchNum() == 1) {
			Utils.dialogCreate(mContext, getString(R.string.dialogTip),
					getString(R.string.dialogTipContent), null, false);
		}
	}
	Handler handler = new Handler() {
		public void handleMessage(Message message) {
			go(message.arg1);
		}
	};
	
	
	private void go(int i){
		if( list==null || list.size()==0 )
			return;
		i%=list.size();
		mockByGPS(list.get(i).getDoubleLng(), list.get(i).getDoubleLat());
	}
	private void mockByGPS(double longitude, double latitude){
		startTestGPS(mContext, getString(R.string.notifyLocationLabel), latitude,
				longitude);

		//DataAdapter db = new DataAdapter(mContext);
		DataModel dm = new DataModel("", longitude, latitude);
		dm.type = Constants.TYPE_COMMON;
		//db.insertFav(dm);

		addOverlay(searchOverlay, dm, Constants.TYPE_COMMON);
	}

	private void initView() {

		mMapView = (MapView) findViewById(R.id.mapView);
		mMapView.setBuiltInZoomControls(true);
		//mMapView.setTraffic(true);
		mMapView.setSatellite(false);//------淇敼
		mMapView.postInvalidate();
		
		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mMyLocationOverlay);

		

		mBtnSet = (Button) findViewById(R.id.btnSet);
		mBtnSet.setOnClickListener(this);
		mBtnSetAndFav = (Button) findViewById(R.id.btnSetAndFav);
		mBtnSetAndFav.setOnClickListener(this);
		mBtnStop = (Button) findViewById(R.id.btnStop);
		mBtnStop.setOnClickListener(this);

		View view = this.getLayoutInflater().inflate(R.layout.about, null);
		popMenu = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		popMenu.setOutsideTouchable(false);
		popMenu.setBackgroundDrawable(new BitmapDrawable());
		popMenu.setFocusable(true);

		final TextView txtAboutVersion = (TextView) view
				.findViewById(R.id.txtAbout);
		final TextView txtAboutContact = (TextView) view
				.findViewById(R.id.txtAboutContact);

		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			txtAboutVersion.setText(txtAboutVersion.getText() + " v"
					+ info.versionName);
		} catch (NameNotFoundException e) {

		}

		txtAboutContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(Intent.ACTION_VIEW, Uri
						.parse(txtAboutContact.getText().toString().replace(
								"Home: ", "")));
				startActivity(it);
			}
		});

		searchLoading = ProgressDialog.show(mContext, "",
				getString(R.string.dialogSearchTitle), true, true);
		searchLoading.dismiss();
	}

	private void initData() {
		mMapController = mMapView.getController();
		//mMapController.setZoom(Preferences.getLastZoomLevel());
		mMapController.setZoom(7);
		
		mapOverlays = mMapView.getOverlays();


		searchOverlay = new MapItemizedOverlay(getResources().getDrawable(
				R.drawable.search_place), this, 12);

		//initAllOverlayItems();


		mapOverlays.add(searchOverlay);
		searchOverlay.clear();

		mMapView.invalidate();
		mMapView.postInvalidate();

		//mMapView.setBuiltInZoomControls(true);

		mNotification = new MyNotification(this, Constants.NOTIFICATION_ID);

		
		IntentFilter deleteOverlayFilter = new IntentFilter(
				Constants.DELETE_BROADCAST);
		registerReceiver(deleteOverlayReceiver, deleteOverlayFilter);

		IntentFilter addOverlayFilter = new IntentFilter(
				Constants.ADD_BROADCAST);
		registerReceiver(addOverlayReceiver, addOverlayFilter);

		IntentFilter setLocationFilter = new IntentFilter(
				Constants.SHOW_ON_MAP_BROADCAST);
		registerReceiver(setLocationReceiver, setLocationFilter);

		IntentFilter refreshFilter = new IntentFilter(
				Constants.REFRESH_BROADCAST);
		registerReceiver(refreshReceiver, refreshFilter);

		IntentFilter finishFilter = new IntentFilter(Constants.FINISH_BROADCAST);
		registerReceiver(finishReceiver, finishFilter);

		currentMockLoction = "";

		// track
		Apsalar.startSession(this, "jacksun", "pyhffNv1");
		// Launch event
		Apsalar.event(Constants.TRACK_EVENT_LAUNCH,
				Constants.TRACK_EVENT_LAUNCH_NUM, Preferences.getLaunchNum());
	}
	void timeSetting(){
		TextView textView1 = new TextView(this);
		textView1.setText("间隔秒数:");
		textView1.setTextSize(18);
		textView1.setTextColor(Color.GREEN);
		textView1.setGravity(Gravity.CENTER);
		timeEdit = new EditText(this);
		timeEdit.setText(""+seconds);
		timeEdit.setSingleLine();
		timeEdit.setTextSize(18);
		textView1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
		timeEdit.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));

		LinearLayout layout1 = new LinearLayout(this);
		layout1.setOrientation(LinearLayout.HORIZONTAL);
		layout1.addView(textView1);
		layout1.addView(timeEdit);

		new AlertDialog.Builder(this)  //这个是会令起一个线程的
				.setTitle("时间间隔设置")
				.setView(layout1)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						int tmp = Integer.parseInt(timeEdit.getText().toString());
						if( tmp<1 ){
							Toast.makeText(mContext, "设置失败,参数错误", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
							seconds = tmp;
							if( moveThread!=null ) {
								moveThread.seconds = seconds;
							}
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					//@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				})
				.show();
		
	}
	private void readData(){
		File file = new File("/sdcard/in.txt");
		if( file.exists() == false){
			Toast.makeText(mContext, "/sdcard/in.txt文件不存在", Toast.LENGTH_SHORT).show();
			return;
		}
		list = new ArrayList<DataModel>();
		int cnt = 0;
		try {
			Scanner scanner = new Scanner(file);
			String line;
			while( scanner.hasNext() ){
				line = scanner.nextLine();
				String[] values = line.split(",");
				if( !(values[1].charAt(0)>='0' && values[1].charAt(0)<='9') )
					continue;
				if( values.length<6 ) continue;
				DataModel dm = new DataModel();
				dm.name = values[5];
				dm.lat = values[3];
				dm.lng = values[4];
				list.add(dm);
				cnt++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(mContext, cnt+"个经纬度数据读取完毕!", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnSet) {
			setLocation();
			Apsalar.event(Constants.TRACK_EVENT_SET_LOCATION);
		} else if (v == mBtnSetAndFav) {
			if( moveThread!=null ){
				moveThread.stop = true;
			}
			moveThread = new MoveThread(handler, seconds);
			moveThread.start();
			//Apsalar.event(Constants.TRACK_EVENT_ADD_FAVORITE);
		} else if (v == mBtnStop) {
			endTestGPS(mContext);
		}
	}

	private void setLocation() {
		centerOverlay = getCenterOverlayItem("", Constants.TYPE_COMMON);
		// start the test gps
		GeoPoint gp = centerOverlay.getPoint();
		final double lat = gp.getLatitudeE6() / 1E6;
		final double lng = gp.getLongitudeE6() / 1E6;
		startTestGPS(mContext, getString(R.string.notifyLocationLabel), lat,
				lng);

		//DataAdapter db = new DataAdapter(mContext);
		DataModel dm = new DataModel("", gp);
		dm.type = Constants.TYPE_COMMON;
		//db.insertFav(dm);

		addOverlay(searchOverlay, dm, Constants.TYPE_COMMON);
	}

	public static void startTestGPS(Context ctx, String name, double lat,
			double lng) {
		startTestGPS(ctx, name, lat, lng, Preferences.getSpeed(), Preferences
				.getAccuracy(), Preferences.getBearing(), true);
	}

	public static void startTestGPS(Context ctx, String name, double lat,
			double lng, float speed, float accuracy, float bearing,
			boolean local) {
		if (mNotification == null) {
			mNotification = ActivityTools.getMyNotification(ctx);
		}

		mNotification.cancelNotify();

		mLocationManager = ActivityTools.getTestLocationManager(ctx);

		if (mLocationManager == null) {
			// if triggle by other app
			if (!local) {
				Intent it = new Intent(ctx, TestGPSActivity.class);
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ctx.startActivity(it);
			} else {
				Utils.dialogCreate(ctx, ctx
						.getString(R.string.errorMockSettingDisabled),
						new DialogClick(ctx), false);
			}
			endTestGPS(ctx);
			return;
		}

		mLocationManager.addTestProvider(Constants.GPS_LABEL, false, false,
				false, false, true, true, true, 0, 5);

		MockLocationService.forFlag = false;
		Intent it = new Intent(ctx, MockLocationService.class);
		it.putExtra(Constants.EXTRA_LAT, lat);
		it.putExtra(Constants.EXTRA_LNG, lng);
		it.putExtra(Constants.EXTRA_ACCURACY, accuracy);
		it.putExtra(Constants.EXTRA_BEARING, bearing);
		it.putExtra(Constants.EXTRA_SPEED, speed);
		ctx.startService(it);

		currentMockLoction = name + ": " + lat + ", " + lng + " accuracy:"
				+ accuracy + " speed:" + speed;

		mNotification.startNotify(currentMockLoction);
	}

	public static void endTestGPS(Context ctx) {
		if (mLocationManager == null) {
			mLocationManager = ActivityTools.getTestLocationManager(ctx);
		}
		if (mNotification == null) {
			mNotification = ActivityTools.getMyNotification(ctx);
		}

		mNotification.cancelNotify();

		try {
			mLocationManager.removeTestProvider(Constants.GPS_LABEL);
		} catch (Exception e) {
			Log.e(LOG_TAG, "" + e.getMessage());
		}
		MockLocationService.forFlag = false;
		Intent it = new Intent(ctx, MockLocationService.class);
		it.putExtra("finishFlag", true);
		ctx.startService(it);
	}

	private OverlayItem getCenterOverlayItem(String title, String snippet) {
		GeoPoint gp = mMapView.getMapCenter();
		return new OverlayItem(gp, title, snippet);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		switch (id) {
		case R.id.timeSetting:
			timeSetting();
			break;
		case R.id.readData:
			readData();
			break;
		}
		return false;
	}
	
	

	private BroadcastReceiver deleteOverlayReceiver = new BroadcastReceiver() {
		public void onReceive(final Context context, final Intent intent) {
			// initAllOverlayItems();
			DataModel dm = getIntentDataModel(intent);

			if (Constants.TYPE_FAVORITE.equals(dm.type)) {
				//deleteOverlay(favoritedOverlay, dm.getKey());
			} else {
				deleteOverlay(searchOverlay, dm.getKey());
			}
		}
	};

	private BroadcastReceiver addOverlayReceiver = new BroadcastReceiver() {
		public void onReceive(final Context context, final Intent intent) {
			DataModel dm = getIntentDataModel(intent);
			dm.type = Constants.TYPE_COMMON;
			deleteOverlay(searchOverlay, dm.getKey());
		}
	};

	private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
		public void onReceive(final Context context, final Intent intent) {
			//initAllOverlayItems();
		}
	};

	private BroadcastReceiver setLocationReceiver = new BroadcastReceiver() {
		public void onReceive(final Context context, final Intent intent) {
			DataModel dm = getIntentDataModel(intent);
			addOverlay(searchOverlay, dm, Constants.TYPE_COMMON);
		}
	};

	private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
		public void onReceive(final Context context, final Intent intent) {
			finish();
		}
	};

	private DataModel getIntentDataModel(Intent it) {
		DataModel dm = new DataModel();
		dm.name = it.getStringExtra(Constants.EXTRA_NAME);
		dm.lat = it.getStringExtra(Constants.EXTRA_LAT);
		dm.lng = it.getStringExtra(Constants.EXTRA_LNG);
		dm.type = it.getStringExtra(Constants.EXTRA_TYPE);
		return dm;
	}

	private void deleteOverlay(MapItemizedOverlay overlay, String key) {
		if (tempOverlays.containsKey(key)) {
			overlay.removeOverlay(tempOverlays.get(key));
			mMapView.invalidate();
			tempOverlays.remove(key);
		}

		if (currentMockLoction.contains(key)) {
			endTestGPS(mContext);
		}
	}

	@Override
	protected void onDestroy() {
		try {
			Preferences.putLastZoomLevel(mMapView.getZoomLevel());
			this.unregisterReceiver(deleteOverlayReceiver);
			this.unregisterReceiver(addOverlayReceiver);
			this.unregisterReceiver(setLocationReceiver);
			this.unregisterReceiver(refreshReceiver);
			this.unregisterReceiver(finishReceiver);
		} catch (Exception e) {
			Log.e(LOG_TAG, "" + e.getMessage());
		}

		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//按下键盘上返回按钮
		if(keyCode == KeyEvent.KEYCODE_BACK){
			new AlertDialog.Builder(this)
				.setTitle("确定要退出吗")
				.setMessage("退出将无法使用模拟地理位置功能!")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if( moveThread!=null ){ //-------------------------
							moveThread.stop = true;
						}
						endTestGPS(mContext);
						finish();
					}
				}).show();
			
			return true;
		}else{		
			return super.onKeyDown(keyCode, event);
		}
	}
	private void addOverlay(MapItemizedOverlay overlay, DataModel dm,
			String which) {

		if (tempOverlays.containsKey(dm.getKey())) {
			mMapController.animateTo(tempOverlays.get(dm.getKey()).getPoint());
			return;
		}

		Log.d(LOG_TAG, "addOverlay--name:" + dm.name + "--lat/lng:" + dm.lat
				+ "/" + dm.lng);
		if ("".equals(dm.lat) || "".equals(dm.lng)) {
			return;
		}
		final double lat = Double.parseDouble(dm.lat);
		final double lng = Double.parseDouble(dm.lng);

		GeoPoint gp = new GeoPoint((int) (lat * 1000000), (int) (lng * 1000000));
		OverlayItem overlayItem = new OverlayItem(gp, dm.name, which);
		overlay.addOverlay(overlayItem);
		tempOverlays.put(dm.getKey(), overlayItem);
		mMapView.invalidate();

		mMapController.animateTo(overlayItem.getPoint());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//MenuItem delMenu = menu.add(0, DELETE_ID, 0, R.string.btnDelete); 
		//MenuItem setLocationMenu = menu.add(0, SETLOCATION_ID, 0,
		//		R.string.btnSet);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (searchLoading != null && searchLoading.isShowing()) {
			searchLoading.dismiss();
		}
	}

	static class DialogClick extends DialogClickLisener {
		private Context ctx;

		public DialogClick(Context ctx) {
			this.ctx = ctx;
		}

		@Override
		public void onClick() {
			try {
				ctx.sendBroadcast(new Intent(Constants.FINISH_BROADCAST));
			} catch (Exception e) {
				Log.e("DialogClick", "" + e.getMessage());
			}
		}

	}
}

class MoveThread extends Thread {
	Handler handler;
	public int seconds;
	Boolean stop = false;

	public MoveThread(Handler handler, int seconds) {
		this.handler = handler;
		this.seconds = seconds;
	}

	public void run() {
		Looper.prepare(); //要加上这个。。。
		int i = 0;
		while (!stop) {
			Message message = new Message();
			message.arg1 = i++;
			//handler.handleMessage(message);
			//handler.dispatchMessage(message);
			handler.sendMessage(message);
			try {
				sleep(seconds * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}