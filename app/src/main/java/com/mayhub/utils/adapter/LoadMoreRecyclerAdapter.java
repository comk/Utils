package com.mayhub.utils.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/11.
 */
public abstract class LoadMoreRecyclerAdapter extends RecyclerView.Adapter {

    public static final int ITEM_TYPE_HEAD = 0x998;
    public static final int ITEM_TYPE_FOOT = 0x999;

    private ArrayList<View> headViews = new ArrayList<>();
    private ArrayList<View> footViews = new ArrayList<>();

    public void addHeadView(View headView){
        headViews.add(headView);
        notifyItemInserted(headViews.size() - 1);
    }

    public void addFootView(View footView){
        footViews.add(footView);
        notifyItemInserted(footViews.size() - 1 + headViews.size() + getChildCount());
    }

    public void insertHeadView(View headView, int pos){
        if(pos >= 0 && pos < headViews.size()) {
            headViews.add(pos, headView);
            notifyItemInserted(pos);
        }else{
            throw new IndexOutOfBoundsException("pos is out of head views range");
        }
    }

    public void insertFootView(View footView, int pos){
        if(pos >= 0 && pos < footViews.size()) {
            footViews.add(pos, footView);
            notifyItemInserted(pos + headViews.size() + getChildCount());
        }else{
            throw new IndexOutOfBoundsException("pos is out of foot views range");
        }
    }

    public void removeHeadView(View headView){
        int index = headViews.indexOf(headView);
        if(index >= 0) {
            headViews.remove(headView);
            notifyItemRemoved(index);
        }
    }

    public void removeFootView(View footView){
        int index = footViews.indexOf(footView);
        if(index >= 0) {
            footViews.remove(footView);
            notifyItemRemoved(headViews.size() + getChildCount() + index);
        }
    }

    public void removeHeadView(int posOfHead){
        if(posOfHead >= 0 && posOfHead < headViews.size()){
            headViews.remove(posOfHead);
            notifyItemRemoved(posOfHead);
        }else{
            throw new IndexOutOfBoundsException("posOfHead is out of head views range");
        }
    }

    public void removeFootView(int posOfFoot){
        if(posOfFoot >= 0 && posOfFoot < footViews.size()){
            footViews.remove(posOfFoot);
            notifyItemRemoved(headViews.size() + getChildCount() + posOfFoot);
        }else{
            throw new IndexOutOfBoundsException("posOfFoot is out of head views range");
        }
    }

    public int getHeadViewsCount(){
        return headViews.size();
    }

    public int getFootViewsCount(){
        return footViews.size();
    }

    public abstract int getChildCount();

    public abstract int getChildItemViewType(int pos);

    public abstract RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindChildViewHolder(RecyclerView.ViewHolder holder, int position);

    @Override
    public final int getItemCount() {
        return getChildCount() + headViews.size() + footViews.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if(itemType == ITEM_TYPE_HEAD){
            if(holder instanceof HeadFootViewHolder){
                ((HeadFootViewHolder) holder).setContentView(headViews.get(position));
            }
        }else if(itemType == ITEM_TYPE_FOOT){
            if(holder instanceof HeadFootViewHolder){
                ((HeadFootViewHolder) holder).setContentView(footViews.get(position - headViews.size() - getChildCount()));
            }
        }else{
            onBindChildViewHolder(holder, position - headViews.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position < headViews.size()){
            return ITEM_TYPE_HEAD;
        }else if(position < headViews.size() + getChildCount()){
            return getChildItemViewType(position - headViews.size());
        }else{
            return ITEM_TYPE_FOOT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case ITEM_TYPE_HEAD:
            case ITEM_TYPE_FOOT:
                return new HeadFootViewHolder(new LinearLayout(parent.getContext()));
            default:
                return onCreateChildViewHolder(parent, viewType);
        }
    }

    public static class HeadFootViewHolder extends RecyclerView.ViewHolder{

        public void setContentView(View contentView){
            if(contentView.getParent() != null && contentView.getParent() instanceof ViewGroup){
                ViewGroup viewGroup = (ViewGroup) contentView.getParent();
                viewGroup.removeView(contentView);
            }
            if (itemView instanceof ViewGroup) {
                ((ViewGroup) itemView).removeAllViews();
                ((ViewGroup) itemView).addView(contentView);
            }

        }

        public HeadFootViewHolder(View itemView) {
            super(itemView);
        }
    }

}
