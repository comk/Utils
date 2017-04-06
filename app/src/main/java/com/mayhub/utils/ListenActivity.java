package com.mayhub.utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mayhub.utils.adapter.LoadMoreRecyclerAdapter;
import com.mayhub.utils.common.MLogUtil;
import com.mayhub.utils.common.PlayerUtils;
import com.mayhub.utils.common.ToastUtils;

import java.util.ArrayList;

/**
 * Created by comkdai on 2017/4/6.
 */
public class ListenActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "ListenActivity";

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_start:
                PlayerUtils.getInstance().switchPlayOrPause();
                break;
            case R.id.iv_pre:
                PlayerUtils.getInstance().addSpeed(0.25f);
                break;
            case R.id.iv_next:
                PlayerUtils.getInstance().subSpeed(0.25f);
                break;
            case R.id.password:
                if(v.getTag() != null && v.getTag() instanceof AudioSentenceBean){
                    AudioSentenceBean audioSentenceBean = (AudioSentenceBean) v.getTag();
                    PlayerUtils.getInstance().setSentenceLoopInfo(audioSentenceBean.startTime, audioSentenceBean.startTime + audioSentenceBean.duration);
                    PlayerUtils.getInstance().seekTo((int) audioSentenceBean.startTime);
                }
                break;
            case R.id.iv_passage_loop:
                PlayerUtils.getInstance().startPassageLoop();
                ToastUtils.getInstance().showShortToast(getApplicationContext(), "单曲循环");
                break;
            case R.id.iv_sentence_loop:
                ToastUtils.getInstance().showShortToast(getApplicationContext(), "单句循环");
                PlayerUtils.getInstance().startLoop();
                break;
        }
    }

    private String[] sentences = new String[]{
            "学生：嗨，嗯……，我真的希望你能帮我个忙。",
            "图书管理员：这是我分内事。",
            "我能帮你什么忙呢？",
            "学生：我想写一篇关于心理学课程的文学评论，但是我在找文章的时候遇到了困难。",
            "甚至我都不知道应该从哪开始找起。",
            "图书管理员：给心理学做准备，是吧？",
            "所以，你把重点放在……",
            "学生：解梦/ 对梦的解释。",
            "图书管理员：行，你有一个重点，这算是一个很好的开始。",
            "嗯...好，有这么几件事... ... 哦，等等... ...你有没有检查看教授是否给你预留了学习资料？",
            "学生：啊哈，这是我知道自己该做的唯一一件事。",
            "我刚刚复印了一篇文章，但我还需要从三 个不同的期刊里找出三篇关于我主题的文章。",
            "图书管理员：那就让我们一块找这三篇文章呗。",
            "在“参考书库区”，我们已经印刷过二十种版本的心理学期刊。",
            "这些都是去年一年内发行的。",
            "此外，我想想看... ...（貌似）有一份 名为《睡眠与梦》的期刊。",
            "学生：哦，是的，我刚才复印的一篇文章就来自那本期刊，所以我得看看其他资料来源。",
            "图书管理员：好的，其实，我们的大部分资料都是在网络上可用的。",
            "你可以通过图书馆的电脑，接触到心理学数据库或相关电子期刊和文章；同时，如果你想通过搜索标题关键字，如“梦想”一词，输入该词，所有包含“梦想”一词的文章将都出现在屏幕上。",
            "学生：酷,太好了！",
            "哎呀,要是在家里也能这样做就好了。"
    };

    private long[] startTimes = new long[]{
            7120,
            12416,
            13631,
            14847,
            22623,
            24655,
            27647,
            29804,
            31196,
            34508,
            42514,
            45105,
            50867,
            53203,
            58882,
            60000+1552,
            60000+5725,
            60000+11341,
            60000+15709,
            60000+30956,
            60000+32829,
            60000+34877

    };

private long[] durationTimes = new long[]{
        5296,
        1215,
        1216,
        7776,
        2032,
        2992,
        2157,
        1392,
        3312,
        8006,
        2591,
        5762,
        2336,
        5679,
        2670,
        4173,
        5616,
        4368,
        15247,
        1873,
        2048
    };



    public static class AudioPassageBean{
        String audioPath;
        SparseArray<String> sentences = new SparseArray<>();
    }

    public static class AudioSentenceBean{
        private String sentence;
        private long startTime;
        private long duration;
    }

    public static class AudioParagrahBean{
        ArrayList<AudioSentenceBean> sentenceBeanArrayList;
        Spannable span;
    }

    private RecyclerView recyclerView;

    private View start;

    private View pre;

    private SeekBar seekBar;

    private View next;

    private AudioPassageBean audioPassageBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        printDuartion();
        initView();

        initValue();


    }

    ArrayList<AudioSentenceBean> list = new ArrayList<>();
    ArrayList<AudioSentenceBean> list2 = new ArrayList<>();
    ArrayList<AudioParagrahBean> passages = new ArrayList<>(2);
    private void printDuartion(){

        AudioSentenceBean audioSentenceBean;
        for (int i = 0; i < durationTimes.length; i++) {
            audioSentenceBean = new AudioSentenceBean();
            audioSentenceBean.duration = durationTimes[i];
            audioSentenceBean.startTime = startTimes[i];
            audioSentenceBean.sentence = sentences[i];
            if(i > durationTimes.length / 2){
                list2.add(audioSentenceBean);
            }else {
                list.add(audioSentenceBean);
            }
        }
        AudioParagrahBean audioPassageBean = new AudioParagrahBean();
        audioPassageBean.sentenceBeanArrayList = list;
        passages.add(audioPassageBean);
        audioPassageBean = new AudioParagrahBean();
        audioPassageBean.sentenceBeanArrayList = list2;
        passages.add(audioPassageBean);
    }

    private void initValue() {
        recyclerView.setAdapter(new AudioPassageAdapter(passages, this));
        PlayerUtils.getInstance().play("assets://tpo1_listening_passage1_1.mp3");
        PlayerUtils.getInstance().hookWithSeekBar(seekBar);
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        start = findViewById(R.id.iv_start);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        start.setOnClickListener(this);
        pre = findViewById(R.id.iv_pre);
        pre.setOnClickListener(this);
        next = findViewById(R.id.iv_next);
        next.setOnClickListener(this);
        findViewById(R.id.iv_passage_loop).setOnClickListener(this);
        findViewById(R.id.iv_sentence_loop).setOnClickListener(this);
    }

    private static class AudioPassageAdapter extends LoadMoreRecyclerAdapter{

        private ArrayList<AudioParagrahBean> data = new ArrayList<>();

        private View.OnClickListener onClickListener;

        public AudioPassageAdapter(ArrayList<AudioParagrahBean> data, View.OnClickListener onClickListener) {
            this.data = data;
            this.onClickListener = onClickListener;
        }

        public AudioPassageAdapter(ArrayList<AudioParagrahBean> data) {
            this.data = data;
        }

        @Override
        public int getChildCount() {
            return data.size();
        }

        @Override
        public int getChildItemViewType(int pos) {
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setId(R.id.password);
            tv.setPadding(20, 20, 20, 20);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            return new RecyclerView.ViewHolder(tv) {};
        }

        @Override
        public void onBindChildViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder.itemView instanceof TextView){
                if(data.get(position).span == null){
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    ArrayList<AudioSentenceBean> list = data.get(position).sentenceBeanArrayList;
                    for (int i = 0; i < list.size(); i++) {
                        spannableStringBuilder.append(list.get(i).sentence);
                        spannableStringBuilder.setSpan(new CusClickSpan(list.get(i), onClickListener), spannableStringBuilder.length() - list.get(i).sentence.length(), spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    data.get(position).span = spannableStringBuilder;
                }
                ((TextView) holder.itemView).setText(data.get(position).span);
            }
        }
    }

    private static class CusClickSpan extends ClickableSpan {

        private AudioSentenceBean audioSentenceBean;
        private View.OnClickListener onClickListener;
        public CusClickSpan(AudioSentenceBean audioSentenceBean, View.OnClickListener onClickListener1) {
            this.audioSentenceBean = audioSentenceBean;
            onClickListener = onClickListener1;
        }

        @Override
        public void onClick(View view) {
            view.setTag(audioSentenceBean);
            onClickListener.onClick(view);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

    }

}
