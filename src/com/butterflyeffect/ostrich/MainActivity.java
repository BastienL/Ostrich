package com.butterflyeffect.ostrich;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private NetworkInfo mobileNetwork;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		mobileNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		TextView ostrichWords = (TextView) findViewById(R.id.ostrich_speaking);
		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
		ostrichWords.setTypeface(face);

		ImageView buttonOn = (ImageView) findViewById(R.id.button_on);
		ImageView buttonOff = (ImageView) findViewById(R.id.button_off);
		
		// if we can't get network info about the mobile connection, we're facing an error
		if(mobileNetwork == null) {
			
			Toast.makeText(this, R.string.error_occured, Toast.LENGTH_SHORT).show();
		}
		
		// the mobile network is not available, it means the user does not have a sim card
		else if(!mobileNetwork.isAvailable()) {
			
			Toast.makeText(getApplicationContext(), R.string.have_a_sim_card, Toast.LENGTH_LONG).show();
			updateView(false);
			updateWidget(getApplicationContext(), false);
		}
		
		// the user is connected to the mobile network
		else if (mobileNetwork.isConnectedOrConnecting()) {
			
			updateView(true);
		}
		
		// the user is not connected to the mobile network
		else {
			
			updateView(false);
		}
		
		// onClickListener to enable the mobile connection
		buttonOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mobileNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				
				if(mobileNetwork == null) {
					
					Toast.makeText(getApplicationContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
				}
				
				else if(!mobileNetwork.isAvailable()) {
					
					Toast.makeText(getApplicationContext(), R.string.have_a_sim_card, Toast.LENGTH_LONG).show();
					updateView(false);
					updateWidget(getApplicationContext(), false);
				}
				
				else if (!mobileNetwork.isConnectedOrConnecting()) {
					
					try {
						
						NetworkUtils.turnData(getApplicationContext(), true);
					} 
					
					catch (Exception e) {
						
						Toast.makeText(getApplicationContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
					}
					
					updateView(true);
					updateWidget(getBaseContext(), true);
				}
				
			}
		});
		
		// onClickListener to disable the mobile connection
		buttonOff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mobileNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				
				if(mobileNetwork == null) {
					
					Toast.makeText(getApplicationContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
				}
				
				else if(!mobileNetwork.isAvailable()) {
					
					Toast.makeText(getApplicationContext(), R.string.have_a_sim_card, Toast.LENGTH_LONG).show();
					updateView(false);
					updateWidget(getApplicationContext(), false);
				}
				
				else if (mobileNetwork.isConnectedOrConnecting()) {
					
					try {
						
						NetworkUtils.turnData(getApplicationContext(), false);
					} 
					
					catch (Exception e) {
					
						Toast.makeText(getApplicationContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
					}
					
					updateView(false);
					updateWidget(getBaseContext(), false);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		final ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		mobileNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		TextView ostrichWords = (TextView) findViewById(R.id.ostrich_speaking);
		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
		ostrichWords.setTypeface(face);

		if(mobileNetwork == null) {
			
			Toast.makeText(getApplicationContext(), R.string.error_occured, Toast.LENGTH_SHORT).show();
		}
		
		else if(!mobileNetwork.isAvailable()) {
			
			Toast.makeText(getApplicationContext(), R.string.have_a_sim_card, Toast.LENGTH_LONG).show();
			updateView(false);
			updateWidget(getApplicationContext(), false);
		}
		
		else if(mobileNetwork.isConnectedOrConnecting()) {
			
			updateView(true);
			updateWidget(getApplicationContext(), true);
		}
		
		else {
			
			updateView(false);
			updateWidget(getApplicationContext(), false);
		}
	}
	
	private void updateView(boolean on) {
		
		TextView ostrichWords = (TextView) findViewById(R.id.ostrich_speaking);
		LinearLayout main = (LinearLayout) findViewById(R.id.main);
		ImageView ostrichImage = (ImageView) findViewById(R.id.ostrich_image);
		
		if (on) {
			ostrichWords.setText(R.string.ostrich_listening);
			main.setBackgroundColor(getResources().getColor(R.color.green));
			ostrichImage.setImageResource(R.drawable.ostrich_on);
		}
		
		else {
			ostrichWords.setText(R.string.ostrich_dont_care);
			main.setBackgroundColor(getResources().getColor(R.color.red));
			ostrichImage.setImageResource(R.drawable.ostrich_off);
		}
	}
	
	private void updateWidget(Context context, boolean on)
	{
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		ComponentName ostrichWidget = new ComponentName(context, OstrichWidgetProvider.class);
		if(on) remoteViews.setImageViewResource(R.id.ostrich_widget_button, R.drawable.widget_off);
		else remoteViews.setImageViewResource(R.id.ostrich_widget_button, R.drawable.widget_on);
		appWidgetManager.updateAppWidget(ostrichWidget, remoteViews);
	}
}
