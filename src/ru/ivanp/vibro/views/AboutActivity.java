package ru.ivanp.vibro.views;

import java.util.regex.Matcher;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends ActionBarActivity {
	// ============================================================================================
	// LIFECYCLE
	// ============================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		setContentView(R.layout.activity_about);

		TextView textVersion = (TextView) findViewById(R.id.textVersion);
		textVersion.setText(String.format("%s %s", getString(R.string.version), App.VERSION_NAME));

		TextView textFeedback = (TextView) findViewById(R.id.textFeedback);
		TransformFilter addSubjFilter = new TransformFilter() {
			public final String transformUrl(final Matcher match, String url) {
				return url + "?subject=Customize Vibrancy feedback"; // &body=message
			}
		};
		Linkify.addLinks(textFeedback, App.EMAIL_ADDRESS_PATTERN, "mailto:", null, addSubjFilter);
	}

	// ============================================================================================
	// MENU
	// ============================================================================================
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    // ============================================================================================
    // METHODS
    // ============================================================================================
    public static void startActivity(Context _context) {
        Intent intent = new Intent(_context, AboutActivity.class);
        _context.startActivity(intent);
    }
}