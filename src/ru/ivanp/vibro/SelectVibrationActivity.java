package ru.ivanp.vibro;

import java.util.ArrayList;

import ru.ivanp.vibro.vibrations.Trigger;
import ru.ivanp.vibro.vibrations.UserVibration;
import ru.ivanp.vibro.vibrations.Vibration;
import ru.ivanp.vibro.vibrations.VibrationsManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Allow to set vibration to the trigger
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class SelectVibrationActivity extends Activity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener {
	// ========================================================================
	// CONSTANTS
	// ========================================================================
	public static final String TRIGGER_ID_KEY = "trigger_id";
	public static final String VIBRATION_ID_KEY = "vibration_id";
	private static final int MI_REMOVE = 0;
	private static final int RECODER_ACTIVITY_REQUEST_CODE = 0;

	// ========================================================================
	// FIELDS
	// ========================================================================
	private ListView lv;
	private ArrayList<Vibration> list;
	private Trigger trigger;
	private int selectedPosition;
	private int longClickedID;

	// ========================================================================
	// OVERRIDDEN
	// ========================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_vibration);

		Bundle data = getIntent().getExtras();
		int triggerID = data.getInt(TRIGGER_ID_KEY, -1);
		trigger = App.getTriggerManager().getTrigger(triggerID);

		setupWidgets();
	}

	@Override
	protected void onResume() {
		list = App.getVibrationManager().getVibrations(trigger.id);
		lv.setAdapter(new ArrayAdapter<Vibration>(this,
				R.layout.selectable_list_item_view, R.id.text, list));
		for (int i = 0; i < list.size(); i++) {
			Vibration vibration = list.get(i);
			lv.setItemChecked(i, vibration.id == trigger.vibrationID);
			if (vibration.id == trigger.vibrationID) {
				// scrolls to checked item
				lv.setSelection(i);
				selectedPosition = i;
			}
		}
		longClickedID = VibrationsManager.NO_VIBRATION_ID;
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		Vibration vibration = list.get(selectedPosition);
		App.getTriggerManager().setTrigger(trigger.id, vibration.id);
		App.getPlayer().stop();
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_new:
			showRecorderSelectionDialog();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long id) {
		App.getPlayer().playOrStop(list.get(position));
		selectedPosition = position;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View view,
			int position, long id) {
		Vibration vibration = (Vibration) adapter.getItemAtPosition(position);
		if (vibration instanceof UserVibration) {
			longClickedID = vibration.id;
			lv.showContextMenu();
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RECODER_ACTIVITY_REQUEST_CODE && data != null) {
			int createdID = data.getIntExtra(VIBRATION_ID_KEY,
					VibrationsManager.NO_VIBRATION_ID);
			App.getTriggerManager().setTrigger(trigger.id, createdID);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// ========================================================================
	// MENU
	// ========================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (App.getTriggerManager().isCustomVibrationAllowed(trigger.id)) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.activity_select_vibration, menu);
		}

		return true;
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_new_vibration:
			showRecorderSelectionDialog();
			break;
		}
		return true;
	};

	// ========================================================================
	// CONTEXT MENU
	// ========================================================================
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (longClickedID != VibrationsManager.NO_VIBRATION_ID) {
			menu.add(Menu.NONE, MI_REMOVE, Menu.NONE, R.string.remove);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MI_REMOVE:
			if (longClickedID != VibrationsManager.NO_VIBRATION_ID) {
				App.getVibrationManager().remove(longClickedID);
				// dirty hack to reload listview items
				onResume();
			}
			break;
		}
		return true;
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		super.onContextMenuClosed(menu);
		longClickedID = VibrationsManager.NO_VIBRATION_ID;
	}

	// ========================================================================
	// METHODS
	// ========================================================================
	/**
	 * Start activity with needed arguments
	 * 
	 * @param _context
	 *            application context
	 * @param _triggerID
	 *            identifier of configurable trigger
	 */
	public static void start(Context _context, int _triggerId) {
		Intent intent = new Intent(_context, SelectVibrationActivity.class);
		intent.putExtra(TRIGGER_ID_KEY, _triggerId);
		_context.startActivity(intent);
	}

	private void setupWidgets() {
		ImageView img_new = (ImageView) findViewById(R.id.img_new);
		if (App.getTriggerManager().isCustomVibrationAllowed(trigger.id)) {
			img_new.setVisibility(View.GONE);
		} else {
			img_new.setOnClickListener(this);
		}

		lv = (ListView) findViewById(R.id.lv);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		lv.setOnItemClickListener(this);
		lv.setOnItemLongClickListener(this);
		registerForContextMenu(lv);
	}

	private void showRecorderSelectionDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_recorder)
				.setNegativeButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.setItems(R.array.list_recorders,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									startActivityForResult(new Intent(
											SelectVibrationActivity.this,
											RecorderActivity.class),
											RECODER_ACTIVITY_REQUEST_CODE);
								} else {
									startActivityForResult(new Intent(
											SelectVibrationActivity.this,
											MorseActivity.class),
											RECODER_ACTIVITY_REQUEST_CODE);
								}
							}
						}).create().show();
	}
}