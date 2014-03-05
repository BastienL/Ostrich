package com.butterflyeffect.ostrich;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.RemoteViews;
import android.widget.Toast;
 
public class OstrichWidgetProvider extends AppWidgetProvider {
 
	private NetworkInfo mobileNetwork;
	
	@Override
	public void onEnabled(Context context) {
		
		super.onEnabled(context);
		
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		mobileNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		
		if(mobileNetwork == null) {
			
			Toast.makeText(context, R.string.error_occured, Toast.LENGTH_SHORT).show();
		}
		
		else if(!mobileNetwork.isAvailable()) {
			
			Toast.makeText(context, R.string.have_a_sim_card, Toast.LENGTH_LONG).show();
			remoteViews.setImageViewResource(R.id.ostrich_widget_button, R.drawable.widget_on);
		}
		
		else if (mobileNetwork.isConnectedOrConnecting()) {
			remoteViews.setImageViewResource(R.id.ostrich_widget_button, R.drawable.widget_off);
		}
		
		else {
			remoteViews.setImageViewResource(R.id.ostrich_widget_button, R.drawable.widget_on);
		}
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		// we check if it's the first launch or not
		SharedPreferences prefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
		Boolean firstLaunch = prefs.getBoolean("firstLaunch", true);
		
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		mobileNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		// if it's the first launch of the widget, we won't change state of the mobile connection
		if(firstLaunch) {
			Editor editor = prefs.edit();
        	editor.putBoolean("firstLaunch", false);
        	editor.commit();
        	
        	updateToggle(context, appWidgetManager, appWidgetIds, false);
		}
		
		else {
		
			updateToggle(context, appWidgetManager, appWidgetIds, true);
		}
		
	}
	
	private void updateToggle(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, boolean changeState) {
		
		ComponentName thisWidget = new ComponentName(context, OstrichWidgetProvider.class);
	    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	    
	    for (int widgetId : allWidgetIds) {
	
	    	RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			   
	    	if(mobileNetwork == null) {
				
				Toast.makeText(context, R.string.error_occured, Toast.LENGTH_SHORT).show();
			}
	    	
	    	else if(!mobileNetwork.isAvailable()) {
				
				Toast.makeText(context, R.string.have_a_sim_card, Toast.LENGTH_LONG).show();
			}
	    	
	    	else if (mobileNetwork.isConnectedOrConnecting()) {
	    		
	    		if(changeState) {
	    			
	    			try {
	    				
	            		NetworkUtils.turnData(context, false);
					} 
	    			
	    			catch (Exception e) {
	    				
	    				Toast.makeText(context, R.string.error_occured, Toast.LENGTH_SHORT).show();
					}
	    			
	    			remoteViews.setImageViewResource(R.id.ostrich_widget_button, R.drawable.widget_on);
	    		}
	    		
	    		else {
	    		
	    			remoteViews.setImageViewResource(R.id.ostrich_widget_button, R.drawable.widget_off);
	    		}
	    	}
			
			else {
				
				if(changeState) {
	    			
	    			try {
	    				
	            		NetworkUtils.turnData(context, true);
					} 
	    			
	    			catch (Exception e) {
	    				
	    				Toast.makeText(context, R.string.error_occured, Toast.LENGTH_SHORT).show();
					}
	    			
	    			remoteViews.setImageViewResource(R.id.ostrich_widget_button, R.drawable.widget_off);
	    		}
				
				else {
					remoteViews.setImageViewResource(R.id.ostrich_widget_button, R.drawable.widget_on);
				}
				
			}
		
			Intent intent = new Intent(context, OstrichWidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.ostrich_widget_button, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
	    }
	}
	
	@Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        
		SharedPreferences prefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
    	editor.putBoolean("firstLaunch", true);
    	editor.commit();
        
        super.onDeleted(context, appWidgetIds);
    }
}