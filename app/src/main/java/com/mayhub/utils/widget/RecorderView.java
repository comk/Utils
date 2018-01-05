package com.mayhub.utils.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mayhub.utils.common.MLogUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by comkdai on 2017/7/19.
 */
public class RecorderView extends View {
    String str = "-C\n" +
            "\n" +
            "\n" +
            "urutora.si- ウルトラ・シー\n" +
            "\n" +
            "〈体操〉超高难动作chāogāonán dòngzuò;最佳zuìjiā.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "-いじり\n" +
            "\n" +
            "\n" +
            "-ijiri ‐いじり\n" +
            "\n" +
            "摆弄bǎinòng,鼓捣gǔdao『方』,玩弄wánnòng;胡乱húluàn〔任意rènyì〕 改动gǎidòng.\n" +
            "\n" +
            "$庭いじり／摆弄庭园（中的花木等）.\n" +
            "\n" +
            "$機構いじり／随意改动组织机构jīgòu.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "-がかる\n" +
            "\n" +
            "\n" +
            "-gakaru ‐がかる\n" +
            "\n" +
            "（1）〔…のようである〕类似lèisì,仿效fǎngxiào,带有dàiyǒu……的样子de yàngzi.\n" +
            "\n" +
            "$芝居がかった動作／仿佛fǎngfú做戏的动作; 矫揉jiǎo róu造作的动作.\n" +
            "\n" +
            "（2）〔…おびる〕稍带shāodài,带点dàidiǎn.\n" +
            "\n" +
            "$紫がかった雲／带点紫色的云彩.\n";
    private static final int LINE_WIDTH = 50; // width of visualizer lines
    private static final int LINE_SCALE = 100; // scales visualizer lines
    private static final int REFRESH_DURATION = 20; // scales visualizer lines
    private static final float factor = 0.99f;
//    private List<Float> drawAmplitudes; // amplitudes for line lengths
    private LinkedList<Float> list;
    private int maxCount;
    private int width; // width of this View
    private int height; // height of this View
    private boolean isRecording = false;
    private float amplitude;
    private Paint linePaint; // specifies line drawing characteristics
    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            if(list.size() > 0) {
                list.remove();
                list.add(amplitude);
//                drawAmplitudes.remove(0); // remove oldest power value
//                drawAmplitudes.add(amplitude);
                amplitude = 0;
                postInvalidate();
            }
            if (isRecording) {
                postDelayed(refresh, REFRESH_DURATION);
            }
        }
    };
    // constructor
    public RecorderView(Context context, AttributeSet attrs) {
        super(context, attrs); // call superclass constructor
        linePaint = new Paint(); // create Paint for lines
        linePaint.setColor(Color.GREEN); // set color to green
        linePaint.setStrokeWidth(LINE_WIDTH); // set stroke width
    }

    // called when the dimensions of the View change
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w; // new width of this View
        height = h; // new height of this View
        maxCount = width / LINE_WIDTH/2;
//        drawAmplitudes = new ArrayList<>(maxCount);
        list = new LinkedList<>();
        for (int i = 0; i < maxCount + 5; i++) {
//            drawAmplitudes.add(0f);
            list.add(0f);
        }
    }

    // clear all amplitudes to prepare for a new visualization
    public void clear() {
        list.clear();
    }

    // add the given amplitude to the amplitudes ArrayList
    public void setCurrentAmplitude(float amplitude) {
        // if the power lines completely fill the VisualizerView
        this.amplitude = amplitude;

    }

    private void test(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(REFRESH_DURATION);
                        float db = (float) Math.log10((float) Math.random());
                        setCurrentAmplitude(db);
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    public void startRecord(){
        isRecording = true;
        postDelayed(refresh, REFRESH_DURATION);
        test();
    }

    public void stopRecord(){
        isRecording = false;
        removeCallbacks(refresh);
    }

    private int printFirstLoop = 0;


    // draw the visualizer with scaled lines representing the amplitudes
    @Override
    public void onDraw(Canvas canvas) {
        int middle = height / 2; // get the middle of the View
        float curX = 0; // start curX at zero
        super.onDraw(canvas);
//        StringBuilder sb = new StringBuilder();
        canvas.drawLine(0, middle, width, middle, linePaint);
        if(maxCount > 0) {
            for (int i = 0; i < maxCount; i++) {
                if (i < list.size()) {
                    float power = list.get(i);
                    float scaledHeight = power * LINE_SCALE/2; // scale the power
                    curX += LINE_WIDTH; // increase X by LINE_WIDTH
                    canvas.drawLine(curX, middle - scaledHeight, curX, middle
                            + scaledHeight, linePaint);
                }
            }
            curX += LINE_WIDTH;
            for (int i = maxCount; i > 0; i--) {
                if (i < list.size()) {
                    float power = list.get(i);
                    float scaledHeight = power * LINE_SCALE/2; // scale the power
                    curX += LINE_WIDTH; // increase X by LINE_WIDTH
                    canvas.drawLine(curX, middle - scaledHeight, curX, middle
                            + scaledHeight, linePaint);
                }
            }
//            for (int i = 0; i < list.size(); i++) {
//                float power = list.get(i);
//                if(Math.abs(power) > 0f && printFirstLoop < 50) {
//                    MLogUtil.e("power", " = " + power);
//                    printFirstLoop ++;
//                }
//                if(Math.abs(power) > 0.6f){
//                    power = power * factor * 0.91f;
//                }else if(Math.abs(power) > 0.3f){
//                    power = power * factor * 0.95f;
//                }else{
//                    power = power * factor * 3f;
//                }
//                list.set(i, power);
//            }
        }
    }

}
