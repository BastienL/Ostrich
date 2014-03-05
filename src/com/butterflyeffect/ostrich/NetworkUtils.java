package com.butterflyeffect.ostrich;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class NetworkUtils {
	
	static void turnData(Context context, boolean on) throws Exception {

		int buildVersion = Build.VERSION.SDK_INT;

		if(buildVersion == Build.VERSION_CODES.FROYO) {

			try {
				
				Method dataConnSwitchmethod;
				Class telephonyManagerClass;
				Object ITelephonyStub;
				Class ITelephonyClass;
				
				TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
				
				Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
				getITelephonyMethod.setAccessible(true);
				
				ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
				ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

				if(on) {
					
					dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("enableDataConnectivity"); 
				} 
				
				else {
					
					dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("disableDataConnectivity");
				}
				
				dataConnSwitchmethod.setAccessible(true);
				dataConnSwitchmethod.invoke(ITelephonyStub);
			} 
			
			catch(Exception e) {
				
				Toast.makeText(context, R.string.error_occured, Toast.LENGTH_SHORT).show();
			}
		}
		
		else {
			
			final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final Class conmanClass = Class.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField.get(conman);
			final Class iConnectivityManagerClass =  Class.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);
			setMobileDataEnabledMethod.invoke(iConnectivityManager, on);
		}
	}
}
