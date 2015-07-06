package zpln.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.EditText;

import java.util.LinkedList;
import java.util.List;

public class CreatePollActivity extends ActionBarActivity {

    static Poll poll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.create_poll_view);


        LinearLayout mainView = (LinearLayout) findViewById(R.id.createPollView);
        final ScrollView scrollView = (ScrollView) mainView.getChildAt(0);
        final LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);
        Button addPollButton = (Button) findViewById(R.id.addPollButton);
        final CreatePollActivity that = this;
        this.poll = null;

        addPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pollName = ((EditText) linearLayout.getChildAt(0)).getText().toString();
                List<PollOption> options = new LinkedList<PollOption>();
                for(int i = 0; i < 6; i++) {
                    String pollOptionName = ((EditText) linearLayout.getChildAt(i + 2)).getText().toString();
                    if (pollOptionName.length() != 0) {
                        options.add(new PollOption(pollOptionName));
                    }
                }
                poll = new Poll(pollName, options); 
                finish();
            }
        });
    }
}
