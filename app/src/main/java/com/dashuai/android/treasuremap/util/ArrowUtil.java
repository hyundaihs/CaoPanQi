package com.dashuai.android.treasuremap.util;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

import com.dashuai.android.treasuremap.CPQApplication;
import com.dashuai.android.treasuremap.R;
import com.dashuai.android.treasuremap.entity.ArrowType;
import com.dashuai.android.treasuremap.entity.Stock;

/**
 * Created by kevin on 16/11/21.
 */
public class ArrowUtil {

    public static Spanned getStockPrice(Context context, Stock stock) {
        String priceStr = CPQApplication.half_up(stock.getDq());
        String cl4 = ArrowUtil.getArrow(context, ArrowType.ARROW_HIGH, stock.getCl4());
        priceStr += cl4;
        String cl5 = ArrowUtil.getArrow(context, ArrowType.ARROW_LOW, stock.getCl5());
        priceStr += cl5;
        String cl6 = ArrowUtil.getArrow(context, ArrowType.ARROW_CLOSE, stock.getCl6());
        priceStr += cl6;
        return Html.fromHtml(priceStr);
    }


    private static String getArrow(Context context, ArrowType type, int which) {
        switch (type) {
            case ARROW_HIGH:
                switch (which) {
                    case 2:
                        return "<font color='#fff83a'>↑</font>";
                    case 1:
                        return "<font color='#fff83a'>↓</font>";
                    default:
                        return "";
                }
            case ARROW_LOW:
                switch (which) {
                    case 2:
                        return "<font color='#00ffff'>↑</font>";
                    case 1:
                        return "<font color='#00ffff'>↓</font>";
                    default:
                        return "";
                }
            default:
                switch (which) {
                    case 2:
                        return "<font color='#ffffff'>↑</font>";
                    case 1:
                        return "<font color='#ffffff'>↓</font>";
                    default:
                        return "";
                }
        }
    }

    private static String getString(Context context, int id) {
        return context.getString(id);
    }

    public static Spanned getColorString(String text, String color, String elseText) {
        return getColorString(text, color, elseText, false);
    }

    public static Spanned getColorString(String text, String color, String elseText, boolean colorEnd) {
        return colorEnd ? Html.fromHtml(elseText + "<font color='#" + color + "'>" + text + "</font>")
                : Html.fromHtml("<font color='#" + color + "'>" + text + "</font>" + elseText);
    }
}
