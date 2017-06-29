package com.droi.sdk.pushdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.push.DroiPush;
import com.droi.sdk.push.utils.GetDeviceIdCallback;

public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
    private Context mContext = MainActivity.this;
    private TextView mAppIdTextView;
    private TextView mChannelTextView;
    private TextView mClientIdTextView;

    private CheckBox mPushSwitch;
    private TextView mPushState;

    private TextView mSilentViewer;
    private Button mSetSilenceTimeBtn;
    private Button mClearSilenceTimeBtn;

    private TextView mTagsViewer;
    private Button mAppendTag;
    private Button mReplaceTag;
    private Button mDeleteTag;
    private Button mQueryTag;

    private GetDeviceIdCallback mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_main_layout);
        mAppIdTextView = (TextView) findViewById(R.id.appid);
        mChannelTextView = (TextView) findViewById(R.id.channel);
        mClientIdTextView = (TextView) findViewById(R.id.clientid);

        mPushSwitch = (CheckBox) findViewById(R.id.push_switch);
        mPushState = (TextView) findViewById(R.id.push_state);

        mTagsViewer = (TextView) findViewById(R.id.tags_viewer);
        mAppendTag = (Button) findViewById(R.id.append_tag);
        mDeleteTag = (Button) findViewById(R.id.del_tag);
        mReplaceTag = (Button) findViewById(R.id.replace_tag);
        mQueryTag = (Button) findViewById(R.id.query_tag);
        mSilentViewer = (TextView) findViewById(R.id.silent_viewer);
        mSetSilenceTimeBtn = (Button) findViewById(R.id.set_silent_time_btn);
        mClearSilenceTimeBtn = (Button) findViewById(R.id.clear_silent_time_btn);

        mAppendTag.setOnClickListener(this);
        mDeleteTag.setOnClickListener(this);
        mReplaceTag.setOnClickListener(this);
        mQueryTag.setOnClickListener(this);
        mSetSilenceTimeBtn.setOnClickListener(this);
        mClearSilenceTimeBtn.setOnClickListener(this);
        mPushSwitch.setOnCheckedChangeListener(this);
    }

    private void updateView() {
        mAppIdTextView.setText(DroiPush.getAppId(mContext));
        mChannelTextView.setText(DroiPush.getChannel(mContext));
        DroiPush.getDeviceIdInBackground(mContext, new GetDeviceIdCallback() {

            @Override
            public void onGetDeviceId(String s) {
                mClientIdTextView.setText(s);
            }
        });
        updatePushState();
        showSilentTime();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        updateView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        AlertDialog.Builder alertBuilder;
        switch (v.getId()) {
            case R.id.set_silent_time_btn:
                final View view = LayoutInflater.from(this).inflate(R.layout.demo_silent_setting_layout, null);
                final TimePicker start = (TimePicker) view.findViewById(R.id.time_start);
                final TimePicker finish = (TimePicker) view.findViewById(R.id.time_end);
                start.setIs24HourView(true);
                finish.setIs24HourView(true);

                alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle(R.string.text_silent_setting_title)
                        .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int startHour = start.getCurrentHour();
                            int startMin = start.getCurrentMinute();
                            int endHour = finish.getCurrentHour();
                            int endMin = finish.getCurrentMinute();
                            DroiPush.setSilentTime(getApplicationContext(), startHour, startMin, endHour,
                                    endMin);
                            showSilentTime();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                    }
                }).setView(view);
                alertBuilder.create().show();
                break;

            case R.id.clear_silent_time_btn:
                DroiPush.clearSilentTime(getApplicationContext());
                updateView();
                break;

            case R.id.append_tag:
                final View addTagView = LayoutInflater.from(this).inflate(R.layout.demo_tag_setting_layout, null);
                alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle(R.string.text_append_tag)
                        .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            EditText input = (EditText) addTagView.findViewById(R.id.input_tag);
                            String tag = input.getEditableText().toString();
                            if (tag != null && tag.length() != 0) {
                                mTagsViewer.setText("");
                                DroiPush.appendTagsInBackground(getApplicationContext(), tag, new DroiCallback<String>() {
                                    @Override
                                    public void result(final String result, DroiError droiError) {
                                        String resultStr = "";
                                        if (droiError.isOk()) {
                                            resultStr = "操作成功";
                                        } else {
                                            resultStr = droiError.getAppendedMessage();
                                        }
                                        final String finalResultStr = resultStr;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mTagsViewer.setText(finalResultStr);
                                            }
                                        });
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }).setView(addTagView);
                alertBuilder.create().show();
                break;

            case R.id.del_tag:
                final View delTagView = LayoutInflater.from(this).inflate(R.layout.demo_tag_setting_layout, null);

                alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle(R.string.text_delete_tag)
                        .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            EditText input = (EditText) delTagView.findViewById(R.id.input_tag);
                            String tag = input.getEditableText().toString();
                            if (tag != null && tag.length() != 0) {
                                DroiPush.deleteTagsInBackground(getApplicationContext(), tag, new DroiCallback<String>() {
                                    @Override
                                    public void result(final String result, DroiError droiError) {
                                        String resultStr = "";
                                        if (droiError.isOk()) {
                                            resultStr = "操作成功";
                                        } else {
                                            resultStr = droiError.getAppendedMessage();
                                        }
                                        final String finalResultStr = resultStr;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mTagsViewer.setText(finalResultStr);
                                            }
                                        });
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }).setView(delTagView);
                alertBuilder.create().show();
                break;


            case R.id.replace_tag:
                final View replaceTagView = LayoutInflater.from(this).inflate(R.layout.demo_tag_setting_layout, null);
                alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle(R.string.text_replace_tag)
                        .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            EditText input = (EditText) replaceTagView.findViewById(R.id.input_tag);
                            String tag = input.getEditableText().toString();

                            if (tag != null && tag.length() != 0) {
                                DroiPush.replaceTagsInBackground(getApplicationContext(), tag, new DroiCallback<String>() {
                                    @Override
                                    public void result(final String result, DroiError droiError) {
                                        String resultStr = "";
                                        if (droiError.isOk()) {
                                            resultStr = "操作成功";
                                        } else {
                                            resultStr = droiError.getAppendedMessage();
                                        }

                                        final String finalResultStr = resultStr;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mTagsViewer.setText(finalResultStr);
                                            }
                                        });
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }).setView(replaceTagView);
                alertBuilder.create().show();
                break;

            case R.id.query_tag:
                DroiPush.queryTagsInBackground(getApplicationContext(), new DroiCallback<String>() {
                    @Override
                    public void result(final String result, DroiError droiError) {
                        String resultStr = "";
                        if (droiError.isOk()) {
                            resultStr = result;
                        } else {
                            resultStr = droiError.getAppendedMessage();
                        }
                        final String finalResultStr = resultStr;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTagsViewer.setText(finalResultStr);
                            }
                        });
                    }
                });
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        if (buttonView == mPushSwitch) {
            DroiPush.setPushableState(MainActivity.this, isChecked);
            updatePushState();
        }
    }

    private void showSilentTime() {
        int[] time = DroiPush.getSilentTime(MainActivity.this);
        if (time != null && time.length == 4) {
            String sh = String.format("%02d", time[0]);
            String sm = String.format("%02d", time[1]);
            String eh = String.format("%02d", time[2]);
            String em = String.format("%02d", time[3]);
            String silentTime = sh + ":" + sm + "-" + eh + ":" + em;
            mSilentViewer.setText(silentTime);
        } else {
            mSilentViewer.setText("");
        }
    }

    private void updatePushState() {
        boolean isEnabled = DroiPush.getPushableState(MainActivity.this);
        String pushState = null;
        if (isEnabled) {
            pushState = getString(R.string.text_push_state_hint, getString(R.string.text_push_state_enable));
        } else {
            pushState = getString(R.string.text_push_state_hint, getString(R.string.text_push_state_disable));
        }
        mPushState.setText(pushState);
        mPushSwitch.setChecked(isEnabled);
    }
}
