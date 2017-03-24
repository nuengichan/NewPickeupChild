package com.example.database;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.database.models.Post;
import com.example.database.models.Topic;

import java.security.Timestamp;
import java.util.List;




public class ReportEventAdapter extends RecyclerView.Adapter<ReportEventAdapter.CustomViewHolder>{


        private List<Post> feedItemList;
        private Context mContext;


    public ReportEventAdapter(Context context, List<Post> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }


    @Override
    public int getItemCount() {
        return feedItemList.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_listreport, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
        //        customViewHolder.mPhone.setText(feedItem.getPhone());
        //            this.mPhone = (TextView) view.findViewById(R.id.text_phone);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final Post feedItem = feedItemList.get(i);
        customViewHolder.mIdStudent.setText(feedItem.author);
        customViewHolder.mName.setText(feedItem.title);


    }



    class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView mIdStudent, mName, mPhone;

        public CustomViewHolder(View view) {


            super(view);


            this.mIdStudent = (TextView) view.findViewById(R.id.text_idstudent);
            this.mName = (TextView) view.findViewById(R.id.text_name);

        }
    }


}



