package zpln.meetme;

import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);



          new GetEventsTask().execute();
    }

    private void createPartyListView(final List<DetailedEvent> events) {
        ListView listview = (ListView) findViewById(R.id.listView);


        final MainActivity that = this;
        listview.setAdapter(new BaseAdapter() {
                                @Override
                                public int getCount() {
                                    return events.size();
                                }

                                @Override
                                public Object getItem(int position) {
                                    return null;
                                }

                                @Override
                                public long getItemId(int position) {
                                    return 0;
                                }

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    LinearLayout layout = (LinearLayout) LayoutInflater.from(that).inflate(R.layout.mylist, null);
                                    LinearLayout dataLayout = (LinearLayout) layout.getChildAt(1);
                                    LinearLayout upperDataLayout = (LinearLayout) dataLayout.getChildAt(0);
                                    LinearLayout lowerDataLayout = (LinearLayout) dataLayout.getChildAt(1);


                                    TextView event_name = (TextView) upperDataLayout.getChildAt(0);
                                    final DetailedEvent detailedEvent = events.get(position);
                                    event_name.setText(detailedEvent.getEventName());
                                    TextView location = (TextView) upperDataLayout.getChildAt(1);
                                    location.setText(detailedEvent.getPollResult("Location"));
                                    TextView date = (TextView) lowerDataLayout.getChildAt(0);
                                    date.setText(detailedEvent.getPollResult("Time"));
                                    TextView participants = (TextView) lowerDataLayout.getChildAt(1);
                                    participants.setText(String.format("%d invited", detailedEvent.getUsers().size()));

                                    layout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(that, DetailedEventActivity.class);
                                            intent.putExtra("eventId", String.format("%d", detailedEvent.getEventId()));
                                            startActivity(intent);
                                        }
                                    });
                                    return layout;
                                }
                            }

        );
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
        HttpClient client = new DefaultHttpClient();
        HttpPost post = null;
        HttpResponse response = null;

        try {
            post = new HttpPost(Utility.serverUrl);
            List<NameValuePair> invitationResponseData = new ArrayList<NameValuePair>(2);
            invitationResponseData.add(new BasicNameValuePair("user_id", Utility.userId));
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
        HttpClient client = new DefaultHttpClient();
        HttpPost post = null;
        HttpResponse response = null;

        try {
            post = new HttpPost(Utility.serverUrl);
            List<NameValuePair> eventData = new ArrayList<NameValuePair>(2);
            eventData.add(new BasicNameValuePair("user_id",Utility. userId));
            eventData.add(new BasicNameValuePair("event_name", detailedEvent.getEventName()));
            post.setEntity(new UrlEncodedFormEntity(eventData));
            response = client.execute(post);

        } catch (IOException e) {
            System.err.println("IndexOutOfBoundsException: " + e.getMessage());
        }
        finally {

        }
    }


    private class GetEventsTask extends AsyncTask<Void, Void, List<DetailedEvent>> {

        public List<DetailedEvent> doInBackground(Void... params) {

            HttpClient client = new DefaultHttpClient();
            HttpGet request;
            HttpResponse response;
            JsonReader reader;
            List<DetailedEvent> events = null;

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

        public List<DetailedEvent> readEvents(JsonReader reader) throws IOException {
            List<DetailedEvent> events = new ArrayList<>();
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("events")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        events.add(new DetailedEvent(reader));
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return events;
        }

        public void onPostExecute(List<DetailedEvent> events) {
            setContentView(R.layout.event_list);
            createPartyListView(events);
        }
    }
}
