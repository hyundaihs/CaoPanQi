package com.dashuai.android.treasuremap.entity;

/**
 * Created by kevin on 17/4/16.
 */
public class BZListStatus {
    public static final String TAB_NAME = "bz_list_status";//仿真
    private int id;
    private int flag;//顺序
    private String name;//备注名称
    private boolean isOpen;//备注列表是否打开
    private int beizhu;//顺序

    public BZListStatus() {
        super();
    }

    public BZListStatus(int id, int flag, String name, boolean isOpen) {
        this.id = id;
        this.flag = flag;
        this.name = name;
        this.isOpen = isOpen;
    }

    public int getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(int beizhu) {
        this.beizhu = beizhu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    @Override
    public String toString() {
        return "BZListStatus{" +
                "id=" + id +
                ", flag=" + flag +
                ", name='" + name + '\'' +
                ", isOpen=" + isOpen +
                '}';
    }
}
