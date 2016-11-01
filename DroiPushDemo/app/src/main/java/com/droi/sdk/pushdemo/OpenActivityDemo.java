package com.droi.sdk.pushdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class OpenActivityDemo extends Activity {
    private TextView mTextBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_open_activity_layout);
        mTextBoard = (TextView) findViewById(R.id.textboard);
    }
}