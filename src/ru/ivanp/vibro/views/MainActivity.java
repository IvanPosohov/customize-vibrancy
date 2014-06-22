package ru.ivanp.vibro.views;

import java.util.ArrayList;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.R.id;
import ru.ivanp.vibro.R.layout;
import ru.ivanp.vibro.R.menu;
import ru.ivanp.vibro.R.string;
import ru.ivanp.vibro.vibrations.Trigger;
import ru.ivanp.vibro.vibrations.Vibration;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Application main activity. Allows to manage triggers vibration, opens
 * Preference, Donate, SelectVibration, Help activities
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class MainActivity extends Activity implements OnClickListener, OnItemClickListener,
		OnItemLongClickListener {
	// ============================================================================================
	// FIELDS
	// ============================================================================================
	private ListView lv;
	private TriggerAdapter adapter;

	// ============================================================================================
	// OVERRIDDEN
	// ============================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		adapter = new TriggerAdapter();
		setupWidgets();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// refresh trigger list to update trigger vibrations
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		super.onPause();
		App.getPlayer().stop();
	}

	@Override
	public void onItemClick(AdapterView<?> _adapter, View _view, int _position, long _id) {
		// play pattern on click
		Trigger trigger = (Trigger) _adapter.getItemAtPosition(_position);
		Vibration vibration = App.getVibrationManager().getVibration(trigger.vibrationID);
		App.getPlayer().playOrStop(vibration);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> _adapter, View _view, int _position, long _id) {
		// open SelectVibrationActivity on long click
		Trigger trigger = (Trigger) _adapter.getItemAtPosition(_position);
		SelectVibrationActivity.start(this, trigger.id);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_logo:
			startActivity(new Intent(this, TestActivity.class));
			break;
		}

	}

	// ============================================================================================
	// MAIN MENU
	// ============================================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		}
		return true;
	};

	// ============================================================================================
	// METHODS
	// ============================================================================================
	/**
	 * Process widgets setup
	 */
	private void setupWidgets() {
		lv = (ListView) findViewById(R.id.lv);
		lv.setOnItemClickListener(this);
		lv.setOnItemLongClickListener(this);
		lv.setAdapter(adapter);
		
		if (App.DEBUG) {
			ImageView img_logo = (ImageView)findViewById(R.id.img_logo);
			img_logo.setOnClickListener(this);
		}
	}

	// ============================================================================================
	// INTERNAL CLASSES
	// ============================================================================================
	/**
	 * Custom adapter for list
	 */
	private class TriggerAdapter extends BaseAdapter {
		private ArrayList<Trigger> list;

		public TriggerAdapter() {
			super();
			list = App.getTriggerManager().getTriggers();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public boolean isEnabled(int position) {
			return true;
		}

		public long getItemId(int position) {
			return position;
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int _position) {
			return list.get(_position);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.trigger_view, null);
			final TextView text_name = (TextView) itemView.findViewById(R.id.text_name);
			final TextView text_pattern_name = (TextView) itemView
					.findViewById(R.id.text_pattern_name);

			Trigger model = list.get(position);
			Vibration vibration = App.getVibrationManager().getVibration(model.vibrationID);
			text_name.setText(model.name);
			text_pattern_name.setText(vibration != null ? vibration.getName()
					: getString(R.string.do_not_vibrate));

			return itemView;
		}
	}
}