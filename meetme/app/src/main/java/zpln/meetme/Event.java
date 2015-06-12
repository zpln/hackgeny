package zpln.meetme;

/**
 * Created by tamar on 6/12/2015.
 */
public class Event {

    protected int eventId;
    protected String eventName;
    protected Status status;
    protected int creatorId;

    public Event(int eventId, String eventName, Status status, int creatorId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.status = status;
        this.creatorId = creatorId;
    }

    String getEventName(){
        return this.eventName;
    }

}
