package com.example.xiaxiao.xxflowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    XXFlowLayout xxFlowLayout;
    ScrollView scrollView;
    int size=20;
    String msg = "春花秋月何时了，往事知多少。小楼昨夜又东风，";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.et);
        xxFlowLayout = (XXFlowLayout) findViewById(R.id.fl);
        scrollView = (ScrollView) findViewById(R.id.sc);
    }

    int index=0;
    public void addView(View view) {
       size=23;
        add();
    }
    public void addBigView(View view) {
        size=53;
        add();
    }
    public void addMiddleView(View view) {
        size=33;
        add();
    }

    public void add() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin=4;
        params.rightMargin=2;
        params.topMargin =6;
        String s = editText.getText().toString().trim();
        final TextView t = new TextView(this);
        t.setLayoutParams(params);
        int a = (int)(Math.random() * 6);
//        t.setText(msg.substring(0,a)+index);
        t.setText(" hello "+index);
        index++;
        t.setTextSize(size);
        t.setBackgroundResource(R.drawable.bg);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xxFlowLayout.removeView(t);
            }
        });
        xxFlowLayout.addView(t);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }
}
