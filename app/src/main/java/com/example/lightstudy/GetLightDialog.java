package com.example.lightstudy;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

public class GetLightDialog extends Dialog {
    private static final String TAG = "GetLightDialog";
    TimeStudy timeStudy;
    int lightStyle;
    public GetLightDialog(Context context,TimeStudy timeStudy,int lightStyle) {
        super(context);
        this.timeStudy = timeStudy;
        this.lightStyle = lightStyle;
    }

    public GetLightDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected GetLightDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_light_layout);
        ImageView iv = findViewById(R.id.iv);
        iv.setImageResource(lightStyle);
        findViewById(R.id.collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "成功采集到灯泡，请再接再厉！", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });


    }


    @Override
    public void show() {
        super.show();
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = Utils.dp2px(getContext(), 200);
        params.height = Utils.dp2px(getContext(), 270);
        getWindow().setAttributes(params);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


}
