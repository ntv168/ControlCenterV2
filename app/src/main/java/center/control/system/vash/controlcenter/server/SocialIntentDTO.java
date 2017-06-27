package center.control.system.vash.controlcenter.server;

/**
 * Created by MYNVTSE61526 on 09/06/2017.
 */
public class SocialIntentDTO {
    private int id;
    private String name;
    private String question;
    private String reply;

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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
