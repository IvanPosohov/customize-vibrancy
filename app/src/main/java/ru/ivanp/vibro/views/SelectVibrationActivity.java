package ru.ivanp.vibro.views;

import java.util.ArrayList;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.vibrations.Trigger;
import ru.ivanp.vibro.vibrations.Vibration;
import ru.ivanp.vibro.vibrations.VibrationsManager;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
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
 * Allow to set vibration to the trigger
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class SelectVibrationActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	public static final String TRIGGER_ID_KEY = "trigger_id";
	public static final String VIBRATION_ID_KEY = "vibration_id";
	private static final int TAP_RECORDER_POSITION = 0;
	private static final int MORSE_RECORDER_POSITION = 1;
	// TODO filter

	// ============================================================================================
	// FIELDS
	// ============================================================================================
	private ListView listViewVibrations;
	private VibrationsAdapter vibrationsAdapter;
	private ActionMode actionMode;
	private Trigger customizableTrigger;

	// ============================================================================================
	// LIFECYCLE
	// ============================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_vibration);

		int customizableTriggerId = getIntent().getIntExtra(TRIGGER_ID_KEY, -1);
		customizableTrigger = App.getTriggerManager().getTrigger(customizableTriggerId);

		listViewVibrations = (ListView) findViewById(R.id.listView);
		vibrationsAdapter = new VibrationsAdapter();
		listViewVibrations.setAdapter(vibrationsAdapter);
		listViewVibrations.setOnItemClickListener(this);
		listViewVibrations.setOnItemLongClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		update();
	}

	@Override
	protected void onPause() {
		super.onPause();
		int checkedPosition = listViewVibrations.getCheckedItemPosition();
		if (checkedPosition >= 0) {
			Vibration vibration = (Vibration) vibrationsAdapter.getItem(checkedPosition);
			App.getTriggerManager().setTrigger(customizableTrigger.id, vibration.id);
		}
		App.getPlayer().stop();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			int createdVibrationId = data.getIntExtra(VIBRATION_ID_KEY, VibrationsManager.NO_VIBRATION_ID);
			App.getTriggerManager().setTrigger(customizableTrigger.id, createdVibrationId);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ============================================================================================
	// MENU
	// ============================================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (customizableTrigger.isCustomVibrationAllowed()) {
			getMenuInflater().inflate(R.menu.activity_select_vibration, menu);
		}
		return true;
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_new:
			showRecorderSelectionDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	};

	// ============================================================================================
	// CONTEXT MENU
	// ============================================================================================
	private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.activity_select_vibration_contextual, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_remove:
				int selectedVibrationId = Integer.valueOf(mode.getTag().toString());
				App.getVibrationManager().remove(selectedVibrationId);
				update();
				mode.finish();
				return true;
			default:
				return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			actionMode = null;
		}
	};

	// ============================================================================================
	// WIDGET CALLBACKS
	// ============================================================================================
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		if (actionMode != null) {
			actionMode.finish();
		}
		Vibration vibration = (Vibration) vibrationsAdapter.getItem(position);
		App.getPlayer().playOrStop(vibration);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
		App.getPlayer().stop();
		if (actionMode == null) {
			Vibration vibration = (Vibration) adapter.getItemAtPosition(position);
			if (vibration.isDeletable()) {
				actionMode = startSupportActionMode(actionModeCallback);
				actionMode.setTag(vibration.id);
				actionMode.setTitle(vibration.getName());
				view.setSelected(true);
				return true;
			}
		}
		return false;
	}

	// ============================================================================================
	// METHODS
	// ============================================================================================
	/**
	 * Start activity with needed arguments
	 * 
	 * @param _context
	 *            application context
	 * @param _triggerId
	 *            identifier of configurable trigger
	 */
	public static void startActivity(Context _context, int _triggerId) {
		Intent intent = new Intent(_context, SelectVibrationActivity.class);
		intent.putExtra(TRIGGER_ID_KEY, _triggerId);
		_context.startActivity(intent);
	}

	private void update() {
		vibrationsAdapter.update();
		
		int selectedVibrationId = customizableTrigger.vibrationID;
		int vibrationsCount = vibrationsAdapter.getCount();
		for (int i = 0; i < vibrationsCount; i++) {
			Vibration vibration = (Vibration) vibrationsAdapter.getItem(i);
			if (vibration.id == selectedVibrationId) {
				listViewVibrations.setItemChecked(i, true);
				// scrolls to checked item
				listViewVibrations.setSelection(i);
			}
		}
	}
	
	private void showRecorderSelectionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_recorder);
		builder.setItems(R.array.list_recorders, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == TAP_RECORDER_POSITION) {
					RecorderActivity.startActivityForResult(SelectVibrationActivity.this);
				} else if (which == MORSE_RECORDER_POSITION) {
					MorseActivity.startActivityForResult(SelectVibrationActivity.this);
				}
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	// ============================================================================================
	// VIBRATIONS ADAPTER
	// ============================================================================================
	private class VibrationsAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private ArrayList<Vibration> vibrations;

		public VibrationsAdapter() {
			super();
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return vibrations == null ? 0 : vibrations.size();
		}

		@Override
		public Object getItem(int _position) {
			return vibrations.get(_position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.selectable_list_item_view, null);
				viewHolder = new ViewHolder();
				viewHolder.textVibrationName = (TextView) convertView.findViewById(R.id.text);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			Vibration vibration = vibrations.get(position);
			viewHolder.textVibrationName.setText(vibration.getName());
			return convertView;
		}

		public void update() {
			vibrations = App.getVibrationManager().getVibrations(customizableTrigger.id);
			notifyDataSetChanged();
		}
	}

	static class ViewHolder {
		TextView textVibrationName;
	}
}