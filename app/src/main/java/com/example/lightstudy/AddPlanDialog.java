package com.example.lightstudy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class AddPlanDialog extends Dialog implements NumberPicker.OnValueChangeListener, NumberPicker.Formatter,NumberPicker.OnScrollListener {
    private static final String TAG = "TimePickerDialog";
    NumberPicker hourPicker,minutePicker;
    EditText et;
    int hour,min;
    onSlectedListener onSlectedListener;
    public AddPlanDialog(Context context) {
        super(context);
    }

    public AddPlanDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected AddPlanDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_plan_layout);
        hourPicker=(NumberPicker) findViewById(R.id.hourpicker);
        minutePicker=(NumberPicker) findViewById(R.id.minuteicker);
        et= findViewById(R.id.et);
        init();
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et.getText().toString();
                if(TextUtils.isEmpty(content)){
                    Toast.makeText(getContext(),"请输入学习内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
                if(onSlectedListener!=null){
                    Log.i(TAG,"hour:"+hour+",min:"+min);
                    onSlectedListener.onSelected(hour,min,content);
                }
            }
        });

    }

    public void setOnSlectedListener(onSlectedListener onSlectedListener) {
        this.onSlectedListener = onSlectedListener;
    }

    @Override
    public void show() {
        super.show();
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = Utils.dp2px(getContext(),240);
        params.height = Utils.dp2px(getContext(),350);
        getWindow().setAttributes(params);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void init() {
        hourPicker.setFormatter(this);
        hourPicker.setOnValueChangedListener(this);
        hourPicker.setOnScrollListener(this);
        hourPicker.setMaxValue(10);
        hourPicker.setMinValue(0);
        hour = 0;
        hourPicker.setValue(hour);
        minutePicker.setFormatter(this);
        minutePicker.setOnValueChangedListener(this);
        minutePicker.setOnScrollListener(this);
        minutePicker.setMaxValue(60);
        minutePicker.setMinValue(1);
        min = 2;
        minutePicker.setValue(min);
    }
    public String format(int value) {
        String tmpStr = String.valueOf(value);
        Log.i(TAG,"format tmpStr:"+tmpStr);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//        Toast.makeText(getContext(),"原来的值 " + oldVal + "--新值: ",Toast.LENGTH_SHORT).show();
        Log.i(TAG,"onValueChange newVal:"+newVal+",picker:"+picker);
        if(picker==hourPicker){
            hour = newVal;
        }else{
            min = newVal;
        }
    }

    public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch (scrollState) {
            case NumberPicker.OnScrollListener.SCROLL_STATE_FLING:
                break;
            case NumberPicker.OnScrollListener.SCROLL_STATE_IDLE:
                break;
            case NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
        }
    }
    public interface onSlectedListener{
        void onSelected(int hor,int min,String content);
    }
}
