package com.huawei.histogramcomponent.view;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.DragEvent;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.agp.window.service.Display;
import ohos.agp.window.service.DisplayManager;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class HistogramComponent extends Component {
    private HiLogLabel hiLogLabel = new HiLogLabel(HiLog.LOG_APP, 0x00101, "文本");

    private Context mContext;

    /**
     * 柱状图的宽度
     */
    public float mLineWidth;
    /**
     * 柱状图之间的间隔
     */
    public float mLineSpaceWidth;
    /**
     * 柱状图的颜色
     */
    private int barGraphBgColor;
    /**
     * X轴字体的大小
     */
    public float mXTextSize;
    /**
     * Y轴的字体大小
     */
    private float mYTextSize;
    /**
     * xy轴线和字体的颜色
     */
    private int mXYBGColor;
    /**
     * 柱状图执行动画的长度
     */
    private Integer duration;
    /**
     * 是否显示X轴的文字
     */
    private boolean isShowXText;
    /**
     * 是否显示Y轴的文字
     */
    private boolean isShowYText;

    /**
     * 柱状图的画笔
     */
    private Paint mBarGraphPaint;
    /**
     * 柱状图上面字的画笔
     */
    private Paint mBarGraphTextPaint;
    /**
     * X轴的文字画笔
     */
    private Paint mXTextPaint;
    /**
     * Y轴的文字画笔
     */
    private Paint mYTextPaint;
    /**
     * XY轴的坐标画笔
     */
    private Paint mXYLinePaint;
    /**
     * 当前柱状图的最大高度
     */
    private int maxHeight;
    /**
     * 实际高度
     */
    private int heightMeasureSpec;
    /**
     * 实际宽度
     */
    private int widthMeasureSpec;
    /**
     * 圆柱占用的高度
     */
    private float bottomHeight;

    //保存柱状图的数据
    private int[][] barGraphDataList;
    //保存柱状图的颜色
    private int[] barGraphColorList;
    //保存柱状图X轴信息
    private String[] barGraphTextList;
    //Y轴线离左边的距离，以便绘制Y轴数字
    private float mLeftYWidth;
    //X轴线离底部的距离，以便绘制X轴的文字
    private float mBottomXWidth;


    /**
     * 定义DrawTask对象的实例
     * 这里进行具体的绘画工作
     */
    private DrawTask drawTask = new DrawTask() {
        @Override
        public void onDraw(Component component, Canvas canvas) {
            bottomHeight = heightMeasureSpec - mBottomXWidth;
            if (barGraphDataList == null || barGraphDataList.length <= 0)
                return;
            //画柱状图
            drawBarGraph(canvas);
            //画XY轴坐标
            drawXYLine(canvas);
            //给XY轴坐标写字
            drawXYText(canvas);
        }
    };

    public HistogramComponent(Context context) {
        super(context);

    }

    public HistogramComponent(Context context, AttrSet attrSet) {
        super(context, attrSet);
        this.mContext = context;
        //柱状图的宽度
        mLineWidth = 40;
        //柱状图之间的间隔
        mLineSpaceWidth = 40;
        //柱状图的颜色
        barGraphBgColor = 0xFF222222;
        //X轴字体大小
        mXTextSize = 38;
        //y轴字体大小
        mYTextSize = 38;
        //xy轴的颜色
        mXYBGColor = 0xFF000000;
        //执行动画的时间
        duration = 1000;
        isShowXText = true;
        isShowYText = true;
        initView();
    }

    public HistogramComponent(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);

    }

    public HistogramComponent(Context context, AttrSet attrSet, int resId) {
        super(context, attrSet, resId);

    }

    @Override
    public boolean onDrag(Component component, DragEvent event) {
        return super.onDrag(component, event);

    }

    @Override
    public void addDrawTask(DrawTask task) {
        super.addDrawTask(task);
        task.onDraw(this,mCanvasForTaskOverContent);
    }

    /**
     * 初始化画笔
     */
    private void initView() {
        Display display = DisplayManager.getInstance().getDefaultDisplay(mContext).get();
        Point point = new Point();
        display.getSize(point);
        heightMeasureSpec = MeasureSpec.getSize(point.getPointXToInt());
        widthMeasureSpec = MeasureSpec.getSize(point.getPointYToInt());
        if (barGraphDataList != null && barGraphDataList.length > 0) {
            widthMeasureSpec = (int) (mLineSpaceWidth * (barGraphDataList[0].length + 1) + mLineWidth * barGraphDataList.length * barGraphDataList[0].length) + (5);
        }
        this.heightMeasureSpec = MeasureSpec.getSize(heightMeasureSpec);
        //左边字体的长度
        widthMeasureSpec += mLeftYWidth + 10;

        mBarGraphPaint = new Paint();
        mBarGraphPaint.setStrokeWidth(mLineWidth);
        mBarGraphPaint.setAntiAlias(true);

        mBarGraphTextPaint = new Paint();
        mBarGraphTextPaint.setStrokeWidth(18.0f);
        mBarGraphTextPaint.setTextSize(32);
        mBarGraphTextPaint.setAntiAlias(true);

        mXTextPaint = new Paint();
        mXTextPaint.setStrokeWidth(13);
        mXTextPaint.setColor(new Color(mXYBGColor));
        mXTextPaint.setTextSize((int)mXTextSize);
        mXTextPaint.setAntiAlias(true);

        mYTextPaint = new Paint();
        mYTextPaint.setStrokeWidth(13);
        mYTextPaint.setColor(new Color(mXYBGColor));
        mYTextPaint.setTextSize((int)mYTextSize);
        mYTextPaint.setAntiAlias(true);

        mXYLinePaint = new Paint();
        mXYLinePaint.setStrokeWidth(3);
        mXYLinePaint.setColor(new Color(mXYBGColor));
        mXYLinePaint.setAntiAlias(true);
        addDrawTask(drawTask);
    }


    //画柱状图
    private void drawBarGraph(Canvas canvas) {
        if (barGraphDataList != null && barGraphDataList.length > 0) {
            for (int i = 0; i < barGraphDataList[0].length; i++) {
                float startX = mLineSpaceWidth * (i + 1) + mLineWidth * barGraphDataList.length * i + mLeftYWidth + (10) + mLineWidth / 2;
                int index = 0;
                while (index < barGraphDataList.length) {
                    if (barGraphColorList != null && barGraphColorList.length > index) {
                        mBarGraphPaint.setColor(new Color(barGraphColorList[index]));
                        mBarGraphTextPaint.setColor(new Color(barGraphColorList[index]));
                    } else {
                        mBarGraphPaint.setColor(new Color(barGraphBgColor));
                        mBarGraphTextPaint.setColor(new Color(barGraphBgColor));
                    }

                    float stopY = bottomHeight * 0.9f / maxHeight * barGraphDataList[index][i];

                    canvas.drawLine(new Point(startX, bottomHeight), new Point(startX, bottomHeight - stopY), mBarGraphPaint);

                    String text = String.valueOf(barGraphDataList[index][i]);
                    float textWidth = mBarGraphTextPaint.measureText(text);
                    canvas.drawText(mBarGraphTextPaint,text, startX - textWidth / 2, bottomHeight - stopY - 10);
                    startX += mLineWidth;
                    index++;
                }
            }
        }
    }

    /**
     * 传进来的数组要求保持数组长度一致
     */
    public void setBarGraphData( int[][] barGraphDataList, int[] barGraphColorList, String[] barGraphTextList) {
        this.barGraphDataList = barGraphDataList;
        this.barGraphColorList = barGraphColorList;
        this.barGraphTextList = barGraphTextList;

        //计算出最高的坐标
        for (int i = 0; i < barGraphDataList.length; i++) {
            for (int j = 0; j < barGraphDataList[i].length; j++) {
                if (maxHeight < barGraphDataList[i][j]) {
                    maxHeight = barGraphDataList[i][j];
                }
            }
        }
        while (maxHeight % 5 != 0) {
            maxHeight++;
        }
        if (barGraphTextList != null && barGraphTextList.length > 0) {
            isShowXText = true;
        }
        if (isShowYText) {
            mLeftYWidth = mYTextPaint.measureText(String.valueOf(maxHeight));
        }
        mBottomXWidth = (10);
        if (isShowXText) {
            Paint.FontMetrics fontMetrics = mXTextPaint.getFontMetrics();
            mBottomXWidth += ((fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom) * 2;
        }
        measureWidth(heightMeasureSpec);

        invalidate();
    }
    /**
     * 长度可能超出屏幕范围
     */
    private void measureWidth(int heightMeasureSpec) {
        if (barGraphDataList != null && barGraphDataList.length > 0) {
            widthMeasureSpec = (int) (mLineSpaceWidth * (barGraphDataList[0].length + 1) + mLineWidth * barGraphDataList.length * barGraphDataList[0].length) + (5);
        }
        this.heightMeasureSpec = MeasureSpec.getSize(heightMeasureSpec);
        //左边字体的长度
        widthMeasureSpec += mLeftYWidth + (10);
    }
    //画X轴和Y轴的竖线+箭头
    private void drawXYLine(Canvas canvas) {
        /**
         * 让Y轴文字与最左有dip2px(10)的边距
         * */
        //Y轴竖线
        canvas.drawLine(new Point((10) + mLeftYWidth, bottomHeight), new Point((10) + mLeftYWidth, 10), mXYLinePaint);
        //X轴竖线
        canvas.drawLine(new Point((10) + mLeftYWidth, bottomHeight), new Point(widthMeasureSpec - 10, bottomHeight), mXYLinePaint);
        //画个箭头？？Y轴
        canvas.drawLine(new Point((10) + mLeftYWidth, 10), new Point((6) + mLeftYWidth, 20), mXYLinePaint);
        canvas.drawLine(new Point((10) + mLeftYWidth, 10), new Point((14) + mLeftYWidth, 20), mXYLinePaint);
        //X轴箭头
        canvas.drawLine(new Point(widthMeasureSpec - 10, bottomHeight), new Point(widthMeasureSpec - 20, bottomHeight - (4)), mXYLinePaint);
        canvas.drawLine(new Point(widthMeasureSpec - 10, bottomHeight), new Point(widthMeasureSpec - 20, bottomHeight + (4)), mXYLinePaint);
    }
    //给Y轴和X轴写相应的文字
    private void drawXYText(Canvas canvas) {
        if (isShowYText) {
            //Y轴写字
            for (int i = 1; i <= 5; i++) {
                float startY = bottomHeight - bottomHeight * 0.9f / maxHeight * maxHeight / 5 * i;
                canvas.drawLine(new Point((10) + mLeftYWidth, startY), new Point((15) + mLeftYWidth, startY), mYTextPaint);
                float width = mYTextPaint.measureText(maxHeight / 5 * i + "");

                float dy = 12.0f;
                canvas.drawText(mYTextPaint,maxHeight / 5 * i + "", (int) ((10) + mLeftYWidth - width - (5)), startY + dy);
            }
        }
        if (!isShowXText) {
            return;
        }
        //X轴写字
        if (barGraphTextList != null && barGraphTextList.length > 0) {
            for (int i = 0; i < barGraphTextList.length; i++) {
                float startX = mLineSpaceWidth * (i + 1) + mLineWidth * barGraphDataList.length * i + mLeftYWidth + (10);
                //中间有一个间隔
                startX = startX + (mLineWidth * barGraphDataList.length) * 1.0f / 2;
                float textWidth = mXTextPaint.measureText(barGraphTextList[i]);
                canvas.drawText(mXTextPaint,barGraphTextList[i], startX - textWidth / 2, heightMeasureSpec - (5));
            }
        }
    }

    public boolean isShowXText() {
        return isShowXText;
    }
}
