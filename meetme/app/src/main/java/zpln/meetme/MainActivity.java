package zpln.meetme;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = "0545920004";
        client = new DefaultHttpClient();
        serverUrl = new String("http://192.168.0.127:5000/");
        try{
            List<Event> events = getEvents();
            DetailedEvent detailed = getDetailedEvent(1);
        } catch (Exception e) {

        }


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

    public DetailedEvent getDetailedEvent(int eventId) throws IOException {
        HttpGet request = null;
        HttpResponse response = null;
        JsonReader reader = null;
        DetailedEvent detailedEvent = null;

        try {
            request = new HttpGet(serverUrl + "get_event_details");
            request.addHeader("user_id", userId);
            request.addHeader("event_id", String.valueOf(eventId));
            response = client.execute(request);
            reader = new JsonReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            return readDetailedEvent(reader);
        } catch (IOException e) {
            System.err.println("IndexOutOfBoundsException: " + e.getMessage());
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
        return detailedEvent;
    }

    public List<Event> getEvents() throws IOException {
        HttpGet request = null;
        HttpResponse response = null;
        JsonReader reader = null;
        List events = null;

        try {
            // request = new HttpGet(serverUrl + "get_events");
            request = new HttpGet(addParameterToUrl(serverUrl + "get_events", "uid", String.valueOf(userId)));
            // request.addHeader("uid", userId);
            response = client.execute(request);
            reader = new JsonReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            return readEvents(reader);
        } catch (IOException e) {
            System.err.println("IndexOutOfBoundsException: " + e.getMessage());
        }
        catch (Exception e) {
            String msg = e.getMessage();
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
        return events;
    }

    private DetailedEvent readDetailedEvent(JsonReader reader) throws IOException {
        int eventId = -1;
        String eventName = null;
        Status status = null;
        int creadorId = -1;
        List<Poll> polls = null;
        List<Integer> users = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("event_id")) {
                eventId = reader.nextInt();
            } else if (name.equals("event_name")) {
                eventName = reader.nextString();
            } else if (name.equals("status")) {
                status = new Status(reader.nextInt());
            } else if (name.equals("creator_id")) {
                creadorId = reader.nextInt();
            } else if (name.equals("polls")) {
                polls = readPolls(reader);
            } else if (name.equals("users")) {
                users = readUsers(reader);
            }else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return new DetailedEvent(eventId, eventName, status, creadorId, polls, users);
    }

    private List<Poll> readPolls(JsonReader reader) throws IOException {
        List<Poll> polls = new ArrayList<Poll>();
        reader.beginArray();
        while (reader.hasNext()) {
            polls.add(readPoll(reader));
        }
        reader.endArray();
        return polls;
    }

    private Poll readPoll(JsonReader reader) throws IOException {
        int pollId = -1;
        String pollName = null;
        int selectedPollOption = -1;
        int overriddenPollOption = -1;
        List<PollOption> pollOptions = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("poll_id")) {
                pollId = reader.nextInt();
            } else if (name.equals("poll_name")) {
                pollName = reader.nextString();
            } else if (name.equals("selected_poll_option")) {
                selectedPollOption =reader.nextInt();
            } else if (name.equals("overridden_poll_option")) {
                overriddenPollOption = reader.nextInt();
            } else if (name.equals("options")) {
                pollOptions = readPollOptions(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Poll(pollId, pollName, selectedPollOption, overriddenPollOption, pollOptions);
    }

    private List<PollOption> readPollOptions(JsonReader reader) throws IOException {
        List<PollOption> pollOptions = new ArrayList<PollOption>();
        reader.beginArray();
        while (reader.hasNext()) {
            pollOptions.add(readPollOption(reader));
        }
        reader.endArray();
        return pollOptions;
    }

    private PollOption readPollOption(JsonReader reader) throws IOException {
        int pollOptionId = -1;
        String pollOptionName = null;
        int pollOptionCount = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("option_id")) {
                pollOptionId = reader.nextInt();
            } else if (name.equals("option_name")) {
                pollOptionName = reader.nextString();
            } else if (name.equals("option_count")) {
                pollOptionCount =reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new PollOption(pollOptionId, pollOptionName, pollOptionCount);
    }

    private List<Integer> readUsers(JsonReader reader) throws IOException {
        List<Integer> users = new ArrayList<Integer>();
        reader.beginArray();
        while (reader.hasNext()) {
            users.add(reader.nextInt());
        }
        reader.endArray();
        return users;
    }

    private List<Event> readEvents(JsonReader reader) throws IOException {
        List<Event> events = new ArrayList<Event>();
        reader.beginArray();
        while (reader.hasNext()) {
            events.add(readEvent(reader));
        }
        reader.endArray();
        return events;
    }

    private Event readEvent(JsonReader reader) throws IOException {
        int eventId = -1;
        String eventName = null;
        Status status = null;
        int creatorId = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("eventId")) {
                eventId = reader.nextInt();
            } else if (name.equals("name")) {
                eventName = reader.nextString();
            } else if (name.equals("status")) {
                status = new Status(reader.nextInt());
            } else if (name.equals("creator_id")) {
                creatorId = reader.nextInt();
            }else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Event(eventId, eventName, status, creatorId);
    }

    private String addParameterToUrl(String url, String parameterName, String parameterValue){
        if(!url.endsWith("?")) {
            url += "?";
        }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(parameterName, parameterValue));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }
}
