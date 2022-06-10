package github.groupa.fintech.statistics;

import github.groups.fintech.R;

public class StatisticsModelClass {
    private int id, repeat;
    private Integer ivIcon = R.drawable.ic_flat;
    private String tvType;
    private float tvAmount;
    private String date;
    private String time;
    private String comment;

    public StatisticsModelClass() {
    }
    public  StatisticsModelClass(int id, int repeat, String tvType, float tvAmount, String date, String time, String comment) {
        this.id = id;
        this.repeat = repeat;
        this.tvType = tvType;
        this.tvAmount = tvAmount;
        this.date = date;
        this.time = time;
        this.comment = comment;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getIvIcon() {
        return ivIcon;
    }

    public void setIvIcon(Integer ivIcon) {
        this.ivIcon = ivIcon;
    }

    public String getTvType() {
        return tvType;
    }

    public void setTvType(String tvType) {
        this.tvType = tvType;
    }

    public float getTvAmount() {
        return tvAmount;
    }

    public void setTvAmount(float tvAmount) {
        this.tvAmount = tvAmount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "StatisticsModelClass{" +
                "id=" + id +
                ", repeat=" + repeat +
                ", ivIcon=" + ivIcon +
                ", tvType='" + tvType + '\'' +
                ", tvAmount=" + tvAmount +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
