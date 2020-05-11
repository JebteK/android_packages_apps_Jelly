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

    private static int HOLLERD_SLOT_INDEX = 0;

    public HollerdSiteAccessController(Context context) {

        //set the context
        mContext = context;

        Log.v(TAG, "Initializing...");

        initialize();
    }

    @SuppressLint("MissingPermission")
    public void initialize() {
        //load the androidId
        mAndroidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);

        Log.v(TAG, "androidId: " + mAndroidId);

        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);

        mImei = telephonyManager.getImei(HOLLERD_SLOT_INDEX);

        Log.v(TAG, "Imei: " + mImei);
    }

    public String getRequestUrl(String urlToCheck) {
        return "https://portal.hollerd.com/AccessRequest/?id=" + mAndroidId + "&url=" + urlToCheck;
    }

    public boolean isSafeSite(String urlToCheck) {
        try {
            if (urlToCheck.contains("hollerd.com"))
                return true;

            URL url = new URL("https://api.hollerd.com/Hos/CheckSiteAccess/?id=" + mAndroidId + "&url  =" + urlToCheck);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "text/html; charset=utf-8");
            con.setRequestProperty("Accept", "text/html");

            con.setDoOutput(true);

            //int code = con.getResponseCode();
            //Log.i("getWhitelistNumbers", "code: " + code, this);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                    Log.v(TAG, "responseLine: " + responseLine);
                }

                //and return it
                return response.toString().contains("True");
            }
            catch (Exception exception) {
                Log.e(TAG, "Process Response", exception);
            }
        }
        catch (MalformedURLException malformedURLException) {
            Log.e(TAG, "Bad URL?", malformedURLException);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            Log.e(TAG, "Bad Encoding?", unsupportedEncodingException);
        }
        catch (IOException ioException) {
            Log.e(TAG, "Bad IO?", ioException);
        }
        catch (Exception e) {
            Log.e(TAG, "Bad???", e);
        }
        finally {
            //Log.endSession();
        }

        return false;
    }

}
