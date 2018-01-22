package com.dashuai.android.treasuremap.util;

import com.dashuai.android.treasuremap.entity.Stock;

import java.util.List;

public class SortUtil {

    public static final int SORT_BY_NONE = -1;
    public static final int SORT_BY_STOCK = 0;
    public static final int SORT_BY_PRICE = 1;
    public static final int SORT_BY_INCREASE = 2;
    public static final int SORT_BY_ZHENFU = 3;
    public static final int SORT_BY_STATUS = 4;

    /**
     * 二分法排序
     */
    public static void sort(List<Stock> stocks, int sort, boolean isDesc) {

        switch (sort) {
            case SORT_BY_STOCK:
                // 直接插入排序
                for (int i = 1; i < stocks.size(); i++) {
                    // 待插入元素
                    Stock temp = stocks.get(i);
                    int j;
                    for (j = i - 1; j >= 0; j--) {
                        // 将大于temp的往后移动一位
                        if (stocks.get(j).getId() < temp.getId()) {
                            stocks.remove(j + 1);
                            stocks.add(j, temp);
                        } else {
                            break;
                        }
                    }
                }
                break;
            case SORT_BY_PRICE:
                // 直接插入排序
                for (int i = 1; i < stocks.size(); i++) {
                    // 待插入元素
                    Stock temp = stocks.get(i);
                    int j;
                    for (j = i - 1; j >= 0; j--) {
                        // 将大于temp的往后移动一位
                        if (isDesc ? stocks.get(j).getDq() < temp.getDq() : stocks
                                .get(j).getDq() > temp.getDq()) {
                            stocks.remove(j + 1);
                            stocks.add(j, temp);
                        } else {
                            break;
                        }
                    }
                }
                break;
            case SORT_BY_INCREASE:
                // 直接插入排序
                for (int i = 1; i < stocks.size(); i++) {
                    // 待插入元素
                    Stock temp = stocks.get(i);
                    int j;
                    for (j = i - 1; j >= 0; j--) {
                        // 将大于temp的往后移动一位
                        if (isDesc ? stocks.get(j).getZdf() < temp.getZdf()
                                : stocks.get(j).getZdf() > temp.getZdf()) {
                            stocks.remove(j + 1);
                            stocks.add(j, temp);
                        } else {
                            break;
                        }
                    }
                }
                break;
            case SORT_BY_ZHENFU:
                // 直接插入排序
                for (int i = 1; i < stocks.size(); i++) {
                    // 待插入元素
                    Stock temp = stocks.get(i);
                    int j;
                    for (j = i - 1; j >= 0; j--) {
                        // 将大于temp的往后移动一位
                        if (isDesc ? stocks.get(j).getZf() < temp.getZf()
                                : stocks.get(j).getZf() > temp.getZf()) {
                            stocks.remove(j + 1);
                            stocks.add(j, temp);
                        } else {
                            break;
                        }
                    }
                }
                break;
            case SORT_BY_STATUS:
                // 直接插入排序
                for (int i = 1; i < stocks.size(); i++) {
                    // 待插入元素
                    Stock temp = stocks.get(i);
                    int j;
                    for (j = i - 1; j >= 0; j--) {
                        // 将大于temp的往后移动一位
                        if (isDesc ? stocks.get(j).getBeizhu() < temp.getBeizhu()
                                : stocks.get(j).getBeizhu() > temp.getBeizhu()) {
                            stocks.remove(j + 1);
                            stocks.add(j, temp);
                        } else {
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }

    }
}
