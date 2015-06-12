package zpln.meetme;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    class Event {
        String name;
        String location;
        Date date;
        String[] participants;

        public Event(String name, String location, Date date, String[] participants) {
            this.name = name;
            this.location = location;
            this.date = date;
            this.participants = participants;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "name='" + name + '\'' +
                    ", location='" + location + '\'' +
                    ", date=" + date +
                    ", participants=" + Arrays.toString(participants) +
                    '}';
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.event_list);
//        createPartyListView();

        setContentView(R.layout.party_event_view);
        createPartyEventView();
    }

    private void createPartyListView() {
        ListView listview = (ListView) findViewById(R.id.listView);

        Event event1 = new Event("Party", "Hapak", new Date(2015, 1, 2), new String[]{"Stav", "Tamar"});
        Event event2 = new Event("Party2", "Panasi", new Date(2015, 10, 2), new String[]{"Shoham", "Guy"});
        final Event[] events = new Event[]{event1, event2};


        final MainActivity that = this;
        listview.setAdapter(new BaseAdapter() {
                                @Override
                                public int getCount() {
                                    return events.length;
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
                                    for (int i = 0; i < upperDataLayout.getChildCount(); i++) {
                                        View childAt = upperDataLayout.getChildAt(i);
                                        if (childAt instanceof TextView) {
                                            TextView textView = (TextView) childAt;
                                            textView.setText(String.format("%d", i));
                                        }
                                    }
                                    for (int i = 0; i < lowerDataLayout.getChildCount(); i++) {
                                        View childAt = lowerDataLayout.getChildAt(i);
                                        if (childAt instanceof TextView) {
                                            TextView textView = (TextView) childAt;
                                            textView.setText(String.format("_%d", i));
                                        }
                                    }

                                    TextView event_name = (TextView) upperDataLayout.getChildAt(0);
                                    event_name.setText(events[position].name);
                                    TextView location = (TextView) upperDataLayout.getChildAt(1);
                                    location.setText("at " + events[position].location);
                                    TextView date = (TextView) lowerDataLayout.getChildAt(0);
                                    date.setText(new SimpleDateFormat("dd-MM-yy hh:mm").format(events[position].date));
                                    TextView participants = (TextView) lowerDataLayout.getChildAt(1);
                                    participants.setText(Arrays.toString(events[position].participants));
                                    return layout;
                                }
                            }

        );
    }

    private void createPartyEventView() {
        ListView partyPollListView = (ListView) findViewById(R.id.partyPollListView);


        Map<String, Integer> keyCount = new HashMap<>();
        keyCount.put("Hapak", 2);
        keyCount.put("Panasi", 3);
        keyCount.put("Moses", 1);
        keyCount.put("Shmafia", 5);
        Poll whereDoWeEat = new Poll(keyCount, "Where do we eat?");

        Map<String, Integer> keyCount2 = new HashMap<>();
        keyCount2.put("Bat-El", 6);
        keyCount2.put("Nokdim", 4);
        keyCount2.put("Bney Kedem", 3);
        keyCount2.put("Efrat", 2);
        Poll whereDoWeGuard = new Poll(keyCount2, "Where do we guard?");

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
                                             int count = poll.keyCount.size();
                                             Collection<Integer> values = poll.keyCount.values();
                                             int max = 0;
                                             for (Integer integer : values) {
                                                 if (integer > max) {
                                                     max = integer;
                                                 }
                                             }
                                             float range = max;
                                             Iterator<String> iterators = poll.keyCount.keySet().iterator();
                                             ArrayList<String> xVals = new ArrayList<String>();
                                             for (int i = 0; i < count; i++) {
                                                 xVals.add(iterators.next());
                                             }

                                             ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

                                             Iterator<Integer> iterator = poll.keyCount.values().iterator();
                                             for (int i = 0; i < count; i++) {
                                                 float val = (float) (iterator.next());
                                                 yVals1.add(new BarEntry(val, i));
                                             }

                                             BarDataSet set1 = new BarDataSet(yVals1, poll.name);
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
}
