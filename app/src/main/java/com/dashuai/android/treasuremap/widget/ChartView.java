package com.dashuai.android.treasuremap.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.dashuai.android.treasuremap.entity.HistoryStock;
import com.dashuai.android.treasuremap.util.WidgetUtil;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends View {

	private int viewWidth, viewHeight;// view的宽高
	private float vSpace, hSpace;// 横向间距和纵向间距
	private float paddingLeft, paddingRight, paddingTop, paddingBottom;// 内间距
	// private float padding = 10;
	private List<HistoryStock> data;
	private Paint redPaint, greenPaint, whitePaint;
	private int pointLine;// 点线的长度
	private final int vNum = 6;
	private boolean isPress;
	private float totalWidth;
	float totalHeight;
	private float smallText, bigText, mediumText, textSize_8;
	private boolean isSeries;// 连续显示模式

	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		data = new ArrayList<HistoryStock>();
		redPaint = new Paint();
		redPaint.setColor(Color.RED);
		greenPaint = new Paint();
		greenPaint.setColor(Color.GREEN);
		whitePaint = new Paint();
		whitePaint.setColor(Color.WHITE);
		whitePaint.setStyle(Style.STROKE);
		pointLine = WidgetUtil.dip2px(context, 5);
		smallText = WidgetUtil.dip2px(context, 5);
		textSize_8 = WidgetUtil.dip2px(context, 8);
		bigText = WidgetUtil.dip2px(context, 15);
		mediumText = WidgetUtil.dip2px(context, 10);
		whitePaint.setTextSize(mediumText);
		Rect rect = new Rect();
		String text = "-1.5";
		whitePaint.getTextBounds(text, 0, text.length(), rect);
		paddingLeft = rect.width();
		paddingTop = 10;
		paddingRight = 10;
		paddingBottom = 10;
	}

	private void init() {
		totalHeight = viewHeight - paddingTop - paddingBottom;
		vSpace = totalHeight / 6;
		totalWidth = viewWidth - paddingLeft - paddingRight;
		hSpace = totalWidth / this.data.size();
		Log.d("init", "vSpace = " + vSpace + "  hSpace = " + hSpace);
		invalidate();
	}

	public List<HistoryStock> getData() {
		return data;
	}

	public void setData(List<HistoryStock> data) {
		this.data.clear();
		this.data.addAll(data);
		init();
	}

	public boolean isPress() {
		return isPress;
	}

	public void setPress(boolean isPress) {
		this.isPress = isPress;
		init();
	}

	public boolean isSeries() {
		return isSeries;
	}

	public void setSeries(boolean isSeries) {
		this.isSeries = isSeries;
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		if (null == data || data.size() <= 0) {
			return;
		}
		drawOutRect(canvas);
		drawHVLine(canvas);
		drawColumnar(canvas, isPress);
		drawYText(canvas);
	}

	/**
	 * 画外边框
	 * 
	 * @param canvas
	 */
	private void drawOutRect(Canvas canvas) {
		canvas.drawRect(paddingLeft, paddingTop, paddingLeft + totalWidth,
				paddingTop + vNum * vSpace, whitePaint);
	}

	private void drawYText(Canvas canvas) {
		whitePaint.setTextSize(mediumText);
		String text = "-1.5";
		Rect rect = new Rect();
		whitePaint.getTextBounds(text, 0, text.length(), rect);
		canvas.drawText("1.5", 0, paddingTop + rect.height(), whitePaint);
		canvas.drawText("1", 0, paddingTop + vSpace - 2, whitePaint);
		canvas.drawText("0.5", 0, paddingTop + 2 * vSpace - 2, whitePaint);
		canvas.drawText("0", 0, paddingTop + 3 * vSpace - 2, whitePaint);
		canvas.drawText("-0.5", 0, paddingTop + 4 * vSpace - 2, whitePaint);
		canvas.drawText("-1", 0, paddingTop + 5 * vSpace - 2, whitePaint);
		canvas.drawText("-1.5", 0, paddingTop + 6 * vSpace - 2, whitePaint);
	}

	/**
	 * 画横线和纵向点线
	 * 
	 * @param canvas
	 */
	private void drawHVLine(Canvas canvas) {
		for (int i = 1; i < vNum; i++) {
			canvas.drawLine(paddingLeft, paddingTop + i * vSpace, paddingLeft
					+ totalWidth, paddingTop + i * vSpace, whitePaint);
		}
		for (int i = 1; i < data.size(); i++) {
			canvas.drawLine(paddingLeft + i * hSpace, paddingTop + vNum / 2
					* vSpace - pointLine, paddingLeft + i * hSpace, paddingTop
					+ vNum / 2 * vSpace, whitePaint);
		}
	}

	/**
	 * 画柱状图
	 * 
	 * @param canvas
	 * @param isPress
	 *            是否是压力图
	 */
	private void drawColumnar(Canvas canvas, boolean isPress) {
		int num = 0;
		whitePaint.setTextSize(mediumText);
		Rect rect = new Rect();
		String text = "30";
		whitePaint.getTextBounds(text, 0, text.length(), rect);
		for (int i = 0; i < data.size(); i++) {
			Paint paint;
			float left, top, right, bottom, x, y;
			left = paddingLeft + i * hSpace + hSpace / 4;
			right = paddingLeft + i * hSpace + hSpace * 3 / 4;
			x = paddingLeft + i * hSpace + (hSpace - rect.width()) / 2;
			y = paddingTop + vNum / 2 * vSpace + (hSpace - rect.width()) / 2
					+ rect.height();
			HistoryStock historyInfo = data.get(data.size() - 1 - i);
			if (isPress) {
				if (historyInfo.getHeightstatus() != 0) {
					top = paddingTop + 1 * vSpace;
					bottom = paddingTop + vNum / 2 * vSpace;
					paint = redPaint;
					num++;
				} else if ((historyInfo.getLowstatus() != 0) && !isSeries) {
					top = paddingTop + vNum / 2 * vSpace;
					bottom = paddingTop + (vNum - 1) * vSpace;
					paint = greenPaint;
				} else {
					if (isSeries) {
						drawText(canvas, historyInfo.getDay(), i, whitePaint,
								-90);
					} else {
						canvas.drawText((i + 1) + "", x, y, whitePaint);
					}
					continue;
				}
			} else {
				if (historyInfo.getLowstatus() != 0) {
					top = paddingTop + vNum / 2 * vSpace;
					bottom = paddingTop + (vNum - 1) * vSpace;
					paint = greenPaint;
					num++;
				} else if ((historyInfo.getHeightstatus() != 0) && !isSeries) {
					top = paddingTop + 1 * vSpace;
					bottom = paddingTop + vNum / 2 * vSpace;
					paint = redPaint;
				} else {
					if (isSeries) {
						drawText(canvas, historyInfo.getDay(), i, whitePaint,
								-90);
					} else {
						canvas.drawText((i + 1) + "", x, y, whitePaint);
					}

					continue;
				}
			}
			if (isSeries) {
				if ((historyInfo.isIshighseries() && isPress)
						|| (historyInfo.isIslowseries() && !isPress)) {
					canvas.drawRect(left, top, right, bottom, paint);
				}
				drawText(canvas, historyInfo.getDay(), i, whitePaint, -90);
			} else {
				canvas.drawRect(left, top, right, bottom, paint);
				canvas.drawText((i + 1) + "", x, y, whitePaint);
			}
		}
		drawText(canvas, num);
	}

	public void drawText(Canvas canvas, String text, int index, Paint paint,
			float angle) {
		paint.setTextSize(textSize_8);
		Rect rect = new Rect();
		paint.getTextBounds(text, 0, text.length(), rect);
		float x = paddingLeft + index * hSpace + (hSpace - rect.width()) / 2;
		float y = paddingTop + vNum / 2 * vSpace + (hSpace - rect.width()) / 2
				+ rect.height();
		if (angle != 0) {
			canvas.rotate(angle, x + rect.width() / 2, y - rect.height() / 2);
			if (isPress) {
				canvas.translate(2 * rect.height(), 0);
			} else {
				canvas.translate(rect.height() - rect.width(), 0);
			}

		}
		canvas.drawText(text, x, y, paint);
		if (angle != 0) {
			if (isPress) {
				canvas.translate(-2 * rect.height(), 0);
			} else {
				canvas.translate(rect.width() - rect.height(), 0);
			}
			canvas.rotate(-angle, x + rect.width() / 2, y - rect.height() / 2);
		}
		paint.setTextSize(smallText);
	}

	/**
	 * 文字
	 */
	private void drawText(Canvas canvas, int num) {
		whitePaint.setTextSize(mediumText);
		String text;
		if (isPress) {
			if (isSeries) {
				text = "主力出货";
				Rect rect = new Rect();
				whitePaint.getTextBounds(text, 0, text.length(), rect);
				canvas.drawText(text, (viewWidth - rect.width()) / 2,
						paddingTop + vSpace * 3 + rect.height(), whitePaint);
			} else {
				text = "压力(" + num + ")";
				Rect rect = new Rect();
				whitePaint.getTextBounds(text, 0, text.length(), rect);
				canvas.drawText(
						text,
						(viewWidth - rect.width()) / 2,
						paddingTop + (vSpace - rect.height()) / 2
								+ rect.height(), whitePaint);
			}

		} else {
			if (isSeries) {
				text = "主力吸筹";
				Rect rect = new Rect();
				whitePaint.getTextBounds(text, 0, text.length(), rect);
				canvas.drawText(text, (viewWidth - rect.width()) / 2,
						paddingTop + (vNum - 4) * vSpace + rect.height(),
						whitePaint);
			} else {
				text = "支撑(" + num + ")";
				Rect rect = new Rect();
				whitePaint.getTextBounds(text, 0, text.length(), rect);
				canvas.drawText(
						text,
						(viewWidth - rect.width()) / 2,
						paddingTop + (vNum - 1) * vSpace
								+ (vSpace - rect.height()) / 2 + rect.height(),
						whitePaint);
			}
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		viewWidth = measureWidth(widthMeasureSpec);
		viewHeight = measureHeight(heightMeasureSpec);
		setMeasuredDimension(viewWidth, viewHeight);
		init();
	}

	private int measureWidth(int measureSpec) {
		int preferred = 0;
		return getMeasurement(measureSpec, preferred);
	}

	private int measureHeight(int measureSpec) {
		int preferred = 0;
		return getMeasurement(measureSpec, preferred);
	}

	private int getMeasurement(int measureSpec, int preferred) {
		int specSize = MeasureSpec.getSize(measureSpec);
		int measurement;
		switch (MeasureSpec.getMode(measureSpec)) {
		case MeasureSpec.EXACTLY:
			measurement = specSize;
			break;
		case MeasureSpec.AT_MOST:
			measurement = Math.min(preferred, specSize);
			break;
		default:
			measurement = preferred;
			break;
		}
		return measurement;
	}
}
