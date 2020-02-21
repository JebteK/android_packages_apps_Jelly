package org.lineageos.jelly.webview;

//import android.util.Log;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

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

    private static String IMEI_FILE = "hollerd_imei.txt";
    private static int HOLLERD_SLOT_INDEX = 0;

    private Context mContext;

    private String mWhitelistNumbers;
    private String mImei;
    private String mAndroidId;

    public HollerdSiteAccessController(Context context) {

        //set the context
        mContext = context;
    }

    public void initialize() {
        //load the saved imei
        mImei = readFromFile();
    }

    public String getRequestUrl(String urlToCheck) {
        return "https://portal.hollerd.com/AccessRequest/?imei=" + mImei + "&url=" + urlToCheck;
    }

    public boolean isSafeSite(String urlToCheck) {
        try {
            if (urlToCheck.contains("hollerd.com"))
                return true;

            URL url = new URL("https://api.hollerd.com/Hos/CheckSiteAccess/?imei=" + mImei + "&url=" + urlToCheck);

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
                    //Log.i("getWhitelistNumbers", "responseLine: " + responseLine, this);
                }

                //and return it
                return response.toString().contains("True");
            }
            catch (Exception exception) {
                //Log.e("getWhitelistNumbers", exception, "Process Response", this);
            }
        }
        catch (MalformedURLException malformedURLException) {
            //Log.e("getWhitelistNumbers", malformedURLException, "Bad URL?", this);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            //Log.e("getWhitelistNumbers", unsupportedEncodingException, "Bad Encoding?", this);
        }
        catch (IOException ioException) {
            //Log.e("getWhitelistNumbers", ioException, "Bad IO?", this);
        }
        catch (Exception e) {
            //Log.e("getWhitelistNumbers", e, "Bad???", this);
        }
        finally {
            //Log.endSession();
        }

        return false;
    }

    private String readFromFile() {

        String ret = "";

        try {
            //check if the file exists
            File file = new File(IMEI_FILE);

            if (!file.exists())
                return ret;

            InputStream inputStream = mContext.openFileInput(IMEI_FILE);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            //Log.e("readFromFile", e, "File not found: " + e.toString(), this);
        } catch (IOException e) {
            //Log.e("readFromFile", e, "Can not read file: " + e.toString(), this);
        }

        return ret;
    }
}
