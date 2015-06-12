package zpln.meetme;

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
}
