package zpln.meetme;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utility.userId = "0545920004";
        Utility.serverUrl = new String("http://10.0.0.13:5000/");

        new GetEventsTask().execute();
        new GetDetailedEventTask().execute(1);

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
            eventData.add(new BasicNameValuePair("user_id", Utility.userId));
            eventData.add(new BasicNameValuePair("event_name", detailedEvent.getEventName()));
            post.setEntity(new UrlEncodedFormEntity(eventData));
            response = client.execute(post);

        } catch (IOException e) {
            System.err.println("IndexOutOfBoundsException: " + e.getMessage());
        }
        finally {

        }
    }
}
