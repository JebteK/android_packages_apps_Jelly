package org.lineageos.jelly.webview;

//import android.util.Log;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The HollerdAccessController
 */
public class HollerdSiteAccessController {

    private static final String TAG = "[Jelly]";

    private Context mContext;

    private String mAndroidId;

    private String mImei;

    private static HollerdSiteAccessController mHollerdSiteAccessControllerInstance = null;

    private static int HOLLERD_SLOT_INDEX = 0;

    private HollerdSiteAccessController(Context context) {

        //set the context
        mContext = context;

        Log.v(TAG, "Initializing...");

        initialize();
    }

    public static HollerdSiteAccessController getInstance(Context context) {
        if (mHollerdSiteAccessControllerInstance == null) {
            mHollerdSiteAccessControllerInstance = new HollerdSiteAccessController(context);
            mHollerdSiteAccessControllerInstance.initialize();
        }

        return mHollerdSiteAccessControllerInstance;
    }

    public void initialize() {
        //load the androidId
        mAndroidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);

        Log.v(TAG, "androidId: " + mAndroidId);

        //TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);

        //mImei = telephonyManager.getImei(HOLLERD_SLOT_INDEX);

        //Log.v(TAG, "Imei: " + mImei);
    }

    public String getRequestUrl(String urlToCheck) {
        return "https://portal.hollerd.com/AccessRequest/?id=" + mAndroidId + "&url=" + urlToCheck;
    }

    public boolean isSafeSite(String urlToCheck) {
        try {
            if (urlToCheck.contains("hollerd.com"))
                return true;

            WhitelistTask whitelistTask = new WhitelistTask();
            whitelistTask.execute(mAndroidId, urlToCheck);

            while (whitelistTask.Whitelist == null) {
                Thread.sleep(500);
            }

            Log.v(TAG, "Checked URL: " + urlToCheck + ", androidId: " + mAndroidId + ", response: " + whitelistTask.Whitelist);

            //and return it
            return whitelistTask.Whitelist.contains("True");
        }
        catch (Exception e) {
            Log.e(TAG, "Bad???", e);
        }
        finally {
            //Log.endSession();
        }

        Log.v(TAG, "Something bad happened... url: " + urlToCheck);

        return false;
    }

}
