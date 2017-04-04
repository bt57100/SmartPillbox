package com.ece.ing4.ppe.smartpillbox.smartpillbox;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Kevin on 27/03/2017.
 */

public class ConnectToDB {
    private String url = "";

    public ConnectToDB(String url) {
        this.url = url;
    }

    public String connectJSON() {
        String response="";
        int status=0;
        HttpURLConnection connection = null;
        try {
            url = url.replaceAll(" ", "%20");
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(MyGlobalVars.TIMEOUT);
            connection.setReadTimeout(MyGlobalVars.TIMEOUT);
            connection.setRequestProperty("Cookie", MyGlobalVars.myCookie);
            connection.connect();
            status = connection.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    InputStream responseStream = new BufferedInputStream(connection.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(responseStream), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    response = sb.toString();
                    return response;
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return String.valueOf(status)+response;
    }

}
