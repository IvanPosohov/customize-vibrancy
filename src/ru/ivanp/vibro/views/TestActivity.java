package ru.ivanp.vibro.views;

import com.immersion.uhl.ImmVibe;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.telephony.VibrationService;
import ru.ivanp.vibro.vibrations.Trigger;
import ru.ivanp.vibro.vibrations.Vibration;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SeekBar;

/**
 * Allows to test application, can be launched only in DEBUG mode
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class TestActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {
	// ============================================================================================
	// OVERRIDDEN
	// ============================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar_player);
		seekBar.setOnSeekBarChangeListener(this);

		Button btn_player_preset = (Button) findViewById(R.id.btn_player_preset);
		btn_player_preset.setOnClickListener(this);
		Button btn_player_ivt = (Button) findViewById(R.id.btn_player_ivt);
		btn_player_ivt.setOnClickListener(this);
		Button btn_player_user = (Button) findViewById(R.id.btn_player_user);
		btn_player_user.setOnClickListener(this);
		Button btn_player_preset_cycled = (Button) findViewById(R.id.btn_player_preset_cycled);
		btn_player_preset_cycled.setOnClickListener(this);
		Button btn_player_ivt_cycled = (Button) findViewById(R.id.btn_player_ivt_cycled);
		btn_player_ivt_cycled.setOnClickListener(this);
		Button btn_player_user_cycled = (Button) findViewById(R.id.btn_player_user_cycled);
		btn_player_user_cycled.setOnClickListener(this);
		Button btn_stop = (Button) findViewById(R.id.btn_player_stop);
		btn_stop.setOnClickListener(this);
		Button btn_triggers_kill_service = (Button) findViewById(R.id.btn_triggers_kill_service);
		btn_triggers_kill_service.setOnClickListener(this);
		Button btn_triggers_incoming_call = (Button) findViewById(R.id.btn_triggers_incoming_call);
		btn_triggers_incoming_call.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		App.getPlayer().stop();
	}

	public void onItemClick(AdapterView<?> _adapter, View _view, int _position, long _id) {
		// play pattern on click
		Trigger trigger = (Trigger) _adapter.getItemAtPosition(_position);
		Vibration vibration = App.getVibrationManager().getVibration(trigger.vibrationID);
		App.getPlayer().playOrStop(vibration);
	}

	// ============================================================================================
	// WIDGETS CALLBACKS
	// ============================================================================================
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int magnitude = progress * ImmVibe.VIBE_MAX_MAGNITUDE / 100;
		App.getPlayer().playMagnitude(magnitude);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		int magnitude = seekBar.getProgress() * ImmVibe.VIBE_MAX_MAGNITUDE / 100;
		App.getPlayer().playMagnitude(magnitude);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		App.getPlayer().stop();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_player_preset:
			App.getPlayer().playOrStop(App.getVibrationManager().getVibration(21));
			break;
		case R.id.btn_player_ivt:
			App.getPlayer().playOrStop(App.getVibrationManager().getVibration(148));
			break;
		case R.id.btn_player_user:
			App.getPlayer().playOrStop(App.getVibrationManager().getVibration(157));
			break;
		case R.id.btn_player_preset_cycled:
			App.getPlayer().playRepeat(App.getVibrationManager().getVibration(21));
			break;
		case R.id.btn_player_ivt_cycled:
			App.getPlayer().playRepeat(App.getVibrationManager().getVibration(148));
			break;
		case R.id.btn_player_user_cycled:
			App.getPlayer().playRepeat(App.getVibrationManager().getVibration(157));
			break;
		case R.id.btn_triggers_kill_service:
			App.getPlayer().stop();
			break;
		case R.id.btn_triggers_incoming_call:
			int imcomingCallVibrationID = App.getTriggerManager().getVibrationID(
					Trigger.INCOMING_CALL);
			VibrationService.start(this, imcomingCallVibrationID, true, -1);
			break;
		}
	}
	
    // ============================================================================================
    // METHODS
    // ============================================================================================
    public static void startActivity(Context _context) {
        Intent intent = new Intent(_context, TestActivity.class);
        _context.startActivity(intent);
    }
}