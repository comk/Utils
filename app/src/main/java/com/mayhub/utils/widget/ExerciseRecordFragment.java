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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mayhub.utils.R;
import com.mayhub.utils.adapter.LoadMoreRecyclerAdapter;


/**
 * Created by comkdai on 2017/5/4.
 */
public class ExerciseRecordFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{

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
        addFootView();
        recyclerView.setAdapter(adapter = new ExerciseRecordAdapter());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx != 0 || dy != 0) {
                    if (tvLoadMore.getVisibility() != View.VISIBLE && llm.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
//                        int loadingPageNum = adapter.getAudioBeanCount() - localTmpDatas.size();
//                        if (loadedPageNum.get(loadingPageNum, -1) == -1 && !swipeRefreshLayout.isRefreshing()) {//未加载过的页码 进行加载
//                            loadData(loadingPageNum, false);
//                        }
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
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClick(View v) {

    }

    private static class ExerciseRecordAdapter extends LoadMoreRecyclerAdapter{

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        public int getChildItemViewType(int pos) {
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindChildViewHolder(RecyclerView.ViewHolder holder, int position) {

        }
    }

    private static class ExerciseHolder extends RecyclerView.ViewHolder{

        private TextView tvName;

        private TextView tvTime;

        private TextView info;

        public ExerciseHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            info = (TextView) itemView.findViewById(R.id.tv_info);
        }
    }

}
