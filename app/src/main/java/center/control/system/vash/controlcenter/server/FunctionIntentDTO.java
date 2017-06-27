package center.control.system.vash.controlcenter.server;

/**
 * Created by MYNVTSE61526 on 09/06/2017.
 */
public class FunctionIntentDTO {
    private String name;

    private int id;
    private String success;
    private String fail;
    private String remind;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getFail() {
        return fail;
    }

    public void setFail(String fail) {
        this.fail = fail;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }
}
