package db.dd.mockloc;

import java.util.ArrayList;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


import com.apsalar.sdk.Apsalar;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import db.dd.mockloc.constaints.Constants;
import db.dd.mockloc.model.DataModel;
import db.dd.mockloc.utils.ActivityTools;




public class MapItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	// member variables
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private int mTextSize;

	public MapItemizedOverlay(Drawable defaultMarker, Context context,
			int textSize) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		mTextSize = textSize;
	}

	// In order for the populate() method to read each OverlayItem, it will make
	// a request to createItem(int)
	// define this method to properly read from our ArrayList
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		final OverlayItem item = mOverlays.get(index);

		LayoutInflater factory = LayoutInflater.from(mContext);
		final View view = factory.inflate(R.layout.alert_dialog_detail, null);
		final AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle(
				item.getTitle()).setView(view).create();
		
		final Button btnAdd = (Button) view.findViewById(R.id.btnAdd);
		final Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
		final Button btnSetLocation = (Button) view
				.findViewById(R.id.btnSetLocation);
		final Button btnCancel = (Button) view.findViewById(R.id.btnCancle);

		if("".equals(item.getSnippet()) || !Constants.TYPE_FAVORITE.equals(item.getSnippet())){
			btnAdd.setVisibility(View.VISIBLE);
		}
		
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DataModel dm = new DataModel(item.getTitle(), item.getPoint());
				dm.type = Constants.TYPE_FAVORITE;
				
				ActivityTools.addFav(mContext, dm);
				
				dialog.dismiss();
			}
		});
		
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DataModel dm = new DataModel(item.getTitle(), item.getPoint());
				dm.type = item.getSnippet();
				
				ActivityTools.deleteFav(mContext, dm);
				
				dialog.dismiss();
			}
		});

		btnSetLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TestGPSActivity.startTestGPS(mContext, item.getTitle(), item.getPoint()
						.getLatitudeE6() / 1E6, item.getPoint()
						.getLongitudeE6() / 1E6);
				
				dialog.dismiss();
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}});

		dialog.setMessage(item.getPoint().getLatitudeE6() / 1E6 + ","
				+ item.getPoint().getLongitudeE6() / 1E6);
		dialog.show();

		Apsalar.event(Constants.TRACK_EVENT_TAP_OVERLAY);
		
		// return true to indicate we've taken care of it
		return true;
	}

	@Override
	public void draw(android.graphics.Canvas canvas, MapView mapView,
			boolean shadow) {
		super.draw(canvas, mapView, shadow);

		if (shadow == false) {
			// cycle through all overlays
			for (int index = 0; index < mOverlays.size(); index++) {
				OverlayItem item = mOverlays.get(index);

				// Converts lat/lng-Point to coordinates on the screen
				GeoPoint point = item.getPoint();
				Point ptScreenCoord = new Point();
				mapView.getProjection().toPixels(point, ptScreenCoord);

				// Paint
				Paint paint = new Paint();
				paint.setTextAlign(Paint.Align.CENTER);
				paint.setTextSize(mTextSize);
				paint.setARGB(150, 0, 0, 0); // alpha, r, g, b (Black, semi
				// see-through)

				// show text to the right of the icon
				canvas.drawText(item.getTitle(), ptScreenCoord.x,
						ptScreenCoord.y + mTextSize, paint);
			}
		}
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		setLastFocusedIndex(-1);
		populate();
	}

	public void removeOverlay(OverlayItem overlay) {
		mOverlays.remove(overlay);
		setLastFocusedIndex(-1);
		populate();
	}

	public void clear() {
		mOverlays.clear();
		setLastFocusedIndex(-1);
		populate();
	}
}
