package com.mayhub.utils.widget;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mayhub.utils.R;
import com.mayhub.utils.adapter.LoadMoreRecyclerAdapter;

import java.util.ArrayList;


/**
 * Created by comkdai on 2017/5/4.
 */
public class ExerciseRecordFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{

    static class RecordBean{

        public RecordBean() {
        }

        public RecordBean(String timeOccur, String timeCost, String name, String info) {
            this.timeOccur = timeOccur;
            this.timeCost = timeCost;
            this.name = name;
            this.info = info;
        }

        String timeOccur;
        String timeCost;
        String name;
        String info;
    }

    private RecyclerView recyclerView;

    private ProgressBar progressBarLoading;

    private TextView tvLoadMore;

    private ExerciseRecordAdapter adapter;

    private LinearLayoutManager llm;

    private SparseIntArray loadedPageNum = new SparseIntArray();

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_exercise_record, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(llm = new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter = new ExerciseRecordAdapter());
        addFootView();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx != 0 || dy != 0) {
                    if (tvLoadMore.getVisibility() != View.VISIBLE && llm.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
                        int loadingPageNum = adapter.getChildCount() / 10 + 1;
                        if (loadedPageNum.get(loadingPageNum, -1) == -1 && !swipeRefreshLayout.isRefreshing()) {//未加载过的页码 进行加载
                            loadData(loadingPageNum, false);
                        }
                    }
                }
            }
        });
    }

    private void addFootView() {
        View bottomView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_load_record_more, recyclerView, false);
        progressBarLoading = (ProgressBar) bottomView.findViewById(R.id.pb_loading);
        tvLoadMore = (TextView) bottomView.findViewById(R.id.tv_load_more);
        tvLoadMore.setOnClickListener(this);
        adapter.addFootView(bottomView);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            reloadData();
        }
    }

    private void loadData(int pageNum, boolean isRefresh){

    }

    private void reloadData(){
        ArrayList<RecordBean> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new RecordBean("05.04", "56min48s", "test " + i, "this is a long story about how i was born and how do I change the world." + i));
        }
        adapter.resetData(list);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClick(View v) {

    }

    private static class ExerciseRecordAdapter extends LoadMoreRecyclerAdapter{

        ArrayList<RecordBean> data = new ArrayList<>();

        public ExerciseRecordAdapter() {
        }

        public ExerciseRecordAdapter(ArrayList<RecordBean> data) {
            this.data = data;
        }

        public void resetData(ArrayList<RecordBean> data){
            if(data != null){
                if(this.data != data){
                    this.data.clear();
                    this.data.addAll(data);
                }
                notifyDataSetChanged();
            }
        }

        public void appendData(ArrayList<RecordBean> data){
            if(data != null){
                this.data.addAll(data);
                notifyDataSetChanged();
            }
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
            return new ExerciseHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_time_line_item, parent, false));
        }

        @Override
        public void onBindChildViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof ExerciseHolder){
                ExerciseHolder exerciseHolder = (ExerciseHolder) holder;
                RecordBean recordBean = data.get(position);
                exerciseHolder.tvTimeCost.setText(recordBean.timeCost);
                exerciseHolder.tvTimeOccur.setText(recordBean.timeOccur);
                exerciseHolder.tvName.setText(recordBean.name);
                exerciseHolder.info.setText(recordBean.info);
            }
        }
    }

    private static class ExerciseHolder extends RecyclerView.ViewHolder{

        private TextView tvName;

        private TextView tvTimeCost;

        private TextView tvTimeOccur;

        private TextView info;

        private ImageView ivTime;

        public ExerciseHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTimeCost = (TextView) itemView.findViewById(R.id.tv_time);
            tvTimeOccur = (TextView) itemView.findViewById(R.id.tv_time_line);
            ivTime = (ImageView) itemView.findViewById(R.id.iv_time_line);
            info = (TextView) itemView.findViewById(R.id.tv_info);
        }

    }

}
