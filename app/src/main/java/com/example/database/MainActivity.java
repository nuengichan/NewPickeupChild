package com.example.database;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.database.fragment.MyPostsFragment;
import com.example.database.fragment.MyTopPostsFragment;
import com.example.database.fragment.RecentPostsFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);



		FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			private final Fragment[] mFragments = new Fragment[] {
					new RecentPostsFragment(), /// เด็กทั้งหมด
					//new MyPostsFragment(), //////เด็กของฉัน
					new MyTopPostsFragment() /////// กำลังไปรับ
			};

			@Override
			public Fragment getItem(int position) {
				return mFragments[position];
			}
			@Override
			public int getCount() {
				return mFragments.length;
			}
			@Override
			public CharSequence getPageTitle(int position) {
				return getResources().getStringArray(R.array.headings)[position];
			}
		};

		ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mPagerAdapter);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(mViewPager);

		findViewById(R.id.fab_new_post).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, NewPostActivity.class));
			}
		});
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_chat:
				startActivity(new Intent(this, ChatActivity.class));
				return true;
			case R.id.action_map:
				startActivity(new Intent(this, MapActivity.class));
				return true;
			case R.id.action_logout:
				FirebaseAuth.getInstance().signOut();
				startActivity(new Intent(this, SignInActivity.class));
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}