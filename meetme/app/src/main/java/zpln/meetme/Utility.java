package zpln.meetme;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.util.List;

/**
 * Created by tamar on 6/13/2015.
 */
public class Utility {
    public static String userId = null;
    public static String serverUrl = null;

    public static String addParametersToUrl(String url, List<NameValuePair> params){
        if(!url.endsWith("?")) {
            url += "?";
        }

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }
}
