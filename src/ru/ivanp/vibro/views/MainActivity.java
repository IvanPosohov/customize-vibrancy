package ru.ivanp.vibro.views;

import java.util.ArrayList;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.vibrations.Trigger;
import ru.ivanp.vibro.vibrations.Vibration;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class MainActivity extends ActionBarActivity implements OnItemClickListener, OnItemLongClickListener {
	// ============================================================================================
	// FIELDS
	// ============================================================================================
	private ListView listView;
	private TriggerAdapter triggerAdapter;

	// ============================================================================================
	// LIFECYCLE
	// ============================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		if (App.DEBUG) {
			// we able to launch TestActivity by clicking on header
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.listView);
		triggerAdapter = new TriggerAdapter();
		listView.setAdapter(triggerAdapter);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		triggerAdapter.update();
	}

	@Override
	protected void onPause() {
		super.onPause();
		App.getPlayer().stop();
	}

	// ============================================================================================
	// MENU
	// ============================================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			TestActivity.startActivity(this);
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// ============================================================================================
	// WIDGET CALLBACKS
	// ============================================================================================
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

	// ============================================================================================
	// TRIGGER ADAPTER
	// ============================================================================================
	private class TriggerAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private ArrayList<Trigger> triggers;

		public TriggerAdapter() {
			super();
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return triggers == null ? 0 : triggers.size();
		}

		@Override
		public Object getItem(int _position) {
			return triggers.get(_position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.trigger_view, null);
				viewHolder = new ViewHolder();
				viewHolder.textTriggerName = (TextView) convertView.findViewById(R.id.textTriggerName);
				viewHolder.textPatternName = (TextView) convertView.findViewById(R.id.textPatternName);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			Trigger trigger = triggers.get(position);
			Vibration vibration = App.getVibrationManager().getVibration(trigger.vibrationID);
			viewHolder.textTriggerName.setText(trigger.name);
			viewHolder.textPatternName.setText(vibration != null ? vibration.getName()
					: getString(R.string.do_not_vibrate));
			return convertView;
		}

		public void update() {
			triggers = App.getTriggerManager().getTriggers();
			notifyDataSetChanged();
		}
	}

	static class ViewHolder {
		TextView textTriggerName;
		TextView textPatternName;
	}
}