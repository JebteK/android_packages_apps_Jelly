package org.lineageos.jelly.webview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;
import android.support.v4.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.AsyncTask;
import android.util.Log;

public class WhitelistTask extends AsyncTask<String, Void, String> {
    private Exception exception;
    private static String TAG = "[WhitelistTask Jelly]";

    public String Whitelist = null;

    protected String doInBackground(String... strings) {
        try {
            URL url = new URL("https://api.hollerd.com/Hos/CheckSiteAccess/?id=" + strings[0] + "&url=" + strings[1]);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "text/html; charset=utf-8");
            con.setRequestProperty("Accept", "text/html");

            //con.setDoOutput(true);

            int code = con.getResponseCode();
            Log.v(TAG, "code: " + code);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                Log.v(TAG, "response: " + response.toString());

                Whitelist = response.toString();

                //and return it
                return response.toString();
            }
            catch (Exception exception) {
                Log.e(TAG, "getWhitelistUrl: " + exception.getMessage(), exception);
                return "Error";
            }
        } catch (Exception e) {
            this.exception = e;
            Log.e(TAG, "getWhitelistUrl: " + exception.getMessage(), exception);
            return "Error";
        }
    }

    protected void onPostExecute(String whitelist) {
        // TODO: check this.exception
        Log.v(TAG, "Returning whitelist status: " + whitelist);
        Whitelist = whitelist;
    }
}
