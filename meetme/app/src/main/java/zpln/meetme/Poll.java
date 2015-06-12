package zpln.meetme;

import java.util.Iterator;
import java.util.List;

/**
 * Created by tamar on 6/12/2015.
 */
public class Poll {
    private int pollId;
    private String pollName;
    private int selectedPollOption;
    private int overriddenPollOption;
    List<PollOption> pollOptions;

    public Poll(int pollId, String pollName, int selectedPollOption, int overriddenPollOption, List<PollOption> pollOptions){
        this.pollId = pollId;
        this.pollName = pollName;
        this.selectedPollOption = selectedPollOption;
        this.overriddenPollOption = overriddenPollOption;
        this.pollOptions = pollOptions;
    }
    public boolean validatePollOptionId(int optionId) {
        Iterator<PollOption> pollOptionsIterator = this.pollOptions.iterator();
        while (pollOptionsIterator.hasNext()){
            if (pollOptionsIterator.next().getPollOptionId() == optionId){
                return true;
            }
        }
        return false;
    }
}
