package zpln.meetme;

import android.os.AsyncTask;
import android.util.JsonReader;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamar on 6/13/2015.
 */
public class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {

    public List<Event> doInBackground(Void... params) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = null;
        HttpResponse response = null;
        JsonReader reader = null;
        List<Event> events = null;

        try {
            // request = new HttpGet(serverUrl + "get_events");
            // request.addHeader("user_id", userId);
            List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
            urlParams.add(new BasicNameValuePair("user_id", Utility.userId));
            request = new HttpGet(Utility.addParametersToUrl(Utility.serverUrl + "get_events", urlParams));
            response = client.execute(request);
            reader = new JsonReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            events = readEvents(reader);
            reader.close();
        }
        catch (Exception e) {
            String msg = e.getMessage();
        }

        return events;
    }

    private List<Event> readEvents(JsonReader reader) throws IOException {
        List<Event> events = new ArrayList<Event>();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("events")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    events.add(new Event(reader));
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return events;
    }

    public void onPostExecute(List<Event> events) {
        //TODO: tomer, put your code here
        return;
    }
}