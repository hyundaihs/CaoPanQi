package com.dashuai.android.treasuremap.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.dashuai.android.treasuremap.util.WidgetUtil;

import java.util.ArrayList;
import java.util.List;

public class LineChart extends View {

	private List<Double> vData;
	private List<String> hData;
	private float paddingLeft, paddingTop, paddingRight, paddingBottom;
	private float vSpaceOfTextWithLine, hSpaceOfTextWithLine;// 横向与纵向文字与Line的距离
	private float vTextMargin, hTextMargin;// 横向与纵向文字与边界的距离
	private float pointLineLength;// 点线的长度
	private float textSize;// 文字大小
	private float titleTextSize;
	private float vSpace, hSpace;// 横向纵向间距
	private int viewWidth, viewHeight;// View的宽高
	private float totalWidth, totalHeight;// 图表总宽高
	private Paint bluePaint, whitePaint;
	private int vNum;
	private List<Point> points;
	private int radius;

	public LineChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		vData = new ArrayList<Double>();
		hData = new ArrayList<String>();
		init(context);
		initPadding();
	}

	public List<Double> getvData() {
		return vData;
	}

	public void setvData(List<Double> vData) {
		this.vData = vData;
		initPadding();
		invalidate();
	}

	public List<String> gethData() {
		return hData;
	}

	public void sethData(List<String> hData) {
		this.hData = hData;
		initPadding();
		invalidate();
	}

	private void init(Context context) {
		vSpaceOfTextWithLine = WidgetUtil.dip2px(context, 10);
		hSpaceOfTextWithLine = WidgetUtil.dip2px(context, 10);
		vTextMargin = WidgetUtil.dip2px(context, 2);
		hTextMargin = WidgetUtil.dip2px(context, 2);
		textSize = WidgetUtil.dip2px(context, 8);
		titleTextSize = WidgetUtil.dip2px(context, 10);
		pointLineLength = WidgetUtil.dip2px(context, 5);
		paddingTop = WidgetUtil.dip2px(context, 10);
		paddingRight = WidgetUtil.dip2px(context, 10);
		bluePaint = new Paint();
		bluePaint.setColor(Color.BLUE);
		bluePaint.setStrokeWidth(WidgetUtil.dip2px(context, 3));
		radius = WidgetUtil.dip2px(context, 3);
		whitePaint = new Paint();
		whitePaint.setColor(Color.WHITE);
		whitePaint.setStyle(Style.STROKE);
		whitePaint.setTextSize(textSize);
	}

	private void initPadding() {
		Rect rect = new Rect();
		whitePaint.setTextSize(textSize);
		String text = "20.00%";
		whitePaint.getTextBounds(text, 0, text.length(), rect);
		paddingLeft = rect.width() + vSpaceOfTextWithLine + vTextMargin
				+ hSpace;
		Rect rect1 = new Rect();
		text = "振幅区间";
		whitePaint.setTextSize(titleTextSize);
		whitePaint.getTextBounds(text, 0, text.length(), rect1);
		paddingBottom = rect.height() + hSpaceOfTextWithLine + hTextMargin
				+ pointLineLength + rect1.height();
		whitePaint.setTextSize(textSize);
		totalWidth = viewWidth - paddingLeft - paddingRight;
		totalHeight = viewHeight - paddingTop - paddingBottom;
		if (vData.size() > 0) {
			vSpace = getVSpace();
		}
		if (hData.size() > 0) {
			hSpace = totalWidth / hData.size();
		}
		points = getPoints();
	}

	private float getVSpace() {
		double temp = 0;
		for (int i = 0; i < vData.size(); i++) {
			temp = temp > vData.get(i) ? temp : vData.get(i);
		}
		vNum = temp % 5 == 0 ? (int) temp / 5 + 1 : (int) temp / 5 + 2;
		return totalHeight / vNum;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (vData == null || vData.size() <= 0 || hData == null
				|| hData.size() <= 0) {
			return;
		}
		drawCoord(canvas);
		drawPointline(canvas);
		drawPoints(canvas);
		drawLine(canvas);
	}

	/**
	 * 画坐标轴
	 * 
	 * @param canvas
	 */
	private void drawCoord(Canvas canvas) {
		canvas.drawLine(paddingLeft - hSpace, totalHeight + paddingTop,
				paddingLeft + totalWidth, totalHeight + paddingTop, whitePaint);
		canvas.drawLine(paddingLeft - hSpace, totalHeight + paddingTop
				+ pointLineLength, paddingLeft - hSpace, paddingTop, whitePaint);
	}

	/**
	 * 画标线
	 * 
	 * @param canvas
	 */
	private void drawPointline(Canvas canvas) {
		for (int i = 0; i < vNum; i++) {
			canvas.drawLine(paddingLeft - hSpace, paddingTop + i * vSpace,
					paddingLeft + totalWidth, paddingTop + i * vSpace,
					whitePaint);
			Rect rect = new Rect();
			String text = ((vNum - i) * 5) + ".00%";
			whitePaint.getTextBounds(text, 0, text.length(), rect);
			canvas.drawText(text, paddingLeft - hSpace - rect.width(),
					paddingTop + i * vSpace + rect.height() / 2, whitePaint);
		}
		// Rect rect = new Rect();
		// String text = "30";
		// whitePaint.getTextBounds(text, 0, text.length(), rect);
		// canvas.drawText(text, paddingLeft - hSpace - rect.width() / 2, vSpace
		// * vNum + pointLineLength + paddingTop + rect.height(),
		// whitePaint);
		for (int i = 0; i < hData.size(); i++) {
			Rect rect = new Rect();
			String text = hData.get(i);
			whitePaint.getTextBounds(text, 0, text.length(), rect);
			canvas.drawLine(paddingLeft + i * hSpace, vSpace * vNum
					+ paddingTop, paddingLeft + i * hSpace, vSpace * vNum
					+ pointLineLength + paddingTop, whitePaint);
			canvas.drawText(
					text,
					paddingLeft + i * hSpace - rect.width() / 2,
					vSpace * vNum + pointLineLength + paddingTop
							+ rect.height(), whitePaint);
		}
		Rect rect = new Rect();
		whitePaint.setTextSize(titleTextSize);
		String text = "振幅变化";
		whitePaint.getTextBounds(text, 0, text.length(), rect);
		canvas.drawText("振幅变化",
				paddingLeft + totalWidth / 2 - rect.width() / 2, viewHeight
						- rect.height() / 2, whitePaint);
		whitePaint.setTextSize(textSize);
	}

	/**
	 * 画点
	 * 
	 * @param canvas
	 */
	private void drawPoints(Canvas canvas) {
		for (int i = 0; i < points.size(); i++) {
			Point point1 = points.get(i);
			canvas.drawCircle(point1.x, point1.y, radius, bluePaint);
		}
	}

	/**
	 * 画线
	 */
	private void drawLine(Canvas canvas) {
		for (int i = 0; i < points.size() - 1; i++) {
			Point point1 = points.get(i);
			Point point2 = points.get(i + 1);
			canvas.drawLine(point1.x, point1.y, point2.x, point2.y, bluePaint);
		}
	}

	private List<Point> getPoints() {
		List<Point> points = new ArrayList<LineChart.Point>();
		for (int i = 0; i < vData.size(); i++) {
			Point point = new Point((vData.size() - 1 - i) * hSpace
					+ paddingLeft, getY(vData.get(i)));
			points.add(point);
		}
		return points;
	}

	private float getY(double value) {
		float y = (float) (totalHeight + paddingTop - (value / 5 * vSpace));
		return y;
	}

	class Point {
		float x, y;

		public Point() {
			super();
		}

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "Point [x=" + x + ", y=" + y + "]";
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		viewWidth = measureWidth(widthMeasureSpec);
		viewHeight = measureHeight(heightMeasureSpec);
		setMeasuredDimension(viewWidth, viewHeight);
		// totalHeight = viewHeight - paddingTop - paddingBottom;
		// vSpace = totalHeight / 6;
		// totalWidth = viewWidth - paddingLeft - paddingRight;
		// hSpace = totalWidth / this.data.size();
		initPadding();
		invalidate();
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
