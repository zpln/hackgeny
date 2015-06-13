package zpln.meetme;

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
          // new GetDetailedEventTask().execute(1);

//          setContentView(R.layout.activity_main);

          //setContentView(R.layout.event_list);
          //createPartyListView();
//
  //      setContentView(R.layout.party_event_view);
//        createPartyEventView();
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
                                    DetailedEvent detailedEvent = events.get(position);
                                    event_name.setText(detailedEvent.getEventName());
                                    TextView location = (TextView) upperDataLayout.getChildAt(1);
                                    location.setText(detailedEvent.getPollResult("Location"));
                                    TextView date = (TextView) lowerDataLayout.getChildAt(0);
                                    date.setText(detailedEvent.getPollResult("Time"));
                                    TextView participants = (TextView) lowerDataLayout.getChildAt(1);
                                    participants.setText(String.format("%d invited", detailedEvent.getUsers().size()));
                                    return layout;
                                }
                            }

        );
    }

    private void createPartyEventView() {
        ListView partyPollListView = (ListView) findViewById(R.id.partyPollListView);


        List<PollOption> pollOptions = new LinkedList<>();
        pollOptions.add(new PollOption(1, "Hapak", 2));
        pollOptions.add(new PollOption(1, "Panasi", 3));
        pollOptions.add(new PollOption(1, "Moses", 1));
        pollOptions.add(new PollOption(1, "Shmafia", 5));
        Poll whereDoWeEat = new Poll(1, "Where do we eat?", 1, 1, pollOptions);


        List<PollOption> pollOptions2 = new LinkedList<>();
        pollOptions2.add(new PollOption(1, "Bat-El", 6));
        pollOptions2.add(new PollOption(1, "Nokdim", 4));
        pollOptions2.add(new PollOption(1, "Bney Kedem", 3));
        pollOptions2.add(new PollOption(1, "Efrat", 2));
        Poll whereDoWeGuard = new Poll(1, "Where do we guard?", 1, 1, pollOptions2);

        final Poll polls[] = {whereDoWeEat, whereDoWeGuard, whereDoWeEat};
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) partyPollListView.getLayoutParams();
        lp.height = lp.height*polls.length;
        partyPollListView.setLayoutParams(lp);
        final MainActivity that = this;
        partyPollListView.setAdapter(new BaseAdapter() {
                                         private Typeface mTf;

                                         @Override
                                         public int getCount() {
                                             return polls.length;
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
                                             LinearLayout relativeLayout = (LinearLayout) LayoutInflater.from(that).inflate(R.layout.activity_barchart, null);

                                             BarChart mChart = (BarChart) relativeLayout.getChildAt(0);
                                             updatedChartByPoll(polls[position], mChart);
                                             return relativeLayout;
                                         }

                                         private void updatedChartByPoll(Poll poll, BarChart mChart) {

                                             mChart.setDrawBarShadow(false);
                                             mChart.setDrawValueAboveBar(true);

                                             mChart.setDescription("");

                                             // if more than 60 entries are displayed in the chart, no values will be
                                             // drawn
                                             mChart.setMaxVisibleValueCount(60);

                                             // scaling can now only be done on x- and y-axis separately
                                             mChart.setPinchZoom(false);

                                             // draw shadows for each bar that show the maximum value
                                             // mChart.setDrawBarShadow(true);

                                             // mChart.setDrawXLabels(false);

                                             mChart.setDrawGridBackground(false);
                                             // mChart.setDrawYLabels(false);

                                             mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

                                             XAxis xAxis = mChart.getXAxis();
                                             xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                             xAxis.setTypeface(mTf);
                                             xAxis.setDrawGridLines(false);
                                             xAxis.setSpaceBetweenLabels(2);

                                             ValueFormatter custom = new MyValueFormatter();

                                             YAxis leftAxis = mChart.getAxisLeft();
                                             leftAxis.setTypeface(mTf);
                                             leftAxis.setLabelCount(4);
                                             leftAxis.setValueFormatter(custom);
                                             leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                                             leftAxis.setSpaceTop(15f);

                                             YAxis rightAxis = mChart.getAxisRight();
                                             rightAxis.setDrawGridLines(false);
                                             rightAxis.setTypeface(mTf);
                                             rightAxis.setLabelCount(4);
                                             rightAxis.setValueFormatter(custom);
                                             rightAxis.setSpaceTop(15f);

                                             Legend l = mChart.getLegend();
                                             l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                                             l.setForm(Legend.LegendForm.SQUARE);
                                             l.setFormSize(9f);
                                             l.setTextSize(11f);
                                             l.setXEntrySpace(4f);
                                             // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
                                             // "def", "ghj", "ikl", "mno" });
                                             // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
                                             // "def", "ghj", "ikl", "mno" });

                                             setData(poll, mChart);
                                         }

                                         private void setData(Poll poll, BarChart mChart) {
                                             int count = poll.pollOptions.size();
                                             List<PollOption> values = poll.pollOptions;
                                             int max = 0;
                                             for (PollOption pollOption: values) {
                                                 if (pollOption.pollOptionCount > max) {
                                                     max = pollOption.pollOptionCount;
                                                 }
                                             }
                                             float range = max;
                                             Iterator<PollOption> iterators = poll.pollOptions.iterator();
                                             ArrayList<String> xVals = new ArrayList<String>();
                                             for (int i = 0; i < count; i++) {
                                                 xVals.add(iterators.next().pollOptionName);
                                             }

                                             ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

                                             iterators = poll.pollOptions.iterator();
                                             for (int i = 0; i < count; i++) {
                                                 float val = (float) (iterators.next().pollOptionCount);
                                                 yVals1.add(new BarEntry(val, i));
                                             }

                                             BarDataSet set1 = new BarDataSet(yVals1, poll.pollName);
                                             set1.setBarSpacePercent(40f);

                                             ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
                                             dataSets.add(set1);

                                             BarData data = new BarData(xVals, dataSets);
                                             // data.setValueFormatter(new MyValueFormatter());
                                             data.setValueTextSize(10f);
                                             data.setValueTypeface(mTf);

                                             mChart.setData(data);
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
