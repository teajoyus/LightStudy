package com.example.lightstudy;

import android.Manifest;
import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
//    int[] lightStyles = new int[]{R.drawable.top_light_1, R.drawable.top_light_2, R.drawable.top_light_3, R.drawable.top_light_4, R.drawable.top_light_5, R.drawable.top_light_6, R.drawable.top_light_7, R.drawable.bottom_light_1, R.drawable.bottom_light_2, R.drawable.bottom_light_3, R.drawable.bottom_light_4, R.drawable.bottom_light_5, R.drawable.bottom_light_6, R.drawable.bottom_light_7, R.drawable.bottom_light_8};
    int[][] lightStyles;
    String[][] lightStylesName;
    //    ImageView imageView;
    LightView imageView;
    TintDrawable tintDrawable;
    SaleProgressView spv;

    MySqliteOpenHelper<TimeStudy> helper;

    int studyStatus;//1是开始学习 2是学习完成 3是提前学习
    int min, hour, ms;
    TextView timer;
    int duration;//秒为单位
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                subTime();
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
        //灯泡的点击
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果是在学习中，则弹出提示是否终止
                if (studyStatus == 1) {
                    askInterrupt(false);
                    return;
                }
                //弹出时间选择框
                TimePickerDialog dialog = new TimePickerDialog(MainActivity.this);
                dialog.show();
                dialog.setOnSlectedListener(new TimePickerDialog.onSlectedListener() {

                    @Override
                    public void onSelected(int hour, int min) {
                        //在时间选择框里面确定后开始学习
                        startStudy(hour, min);
                    }
                });

            }
        });
        // 获取数据库
        helper = new MySqliteOpenHelper<>(this, TimeStudy.class);
        //加载所有灯的图标
        initLightStyleRes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 一个询问是否终止学习的弹出框
     * @param forceExit
     */
    private void askInterrupt(final boolean forceExit) {
        final ObjectAnimator animator = (ObjectAnimator) imageView.getTag();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("温馨提醒");
        builder.setMessage("您正在学习倒计时中，确定终止吗？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                studyStatus = 3;
                animator.cancel();
                imageView.setTag(null);
                onStudyInterrupt();
                if (forceExit) {
                    finish();
                }

            }
        });
        builder.show();
    }

    /**
     * 开始学习
     * @param hour
     * @param min
     */
    private void startStudy(int hour, int min) {
        studyStatus = 1;
        Log.i(TAG, "startStudy hour:" + hour + ",min:" + min);
        //获取指定的秒数
        duration = hour * 3600 + min * 60;
        int delayed = 2000;
        //先开始2秒的开灯动画
        startLight(delayed, true);
        //等着2秒过完再开始倒计时
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //开始灯泡变暗倒计时
                startLight(duration*1000, false);
//                startLight(5000, false);
                showToast("您已开始学习，学习时长为:" + (duration / 60) + "分钟");
                //开始倒计时时钟的时间
                startCalcTime();


                //往数据库插入一条开始学习的记录
                TimeStudy timeStudy = new TimeStudy();
                timeStudy.startTime = System.currentTimeMillis() + "";
                timeStudy.status = 1;
                timeStudy.mark = "开始学习";
                helper.insert(timeStudy);

            }
        }, delayed + 100);
    }

    /**
     * 当终止学习的时候
     */
    private void onStudyInterrupt() {
        //重置时间
        resetClock();
        spv.setTotalAndCurrentCount(1, 0);
        showToast("您已经终止学习，没有采集到灯泡");
        handler.removeMessages(1);


        //往数据库插入一条提前退出的记录
        TimeStudy timeStudy = new TimeStudy();
        timeStudy.startTime = System.currentTimeMillis() + "";
        timeStudy.status = 3;
        timeStudy.mark = "提前退出";
        helper.insert(timeStudy);
        studyStatus = 0;
    }

    /**
     * 当学习完成的时候
     */
    private void onStudySuccessed() {
        //重置时间
        resetClock();
        //往数据库插入一条学习完成的记录
        TimeStudy timeStudy = new TimeStudy();
        timeStudy.startTime = startTime + "";
        timeStudy.endTime = System.currentTimeMillis() + "";
        timeStudy.status = 2;
        timeStudy.mark = "学习完成";
        Random random = new Random();
        int layout = random.nextInt(lightStyles.length);
        int index = random.nextInt(lightStyles[layout].length);
        timeStudy.lightStyleName = lightStylesName[layout][index];
        timeStudy.lightLayout = layout + 1;
        helper.insert(timeStudy);

        //弹出收集到灯泡的弹出框
        GetLightDialog dialog = new GetLightDialog(this, timeStudy,lightStyles[layout][index]);
        dialog.show();
        studyStatus = 0;
    }

    long startTime, endTime;
    int current;

    /**
     * 开始灯泡的变暗效果
     * @param duration
     * @param reversed
     */
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
                    spv.setTotalAndCurrentCount((int) (duration * 1.0f / 1000 + 0.5f), (int) ((nowTime - startTime) * 1.0f / 1000 + 0.5f));
                }
                imageView.invalidate();
            }
        };
        ObjectAnimator animator = ObjectAnimator.ofObject(tintDrawable, "tint", new ArgbEvaluator(), 0xffffffff, 0xFFAEAEAE);
        if (reversed) {
            animator = ObjectAnimator.ofObject(tintDrawable, "tint", new ArgbEvaluator(), 0xFFAEAEAE, 0xffffffff);
        }
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator() {
            //把cos函数改为sin函数 这样是先减速后加速
            public float getInterpolation(float input) {
                return (float) (Math.sin((input + 1) * Math.PI) / 2.0f) + 0.5f;
            }
        });
        final ObjectAnimator finalObjectAnimator = animator;
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (!reversed) {
                    imageView.setTag(finalObjectAnimator);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!reversed&&studyStatus!=3) {
                    studyStatus = 2;
                    onStudySuccessed();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                studyStatus = 3;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        startTime = System.currentTimeMillis();
        Log.i(TAG, "update startTime:" + startTime);
    }

    @Override
    public void onBackPressed() {
        //如果正在学习中的话，那么按下返回键会询问是否终止
        if (studyStatus == 1) {
            askInterrupt(true);
            return;
        }
        super.onBackPressed();

    }

    private void startCalcTime() {
        initClock();
        subTime();
    }

    private synchronized void subTime() {
        //如果秒已经到达59，则进1
//        if (ms == 0) {
//            //如果分钟已经到达59，则进1
//            if (min == 59) {
//                hour++;
//                min = 0;
//                ms = 0;
//            } else {
//                min++;
//                ms = 0;
//            }
//
//        } else {
//            ms++;
//        }

        if (ms == 0) {
            if (hour == 0 && min == 0) {

            } else {
                ms = 59;
                //如果分钟已经到达59，则进1
                if (min == 0) {
                    hour--;
                    min = 59;
                } else {
                    min--;
                }
            }
        } else {
            ms--;
        }
        timer.setText(formatTime(hour, min, ms));
        handler.sendEmptyMessageDelayed(1, 1000);
    }

    private String formatTime(int hour, int min, int ms) {
        String s = "";
        if (hour < 10) {
            s = "0" + hour;
        } else {
            s += hour;
        }
        s += ":";
        if (min < 10) {
            s += "0" + min;
        } else {
            s += min;
        }
        s += ":";
        if (ms < 10) {
            s += "0" + ms;
        } else {
            s += ms;
        }
        Log.i("123", s);
        return s;
    }

    private void resetClock() {
        handler.removeCallbacksAndMessages(null);
        hour = 0;
        min = 0;
        ms = 0;
        timer.setText("00:00:00");
//        timer.setText(hour+":"+min+":"+ms);

    }

    private void initClock() {
        hour = duration / 3600;
        min = duration % 3600 / 60;
        ms = duration % 60;
//        timer.setText("00:00:00");
        timer.setText(hour + ":" + min + ":" + ms);

    }

    private void showToast(String option) {
        Toast.makeText(this, option, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取所有时钟数字图片资源
     */
    private void initLightStyleRes() {
        String[] strs = new String[]{"top_light", "bottom_light"};
        lightStyles = new int[strs.length][];
        lightStylesName = new String[strs.length][];
        Resources res = getResources();
        int size = 8;
        lightStyles[0] = new int[size];
        lightStylesName[0] = new String[size];
        for (int i = 0; i < size; i++) {
            lightStylesName[0][i] =strs[0] + "_" + i;
            int drawable = res.getIdentifier(lightStylesName[0][i] , "drawable", getPackageName());
            lightStyles[0][i] = drawable;
        }

         size = 7;
        lightStyles[1] = new int[size];
        lightStylesName[1] = new String[size];
        for (int i = 0; i < size; i++) {
            lightStylesName[1][i] =strs[1] + "_" + i;
            int drawable = res.getIdentifier(lightStylesName[1][i] , "drawable", getPackageName());
            lightStyles[1][i] = drawable;
        }


    }

}
