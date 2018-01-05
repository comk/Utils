package com.mayhub.utils.test;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mayhub.utils.R;
import com.mayhub.utils.adapter.LoadMoreRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/16.
 */
public class TestHeadFootAdapter extends LoadMoreRecyclerAdapter {


    public ArrayList<String> datas = new ArrayList<>();
    String text = "Groundwater is the word used to describe water that saturates the ground, filling all the available spaces.";
    String text2 = "By far the most abundant type of groundwater is meteoric water; this is the groundwater that circulates as part of the water cycle.";
    String text3 = "Ordinary meteoric water is water that has soaked into the ground from the surface, from precipitation (rain and snow) and from lakes and streams.";
    String text4 = "There it remains, sometimes for long periods, before emerging at the surface again.";
    String text5 = "At first thought it seems incredible that there can be enough space in the “solid” ground underfoot to hold all this water.";
    String text6 = "\"ずっと刻まれる「母の愛」\n" +
            "　　友人からの深夜の電話。彼女は息子の小1のときのクラスメートの母親で、いわゆるハハ友である。お寿司屋さんの彼女とは家族ぐるみの付き合いで、もう20年以上が経つ。仕事を持つ私たちが自分の時間を持てるのは、決まって深夜。いつも真夜中にいろいろなことを話し、助け合いながら生きてきた。 (41)息子が一人暮らしを始めた。育ち続けるのだそうお祝いを兼ねてアパートを訪ね、夕食を作り、洗濯をし、汗だくになって真新しいカーテンをつけ終えたという、そして帰る母親に彼は一言「じゃあね」。その「じゃあね」に頭にきたと。なぜ、「お母さんありがとう」と言えないのだ。(42)と、寂しくなったという。\n" +
            "　　彼女自身は幼い頃に両親が離婚し、父親の元で育ち、中学から家事一切を任されていた。彼女の洗濯物のたたみ方は今でも見ていて美しく、気持ちがいい。子供たちにはできる限りのことをしてあげたいと、いつも一生懸命やってきたという。「それでいいんだよ。」そう答えながら、私には突然、自分が7歳のときの光景が蘇った。\n" +
            "　　(43)、私の母も働いていた。ある日、初めて友人が家に遊びに来ることになり、前の晩一緒にお風呂に入りながら、母にそのことを告げた。「明日ね、○○チャンと○○チャンと…」。母は黙って(44)。翌日帰宅すると、テーブルの上の紙皿に、人数分の数種類のお菓子がきれいに並べられていた。手作りのケーキや高価なお菓子でもなんでもないけれど、私は自慢げにみなに言った。「さあ、おやつですよー」。\n" +
            "　　そうなのだ。愛情は突然蘇り、胸いっぱいに広がり、生き続けるものなのだ。彼女の息子への愛も、これからずっと彼の心で(45)。\n" +
            "　　(大竹しのぶ 朝日新聞2013年7月19日付夕刊による)\"\n";
    public TestHeadFootAdapter(){
        datas.add(text + text2 + text3 + text4 + text5);
        datas.add(text6);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
        datas.addAll(datas);
    }

    @Override
    public int getChildCount() {
        return datas.size();
    }

    @Override
    public int getChildItemViewType(int pos) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new ChildViewHolder(View.inflate(parent.getContext(), R.layout.layout_list_item_child, null));
    }

    @Override
    public void onBindChildViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ChildViewHolder){
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append(text);
            int startPos = 0;
            int endPos = text.length();
            spannableStringBuilder.setSpan(new CusClickSpan(startPos, endPos, position), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            startPos += text.length();
            endPos += text2.length();
            spannableStringBuilder.append(text2);
            spannableStringBuilder.setSpan(new CusClickSpan(startPos, endPos, position), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.LTGRAY), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            startPos += text2.length();
            endPos += text3.length();
            spannableStringBuilder.append(text3);
            spannableStringBuilder.setSpan(new CusClickSpan(startPos, endPos, position), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.GREEN), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            startPos += text3.length();
            endPos += text4.length();
            spannableStringBuilder.append(text4);
            spannableStringBuilder.setSpan(new CusClickSpan(startPos, endPos, position), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.MAGENTA), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            startPos += text4.length();
            endPos += text5.length();
            spannableStringBuilder.append(text5);
            spannableStringBuilder.setSpan(new CusClickSpan(startPos, endPos, position), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((ChildViewHolder) holder).tvChild.setText(spannableStringBuilder);
        }
    }

    public static class CusClickSpan extends ClickableSpan{

        private static final String TAG = CusClickSpan.class.getSimpleName();

        private int startPos;

        private int endPos;

        private int index;

        public CusClickSpan(int startPos, int endPos, int index) {
            this.startPos = startPos;
            this.endPos = endPos;
            this.index = index;
        }

        @Override
        public void onClick(View view) {
            Log.e(TAG, "onClick: " + "startPos = [" + startPos + "], endPos = [" + endPos + "], index = [" + index + "]");
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder{

        public TextView tvChild;

        public TextView tvDelete;

        public ChildViewHolder(View itemView) {
            super(itemView);
            tvChild = (TextView) itemView.findViewById(R.id.tv_child);
            tvDelete = (TextView) itemView.findViewById(R.id.tv_delete);
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("onClick()", " called with: " + "v = [" + v + "]");
                }
            });
            tvChild.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

}
