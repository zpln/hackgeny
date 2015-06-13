package zpln.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CreatePollActivity extends ActionBarActivity {

    static MiniPoll miniPoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra("eventId");
        int eventId = Integer.parseInt(message);
        setContentView(R.layout.create_poll_view);

        LinearLayout mainView = (LinearLayout) findViewById(R.id.createPollView);
        final ScrollView scrollView = (ScrollView) mainView.getChildAt(0);
        Button addPollButton = (Button) scrollView.getChildAt(7);
        final CreatePollActivity that = this;

        addPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miniPoll = new MiniPoll();
                miniPoll.name = ((TextView) scrollView.getChildAt(1)).getText().toString();
                miniPoll.options = new String[6];
                for(int i = 0; i < 6; i++) {
                    String pollOption = ((TextView) scrollView.getChildAt(i+1)).getText().toString();
                    if (pollOption.startsWith("Option ")) {
                        miniPoll.options[i] = null;
                    } else {
                        miniPoll.options[i] = pollOption;
                    }
                }
                finish();
            }
        });
    }
}
