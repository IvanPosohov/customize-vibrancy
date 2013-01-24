package ru.ivanp.vibro;

import ru.ivanp.vibro.inapp.BillingService;
import ru.ivanp.vibro.inapp.BillingService.RequestPurchase;
import ru.ivanp.vibro.inapp.BillingService.RestoreTransactions;
import ru.ivanp.vibro.inapp.Consts;
import ru.ivanp.vibro.inapp.Consts.PurchaseState;
import ru.ivanp.vibro.inapp.Consts.ResponseCode;
import ru.ivanp.vibro.inapp.PurchaseObserver;
import ru.ivanp.vibro.inapp.ResponseHandler;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Activity for donating
 * 
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class DonateActivity extends Activity implements OnClickListener {
	// ============================================================================================
	// CONSTANTS
	// ============================================================================================
	public final static String[] CATALOG = { "donate_1_dollar",
			"donate_2_dollar", "donate_3_dollar" };

	// ============================================================================================
	// FIELDS
	// ============================================================================================
	private InappPurchaseObserver mInnapPurchaseObserver;
	private Handler mHandler;
	private BillingService mBillingService;

	// ============================================================================================
	// OVERRIDDEN
	// ============================================================================================
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_donate);
		setupWidgets();

		mHandler = new Handler();
		mInnapPurchaseObserver = new InappPurchaseObserver(mHandler);
		mBillingService = new BillingService();
		mBillingService.setContext(this);

		// check if billing is available
		ResponseHandler.register(mInnapPurchaseObserver);
		if (!mBillingService.checkBillingSupported()) {
			showDialog(Consts.DIALOG_CANNOT_CONNECT_ID);
		}
		ResponseHandler.register(mInnapPurchaseObserver);
	}

	@Override
	protected void onPause() {
		super.onPause();
		ResponseHandler.unregister(mInnapPurchaseObserver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBillingService != null) {
			mBillingService.unbind();
		}
	}

	@Override
	public void onClick(View _view) {
		switch (_view.getId()) {
		case R.id.btn_rate:
			Intent marketIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=ru.ivanp.vibro"));
			marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
					| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			startActivity(marketIntent);
			break;
		case R.id.btn_donate_1:
			makeDonate(0);
			break;
		case R.id.btn_donate_2:
			makeDonate(1);
			break;
		case R.id.btn_donate_3:
			makeDonate(2);
			break;
		}

	}

	// ============================================================================================
	// METHODS
	// ============================================================================================
	/**
	 * Process widgets setup
	 */
	private void setupWidgets() {
		Button btn_rate = (Button) findViewById(R.id.btn_rate);
		btn_rate.setOnClickListener(this);
		Button btn_donate1 = (Button) findViewById(R.id.btn_donate_1);
		btn_donate1.setText(String.format(getString(R.string.donate_amount),
				"1"));
		btn_donate1.setOnClickListener(this);
		Button btn_donate2 = (Button) findViewById(R.id.btn_donate_2);
		btn_donate2.setText(String.format(getString(R.string.donate_amount),
				"2"));
		btn_donate2.setOnClickListener(this);
		Button btn_donate3 = (Button) findViewById(R.id.btn_donate_3);
		btn_donate3.setText(String.format(getString(R.string.donate_amount),
				"3"));
		btn_donate3.setOnClickListener(this);
	}

	private void makeDonate(int _index) {
		if (!mBillingService.requestPurchase(CATALOG[_index],
				Consts.ITEM_TYPE_INAPP, null)) {
			showDialog(Consts.DIALOG_BILLING_NOT_SUPPORTED_ID);
		}
	}

	// ============================================================================================
	// INTERNAL CLASSES
	// ============================================================================================
	/**
	 * Used for receiving callbacks from Android Market application
	 */
	private class InappPurchaseObserver extends PurchaseObserver {
		public InappPurchaseObserver(Handler handler) {
			super(DonateActivity.this, handler);
		}

		public void onBillingSupported(boolean supported, String type) {
			if (!supported) {
				showDialog(Consts.DIALOG_BILLING_NOT_SUPPORTED_ID);
			}
		}

		public void onPurchaseStateChange(PurchaseState purchaseState, String itemId, int quantity,
				long purchaseTime,
				String developerPayload) {
		}

		public void onRequestPurchaseResponse(RequestPurchase request,
				ResponseCode responseCode) {
			if (responseCode == ResponseCode.RESULT_OK) {
				setResult(Activity.RESULT_OK);
				finish();
			}
		}

		public void onRestoreTransactionsResponse(RestoreTransactions request,
				ResponseCode responseCode) {
		}
	}
}