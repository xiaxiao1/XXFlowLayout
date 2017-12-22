package com.example.xiaxiao.xxflowlayout;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FlowListViewActivity extends AppCompatActivity {

    XXFlowAsListView xxFlowAsListView;
    MyFlowAdapter flowAdapter;
    List<String> datas = new ArrayList<>();
    String msg = "春花秋月何时了，往事知多少。小楼昨夜又东风，故国不堪回首明月中。雕栏玉砌应犹在，只是朱颜改，问君能有几多愁，恰似一江春水向东流。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_list_view);
        xxFlowAsListView = (XXFlowAsListView) findViewById(R.id.flow);
        xxFlowAsListView.setHorizontalUniformed(true);
    }

    public void addList(View view) {
        datas = new ArrayList<>();
        for (int i=0;i<10;i++) {
            int a = (int)(Math.random() * 35);
            int l = (int)(Math.random() * 5+2);
            datas.add(msg.substring(a,a+l)+i);
        }
        flowAdapter = new MyFlowAdapter(this, datas);
        xxFlowAsListView.setAdapter(flowAdapter);

    }
    public void clear(View view) {
//        xxFlowAsListView.deepClear();
        xxFlowAsListView.removeAllViews();
        datas.clear();

    }



    class MyFlowAdapter extends FlowAdapter{

        View mClickView;
        public MyFlowAdapter(Context context, List list) {
            super(context, list);
        }

        @Override
        public int getItemLayoutId(int position) {
            return R.layout.item2;
        }

        @Override
        public Holder createHolder(View view) {
            return new MyHolder(view);
        }

        class MyHolder extends Holder{

            TextView textView;
            public MyHolder(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.tv);
                textView.setBackgroundResource(R.drawable.bg_2);
            }

            @Override
            public void initView(int position) {
                String s =(String) mList.get(position);
                textView.setText(s);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textView.setBackgroundResource(R.drawable.bg);
                        if (mClickView!=null) {
                            mClickView.setBackgroundResource(R.drawable.bg_2);
                        }
                        mClickView = textView;
                    }
                });
            }
        }
    }

}
