package com.example.lightstudy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.NumberPicker;

public class TimePickerDialog extends Dialog implements NumberPicker.OnValueChangeListener, NumberPicker.Formatter,NumberPicker.OnScrollListener {
    NumberPicker hourPicker,minutePicker;
    int hour,min;
    onSlectedListener onSlectedListener;
    public TimePickerDialog(Context context) {
        super(context);
    }

    public TimePickerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TimePickerDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_picker_layout);
        hourPicker=(NumberPicker) findViewById(R.id.hourpicker);
        minutePicker=(NumberPicker) findViewById(R.id.minuteicker);
        init();
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(onSlectedListener!=null){
                    onSlectedListener.onSelected(hour,min);
                }
            }
        });

    }

    public void setOnSlectedListener(TimePickerDialog.onSlectedListener onSlectedListener) {
        this.onSlectedListener = onSlectedListener;
    }

    @Override
    public void show() {
        super.show();
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = Utils.dp2px(getContext(),200);
        params.height = Utils.dp2px(getContext(),290);
        getWindow().setAttributes(params);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void init() {
        hourPicker.setFormatter(this);
        hourPicker.setOnValueChangedListener(this);
        hourPicker.setOnScrollListener(this);
        hourPicker.setMaxValue(8);
        hourPicker.setMinValue(0);
        hour = 1;
        hourPicker.setValue(hour);

        minutePicker.setFormatter(this);
        minutePicker.setOnValueChangedListener(this);
        minutePicker.setOnScrollListener(this);
        minutePicker.setMaxValue(60);
        minutePicker.setMinValue(1);
        min = 5;
        minutePicker.setValue(min);
    }
    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//        Toast.makeText(getContext(),"原来的值 " + oldVal + "--新值: ",Toast.LENGTH_SHORT).show();
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
        void onSelected(int hor,int min);
    }
}
