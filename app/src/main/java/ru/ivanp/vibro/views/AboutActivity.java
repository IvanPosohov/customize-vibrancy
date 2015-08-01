package ru.ivanp.vibro.views;

import java.util.regex.Matcher;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;

/**
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class AboutActivity extends BaseActivity {
	// ============================================================================================
	// LIFECYCLE
	// ============================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
    // METHODS
    // ============================================================================================
    public static void startActivity(Context _context) {
        Intent intent = new Intent(_context, AboutActivity.class);
        _context.startActivity(intent);
    }
}