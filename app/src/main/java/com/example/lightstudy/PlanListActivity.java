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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
public class PlanListActivity extends AppCompatActivity {
    private static final String TAG ="RecordActivity" ;
    private List<Plan> list;

    RecyclerView recyclerView;

    AnalogAdapter adapter;
    MySqliteOpenHelper<Plan> helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);
        recyclerView = findViewById(R.id.recyclerView);
        helper = new MySqliteOpenHelper<>(this,Plan.class);
        initView();
        initData();

    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        final TimeLineDecoration decoration = new TimeLineDecoration(this)
//                .setLineColor(android.R.color.black)
//                .setLineWidth(1)
//                .setLeftDistance(16)
//                .setTopDistance(16)
//                .setBeginMarker(R.drawable.begin_marker)
//                .setMarkerRadius(4)
//                .setMarkerColor(R.color.colorAccent)
//                .setCallback(new TimeLineDecoration.TimeLineAdapter() {
//
//                    @Nullable
//                    @Override
//                    public Rect getRect(int position) {
//                        return new Rect(0, 16, 0, 16);
//                    }
//
//                    @Override
//                    public int getTimeLineType(int position) {
//                        if (position == 0) return BEGIN;
//                        else if (position == adapter.getItemCount() - 1) return END_FULL;
//                        else return NORMAL;
//                    }
//                });
//        recyclerView.addItemDecoration(decoration);
    DividerItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL){
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0,0,0,30);
        }
    };
        recyclerView.addItemDecoration(itemDecoration);
        adapter = new AnalogAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        list = helper.findData(this,null,null,null,null,null,"planTime desc");
        Log.i(TAG,"list:"+list);
        adapter.setItems(list);
    }

    public void onClickAdd(View view) {
        AddPlanDialog dialog = new AddPlanDialog(this);
        dialog.show();
        dialog.setOnSlectedListener(new AddPlanDialog.onSlectedListener() {
            @Override
            public void onSelected(int hor, int min, String content) {
                String time = String.valueOf(hor*3600*1000+min*60*1000+System.currentTimeMillis());
                Plan plan = new Plan();
                plan.content = content;
                plan.planTime = time;
                helper.insert(plan);
                Toast.makeText(PlanListActivity.this,"添加学习计划成功",Toast.LENGTH_SHORT).show();
                initData();
            }
        });

    }

    public class AnalogAdapter extends SingleAdapter<Plan, AnalogAdapter.ChildViewHolder> {

        public AnalogAdapter(@Nullable Object listener) {
            super(listener);
        }

        @Override
        public ChildViewHolder onCreateVHolder(ViewGroup parent, int viewType) {
            return new ChildViewHolder(parent, R.layout.plan_item);
        }

        @Override
        public void onBindVHolder(ChildViewHolder holder, int position) {
            final Context context = holder.itemView.getContext();
            final Plan plan = getItem(position);
//            final int color = ContextCompat.getColor(context,
//                    position==0 ? android.R.color.black : android.R.color.darker_gray);
//            holder.title.setTextColor(color);
            holder.title.setText(plan.content);
            holder.timecount.setText(plan.content);
            long duration = Long.parseLong(plan.planTime) - System.currentTimeMillis();
            duration = duration/1000;
            if(duration<=0){
                holder.timecount.setText("已到学习时间");
            }else{
                long hour = duration/3600;
                long min = (duration%3600)/60;
                if(hour==0&&min==0){
                    holder.timecount.setText("已到学习时间");
                }else{
                    holder.timecount.setText("剩余"+hour+"小时"+min+"分");
                }
            }
            holder.subtitle.setText(DateTimeUtils.timeStamp2Date(Long.parseLong(plan.planTime),null));

        }

        class ChildViewHolder extends PowerViewHolder {

            TextView title;
            TextView subtitle;
            TextView timecount;
            ImageView iv;


            public ChildViewHolder(@NonNull ViewGroup parent, int layoutResId) {
                super(parent, layoutResId);
                title = itemView.findViewById(R.id.title);
                subtitle = itemView.findViewById(R.id.subtitle);
                timecount = itemView.findViewById(R.id.timecount);
                iv = itemView.findViewById(R.id.iv);
            }
        }

    }
}
