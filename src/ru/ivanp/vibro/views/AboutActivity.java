package ru.ivanp.vibro.views;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.ivanp.vibro.R;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;

public class AboutActivity extends Activity {
	// ============================================================================================
	// OVERRIDEN
	// ============================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		TextView text_version = (TextView) findViewById(R.id.about_text_version);
		TextView text_feedback = (TextView) findViewById(R.id.about_text_feedback);

		// find version name and set it to TextView
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			text_version.setText(String.format(getString(R.string.about_version), pinfo.versionName));
		} catch (NameNotFoundException e) {
			/* doesn't matter */
		}

		// using Linkify to detect mail-me URL
		Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-]{1,256}" + "\\@"
				+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
		TransformFilter addSubjFilter = new TransformFilter() {
			public final String transformUrl(final Matcher match, String url) {
				return url + "?subject=" + getString(R.string.app_name) + " feedback"; // &body=message
			}
		};
		Linkify.addLinks(text_feedback, EMAIL_ADDRESS_PATTERN, "mailto:", null, addSubjFilter);
	}
}