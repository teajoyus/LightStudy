package com.example.lightstudy;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.List;

public class LightListActivity extends AppCompatActivity {
    private static final String TAG = "LightListActivity";
    private List<TimeStudy> topList;
    private List<TimeStudy> bottomList;
    MySqliteOpenHelper<TimeStudy> helper;
    RecyclerView rc_top,rc_bottom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_list);
        rc_top = findViewById(R.id.rc_top);
        rc_bottom = findViewById(R.id.rc_bottom);
        helper = new MySqliteOpenHelper<>(this,TimeStudy.class);
        topList = helper.findData(this,null,"status=2 and lightLayout=1",null,null,null,null);
        bottomList = helper.findData(this,null,"status=2 and lightLayout=2",null,null,null,null);
        Log.i(TAG,"list:"+topList);
        Log.i(TAG,"list:"+bottomList);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        rc_top.setLayoutManager(llm);
        rc_top.setHasFixedSize(true);

        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        llm2.setOrientation(LinearLayoutManager.HORIZONTAL);
        rc_bottom.setLayoutManager(llm2);
        rc_bottom.setHasFixedSize(true);

        rc_top.setAdapter(new MyAdapter(topList));
        rc_bottom.setAdapter(new MyAdapter(bottomList));
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHodler> {
        private List<TimeStudy> list;

        public MyAdapter(List<TimeStudy> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            LoadingImgView imageView = new LoadingImgView(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.light_item,null);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(Utils.dp2px(getActivity(),100), Utils.dp2px(getActivity(),100));
//            params.bottomMargin = 5;
//            params.topMargin = 5;
            view.setLayoutParams(params);

            MyViewHodler holder = new MyViewHodler(view);
            return holder;
        }

        public void removeItem(int position) {
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size() - position);
        }

        @Override
        public void onBindViewHolder( @NonNull  MyViewHodler hodler,int position) {
            Log.i(TAG,"onBindViewHolder: position:"+position);
            int res = getResources().getIdentifier(list.get(position).lightStyleName , "drawable", getPackageName());
            Drawable drawable = getResources().getDrawable(res);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable.setTint(0xffffffff);
            }
            hodler.iv.setImageDrawable(drawable);
//            iv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setTitle("删除");
//                    builder.setMessage("您要删除掉这个图片吗？");
//                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            removeItem(pos);
//                        }
//                    });
//                    builder.setNegativeButton("取消", null);
//                    builder.show();
//                }
//            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHodler extends RecyclerView.ViewHolder {
            ImageView iv;
            public MyViewHodler(View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.iv);

            }
        }
    }

    public Activity getActivity() {
        return this;
    }

    int width, height;

    private void getDisplay() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;         // 屏幕宽度（像素）
        height = dm.heightPixels;       // 屏幕高度（像素）
    }

}
