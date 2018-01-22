package com.dashuai.android.treasuremap.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dashuai.android.treasuremap.entity.HistoryStock;
import com.dashuai.android.treasuremap.fragment.HistoryChartFragment;
import com.dashuai.android.treasuremap.fragment.HistoryInfoFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HistoryInfoIndicatorAdapter extends FragmentPagerAdapter {
    /**
     * Tab标题
     */
    private ArrayList<HistoryStock> dataD;
    private ArrayList<HistoryStock> dataW;
    private ArrayList<HistoryStock> dataM;

    public HistoryInfoIndicatorAdapter(FragmentManager fm, ArrayList<HistoryStock> dataD,
                                       ArrayList<HistoryStock> dataW, ArrayList<HistoryStock> dataM) {
        super(fm);
        this.dataD = dataD;
        this.dataW = dataW;
        this.dataM = dataM;
    }

    @Override
    public Fragment getItem(int position) {
        // 新建一个Fragment来展示ViewPager item的内容，并传递参数
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                fragment = new HistoryInfoFragment();
                args.putParcelableArrayList("data", dataD);
                break;
            case 1:
                fragment = new HistoryInfoFragment();
                args.putParcelableArrayList("data", dataW);
                break;
            case 2:
                fragment = new HistoryInfoFragment();
                args.putParcelableArrayList("data", dataM);
                break;
            case 3:
                fragment = new HistoryChartFragment();
                args.putParcelableArrayList("data", dataD);
                break;
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getCount() {
        return 4;
    }
}