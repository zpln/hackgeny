package zpln.meetme;

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
        setContentView(R.layout.event_list);
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
