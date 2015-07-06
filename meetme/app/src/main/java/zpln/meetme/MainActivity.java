package zpln.meetme;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    final MainActivity that = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new GetEventsTask().execute();
    }

    private int getImageByStatus(Status status) {
        switch (status) {
            case NOT_ATTENDING:
                return R.mipmap.no;

            case ATTENDING:
                return R.mipmap.yes;

            case UNANSWERED:
            default:
                return R.mipmap.maybe;
        }
    }

    @Override
    // in order that this activity be refreshred whenever it is displayed (after a call to startActivityOnResult returns)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
        startActivity(getIntent());
    }

    private void createPartyListView(final List<DetailedEvent> events) {
        ListView listview = (ListView) findViewById(R.id.listView);



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
                                    ImageView icon = (ImageView) layout.getChildAt(0);
                                    LinearLayout dataLayout = (LinearLayout) layout.getChildAt(1);
                                    LinearLayout upperDataLayout = (LinearLayout) dataLayout.getChildAt(0);
                                    LinearLayout lowerDataLayout = (LinearLayout) dataLayout.getChildAt(1);


                                    TextView event_name = (TextView) upperDataLayout.getChildAt(0);
                                    final DetailedEvent detailedEvent = events.get(position);
                                    event_name.setText(detailedEvent.getEventName());
                                    event_name.setTextColor(Color.BLACK);
                                    event_name.setTextSize(17);
                                    TextView location = (TextView) upperDataLayout.getChildAt(1);
                                    location.setText("@"+detailedEvent.getPollResult("Location"));
                                    location.setTextSize(16);
                                    TextView date = (TextView) lowerDataLayout.getChildAt(0);
                                    date.setText("time: " + detailedEvent.getPollResult("Time"));
                                    date.setTextSize(16);
                                    TextView participants = (TextView) lowerDataLayout.getChildAt(1);
                                    participants.setText(String.format("%d invited", detailedEvent.getUsers().size()));
                                    participants.setTextSize(16);

                                    layout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(that, DetailedEventActivity.class);
                                            intent.putExtra("eventId", String.format("%d", detailedEvent.getEventId()));
                                            startActivity(intent);
                                        }
                                    });

                                    icon.setImageResource(that.getImageByStatus(detailedEvent.status));
                                    icon.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Status nextStatus;

                                            switch (detailedEvent.status) {
                                                case NOT_ATTENDING:
                                                case UNANSWERED:
                                                    nextStatus = Status.ATTENDING;
                                                    break;
                                                case ATTENDING:
                                                default:
                                                    nextStatus = Status.NOT_ATTENDING;
                                                    break;
                                            }
                                            detailedEvent.setStatus(nextStatus);
                                            ((ImageView) v).setImageResource(that.getImageByStatus(nextStatus));
                                            new PostEventStatus().execute(new EventStatusAnswer(detailedEvent.getEventId(), nextStatus));
                                        }
                                    });
                                    return layout;
                                }
                            }

        );
    }

    public class PostEventStatus extends AsyncTask<EventStatusAnswer, Void, Void> {

        protected Void doInBackground(EventStatusAnswer... eventStatusAnswer) {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = null;
            HttpResponse response = null;

            try {
                post = new HttpPost(Utility.serverUrl + "change_status");
                List<NameValuePair> eventData = new ArrayList<NameValuePair>(2);
                eventData.add(new BasicNameValuePair("user_id",Utility.userId));
                eventData.add(new BasicNameValuePair("event_id", String.valueOf(eventStatusAnswer[0].getEventId())));
                eventData.add(new BasicNameValuePair("new_status", String.valueOf(eventStatusAnswer[0].getStatus().ordinal())));
                post.setEntity(new UrlEncodedFormEntity(eventData));
                response = client.execute(post);
            } catch (Exception e) {
                String msg = e.getMessage();
            }
            return null;
        }
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
            List<DetailedEvent> events = new ArrayList<DetailedEvent>();
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
            Button createNewEventButton = (Button) findViewById(R.id.createNewEventButton);
            createNewEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(that, CreateEventActivity.class);
                    startActivityForResult(intent, 0);
                }
            });
            createPartyListView(events);
        }
    }
}
