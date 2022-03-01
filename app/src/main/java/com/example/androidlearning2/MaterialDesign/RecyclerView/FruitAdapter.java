package com.example.androidlearning2.MaterialDesign.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.androidlearning2.MaterialDesign.Fruit.FruitActivity;
import com.example.androidlearning2.R;

import java.util.List;

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {

    private Context mContext;

    private List<Fruit> mFruitList;

    // 构造方法
    public FruitAdapter(List<Fruit> fruitList) {
        this.mFruitList = fruitList;
    }

    // 创建模板
    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        ImageView fruitImage;
        TextView fruitName;

        public ViewHolder(@NonNull View view) {
            super(view);

            // 绑定控件
            cardView = (CardView) view;
            fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
            fruitName = (TextView) view.findViewById(R.id.fruit_name);

        }
    }

    @NonNull
    @Override
    // 绑定模板
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (mContext == null){
            mContext = parent.getContext();
        }

        // 把模板导入
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.fruit_item, parent, false);

        // 设置点击事件（跳转到详情页）
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取 点击时所处的 卡片位置
                int position = holder.getAdapterPosition();
                Fruit fruit = mFruitList.get(position);

                // 指定跳转的目标，以及传参数过去
                Intent intent = new Intent(mContext, FruitActivity.class);
                intent.putExtra(FruitActivity.FRUIT_NAME, fruit.getName());
                intent.putExtra(FruitActivity.FRUIT_IMAGE_ID, fruit.getImageId());
                mContext.startActivity(intent);

            }
        });

        return holder;
    }

    @Override
    // 向模板中导入图片（position：每一行的坐标）
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fruit fruit = mFruitList.get(position);
        holder.fruitName.setText(fruit.getName());

        // 将图片导入模板的图片位置中（会压缩照片，避免内存泄露）
        Glide.with(mContext).load(fruit.getImageId()).into(holder.fruitImage);

    }

    @Override
    // 获取列的数量
    public int getItemCount() {
        return mFruitList.size();
    }


}
