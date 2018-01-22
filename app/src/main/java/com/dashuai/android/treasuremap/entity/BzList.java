package com.dashuai.android.treasuremap.entity;

import java.util.List;

public class BzList {
	private int beizhu;
	private List<Stock> lists;

	public BzList() {
		super();
	}

	public BzList(int beizhu, List<Stock> lists) {
		super();
		this.beizhu = beizhu;
		this.lists = lists;
	}

	public int getBeizhu() {
		return beizhu;
	}

	public void setBeizhu(int beizhu) {
		this.beizhu = beizhu;
	}

	public List<Stock> getLists() {
		return lists;
	}

	public void setLists(List<Stock> lists) {
		this.lists = lists;
	}

	@Override
	public String toString() {
		return "BzList [beizhu=" + beizhu + ", lists=" + lists + "]";
	}

}
