package zpln.meetme;

import android.os.AsyncTask;
import android.util.JsonReader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamar on 6/13/2015.
 */
public class GetDetailedEventTask extends AsyncTask<Integer, Void, DetailedEvent> {

    protected DetailedEvent doInBackground(Integer... eventId) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = null;
        HttpResponse response = null;
        JsonReader reader = null;
        DetailedEvent detailedEvent = null;

        try {
            //request = new HttpGet(serverUrl + "get_event_details");
            //request.addHeader("user_id", userId);
            //request.addHeader("event_id", String.valueOf(eventId[0]));
            List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
            urlParams.add(new BasicNameValuePair("user_id", Utility.userId));
            urlParams.add(new BasicNameValuePair("event_id", String.valueOf(eventId[0])));
            request = new HttpGet(Utility.addParametersToUrl(Utility.serverUrl + "get_event_details", urlParams));
            response = client.execute(request);
            reader = new JsonReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            detailedEvent = new DetailedEvent(reader);
            reader.close();
        }
        catch (Exception e) {
            String msg = e.getMessage();
        }

        return detailedEvent;
    }

    protected void onPostExecute(DetailedEvent detailedEvent) {
        //TODO: tomer, put your code here
        return;
    }
}