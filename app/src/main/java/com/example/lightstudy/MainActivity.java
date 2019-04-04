package com.example.lightstudy;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //    ImageView imageView;
    LightView imageView;
    TintDrawable tintDrawable;
    SaleProgressView spv;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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
        imageView = findViewById(R.id.iv1);
        spv = findViewById(R.id.spv);
        Drawable drawable = getResources().getDrawable(R.drawable.light_un);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(0x80000000);
        }
        imageView.setBlueColor(0);
        imageView.setImageDrawable(drawable);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLight(3000, true);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startLight(18000, false);
                    }
                }, 3100);

            }
        });
//        tintDrawable = new TintDrawable(drawable){
//            @Override
//            public void update(int tint) {
//                super.update(tint);
////                Log.i(TAG,"update tint:"+tint);
////                imageView.setImageDrawable(getDrawable());
//                imageView.setBlueColor(tint);
//                imageView.invalidate();
//            }
//        };
//        ObjectAnimator  animator = ObjectAnimator.ofObject(tintDrawable, "tint",new ArgbEvaluator(),0xffffffff,0x80000000);
//        animator.setDuration(10000);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
//        animator.start();
//        startLight(10000);
    }

    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        imageView = findViewById(R.id.iv1);
//        Drawable light_un = getResources().getDrawable(R.drawable.light_un);
//        light_un.setTint(0xcf000000);
//        imageView.setImageDrawable(light_un);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startLight();
//            }
//        });
//    }
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
            spv.setTotalAndCurrentCount(duration/1000,current);
        }
        tintDrawable = new TintDrawable(light_in) {
            @Override
            public void update(int tint) {
                super.update(tint);
//                Log.i(TAG,"update tint:"+tint);
//                imageView.setImageDrawable(getDrawable());
                long nowTime = SystemClock.elapsedRealtime();
                float yu = (duration - (nowTime - startTime)) * 1.0f / duration;
//                Log.i(TAG,"update yu:"+yu);
//                Log.i(TAG,"update startTime:"+startTime);
//                Log.i(TAG,"update nowTime:"+nowTime);
                if (yu > 0 && ((yu < 0.6f && !reversed) || (yu < 0.4f && reversed))) {
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
                spv.setTotalAndCurrentCount(duration/1000, (int) ((nowTime - startTime)/1000));
                imageView.invalidate();
            }
        };
        ObjectAnimator animator = ObjectAnimator.ofObject(tintDrawable, "tint", new ArgbEvaluator(), 0xffffffff, 0xC0000000);
        if (reversed) {
            animator = ObjectAnimator.ofObject(tintDrawable, "tint", new ArgbEvaluator(), 0x80000000, 0xffffffff);
        }
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        startTime = SystemClock.elapsedRealtime();
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
}