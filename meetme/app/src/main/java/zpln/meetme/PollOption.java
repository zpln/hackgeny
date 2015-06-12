package zpln.meetme;

/**
 * Created by tamar on 6/12/2015.
 */
public class PollOption {
    private int pollOptionId;
    private String pollOptionName;
    private int pollOptionCount;

    public PollOption(int pollOptionId, String pollOptionName, int pollOptionCount){
        this.pollOptionId = pollOptionId;
        this.pollOptionName = pollOptionName;
        this.pollOptionCount = pollOptionCount;
    }

    public int getPollOptionId() {
        return pollOptionId;
    }
}
