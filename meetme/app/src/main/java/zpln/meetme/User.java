package zpln.meetme;

import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by tamar on 6/13/2015.
 */
public class User {
    private String userId;
    private String userName;

    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public User(JsonReader reader) throws IOException {
        this.userId = null;
        this.userName = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("user_id")) {
                this.userId = reader.nextString();
            } else if (name.equals("user_name")) {
                this.userName = reader.nextString();
            }else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    public JSONObject getJsonObject() throws JSONException{
        JSONObject userJsonObject = new JSONObject();
        userJsonObject.put("user_id", this.getUserId());
        userJsonObject.put("user_name", this.getUserName());
        return userJsonObject;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
