package com.android.shnellers.hushed.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sean on 31/10/17.
 */

public class DownloadUrl
{
    public String readUrl(String strUrl) throws IOException
    {
        String data = "";

        URL url = new URL(strUrl);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();

        InputStream in = urlConnection.getInputStream();

        InputStreamReader isr = new InputStreamReader(in);

        BufferedReader reader = new BufferedReader(isr);

        StringBuilder sb = new StringBuilder();

        String line = "";

        while ((line = reader.readLine()) != null)
        {
            sb.append(line);
        }

        data = sb.toString();

        reader.close();
        isr.close();
        in.close();
        urlConnection.disconnect();

        return data;
    }
}
