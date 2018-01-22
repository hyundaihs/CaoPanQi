package com.dashuai.android.treasuremap.entity;

import java.io.Serializable;

public class Score implements Serializable {
    public static final String TAB_NAME = "score";// 模拟器成绩
    private int id;
    private String date;
    private String code;
    private String name;
    private String score;
    private int level;//仿真级别
    private double totalAssets;//当前总资产
    private String startDate;//仿真时间起点
    private String endDate;//仿真时间终点
    private String fangzhenId;//仿真ID 即日期字符串

    public Score() {
        super();
    }

    public Score(int id, String date, String code, String name, String score, int level, double totalAssets, String startDate, String endDate, String fangzhenId) {
        this.id = id;
        this.date = date;
        this.code = code;
        this.name = name;
        this.score = score;
        this.level = level;
        this.totalAssets = totalAssets;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fangzhenId = fangzhenId;
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(double totalAssets) {
        this.totalAssets = totalAssets;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFangzhenId() {
        return fangzhenId;
    }

    public void setFangzhenId(String fangzhenId) {
        this.fangzhenId = fangzhenId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", score='" + score + '\'' +
                ", level=" + level +
                ", totalAssets=" + totalAssets +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", fangzhenId='" + fangzhenId + '\'' +
                '}';
    }
}
