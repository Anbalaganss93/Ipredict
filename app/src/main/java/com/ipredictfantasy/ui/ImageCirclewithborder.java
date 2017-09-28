package com.ipredictfantasy.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.ipredictfantasy.R;

/**
 * Created by UMMWDC001 on 2/12/2016.
 */
public class ImageCirclewithborder extends ImageView {
        private int borderWidth;
        private int canvasSize;
        private Bitmap image;
        private Drawable drawable;
        private Paint paint;
        private Paint paintBorder;

        public ImageCirclewithborder(Context context) {
            this(context, (AttributeSet)null);
        }

        public ImageCirclewithborder(Context context, AttributeSet attrs) {
            this(context, attrs, com.mikhaellopez.circularimageview.R.attr.circularImageViewStyle);
        }

        public ImageCirclewithborder(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            this.init(context, attrs, defStyleAttr);
        }

        @TargetApi(21)
        public ImageCirclewithborder(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            this.init(context, attrs, defStyleAttr);
        }

        private void init(Context context, AttributeSet attrs, int defStyleAttr) {
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            this.paintBorder = new Paint();
            this.paintBorder.setAntiAlias(true);
            TypedArray attributes = context.obtainStyledAttributes(attrs, com.mikhaellopez.circularimageview.R.styleable.CircularImageView, defStyleAttr, 0);
            if(attributes.getBoolean(com.mikhaellopez.circularimageview.R.styleable.CircularImageView_border, true)) {
                int defaultBorderSize = (int)(2.0F * this.getContext().getResources().getDisplayMetrics().density + 0.5F);
                this.setBorderWidth(defaultBorderSize);   //attributes.getDimensionPixelOffset(com.mikhaellopez.circularimageview.R.styleable.CircularImageView_border_width,8)
                this.setBorderColor(ContextCompat.getColor(getContext(),R.color.white)); //attributes.getColor(com.mikhaellopez.circularimageview.R.styleable.CircularImageView_border_color, -1)
            }

            if(attributes.getBoolean(com.mikhaellopez.circularimageview.R.styleable.CircularImageView_shadow, false)) {
                this.addShadow();
            }
        }

        public void setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
            this.requestLayout();
            this.invalidate();
        }

        public void setBorderColor(int borderColor) {
            if(this.paintBorder != null) {
                this.paintBorder.setColor(borderColor);
            }
            this.invalidate();
        }

        public void addShadow() {
            if(Build.VERSION.SDK_INT >= 11) {
                this.setLayerType(1, this.paintBorder);
            }
            this.paintBorder.setShadowLayer(4.0F, 0.0F, 2.0F, -16777216);
        }

        public void onDraw(Canvas canvas) {
            this.loadBitmap();
            if(this.image != null) {
                this.canvasSize = canvas.getWidth();
                if(canvas.getHeight() < this.canvasSize) {
                    this.canvasSize = canvas.getHeight();
                }

                int circleCenter = (this.canvasSize - this.borderWidth * 2) / 2;
                canvas.drawCircle((float)(circleCenter + this.borderWidth), (float)(circleCenter + this.borderWidth), (float)(circleCenter + this.borderWidth) - 4.0F, this.paintBorder);
                canvas.drawCircle((float)(circleCenter + this.borderWidth), (float)(circleCenter + this.borderWidth), (float)circleCenter - 4.0F, this.paint);
            }
        }

        private void loadBitmap() {
            if(this.drawable != this.getDrawable()) {
                this.drawable = this.getDrawable();
                this.image = this.drawableToBitmap(this.drawable);
                this.updateShader();
            }
        }

        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            this.canvasSize = w;
            if(h < this.canvasSize) {
                this.canvasSize = h;
            }

            if(this.image != null) {
                this.updateShader();
            }

        }

        private void updateShader() {
            if(this.image != null) {
                BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(ThumbnailUtils.extractThumbnail(this.image, this.canvasSize, this.canvasSize), this.canvasSize, this.canvasSize, false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                this.paint.setShader(shader);
            }
        }

        private Bitmap drawableToBitmap(Drawable drawable) {
            if(drawable == null) {
                return null;
            } else if(drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable)drawable).getBitmap();
            } else {
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicHeight();
                if(intrinsicWidth > 0 && intrinsicHeight > 0) {
                    try {
                        Bitmap e = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(e);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                        return e;
                    } catch (OutOfMemoryError var6) {
                        Log.e(this.getClass().toString(), "Encountered OutOfMemoryError while generating bitmap!");
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = this.measureWidth(widthMeasureSpec);
            int height = this.measureHeight(heightMeasureSpec);
            this.setMeasuredDimension(width, height);
        }

        private int measureWidth(int measureSpec) {
            int specMode = MeasureSpec.getMode(measureSpec);
            int specSize = MeasureSpec.getSize(measureSpec);
            int result;
            if(specMode == 1073741824) {
                result = specSize;
            } else if(specMode == -2147483648) {
                result = specSize;
            } else {
                result = this.canvasSize;
            }
            return result;
        }

        private int measureHeight(int measureSpecHeight) {
            int specMode = MeasureSpec.getMode(measureSpecHeight);
            int specSize = MeasureSpec.getSize(measureSpecHeight);
            int result;
            if(specMode == 1073741824) {
                result = specSize;
            } else if(specMode == -2147483648) {
                result = specSize;
            } else {
                result = this.canvasSize;
            }
            return result + 2;
        }
    }


