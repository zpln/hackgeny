package zpln.meetme;

import android.util.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamar on 6/12/2015.
 */
public class DetailedEvent extends Event{
    private List<Poll> polls;
    private List<Integer> users;

    public DetailedEvent(int eventId, String eventName, Status status, int creatorId, List<Poll> polls, List<Integer> users){
        super(eventId, eventName, status, creatorId);
        this.polls = polls;
        this.users = users;
    }

    public DetailedEvent(JsonReader reader) throws IOException {
        this.eventId = -1;
        this.eventName = null;
        this.status = null;
        this.creatorId = -1;
        this.polls = null;
        this.users = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("event_id")) {
                this.eventId = reader.nextInt();
            } else if (name.equals("event_name")) {
                this.eventName = reader.nextString();
            } else if (name.equals("status")) {
                this.status = new Status(reader.nextInt());
            } else if (name.equals("creator_id")) {
                this.creatorId = reader.nextInt();
            } else if (name.equals("polls")) {
                this.polls = readPolls(reader);
            } else if (name.equals("users")) {
                this.users = readUsers(reader);
            }else {
                reader.skipValue();
            }
        }

        reader.endObject();
    }

    private List<Poll> readPolls(JsonReader reader) throws IOException {
        List<Poll> polls = new ArrayList<Poll>();
        reader.beginArray();
        while (reader.hasNext()) {
            polls.add(new Poll(reader));
        }
        reader.endArray();
        return polls;
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
}
