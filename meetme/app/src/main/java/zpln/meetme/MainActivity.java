package zpln.meetme;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private String userId;
    private HttpClient client;
    private String serverUrl;

    private class getEventsTask extends AsyncTask<Void, Void, List<Event>> {

        protected List<Event> doInBackground(Void... params) {
            HttpGet request = null;
            HttpResponse response = null;
            JsonReader reader = null;
            List<Event> events = null;

            try {
                // request = new HttpGet(serverUrl + "get_events");
                // request.addHeader("user_id", userId);
                List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
                urlParams.add(new BasicNameValuePair("user_id", userId));
                request = new HttpGet(addParametersToUrl(serverUrl + "get_events", urlParams));
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

        protected void onPostExecute(List<Event> events) {
            //TODO: tomer, put your code here
            return;
        }
    }

    private class getDetailedEventTask extends AsyncTask<Integer, Void, DetailedEvent> {

        protected DetailedEvent doInBackground(Integer... eventId) {
            HttpGet request = null;
            HttpResponse response = null;
            JsonReader reader = null;
            DetailedEvent detailedEvent = null;

            try {
                //request = new HttpGet(serverUrl + "get_event_details");
                //request.addHeader("user_id", userId);
                //request.addHeader("event_id", String.valueOf(eventId[0]));
                List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
                urlParams.add(new BasicNameValuePair("user_id", userId));
                urlParams.add(new BasicNameValuePair("event_id", String.valueOf(eventId[0])));
                request = new HttpGet(addParametersToUrl(serverUrl + "get_event_details", urlParams));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = "0545920004";
        client = new DefaultHttpClient();
        serverUrl = new String("http://10.0.0.13:5000/");

        new getEventsTask().execute();
        new getDetailedEventTask().execute(1);

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void postPollResponse(int poll_option_id) {
        HttpPost post = null;
        HttpResponse response = null;

        try {
            post = new HttpPost(serverUrl);
            List<NameValuePair> invitationResponseData = new ArrayList<NameValuePair>(2);
            invitationResponseData.add(new BasicNameValuePair("user_id", userId));
            invitationResponseData.add(new BasicNameValuePair("option_id", String.valueOf(poll_option_id)));
            post.setEntity(new UrlEncodedFormEntity(invitationResponseData));
            response = client.execute(post);

        } catch (IOException e) {
            System.err.println("IndexOutOfBoundsException: " + e.getMessage());
        }
        finally {

        }
    }

    public void postEvent(DetailedEvent detailedEvent) {
        HttpPost post = null;
        HttpResponse response = null;

        try {
            post = new HttpPost(serverUrl);
            List<NameValuePair> eventData = new ArrayList<NameValuePair>(2);
            eventData.add(new BasicNameValuePair("user_id", userId));
            eventData.add(new BasicNameValuePair("event_name", detailedEvent.getEventName()));
            post.setEntity(new UrlEncodedFormEntity(eventData));
            response = client.execute(post);

        } catch (IOException e) {
            System.err.println("IndexOutOfBoundsException: " + e.getMessage());
        }
        finally {

        }
    }

    private String addParametersToUrl(String url, List<NameValuePair> params){
        if(!url.endsWith("?")) {
            url += "?";
        }

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }
}
