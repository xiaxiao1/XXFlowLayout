package com.example.xiaxiao.xxflowlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/14.
 */

public class XXFlowAsListView extends XXFlowLayout {

    FlowAdapter mFlowAdapter;
    public XXFlowAsListView(Context context) {
        super(context);
    }

    public XXFlowAsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XXFlowAsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public XXFlowAsListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setAdapter(FlowAdapter flowAdapter) {
        if (this.mFlowAdapter != null) {
//            this.deepClear();
            this.removeAllViews();
            this.mFlowAdapter=null;

        }
//        else {
            if (flowAdapter!=null) {
                this.mFlowAdapter = flowAdapter;
                fromAdapter();
            }
//        }
    }
    protected void fromAdapter() {
        for (int i=0;i<mFlowAdapter.getCount();i++) {
            this.addView(mFlowAdapter.getView(i));
        }

    }


}
