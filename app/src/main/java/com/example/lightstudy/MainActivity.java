package com.example.lightstudy;

import android.Manifest;
import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //    ImageView imageView;
    LightView imageView;
    TintDrawable tintDrawable;
    SaleProgressView spv;


    int min, hour, ms;
    TextView timer;
    int duration;//秒为单位
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                addTimer();
            }
        }
    };


    public boolean myPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.CAMERA}, 100
            );
            return false;
        }
        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
//        Log.i(TAG, "onCreate b:" + b);
//        startActivity(new Intent(this, RecordActivity.class));
        timer = findViewById(R.id.time);
        imageView = findViewById(R.id.iv1);
        spv = findViewById(R.id.spv);
        Drawable drawable = getResources().getDrawable(R.drawable.light_un);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(0xFFAEAEAE);
        }
        imageView.setBlueColor(0);
        imageView.setImageDrawable(drawable);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag()!=null){
                    final ObjectAnimator animator = (ObjectAnimator) imageView.getTag();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("温馨提醒");
                    builder.setMessage("您正在学习倒计时中，确定终止吗？");
                    builder.setNegativeButton("取消",null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            animator.cancel();
                            imageView.setTag(null);
                            onStudyInterrupt();
                        }
                    });
                    builder.show();
                    return;
                }
                TimePickerDialog dialog = new TimePickerDialog(MainActivity.this);
                dialog.show();
                dialog.setOnSlectedListener(new TimePickerDialog.onSlectedListener() {
                    @Override
                    public void onSelected(int hor, int min) {
                        startStudy(hour,min);
                    }
                });

            }
        });
    }
    private void startStudy(int hour, int min){
        duration = hour * 3600 + min*60;
        int delayed = 2000;
        startLight(delayed, true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startLight(duration*1000, false);
                showToast("您已开始学习，学习时长为:"+(duration/60)+"分钟");
                startCalcTime();
            }
        }, delayed + 100);
    }
    private void onStudyInterrupt() {
        showToast("您已经终止学习，没有采集到灯泡");
        handler.removeMessages(1);
        reset();
    }
    private void onStudySuccessed() {
    }

    long startTime, endTime;
    int current;

    private void startLight(final int duration, final boolean reversed) {
        final Drawable light_un = getResources().getDrawable(R.drawable.light_un);
        final Drawable light_in = getResources().getDrawable(R.drawable.light_in);
        if (reversed) {
            imageView.setImageDrawable(light_un);
        } else {
            imageView.setImageDrawable(light_in);
            current = 0;
            //分钟
            spv.setTotalAndCurrentCount(duration / (1000 * 60), current);
        }
        tintDrawable = new TintDrawable(light_in) {
            @Override
            public void update(int tint) {
                super.update(tint);
//                Log.i(TAG,"update tint:"+tint);
//                imageView.setImageDrawable(getDrawable());
                long nowTime = System.currentTimeMillis();
                float yu = (duration - (nowTime - startTime)) * 1.0f / duration;
//                Log.i(TAG,"update yu:"+yu);
//                Log.i(TAG,"update startTime:"+startTime);
//                Log.i(TAG,"update nowTime:"+nowTime);
                if (yu > 0 && ((yu < 0.2f && !reversed) || (yu < 0.8f && reversed))) {
                    Drawable d = null;
                    if (reversed) {
                        d = light_in;
                    } else {
                        d = light_un;
                    }
                    if (imageView.getDrawable() != d) {
                        imageView.setBlueColor(0);
                        setDrawable(d);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            d.setTint(tint);
                        }
                        imageView.setImageDrawable(getDrawable());
                    }

                } else if (yu > 0) {
                    imageView.setBlueColor(tint);
                }
                if (!reversed) {
                    //分钟
                    spv.setTotalAndCurrentCount((int)(duration *1.0f/ (1000 * 60)+0.5f), (int) ((nowTime - startTime) *1.0f/ (1000 * 60)));
                }
                imageView.invalidate();
            }
        };
        ObjectAnimator animator = ObjectAnimator.ofObject(tintDrawable, "tint", new ArgbEvaluator(), 0xffffffff, 0xFFAEAEAE);
        if (reversed) {
            animator = ObjectAnimator.ofObject(tintDrawable, "tint", new ArgbEvaluator(), 0xFFAEAEAE, 0xffffffff);
        }
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator(){
            //把cos函数改为sin函数 这样是先减速后加速
            public float getInterpolation(float input) {
                return (float)(Math.sin((input + 1) * Math.PI) / 2.0f) + 0.5f;
            }
        });
        final ObjectAnimator finalObjectAnimator  = animator;
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(!reversed){
                    imageView.setTag(finalObjectAnimator);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!reversed){
                    imageView.setTag(null);
                    onStudySuccessed();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        startTime = System.currentTimeMillis();
        Log.i(TAG, "update startTime:" + startTime);
    }


    /**
     * 改变App当前Window亮度
     *
     * @param brightness
     */
    public void changeAppBrightness(int brightness) {
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    private void startCalcTime(){
        reset();
        addTimer();
    }
    private synchronized  void addTimer(){
        //如果秒已经到达59，则进1
        if (ms == 59) {
            //如果分钟已经到达59，则进1
            if (min == 59) {
                hour++;
                min = 0;
                ms = 0;
            } else {
                min++;
                ms = 0;
            }

        } else {
            ms++;
        }
        timer.setText(formatTime(hour, min,ms));
        handler.sendEmptyMessageDelayed(1,1000);
    }
    private String formatTime(int hour,int min,int ms){
        String s = "";
        if(hour<10){
            s = "0"+hour;
        }else{
            s+=hour;
        }
        s+=":";
        if(min<10){
            s += "0"+min;
        }else{
            s+=min;
        }
        s+=":";
        if(ms<10){
            s += "0"+ms;
        }else{
            s+=ms;
        }
        Log.i("123",s);
        return s;
    }
    private  void reset(){
        hour = 0;
        min = 0;
        ms = 0;
//        timer.setText("00:00:00");
        timer.setText("00:00:00");

    }

    private void showToast(String option) {
        Toast.makeText(this,option,Toast.LENGTH_SHORT).show();
    }


}
