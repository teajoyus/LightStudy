package com.example.lightstudy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lin.poweradapter.PowerViewHolder;
import com.lin.poweradapter.SingleAdapter;
import com.lin.timeline.TimeLineDecoration;

import java.util.ArrayList;
import java.util.List;


import static com.lin.timeline.TimeLineDecoration.BEGIN;
import static com.lin.timeline.TimeLineDecoration.END_FULL;
import static com.lin.timeline.TimeLineDecoration.NORMAL;

/**
 */
public class RecordActivity extends AppCompatActivity {
    private static final String TAG ="RecordActivity" ;
    private List<TimeStudy> list;

    RecyclerView recyclerView;

    AnalogAdapter adapter;
    MySqliteOpenHelper<TimeStudy> helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        recyclerView = findViewById(R.id.recyclerView);
        helper = new MySqliteOpenHelper<>(this,TimeStudy.class);
        initView();
        initData();

    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final TimeLineDecoration decoration = new TimeLineDecoration(this)
                .setLineColor(android.R.color.black)
                .setLineWidth(1)
                .setLeftDistance(16)
                .setTopDistance(16)
                .setBeginMarker(R.drawable.begin_marker)
                .setMarkerRadius(4)
                .setMarkerColor(R.color.colorAccent)
                .setCallback(new TimeLineDecoration.TimeLineAdapter() {

                    @Nullable
                    @Override
                    public Rect getRect(int position) {
                        return new Rect(0, 16, 0, 16);
                    }

                    @Override
                    public int getTimeLineType(int position) {
                        if (position == 0) return BEGIN;
                        else if (position == adapter.getItemCount() - 1) return END_FULL;
                        else return NORMAL;
                    }
                });
        recyclerView.addItemDecoration(decoration);

        adapter = new AnalogAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
//        final List<Analog> analogs = new ArrayList<>();
//
//        Analog analog0 = new Analog();
//        analog0.isHead = true;
//        analog0.text = "更新了日志";
//        analog0.time = "2016-01-08 10:20:10";
//        analogs.add(analog0);
//
//        Analog analog1 = new Analog();
//        analog1.isHead = false;
//        analog1.text = "上传了图片";
//        analog1.time = "2016-01-02 15:10:10";
//        analogs.add(analog1);
//
//        Analog analog2 = new Analog();
//        analog2.isHead = false;
//        analog2.text = "开通了空间";
//        analog2.time = "2016-01-01 10:10:10";
//        analogs.add(analog2);

        list = helper.findAllData(this);
        if(list.size()==0){
            TimeStudy timeStudy = new TimeStudy();
            timeStudy.startTime = System.currentTimeMillis()+"";
//            timeStudy.endTime = (System.currentTimeMillis()+10000L)+"";
            timeStudy.status = 1;
            timeStudy.mark = "开始学习";
            helper.insert(timeStudy);
            timeStudy = new TimeStudy();
            timeStudy.startTime = System.currentTimeMillis()+"";
            timeStudy.endTime = (System.currentTimeMillis()+10000L)+"";
            timeStudy.status = 2;
            timeStudy.mark = "学习完成";
            helper.insert(timeStudy);
             timeStudy = new TimeStudy();
            timeStudy.startTime = System.currentTimeMillis()+"";
//            timeStudy.endTime = (System.currentTimeMillis()+10000L)+"";
            timeStudy.status = 1;
            timeStudy.mark = "开始学习";
            helper.insert(timeStudy);
             timeStudy = new TimeStudy();
            timeStudy.startTime = System.currentTimeMillis()+"";
//            timeStudy.endTime = (System.currentTimeMillis()+10000L)+"";
            timeStudy.status = 3;
            timeStudy.mark = "提前退出";
            helper.insert(timeStudy);
             timeStudy = new TimeStudy();
            timeStudy.startTime = System.currentTimeMillis()+"";
//            timeStudy.endTime = (System.currentTimeMillis()+10000L)+"";
            timeStudy.status = 1;
            timeStudy.mark = "开始学习";
            helper.insert(timeStudy);
             timeStudy = new TimeStudy();
            timeStudy.startTime = System.currentTimeMillis()+"";
//            timeStudy.endTime = (System.currentTimeMillis()+10000L)+"";
            timeStudy.status = 2;
            timeStudy.mark = "学习完成";
            helper.insert(timeStudy);
        }
        list = helper.findAllData(this);
        Log.i(TAG,"list:"+list);
        adapter.setItems(list);
    }

    public class AnalogAdapter extends SingleAdapter<TimeStudy, AnalogAdapter.ChildViewHolder> {

        public AnalogAdapter(@Nullable Object listener) {
            super(listener);
        }

        @Override
        public ChildViewHolder onCreateVHolder(ViewGroup parent, int viewType) {
            return new ChildViewHolder(parent, R.layout.analog_item);
        }

        @Override
        public void onBindVHolder(ChildViewHolder holder, int position) {
            final Context context = holder.itemView.getContext();
            final TimeStudy timeStudy = getItem(position);
            final int color = ContextCompat.getColor(context,
                    position==0 ? android.R.color.black : android.R.color.darker_gray);
            holder.title.setTextColor(color);
            holder.title.setText(timeStudy.mark+"");
            holder.subtitle.setTextColor(color);
            holder.subtitle.setText(DateTimeUtils.timeStamp2Date(Long.parseLong(timeStudy.startTime),null));
            if(timeStudy.status==2){
                final Drawable xiao = getResources().getDrawable(R.drawable.xiao);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    xiao.setTint(getResources().getColor(R.color.colorPrimary));
                }
                holder.iv.setImageDrawable(xiao);
            }else if(timeStudy.status==3){
                final Drawable ku = getResources().getDrawable(R.drawable.ku);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ku.setTint(0xFFFFC107);
                }
                holder.iv.setImageDrawable(ku);
            }else{
                holder.iv.setImageDrawable(null);
            }
        }

         class ChildViewHolder extends PowerViewHolder {

            TextView title;
            TextView subtitle;
            ImageView iv;


             public ChildViewHolder(@NonNull ViewGroup parent, int layoutResId) {
                 super(parent, layoutResId);
                 title = itemView.findViewById(R.id.title);
                 subtitle = itemView.findViewById(R.id.subtitle);
                 iv = itemView.findViewById(R.id.iv);
             }
         }

    }
}
