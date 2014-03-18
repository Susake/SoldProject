package db.dd.mockloc;

import java.util.ArrayList;

import db.dd.mockloc.model.DataModel;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ViewAdapter extends BaseAdapter {

	private final ArrayList<DataModel> mActions;

	private final LayoutInflater mInflater;

	public ViewAdapter(Context context, ArrayList<DataModel> actions) {
		mActions = actions;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return mActions.size();
	}

	public Object getItem(int position) {
		return mActions.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// Make sure we have a valid convertView to start with
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.setting_item_two_line_row, parent, false);
		}

		// Fill action with icon and text.
		final DataModel entry = mActions.get(position);
		convertView.setTag(entry);
		TextView text1 = (TextView) convertView.findViewById(R.id.text1);
		TextView text2 = (TextView) convertView.findViewById(R.id.text2);

		text1.setText(entry.name);
		if("".endsWith(entry.name)){
			text1.setText(entry.getLatLng());
		}
		
		if (entry.getLatLng() != null && !"".equals(entry.getLatLng())) {
			text2.setText(entry.getLatLng());
			text2.setVisibility(View.VISIBLE);
		} else {
			text2.setVisibility(View.GONE);
		}

		return convertView;
	}
}
