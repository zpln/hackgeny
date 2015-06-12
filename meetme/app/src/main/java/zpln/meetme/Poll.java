package zpln.meetme;

import android.util.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
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

    public Poll(JsonReader reader) throws IOException {
        this.pollId = -1;
        this.pollName = null;
        this.selectedPollOption = -1;
        this.overriddenPollOption = -1;
        this.pollOptions = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("poll_id")) {
                this.pollId = reader.nextInt();
            } else if (name.equals("poll_name")) {
                this.pollName = reader.nextString();
            } else if (name.equals("selected_poll_option")) {
                this.selectedPollOption =reader.nextInt();
            } else if (name.equals("overridden_poll_option")) {
                this.overriddenPollOption = reader.nextInt();
            } else if (name.equals("options")) {
                this.pollOptions = readPollOptions(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private List<PollOption> readPollOptions(JsonReader reader) throws IOException {
        List<PollOption> pollOptions = new ArrayList<PollOption>();
        reader.beginArray();
        while (reader.hasNext()) {
            pollOptions.add(new PollOption(reader));
        }
        reader.endArray();
        return pollOptions;
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
