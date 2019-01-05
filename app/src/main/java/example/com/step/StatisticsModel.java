package example.com.step;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatisticsModel implements Serializable {

    @SerializedName("date")
    @Expose
    private Long mDate;

    @SerializedName("walk")
    @Expose
    private Integer walk;

    @SerializedName("aerobic")
    @Expose
    private Integer aerobic;

    @SerializedName("run")
    @Expose
    private Integer run;

    public StatisticsModel() {
    }

    public StatisticsModel(Long date, Integer walk, Integer aerobic, Integer run) {
        mDate = date;
        this.walk = walk;
        this.aerobic = aerobic;
        this.run = run;
    }

    public Long getDate() {
        return mDate;
    }

    public void setDate(Long date) {
        mDate = date;
    }

    public Integer getWalk() {
        return walk;
    }

    public void setWalk(Integer walk) {
        this.walk = walk;
    }

    public Integer getAerobic() {
        return aerobic;
    }

    public void setAerobic(Integer aerobic) {
        this.aerobic = aerobic;
    }

    public Integer getRun() {
        return run;
    }

    public void setRun(Integer run) {
        this.run = run;
    }
}
