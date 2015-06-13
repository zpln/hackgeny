package zpln.meetme;

import android.util.JsonReader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamar on 6/13/2015.
 */
public class Utility {
    public static String userId = "0545920004";
    public static String serverUrl = new String("http://10.0.0.7:5000/");


    public static String addParametersToUrl(String url, List<NameValuePair> params){
        if(!url.endsWith("?")) {
            url += "?";
        }

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }



}
