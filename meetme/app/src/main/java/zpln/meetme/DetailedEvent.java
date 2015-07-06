package zpln.meetme;

import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tamar on 6/12/2015.
 */
public class DetailedEvent extends Event{
    private List<Poll> polls;
    private List<User> users;

    public DetailedEvent(int eventId, String eventName, Status status, int creatorId, List<Poll> polls, List<User> users){
        super(eventId, eventName, status, creatorId);
        this.polls = polls;
        this.users = users;
    }

    public DetailedEvent(JsonReader reader) throws IOException {
        this.eventId = -1;
        this.eventName = null;
        this.status = Status.UNANSWERED;
        this.creatorId = -1;
        this.polls = null;
        this.users = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "event_id":
                    this.eventId = reader.nextInt();
                    break;
                case "event_name":
                    this.eventName = reader.nextString();
                    break;
                case "status":
                    this.status = Status.values()[reader.nextInt()];
                    break;
                case "creator_id":
                    this.creatorId = reader.nextInt();
                    break;
                case "polls":
                    this.polls = readPolls(reader);
                    break;
                case "users":
                    this.users = readUsers(reader);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        /**
        this.users = new ArrayList<User>();
        this.users.add(new User("0542254016", "netta"));
        this.users.add(new User("0542254017", "stav"));
        **/
        reader.endObject();
    }

    public DetailedEvent(String eventName, List<Poll> polls, List<User> users) {
        super(eventName);
        // Deep copy to clean up polls and users afterwards
        this.polls = new LinkedList<Poll>();
        this.polls.addAll(polls);
        this.users = new LinkedList<User>();
        this.users.addAll(users);
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

    private List<User> readUsers(JsonReader reader) throws IOException {
        List<User> users = new ArrayList<User>();
        reader.beginArray();
        while (reader.hasNext()) {
            users.add(new User(reader));
        }
        reader.endArray();
        return users;
    }

    public String getPollResult(String pollName) {
        for(Poll poll : this.polls) {
            if(poll.getPollName().equals(pollName) && poll.getSelectedPollOption() != -1) {
                for(PollOption pollOption : poll.getPollOptions()) {
                    if (pollOption.getPollOptionId() == poll.getSelectedPollOption()) {
                        return pollOption.getPollOptionName();
                    }
                }
            }
        }
        return "TBD";
    }

    public JSONArray getPollsJsonArray() {
        JSONArray pollsJsonArray = new JSONArray();
        for(Poll poll : this.polls) {
            try {
                pollsJsonArray.put(poll.getJsonObject());
            } catch (JSONException e) {
                // this sucks
            }

        }
        return pollsJsonArray;
    }

    public JSONArray getUsersJsonArray() {
        JSONArray usersJsonArray = new JSONArray();
        for(User user : this.users) {
            try {
                usersJsonArray.put(user.getJsonObject());
            } catch (JSONException e) {
                // this sucks
            }

        }

        return usersJsonArray;
    }

    public List<Poll> getPolls() {
        return polls;
    }

    public List<User> getUsers() {
        return users;
    }
}
