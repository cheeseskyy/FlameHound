package com.example.hvale.loginapp;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hvale on 23-03-2018.
 */

public class RequestsREST {
    public static String doPOST(URL url, JSONObject data) throws IOException {
        InputStream stream = null;
        OutputStream out = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setChunkedStreamingMode(0);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-type", "application/json");
            //open communication link
            out = new BufferedOutputStream(connection.getOutputStream());
            out.write(data.toString().getBytes());
            out.flush();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code" + responseCode);
            }
            //retrieve the response body as an InputStream
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStream(stream, 1024);
            }
        }finally {
            if (out != null) out.close();
            if (stream != null) stream.close();
            if (connection != null) connection.disconnect();
        }
        return result;
    }

    private static String readStream(InputStream is, int len) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),len);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }
}
