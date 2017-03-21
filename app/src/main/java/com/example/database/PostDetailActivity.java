package com.example.database;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.database.models.Comment;
import com.example.database.models.Post;
import com.example.database.models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.example.database.R.id.txt ;

public class PostDetailActivity extends BaseActivity implements View.OnClickListener ,GoogleMap.OnMyLocationButtonClickListener ,
		GoogleMap.OnMapClickListener,
		GoogleMap.OnMarkerClickListener,
		OnMapReadyCallback,
		ActivityCompat.OnRequestPermissionsResultCallback {
	private static final String TAG = "PostDetailActivity";
	public static final String EXTRA_POST_KEY = "post_key";
	private static final String AUTH_KEY = "key=AAAABRiP3KY:APA91bFsU3vuDt9bZkPaD92BlKnTz0beXZDftoypMVdTbvCRFDJ8VtRst54QmOZgDhwEya1A_VlpJEaIEIiwuKoExBOg0hHPmtu7kyJ5St9obFwLomTr4YXZCZjcWSxUJnp74SVcIE5M";
	private TextView mTextView;
	private DatabaseReference mPostReference, mCommentsReference , mDatabase;
	private ValueEventListener mPostListener;
	private CommentAdapter mAdapter;
	private TextView mAuthorView, mTitleView, mBodyView;
	private EditText mCommentField;
	private RecyclerView mCommentsRecycler;
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
	private static final LatLng School = new LatLng(13.7626198, 100.6625916 );
	private Marker mSelectedMarker;
	/**
	 * Flag indicating whether a requested permission has been denied after returning in
	 * {@link #onRequestPermissionsResult(int, String[], int[])}.
	 */
	private boolean mPermissionDenied = false;

	private GoogleMap mMap , mMap2;

	private Marker add , add2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_detail);
		mAuthorView = (TextView) findViewById(R.id.post_author);
		mTitleView = (TextView) findViewById(R.id.post_title);
		mBodyView = (TextView) findViewById(R.id.post_body);
		mCommentField = (EditText) findViewById(R.id.field_comment_text);

		mTextView = (TextView) findViewById(txt);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String tmp = "";
			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				tmp += key + ": " + value + "\n\n";
			}
			mTextView.setText(tmp);
		}

		mCommentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);
		mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

		Button mCommentButton = (Button) findViewById(R.id.button_post_comment);
		mCommentButton.setOnClickListener(this);

		// Get post key from intent
		String mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
		if (mPostKey == null) {
			throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
		}

		// Initialize Database
		mPostReference = FirebaseDatabase.getInstance().getReference().child("students").child(mPostKey);
		mCommentsReference = FirebaseDatabase.getInstance().getReference().child("students-comments").child(mPostKey);


		SupportMapFragment mapFragment =
				(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);


	}

	@Override
	public void onStart() {
		super.onStart();

		// Add value event listener to the post
		ValueEventListener postListener = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// Get Post object and use the values to update the UI
				Post post = dataSnapshot.getValue(Post.class);

				mAuthorView.setText(post.author);
				//mTitleView.setText(post.title);
				//mBodyView.setText(post.body);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				// Getting Post failed, log a message
				Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
				Toast.makeText(PostDetailActivity.this, "Failed to load post.", Toast.LENGTH_SHORT).show();
			}
		};
		mPostReference.addValueEventListener(postListener);

		// Keep copy of post listener so we can remove it when app stops
		mPostListener = postListener;

		// Listen for comments
		mAdapter = new CommentAdapter(this, mCommentsReference);
		mCommentsRecycler.setAdapter(mAdapter);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mPostListener != null) {
			mPostReference.removeEventListener(mPostListener);
		}
		mAdapter.cleanupListener();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_post_comment:
				postComment();
				break;
		}
	}

	private void postComment() {
		final String uid = getUid();

		FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// Get user information
				User user = dataSnapshot.getValue(User.class);
				String authorName = user.username;

				// Create new comment object
				String commentText = mCommentField.getText().toString().trim();
				Comment comment = new Comment(uid, authorName, commentText);

				// Push the comment, it will appear in the list
				mCommentsReference.push().setValue(comment);

				// Clear the field
				mCommentField.setText(null);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Toast.makeText(PostDetailActivity.this, "onCancelled: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
	}

	private static class CommentViewHolder extends RecyclerView.ViewHolder {
		TextView authorView;
		TextView bodyView;
		CommentViewHolder(View itemView) {
			super(itemView);
			authorView = (TextView) itemView.findViewById(R.id.comment_author);
			bodyView = (TextView) itemView.findViewById(R.id.comment_body);
		}
	}

	private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
		private Context mContext;
		private DatabaseReference mDatabaseReference;
		private ChildEventListener mChildEventListener;
		private List<String> mCommentIds = new ArrayList<>();
		private List<Comment> mComments = new ArrayList<>();

		CommentAdapter(final Context context, DatabaseReference ref) {
			mContext = context;
			mDatabaseReference = ref;

			// Create child event listener
			ChildEventListener childEventListener = new ChildEventListener() {
				@Override
				public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
					Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

					// A new comment has been added, add it to the displayed list
					Comment comment = dataSnapshot.getValue(Comment.class);

					// Update RecyclerView
					mCommentIds.add(dataSnapshot.getKey());
					mComments.add(comment);
					notifyItemInserted(mComments.size() - 1);
				}

				@Override
				public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
					Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

					// A comment has changed, use the key to determine if we are displaying this
					// comment and if so displayed the changed comment.
					Comment newComment = dataSnapshot.getValue(Comment.class);
					String commentKey = dataSnapshot.getKey();

					int commentIndex = mCommentIds.indexOf(commentKey);
					if (commentIndex > -1) {
						// Replace with the new data
						mComments.set(commentIndex, newComment);

						// Update the RecyclerView
						notifyItemChanged(commentIndex);
					} else {
						Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
					}
				}

				@Override
				public void onChildRemoved(DataSnapshot dataSnapshot) {
					Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

					// A comment has changed, use the key to determine if we are displaying this
					// comment and if so remove it.
					String commentKey = dataSnapshot.getKey();

					int commentIndex = mCommentIds.indexOf(commentKey);
					if (commentIndex > -1) {
						// Remove data from the list
						mCommentIds.remove(commentIndex);
						mComments.remove(commentIndex);

						// Update the RecyclerView
						notifyItemRemoved(commentIndex);
					} else {
						Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
					}
				}

				@Override
				public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
					Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

					// A comment has changed position, use the key to determine if we are
					// displaying this comment and if so move it.
					//Comment movedComment = dataSnapshot.getValue(Comment.class);
					//String commentKey = dataSnapshot.getKey();
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					Log.w(TAG, "postComments:onCancelled", databaseError.toException());
					Toast.makeText(mContext, "Failed to load comments.", Toast.LENGTH_SHORT).show();
				}
			};
			ref.addChildEventListener(childEventListener);

			// Store reference to listener so it can be removed on app stop
			mChildEventListener = childEventListener;
		}

		@Override
		public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.item_comment, parent, false);
			return new CommentViewHolder(view);
		}

		@Override
		public void onBindViewHolder(CommentViewHolder holder, int position) {
			Comment comment = mComments.get(position);
			holder.authorView.setText(comment.author);
			holder.bodyView.setText(comment.text);
		}

		@Override
		public int getItemCount() {
			return mComments.size();
		}

		void cleanupListener() {
			if (mChildEventListener != null) {
				mDatabaseReference.removeEventListener(mChildEventListener);
			}
		}
	}


	////////FCM//////

	public void showToken(View view) {
		mTextView.setText(FirebaseInstanceId.getInstance().getToken());
		Log.i("token", FirebaseInstanceId.getInstance().getToken());
		sendWithOtherThread2("token");
	}

	public void subscribe(View view) {
		FirebaseMessaging.getInstance().subscribeToTopic("news");
		mTextView.setText(R.string.subscribed);
	}

	public void unsubscribe(View view) {
		FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
		mTextView.setText(R.string.unsubscribed);
	}

	public void sendToken(View view) {
		sendWithOtherThread("token");
	}

	public void sendTokens(View view) {
		sendWithOtherThread("token");
	}

	public void sendTopic(View view) {
		sendWithOtherThread("topic");
	}

	private void sendWithOtherThread(final String type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pushNotification(type);
			}
		}).start();
	}







	private void sendWithOtherThread2(final String type2) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pushNotification2(type2);
			}
		}).start();
	}


	private void pushNotification(String type) {

		JSONObject jPayload = new JSONObject();
		JSONObject jNotification = new JSONObject();
		JSONObject jData = new JSONObject();
		try {
			jNotification.put("title", "ด.ช. ปิยธร คะเสนา");
			jNotification.put("body", "กำลังไปรับ"  );
			jNotification.put("sound", "default");
			jNotification.put("badge", "1");
			jNotification.put("click_action", "OPEN_ACTIVITY_1");

			//jData.put("picture_url", "http://opsbug.com/static/google-io.jpg");

			switch(type) {
				case "token":
					JSONArray ja = new JSONArray();
					ja.put("eyvcZxgKLHU:APA91bFMarWXxNVe7J5R8XDlsXAL_UzzI2vOq5zpO9jkMdh6gCIn8u3LzvVq19hfCQQcSPvGiIo2JLrhsdLcCUZMsU0tqyncMyBFAXvDfs4jIJDEK8VNNuksujWjxTTFyos2cGWwokHI");
					ja.put(FirebaseInstanceId.getInstance().getToken());
					jPayload.put("registration_ids", ja);
					break;
				case "topic":
					jPayload.put("to", "/topics/news");
					break;
				case "condition":
					jPayload.put("condition", "'sport' in topics || 'news' in topics");
					break;
				default:
					jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
			}

			jPayload.put("priority", "high");
			jPayload.put("notification", jNotification);
			jPayload.put("data", jData);

			URL url = new URL("https://fcm.googleapis.com/fcm/send");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", AUTH_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			// Send FCM message content.
			OutputStream outputStream = conn.getOutputStream();
			outputStream.write(jPayload.toString().getBytes());

			// Read FCM response.
			InputStream inputStream = conn.getInputStream();
			final String resp = convertStreamToString(inputStream);

			Handler h = new Handler(Looper.getMainLooper());
			h.post(new Runnable() {
				@Override
				public void run() {
					mTextView.setText(resp);
				}
			});
		}


		catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	private void pushNotification2(String type2) {
		JSONObject jPayload = new JSONObject();
		JSONObject jNotification = new JSONObject();
		JSONObject jData = new JSONObject();
		try {
			jNotification.put("title", "ด.ญ. หนึ่ง คะเสนา");
			jNotification.put("body", "กำลังไปรับ");
			jNotification.put("sound", "default");
			jNotification.put("badge", "1");
			jNotification.put("click_action", "OPEN_ACTIVITY_1");

			//jData.put("picture_url", "http://opsbug.com/static/google-io.jpg");

			switch(type2) {
				case "token":
					JSONArray ja = new JSONArray();
					ja.put("eyvcZxgKLHU:APA91bFMarWXxNVe7J5R8XDlsXAL_UzzI2vOq5zpO9jkMdh6gCIn8u3LzvVq19hfCQQcSPvGiIo2JLrhsdLcCUZMsU0tqyncMyBFAXvDfs4jIJDEK8VNNuksujWjxTTFyos2cGWwokHI");
					ja.put(FirebaseInstanceId.getInstance().getToken());
					jPayload.put("registration_ids", ja);
					break;
				case "topic":
					jPayload.put("to", "/topics/news");
					break;
				case "condition":
					jPayload.put("condition", "'sport' in topics || 'news' in topics");
					break;
				default:
					jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
			}

			jPayload.put("priority", "high");
			jPayload.put("notification", jNotification);
			jPayload.put("data", jData);

			URL url = new URL("https://fcm.googleapis.com/fcm/send");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", AUTH_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			// Send FCM message content.
			OutputStream outputStream = conn.getOutputStream();
			outputStream.write(jPayload.toString().getBytes());

			// Read FCM response.
			InputStream inputStream = conn.getInputStream();
			final String resp = convertStreamToString(inputStream);

			Handler h = new Handler(Looper.getMainLooper());
			h.post(new Runnable() {
				@Override
				public void run() {
					mTextView.setText(resp);
				}
			});
		}


		catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}



	private String convertStreamToString(InputStream is) {
		Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next().replace(",", ",\n") : "";
	}




	////////////////////////////////
	////
	//// map

	public void onMapReady(GoogleMap map) {
		mMap = map;
		mMap2 = map;

		mMap.getUiSettings().setZoomControlsEnabled(false);
		addMarkersToMap();
		// Set listener for marker click event.  See the bottom of this class for its behavior.
		mMap.setOnMarkerClickListener(this);

		// Set listener for map click event.  See the bottom of this class for its behavior.
		mMap.setOnMapClickListener(this);




		mMap.setOnMyLocationButtonClickListener(this);
		enableMyLocation();

		mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location location) {
				float[] distance = new float[2];
				float[] distance2 = new float[4];
				float[] distance3 = new float[4];
                    /*
                    Location.distanceBetween( mMarker.getPosition().latitude, mMarker.getPosition().longitude,
                            mCircle.getCenter().latitude, mCircle.getCenter().longitude, distance);
                            */

				Location.distanceBetween(location.getLatitude(), location.getLongitude(),
						circle3.getCenter().latitude, circle3.getCenter().longitude, distance3);


				if (distance3[0] > circle3.getRadius()) {
					//Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance2[0] + " radius: " + circle3.getRadius(), Toast.LENGTH_LONG).show();

				}
				else  if (distance3[0] < circle3.getRadius()) {
					//Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance2[0] + " radius: " + circle3.getRadius(), Toast.LENGTH_LONG).show();
					sendWithOtherThread3("token");


				}


				Location.distanceBetween(location.getLatitude(), location.getLongitude(),
						circle2.getCenter().latitude, circle2.getCenter().longitude, distance2);

				if (distance2[0] > circle2.getRadius()) {
					//Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance2[0] + " radius: " + circle2.getRadius(), Toast.LENGTH_LONG).show();

				}
				else  if (distance2[0] < circle2.getRadius()) {
					//Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance2[0] + " radius: " + circle2.getRadius(), Toast.LENGTH_LONG).show();
					sendWithOtherThread3("token");


				}


				Location.distanceBetween(location.getLatitude(), location.getLongitude(),
						circle.getCenter().latitude, circle.getCenter().longitude, distance);




				if (distance[0] > circle.getRadius()) {
					//Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance[0] + " radius: " + circle.getRadius(), Toast.LENGTH_LONG).show();

				}
				else  {
					//Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance[0] + " radius: " + circle.getRadius(), Toast.LENGTH_LONG).show();
					sendWithOtherThread4("token");
					mMap.setOnMyLocationChangeListener(null);

				}
			}
		});



	}
	Circle circle ;

	Circle circle2 ;

	Circle circle3 ;
	public void sendTokens3(View view) {
		sendWithOtherThread3("token");
	}

	public void sendTokens4(View view) {
		sendWithOtherThread4("token");
	}

	private void sendWithOtherThread3(final String type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pushNotification3(type);
			}
		}).start();
	}


	private void pushNotification3(String type) {
		JSONObject jPayload = new JSONObject();
		JSONObject jNotification = new JSONObject();
		JSONObject jData = new JSONObject();
		try {
			jNotification.put("title", "อีก 5 กิโลเมตรถึงโรงเรียน");
			jNotification.put("body", "อีก 5 กิโลเมตรถึงโรงเรียน");
			jNotification.put("sound", "default");
			jNotification.put("badge", "1");
			jNotification.put("click_action", "OPEN_ACTIVITY_1");

			//jData.put("picture_url", "http://opsbug.com/static/google-io.jpg");

			switch(type) {
				case "token":
					JSONArray ja = new JSONArray();
					ja.put("eC3Pf6jsBEg:APA91bHeZDIXgnp2vZgIfl20LZ4XsjthyJ2OkWZXypankHgLMhnewn2P1f3QV0aKKxiirvKHJstoWSauNe4pbBFz0JAsssmocBCJYvXzWRb7kbkljBuFLctMHTv8qt_x7EMJcVoqfT6a");
					ja.put(FirebaseInstanceId.getInstance().getToken());
					jPayload.put("registration_ids", ja);
					break;
				case "topic":
					jPayload.put("to", "/topics/news");
					break;
				case "condition":
					jPayload.put("condition", "'sport' in topics || 'news' in topics");
					break;
				default:
					jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
			}

			jPayload.put("priority", "high");
			jPayload.put("notification", jNotification);
			jPayload.put("data", jData);

			URL url = new URL("https://fcm.googleapis.com/fcm/send");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", AUTH_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			// Send FCM message content.
			OutputStream outputStream = conn.getOutputStream();
			outputStream.write(jPayload.toString().getBytes());

			// Read FCM response.
			InputStream inputStream = conn.getInputStream();
			final String resp = convertStreamToString(inputStream);

			Handler h = new Handler(Looper.getMainLooper());
			h.post(new Runnable() {
				@Override
				public void run() {
					mTextView.setText(resp);
				}
			});
		}


		catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}


	private void sendWithOtherThread4(final String type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pushNotification4(type);
			}
		}).start();
	}


	private void pushNotification4(String type) {
		JSONObject jPayload = new JSONObject();
		JSONObject jNotification = new JSONObject();
		JSONObject jData = new JSONObject();
		try {
			jNotification.put("title", "กำลังจะถึงที่หมาย");
			jNotification.put("body", "ไม่กี่นาทีจะถึงโรงเรียน");
			jNotification.put("sound", "default");
			jNotification.put("badge", "1");
			jNotification.put("click_action", "OPEN_ACTIVITY_1");

			//jData.put("picture_url", "http://opsbug.com/static/google-io.jpg");

			switch(type) {
				case "token":
					JSONArray ja = new JSONArray();
					ja.put("eC3Pf6jsBEg:APA91bHeZDIXgnp2vZgIfl20LZ4XsjthyJ2OkWZXypankHgLMhnewn2P1f3QV0aKKxiirvKHJstoWSauNe4pbBFz0JAsssmocBCJYvXzWRb7kbkljBuFLctMHTv8qt_x7EMJcVoqfT6a");
					ja.put(FirebaseInstanceId.getInstance().getToken());
					jPayload.put("registration_ids", ja);
					break;
				case "topic":
					jPayload.put("to", "/topics/news");
					break;
				case "condition":
					jPayload.put("condition", "'sport' in topics || 'news' in topics");
					break;
				default:
					jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
			}

			jPayload.put("priority", "high");
			jPayload.put("notification", jNotification);
			jPayload.put("data", jData);

			URL url = new URL("https://fcm.googleapis.com/fcm/send");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", AUTH_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			// Send FCM message content.
			OutputStream outputStream = conn.getOutputStream();
			outputStream.write(jPayload.toString().getBytes());

			// Read FCM response.
			InputStream inputStream = conn.getInputStream();
			final String resp = convertStreamToString(inputStream);

			Handler h = new Handler(Looper.getMainLooper());
			h.post(new Runnable() {
				@Override
				public void run() {
					mTextView.setText(resp);
				}
			});
		}


		catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	private void addMarkersToMap() {

		add = mMap.addMarker(new MarkerOptions()
				.position(School)
				.title("School")
				.snippet("Population: 2,074,200"));

		add2 = mMap2.addMarker(new MarkerOptions()
				.position(School)
				.title("School")
				.snippet("Population: 2,074,200"));

		circle = drawCircle(new LatLng(13.7626198, 100.6625916) );

		circle2 = drawCircle2(new LatLng(13.763246, 100.649291));

		circle3 = drawCircle3(new LatLng(13.772699, 100.665926));
	}

	private Circle drawCircle3(LatLng latLng) {


		CircleOptions add2 = new CircleOptions()
				.center(latLng)
				.radius(50)
				.fillColor(0x33FF1493)
				.strokeColor(Color.BLUE)
				.strokeWidth(5);

		return mMap.addCircle(add2);
	}

	private Circle drawCircle2(LatLng latLng) {


		CircleOptions add2 = new CircleOptions()
				.center(latLng)
				.radius(50)
				.fillColor(0x33FF1493)
				.strokeColor(Color.BLUE)
				.strokeWidth(5);

		return mMap.addCircle(add2);
	}


	private Circle drawCircle(LatLng latLng) {

		CircleOptions add = new CircleOptions()
				.center(latLng)
				.radius(400)
				.fillColor(0x3300BFFF)
				.strokeColor(Color.BLUE)
				.strokeWidth(3);


		return mMap.addCircle(add);



	}

	/**
	 * Enables the My Location layer if the fine location permission has been granted.
	 */
	private void enableMyLocation() {
		if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			// Permission to access the location is missing.
			PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
					android.Manifest.permission.ACCESS_FINE_LOCATION, true);

		} else if (mMap != null) {
			// Access to the location has been granted to the app.
			mMap.setMyLocationEnabled(true);

		}
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
			return;
		}

		if (PermissionUtils.isPermissionGranted(permissions, grantResults,
				android.Manifest.permission.ACCESS_FINE_LOCATION)) {
			// Enable the my location layer if the permission has been granted.
			enableMyLocation();
		} else {
			// Display the missing permission error dialog when the fragments resume.
			mPermissionDenied = true;
		}
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		if (mPermissionDenied) {
			// Permission was not granted, display error dialog.
			showMissingPermissionError();
			mPermissionDenied = false;
		}
	}

	/**
	 * Displays a dialog with error message explaining that the location permission is missing.
	 */
	private void showMissingPermissionError() {
		PermissionUtils.PermissionDeniedDialog
				.newInstance(true).show(getSupportFragmentManager(), "dialog");
	}

	@Override
	public void onMapClick(final LatLng point) {
		// Any showing info window closes when the map is clicked.
		// Clear the currently selected marker.
		mSelectedMarker = null;
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		// The user has re-tapped on the marker which was already showing an info window.
		if (marker.equals(mSelectedMarker)) {
			// The showing info window has already been closed - that's the first thing to happen
			// when any marker is clicked.
			// Return true to indicate we have consumed the event and that we do not want the
			// the default behavior to occur (which is for the camera to move such that the
			// marker is centered and for the marker's info window to open, if it has one).
			mSelectedMarker = null;
			return true;
		}

		mSelectedMarker = marker;

		// Return false to indicate that we have not consumed the event and that we wish
		// for the default behavior to occur.
		return false;
	}
}
