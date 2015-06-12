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
//        GridLayout event_list_entry = (GridLayout) findViewById(R.id.event_list_entry);
//        GridLayout event_list_entry = (GridLayout) LayoutInflater.from(this).inflate(R.layout.event_list_entry, null);
//        for(int i = 0; i< event_list_entry.getChildCount(); i++) {
//            View childAt = event_list_entry.getChildAt(i);
//            if (childAt instanceof TextView) {
//                TextView textView = (TextView) childAt;
//                textView.setText(String.format("%d", i));
//            }
//        }
//
//        final ArrayList<GridLayout> list2 = new ArrayList<GridLayout>();
//        list2.add(event_list_entry);

        Event event1 = new Event("Party", "Hapak", new Date(2015, 1, 2), new String[]{"Stav", "Tamar"});
        Event event2 = new Event("Party2", "Panasi", new Date(2015, 10, 2), new String[]{"Shoham", "Guy"});
        final Event[] events = new Event[]{event1, event2};


        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < events.length; ++i) {
            list.add(events[i].toString());
        }

//        final ArrayAdapter adapter = new ArrayAdapter(this,
//                android.R.layout.simple_list_item_1, list);
//        listview.setAdapter(adapter);

//        listview.setAdapter(new ArrayAdapter<String>(
//                this, R.layout.event_list,
//                R.id.event_list_entry, list));


        final String[] itemname = {
                "Safari",
                "Camera",
                "Global",
                "FireFox",
                "UC Browser",
                "Android Folder",
                "VLC Player",
                "Cold War"
        };

        String[] location = {
                "home",
                "homw2",
                "homw2",
                "homw2",
                "homw2",
                "homw2",
                "homw2",
                "homw2"
        };

        final MainActivity that = this;
//        listview.setAdapter(new ArrayAdapter<String>(
//                this, R.layout.mylist,
//                R.id.Itemname, itemname));
//        listview.setAdapter(new ArrayAdapter<String>(
//                this, R.layout.mylist,
//                R.id.Location, location));
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
//                                    LinearLayout layout = (LinearLayout) LayoutInflater.from(that).inflate(R.layout.mylist, null);
//                                    for (int i = 0; i < layout.getChildCount(); i++) {
//                                        View childAt = layout.getChildAt(i);
//                                        if (childAt instanceof TextView) {
//                                            TextView textView = (TextView) childAt;
//                                            textView.setText(String.format("%d", i));
//                                        }
//                                    }
                                    LinearLayout layout = (LinearLayout) LayoutInflater.from(that).inflate(R.layout.mylist, null);
                                    TextView event_name = (TextView) layout.getChildAt(1);
                                    event_name.setText(events[position].name);
                                    TextView location = (TextView) layout.getChildAt(2);
                                    location.setText(events[position].location);
                                    TextView date = (TextView) layout.getChildAt(3);
                                    date.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm").format(events[position].date));
                                    TextView participants = (TextView) layout.getChildAt(4);
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
