package com.dashuai.android.treasuremap.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.dashuai.android.treasuremap.entity.HistoryStock;
import com.dashuai.android.treasuremap.fragment.HistoryChartFragment;
import com.dashuai.android.treasuremap.fragment.HistoryInfoFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HistoryInfoIndicatorAdapterUnCache extends FragmentPagerAdapter {
	/**
	 * Tab标题
	 */
	private List<HistoryStock> data;
	private ArrayList<Fragment> fragments;
	private FragmentManager fm;

	public HistoryInfoIndicatorAdapterUnCache(FragmentManager fm,
			List<HistoryStock> data) {
		super(fm);
		this.data = data;
		this.fm = fm;
		fragments = new ArrayList<Fragment>();
		for (int i = 0; i < 2; i++) {
			fragments.add(makeFragment(i));
		}
	}

	@Override
	public void notifyDataSetChanged() {
		for (int i = 0; i < 2; i++) {
			fragments.add(makeFragment(i));
		}
		setFragments(fragments);
		super.notifyDataSetChanged();
	}

	public void setFragments(ArrayList<Fragment> fragments) {
		if (this.fragments != null) {
			FragmentTransaction ft = fm.beginTransaction();
			for (Fragment f : this.fragments) {
				ft.remove(f);
			}
			ft.commit();
			ft = null;
			fm.executePendingTransactions();
		}
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		// 新建一个Fragment来展示ViewPager item的内容，并传递参数
		return fragments.get(position);
	}

	private Fragment makeFragment(int position) {
		Fragment fragment = null;
		if (position == 0) {
			fragment = new HistoryInfoFragment();
		} else {
			fragment = new HistoryChartFragment();
		}
		Bundle args = new Bundle();
		args.putSerializable("data", (Serializable) data);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "";
	}

	@Override
	public int getCount() {
		return 2;
	}
}
