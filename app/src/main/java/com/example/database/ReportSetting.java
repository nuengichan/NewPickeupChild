package com.example.database;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.database.models.Post;
import com.example.database.models.Topic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;




public class ReportSetting extends AppCompatActivity implements View.OnClickListener {
    private Activity mActivity;
    private RecyclerView mRecyclerViewTopic;
    private DatabaseReference mRootRef, mChildRef;
    private ArrayList<Post> mList = new ArrayList<>();
    private ReportAdapter mReportAdapter;
    private LinearLayoutManager mLayoutManager;
    private ImageView mIcXls ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mActivity = ReportSetting.this;
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mReportAdapter = new ReportAdapter(mActivity ,mList);
        mRecyclerViewTopic = (RecyclerView) findViewById(R.id.topic_recyclerview);
        mIcXls = (ImageView) findViewById(R.id.ic_xls11);


        mIcXls.setVisibility(View.INVISIBLE);


        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerViewTopic.setLayoutManager(mLayoutManager);
        mRecyclerViewTopic.setAdapter(mReportAdapter);
        mChildRef = mRootRef.child(IntentConstans.ACTIVITY_CHECKIN);
        mChildRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    Post checkInActivity = child.getValue(Post.class);
                    mList.add(checkInActivity);


                }
                mReportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }

    }
}
