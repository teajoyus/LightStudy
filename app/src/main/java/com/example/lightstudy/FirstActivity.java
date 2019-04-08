package com.example.lightstudy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import java.util.List;

public class FirstActivity extends AppCompatActivity {
    private static final String TAG = "FirstActivity";
    FloatingActionButton menu, main, light, record, plan;
    boolean isShowing = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        menu = findViewById(R.id.fab);
        main = findViewById(R.id.main);
        main.setVisibility(View.INVISIBLE);
        light = findViewById(R.id.light);
        light.setVisibility(View.INVISIBLE);
        record = findViewById(R.id.record);
        record.setVisibility(View.INVISIBLE);
        plan = findViewById(R.id.plan);
        plan.setVisibility(View.INVISIBLE);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing) {
                    hideMenu();
                } else {
                    showMenu();
                }
                isShowing = !isShowing;
            }
        });

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                if (isShowing) {
                    hideMenu();
                } else {
                    showMenu();
                }
                isShowing = !isShowing;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void showMenu() {
        startShow(main);
        startShow(light);
        startShow(record);
        startShow(plan);
    }

    private void hideMenu() {
        if (main.getVisibility() != View.VISIBLE || main.getScaleX() < 0.5f) {
            return;
        }
        startHide(main);
        startHide(light);
        startHide(record);
        startHide(plan);
    }

    private void startShow(final View view) {
        final float maxScale = 1.2f;
        ScaleAnimation animation = new ScaleAnimation(0.0f, maxScale, 0.0f, maxScale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(100);
        Interpolator interpolator = new DecelerateInterpolator();//开始加速再减速
        animation.setInterpolator(interpolator);
        animation.setFillAfter(true);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation = new ScaleAnimation(maxScale, 1.0f, maxScale, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(50);
                Interpolator interpolator = new DecelerateInterpolator();//开始加速再减速
                animation.setInterpolator(interpolator);
                animation.setFillAfter(true);
                view.setVisibility(View.VISIBLE);
                view.startAnimation(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startHide(View view) {
        ScaleAnimation animation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(150);
        animation.setFillAfter(true);
        Interpolator interpolator = new DecelerateInterpolator();//开始加速再减速
        animation.setInterpolator(interpolator);
//        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);
    }

    public void onClickStart(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }


    public void onClickStudy(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onClickRecord(View view) {
        startActivity(new Intent(this, RecordActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideMenu();
        isShowing = false;
    }

    public void onClickLight(View view) {

        startActivity(new Intent(this, LightListActivity.class));
    }

    public void onClickPlan(View view) {
        startActivity(new Intent(this, PlanListActivity.class));
    }
}
