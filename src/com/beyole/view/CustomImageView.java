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

	// ��������
	private String mTitleText;
	// ����������ɫ
	private int mTitleTextColor;
	// �������ִ�С
	private int mTitleTextSize;
	// ����ͼƬ
	private Bitmap mImage;
	// ����ͼƬģʽ
	private int mImageScaleType;
	// ����
	private Paint mPaint;
	// �ı�����Rect
	private Rect mBounds;
	// ͼƬ���Ʒ�Χ
	private Rect mRect;
	// view���
	private int mWidth;
	// view�߶�
	private int mHeight;
	// ͼƬ��ʽfitXY
	private static final int IMAGE_FITXY = 0;
	// ͼƬ��ʽcenter
	private static final int IMAGE_CENTER = 1;

	public CustomImageView(Context context) {
		this(context, null);
	}

	public CustomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	// ��ʼ�������Զ�������
	public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// ��ȡ�Զ�������
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyle, 0);
		// �Զ������͸���
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
		// ��ȡ������Ʒ�Χ�������������ʹ��
		mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBounds);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// ���ÿ��
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		if (widthMode == MeasureSpec.EXACTLY) {
			mWidth = widthSize;
		} else {
			// ��ͼƬ�����Ŀ�
			int desiredByImg = getPaddingLeft() + mImage.getWidth() + getPaddingRight();
			// �����־����Ŀ��
			int desiredByText = getPaddingLeft() + mBounds.width() + getPaddingRight();
			// ���viewģʽΪwrap_content
			if (widthMode == MeasureSpec.AT_MOST) {
				int desire = Math.max(desiredByImg, desiredByText);
				mWidth = Math.min(desire, widthSize);
			}
		}
		// ���ø߶�
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
		// ���Ʊ߿�
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

		// ��ǰ���õĿ��С����������Ҫ�Ŀ�ȣ��������Ϊxxx...
		if (mBounds.width() > mWidth) {
			// �Ի������ֽ��в��� �����ض����ֺͻ���
			TextPaint paint = new TextPaint(mPaint);
			String msg = TextUtils.ellipsize(mTitleText, paint, (int) (mWidth - getPaddingLeft() - getPaddingRight()), TextUtils.TruncateAt.END).toString();
			canvas.drawText(msg, getPaddingLeft(), mHeight - getPaddingBottom(), mPaint);
		} else {
			// ��������½��������
			canvas.drawText(mTitleText, mWidth / 2 - mBounds.width() / 2, mHeight - getPaddingBottom(), mPaint);
		}
		// ȡ������������ʱ�ĸ߶�
		mRect.bottom -= mBounds.height();
		if (mImageScaleType == IMAGE_FITXY) {
			canvas.drawBitmap(mImage, null, mRect, mPaint);
		} else {
			// ������еľ��η�Χ
			mRect.left = mWidth / 2 - mImage.getWidth() / 2;
			mRect.right = mWidth / 2 + mImage.getWidth() / 2;
			mRect.top = (mHeight - mBounds.height()) / 2 - mImage.getHeight() / 2;
			mRect.bottom = (mHeight - mBounds.height()) / 2 + mImage.getHeight() / 2;
			canvas.drawBitmap(mImage, null, mRect, mPaint);
		}
	}
}
