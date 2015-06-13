package zpln.meetme;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class CreateEventActivity extends ActionBarActivity {
    final public static List<MiniPoll> polls = new LinkedList<>();
    final CreateEventActivity that = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPartyEventView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        polls.add(CreatePollActivity.miniPoll);
        finish();
        startActivity(getIntent());
    }

    private void createPartyEventView() {
        setContentView(R.layout.create_event_view);
        ScrollView scrollView = (ScrollView) findViewById(R.id.createEventScrollView);

        LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);
        Button addPollButton = (Button) linearLayout.getChildAt(1);
        Button sendPollButton = (Button) linearLayout.getChildAt(2);


        addPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(that, CreatePollActivity.class);
                startActivityForResult(intent, 0);

            }
        });

        sendPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FIXME : post to server here
                polls.clear();
                finish();
            }
        });

        updateListView(that);
    }

    private void updateListView(final CreateEventActivity that) {
        ListView partyPollListView = (ListView) findViewById(R.id.newPollsListView);

        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) partyPollListView.getLayoutParams();
        lp.height = lp.height * polls.size();
        partyPollListView.setLayoutParams(lp);

        partyPollListView.setAdapter(new BaseAdapter() {
                                         private Typeface mTf;

                                         @Override
                                         public int getCount() {
                                             return polls.size();
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
                                             TextView dynamicTextView = new TextView(that);
                                             dynamicTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
                                             dynamicTextView.setText((CharSequence) polls.get(position).name);
                                             return dynamicTextView;
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
