package zpln.meetme;

import android.util.JsonReader;

import java.io.IOException;

/**
 * Created by tamar on 6/12/2015.
 */
public class Event {

    protected int eventId;
    protected String eventName;
    protected Status status;
    protected int creatorId;

    public Event() {
        this.eventId = -1;
        this.eventName = null;
        this.status = Status.UNANSWERED;
        this.creatorId = -1;
    }

    public Event(int eventId, String eventName, Status status, int creatorId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.status = Status.UNANSWERED;
        this.creatorId = creatorId;
    }

    public Event(JsonReader reader) throws IOException {
        this.eventId = -1;
        this.eventName = null;
        this.status = Status.UNANSWERED;
        this.creatorId = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("event_id")) {
                this.eventId = reader.nextInt();
            } else if (name.equals("event_name")) {
                this.eventName = reader.nextString();
            } else if (name.equals("status")) {
                this.status = Status.values()[reader.nextInt()];
            } else if (name.equals("creator_id")) {
                this.creatorId = reader.nextInt();
            }else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    public Event(String eventName) {
        this.eventId = -1;
        this.eventName = eventName;
        this.status = Status.UNANSWERED;
        this.creatorId = -1;
    }

    String getEventName(){
        return this.eventName;
    }

    public int getEventId() {
        return eventId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public int getCreatorId() {
        return creatorId;
    }
}
