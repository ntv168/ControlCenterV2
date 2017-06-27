package center.control.system.vash.controlcenter.server;

/**
 * Created by Thuans on 6/25/2017.
 */
public class TermDTO {
    private String val;
    private double tfidf;
    private Integer functId;
    private Integer socialId;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public double getTfidf() {
        return tfidf;
    }

    public void setTfidf(double tfidf) {
        this.tfidf = tfidf;
    }

    public Integer getFunctId() {
        return functId;
    }

    public void setFunctId(Integer functId) {
        this.functId = functId;
    }

    public Integer getSocialId() {
        return socialId;
    }

    public void setSocialId(Integer socialId) {
        this.socialId = socialId;
    }
}
