package zpln.meetme;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DetailedEventActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra("eventId");
        int eventId = Integer.parseInt(message);
        new GetDetailedEventTask().execute(eventId);

    }


    private void createPartyEventView(final DetailedEvent detailedEvent) {
        LinearLayout mainView = (LinearLayout) findViewById(R.id.partyEventView);

        ScrollView scrollView = (ScrollView) mainView.getChildAt(0);
        final LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);
        TextView eventName = (TextView) linearLayout.getChildAt(0);
        eventName.setText(detailedEvent.getEventName());

        LinearLayout extraDetails = (LinearLayout) linearLayout.getChildAt(1);
        TextView eventLocation = (TextView) extraDetails.getChildAt(0);
        eventLocation.setText("at " + detailedEvent.getPollResult("Location"));
        TextView eventDate = (TextView) extraDetails.getChildAt(1);
        eventDate.setText("on " + detailedEvent.getPollResult("Time"));

        ListView partyPollListView = (ListView) findViewById(R.id.partyPollListView);
        View childAt = partyPollListView.getChildAt(1);

        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) partyPollListView.getLayoutParams();
        lp.height = lp.height * detailedEvent.getPolls().size();
        partyPollListView.setLayoutParams(lp);
        final DetailedEventActivity that = this;
        partyPollListView.setAdapter(new BaseAdapter() {
                                         private Typeface mTf;

                                         @Override
                                         public int getCount() {
                                             return detailedEvent.getPolls().size();
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

                                             Poll currentPoll = detailedEvent.getPolls().get(position);

                                             BarChart mChart = (BarChart) relativeLayout.getChildAt(0);
                                             updatedChartByPoll(currentPoll, mChart);
                                             LinearLayout linearLayout1 = (LinearLayout) relativeLayout.getChildAt(1);

                                             final Spinner spinner = (Spinner) linearLayout1.getChildAt(1);

                                             String options[] = new String[currentPoll.getPollOptions().size()];
                                             for (int i = 0; i < options.length; i++) {
                                                 options[i] = currentPoll.getPollOptions().get(i).getPollOptionName();
                                             }
                                             ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(that, android.R.layout.simple_spinner_item, options);
                                             spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                             spinner.setAdapter(spinnerArrayAdapter);

                                             Button voteForPollButton = (Button) linearLayout1.getChildAt(0);
                                             voteForPollButton.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     String selectedOption = (String) spinner.getSelectedItem();
                                                     //FIXME : tamar - send this to server
                                                 }
                                             });

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
                                             for (PollOption pollOption : values) {
                                                 if (pollOption.getPollOptionCount() > max) {
                                                     max = pollOption.getPollOptionCount();
                                                 }
                                             }
                                             float range = max;
                                             Iterator<PollOption> iterators = poll.pollOptions.iterator();
                                             ArrayList<String> xVals = new ArrayList<String>();
                                             for (int i = 0; i < count; i++) {
                                                 xVals.add(iterators.next().getPollOptionName());
                                             }

                                             ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

                                             iterators = poll.pollOptions.iterator();
                                             for (int i = 0; i < count; i++) {
                                                 float val = (float) (iterators.next().getPollOptionCount());
                                                 yVals1.add(new BarEntry(val, i));
                                             }

                                             BarDataSet set1 = new BarDataSet(yVals1, poll.getPollName());
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
            } catch (Exception e) {
                String msg = e.getMessage();
            }

            return detailedEvent;
        }

        protected void onPostExecute(DetailedEvent detailedEvent) {
            setContentView(R.layout.party_event_view);
            createPartyEventView(detailedEvent);
        }
    }
}
