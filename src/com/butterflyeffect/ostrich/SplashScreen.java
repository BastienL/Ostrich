package com.butterflyeffect.ostrich;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashScreen extends Activity {
	
	private static int SPLASH_TIME_OUT = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		TextView ostrichWords = (TextView) findViewById(R.id.ostrich_speaking);
		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
		ostrichWords.setTypeface(face);
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				
				Intent i = new Intent(SplashScreen.this, MainActivity.class);
				startActivity(i);

				finish();
			}
		}, SPLASH_TIME_OUT);
	}
}