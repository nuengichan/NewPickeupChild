package com.example.database;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.database.models.Post;
import com.example.database.models.Topic;

import java.util.List;




public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.CustomViewHolder>{


        private List<Post> feedItemList;
        private Context mContext;


    public ReportAdapter(Context context, List<Post> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return feedItemList.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_report, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final Post feedItem = feedItemList.get(i);
        customViewHolder.textView.setText(feedItem.author());
        customViewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ReportHisActivity.class);
                i.putExtra("author", feedItem.author());
                i.putExtra("title", feedItem.author());
                mContext.startActivity(i);
            }
        });
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.text_report);
        }
    }


}



