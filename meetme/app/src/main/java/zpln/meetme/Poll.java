package zpln.meetme;

import java.util.Map;

class Poll {
    public Map<String, Integer> keyCount;
    public String name;

    public Poll(Map<String, Integer> keyCount, String name) {
        this.keyCount = keyCount;
        this.name = name;
    }
}
