package zpln.meetme;

/**
 * Created by tamar on 6/12/2015.
 */
public class Status {
    private int status;

    public Status(int status){
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String toString(){
        String returnString;
        switch(status) {
            case 0: returnString = "NOT ATTENDING";
                break;
            case 1: returnString = "ATTENDING";
                break;
            default: returnString = "INVALID OPTION";
                break;
        }
        return returnString;
    }
}
