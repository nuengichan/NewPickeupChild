package com.example.database;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.database.models.Post;
import com.example.database.models.Topic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;



public class ReportHisActivity extends AppCompatActivity  implements View.OnClickListener {

    private Activity mActivity;
    private RecyclerView mRecyclerViewTopic;
    private DatabaseReference mRootRef, mChildRef , mRootRef2;
    private ArrayList<Post> mList = new ArrayList<>();
    private ReportEventAdapter mReportAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView mTextTopic ;
    private ImageView mIcXls;
    private String id, title;
    private ArrayList<Post> mCheckInUsers = new ArrayList<>();
    private ExcelObject mExcelObject;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        id = getIntent().getStringExtra("Times");
        title = getIntent().getStringExtra("Title");

        mActivity = ReportHisActivity.this;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef2 = FirebaseDatabase.getInstance().getReference().child("Times");

        mIcXls = (ImageView) findViewById(R.id.ic_xls11);
        mIcXls.setOnClickListener(this);

        mReportAdapter = new ReportEventAdapter(mActivity, mList);
        mRecyclerViewTopic = (RecyclerView) findViewById(R.id.topic_recyclerview);
        mTextTopic = (TextView) findViewById(R.id.text_topic);
        mTextTopic.setText(title);

        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mRecyclerViewTopic.setLayoutManager(mLayoutManager);
        mRecyclerViewTopic.setAdapter(mReportAdapter);
        mChildRef = mRootRef.child("students") ;
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
            case R.id.ic_xls11:
                mChildRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mCheckInUsers.clear();
                        String activityName = (String) dataSnapshot.child("author").getValue();
                        String timeStart = (String) dataSnapshot.child("author").getValue();
                        String locationname = (String) dataSnapshot.child("TimeAdd").getValue();
                        String houractivity = (String) dataSnapshot.child("author").getValue();
                        String Contact = (String) dataSnapshot.child("author").getValue();

                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            Post checkInUser = child.getValue(Post.class);
                            mCheckInUsers.add(checkInUser);
                        }

                        mExcelObject = new ExcelObject();
                        mExcelObject.setLocationName(locationname);
                        mExcelObject.setActivityName(activityName);
//                        mExcelObject.setHourActivity(houractivity);
                        mExcelObject.setTimeStart(timeStart);
                       // mExcelObject.setContect(Contact);
                        mExcelObject.setmCheckInUserNews(mCheckInUsers);

                        WriteExcel excel = new WriteExcel();
                        excel.writeExcel(mActivity, mExcelObject);



                        Toast.makeText(mActivity, "Export Complete", Toast.LENGTH_SHORT).show();



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



//                Intent i = new Intent(mActivity , SettingActivity.class);
//                startActivity(i);
//                break;

        }

    }
}

