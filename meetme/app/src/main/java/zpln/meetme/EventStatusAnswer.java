package zpln.meetme;

/**
 * Created by tamar on 6/13/2015.
 */
public class EventStatusAnswer {
    private int event_id;
    private Status status;

    public EventStatusAnswer(int event_id, Status status)
    {
        this.event_id = event_id;
        this.status = status;
    }

    public int getEventId() {
        return this.event_id;
    }


    public Status getStatus() {
        return this.status;
    }
}
