package com.example.xiaxiao.xxflowlayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by xiaxiao on 2017/12/14.
 */

public abstract class FlowAdapter {
    List mList;
    Context mContext;

    public FlowAdapter(Context context, List list) {
        this.mContext = context;
        this.mList = list;
    }
    public int getCount() {
        return mList==null?0:mList.size();
    }

    public Object getItem(int position) {
        return mList==null?null:mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public abstract int getItemLayoutId(int position);

    public abstract Holder createHolder(View view);
    public View getView(int position) {
        View v = View.inflate(mContext, getItemLayoutId(position), null);
        createHolder(v).initView(position);
        return v;
    }




    public abstract class Holder{
        View itemView;

        public Holder(View view) {
            this.itemView = view;
        }
        public abstract void initView(int position);
    }
}
