package com.beyole.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.beyole.customimageview.R;

public class CustomImageView extends View {

	// 描述文字
	private String mTitleText;
	// 描述文字颜色
	private int mTitleTextColor;
	// 描述文字大小
	private int mTitleTextSize;
	// 描述图片
	private Bitmap mImage;
	// 描述图片模式
	private int mImageScaleType;
	// 画笔
	private Paint mPaint;
	// 文本绘制Rect
	private Rect mBounds;
	// 图片绘制范围
	private Rect mRect;
	// view宽度
	private int mWidth;
	// view高度
	private int mHeight;
	// 图片格式fitXY
	private static final int IMAGE_FITXY = 0;
	// 图片格式center
	private static final int IMAGE_CENTER = 1;

	public CustomImageView(Context context) {
		this(context, null);
	}

	public CustomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	// 初始化所有自定义类型
	public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// 获取自定义类型
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyle, 0);
		// 自定义类型个数
		int n = array.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = array.getIndex(i);
			switch (attr) {
			case R.styleable.CustomImageView_titleText:
				mTitleText = array.getString(attr);
				break;
			case R.styleable.CustomImageView_titleTextColor:
				mTitleTextColor = array.getColor(attr, Color.BLACK);
				break;
			case R.styleable.CustomImageView_titleTextSize:
				mTitleTextSize = array.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
				break;
			case R.styleable.CustomImageView_image:
				mImage = BitmapFactory.decodeResource(getResources(), array.getResourceId(attr, 0));
				break;
			case R.styleable.CustomImageView_imageScaleType:
				mImageScaleType = array.getInt(attr, 0);
				break;
			}
		}
		array.recycle();
		mRect = new Rect();
		mBounds = new Rect();
		mPaint = new Paint();
		mPaint.setTextSize(mTitleTextSize);
		// 获取字体绘制范围，方便下面测量使用
		mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBounds);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 设置宽度
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		if (widthMode == MeasureSpec.EXACTLY) {
			mWidth = widthSize;
		} else {
			// 由图片决定的宽
			int desiredByImg = getPaddingLeft() + mImage.getWidth() + getPaddingRight();
			// 由文字决定的宽度
			int desiredByText = getPaddingLeft() + mBounds.width() + getPaddingRight();
			// 如果view模式为wrap_content
			if (widthMode == MeasureSpec.AT_MOST) {
				int desire = Math.max(desiredByImg, desiredByText);
				mWidth = Math.min(desire, widthSize);
			}
		}
		// 设置高度
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (heightMode == MeasureSpec.EXACTLY) {
			mHeight = heightSize;
		} else {
			int desire = getPaddingTop() + getPaddingBottom() + mImage.getHeight() + mBounds.height();
			// wrap_content
			if (heightMode == MeasureSpec.AT_MOST) {
				mHeight = Math.min(heightSize, desire);
			}
		}
		setMeasuredDimension(mWidth, mHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 绘制边框
		mPaint.setStrokeWidth(4);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.CYAN);
		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

		mRect.left = getPaddingLeft();
		mRect.right = mWidth - getPaddingRight();
		mRect.top = getPaddingTop();
		mRect.bottom = mHeight - getPaddingBottom();

		mPaint.setColor(mTitleTextColor);
		mPaint.setStyle(Paint.Style.FILL);

		// 当前设置的宽度小于字体所需要的宽度，将字体改为xxx...
		if (mBounds.width() > mWidth) {
			// 对绘制文字进行操作 包括截断文字和绘制
			TextPaint paint = new TextPaint(mPaint);
			String msg = TextUtils.ellipsize(mTitleText, paint, (int) (mWidth - getPaddingLeft() - getPaddingRight()), TextUtils.TruncateAt.END).toString();
			canvas.drawText(msg, getPaddingLeft(), mHeight - getPaddingBottom(), mPaint);
		} else {
			// 正常情况下将字体居中
			canvas.drawText(mTitleText, mWidth / 2 - mBounds.width() / 2, mHeight - getPaddingBottom(), mPaint);
		}
		// 取消掉绘制文字时的高度
		mRect.bottom -= mBounds.height();
		if (mImageScaleType == IMAGE_FITXY) {
			canvas.drawBitmap(mImage, null, mRect, mPaint);
		} else {
			// 计算居中的矩形范围
			mRect.left = mWidth / 2 - mImage.getWidth() / 2;
			mRect.right = mWidth / 2 + mImage.getWidth() / 2;
			mRect.top = (mHeight - mBounds.height()) / 2 - mImage.getHeight() / 2;
			mRect.bottom = (mHeight - mBounds.height()) / 2 + mImage.getHeight() / 2;
			canvas.drawBitmap(mImage, null, mRect, mPaint);
		}
	}
}
