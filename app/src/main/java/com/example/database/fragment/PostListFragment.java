package com.example.database.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.database.models.User;
import com.example.database.NewPostActivity;
import com.example.database.PostDetailActivity;
import com.example.database.R;
import com.example.database.models.Post;
import com.example.database.models.Topic;
import com.example.database.models.User;
import com.example.database.viewholder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.example.database.R.id.txt;

public abstract class PostListFragment extends Fragment {
	private Activity mActivity;
	private DatabaseReference mDatabase;
	private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
	private RecyclerView mRecycler;
	public static final String EXTRA_POST_KEY = "post_key";
	private static final String AUTH_KEY = "key=AAAABRiP3KY:APA91bFsU3vuDt9bZkPaD92BlKnTz0beXZDftoypMVdTbvCRFDJ8VtRst54QmOZgDhwEya1A_VlpJEaIEIiwuKoExBOg0hHPmtu7kyJ5St9obFwLomTr4YXZCZjcWSxUJnp74SVcIE5M";
	private TextView mTextView;
	final String userId = getUid();

	public PostListFragment() {
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);
		mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
		mRecycler.setHasFixedSize(true);
		mDatabase = FirebaseDatabase.getInstance().getReference();

		mTextView = (TextView) rootView.findViewById(txt);
		Bundle bundle = getActivity().getIntent().getExtras();
		if (bundle != null) {
			String tmp = "";
			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				tmp += key + ": " + value + "\n\n";
			}
			mTextView.setText(tmp);
		}
		return rootView;

	}




	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();


		final Dialog mDialog = new Dialog(mActivity, R.style.NewDialog);
		mDialog.addContentView(
				new ProgressBar(mActivity),
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
		);
		mDialog.setCancelable(true);
		mDialog.show();

		// Set up Layout Manager, reverse layout
		LinearLayoutManager mManager = new LinearLayoutManager(mActivity);
		mManager.setReverseLayout(true);
		mManager.setStackFromEnd(true);
		mRecycler.setLayoutManager(mManager);

		// Set up FirebaseRecyclerAdapter with the Query
		final Query postsQuery = getQuery(mDatabase);
		mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.item_post, PostViewHolder.class, postsQuery) {



			@Override
			protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
				mDialog.dismiss();
				final DatabaseReference postRef = getRef(position);


				viewHolder.setImage(mActivity.getApplicationContext() , model.getImage());






				// Determine if the current user has liked this post and set UI accordingly
				if (model.stars.containsKey(getUid())) {
					viewHolder.starView.setImageResource(R.drawable.ic_drive_eta_black_48dp);
					//Picasso.with(mActivity.getApplicationContext()).load(model.downloadeUrl).into();

				} else {
					viewHolder.starView.setImageResource(R.drawable.ic_alarm1);

				}

				// Bind Post to ViewHolder, setting OnClickListener for the star button
				viewHolder.bindToPost(model, new View.OnClickListener() {
					@Override
					public void onClick(View starView) {
						switch (starView.getId()) {
							case R.id.star:
								FirebaseUser firebaser = FirebaseAuth.getInstance().getCurrentUser();
								//User user = ;
								String uid = firebaser.getUid();
								//String avatar = firebaser.getPhotoUrl().toString();
								//String speaker = "Jirawatee";
								//String picture = "https://pbs.twimg.com/media/C4yDZ6QUkAEJ0ZW.jpg";
								String title = model.title;
								String author = model.author;

								final Topic topic = new Topic(uid, author, title);
								Map<String, Object> topicValues = topic.toMap();
								Map<String, Object> childUpdates = new HashMap<>();

								String topicKey = mDatabase.push().getKey();
								childUpdates.put("/Times/" + topicKey, topicValues );
								childUpdates.put("/Times-topics/" + uid + "/" + topicKey, topicValues);



								//MyHelper.showDialog(this);
								mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
									@Override
									public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
										//MyHelper.dismissDialog();

									}
								});
								break;
						}

						// Need to write to both places the post is stored
						DatabaseReference globalPostRef = mDatabase.child("students").child(postRef.getKey());

						DatabaseReference userPostRef = mDatabase.child("user-students").child(model.uid).child(postRef.getKey());
						// Run two transactions
						onStarClicked(globalPostRef);
                       // onStarClicked(userPostRef);


					}
				});


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mActivity, PostDetailActivity.class);
						intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postRef.getKey());
						startActivity(intent);
					}
				});
			}
		};
		mRecycler.setAdapter(mAdapter);
	}




	private void onStarClicked(final DatabaseReference postRef ) {



		new AlertDialog.Builder(getContext())
				.setTitle("รับเด็กนักเรียน")
				.setMessage("กด ตกลง เพื่อทำการแจ้งเตือน")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int which) {

								postRef.runTransaction(new Transaction.Handler() {
									@Override
									public Transaction.Result doTransaction(MutableData mutableData) {
										Post p = mutableData.getValue(Post.class);
										if (p == null) {
											return Transaction.success(mutableData);
										}

											p.starCount = p.starCount + 1 ;
											p.stars.put(getUid(), true );


										// Set value and report transaction success
										mutableData.setValue(p);

										sendWithOtherThread("token");
										return Transaction.success(mutableData);
									}

									@Override
									public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
										Log.d("postTransaction", "onComplete:" + dataSnapshot.getKey());

									}


								});
							}
						})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
                        postRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                Post p = mutableData.getValue(Post.class);
                                if (p == null) {
                                    return Transaction.success(mutableData);
                                }


                                    // Unstar the post and remove self from stars
                                    p.starCount = p.starCount - 1;
                                    p.stars.remove(getUid());

                                // Set value and report transaction success
                                mutableData.setValue(p);

                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                Log.d("postTransaction", "onComplete:" + dataSnapshot.getKey());
                            }
                        });
					}
				})
				.setIcon(R.drawable.ic_alarm1)
				.show();


	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mAdapter != null) {
			mAdapter.cleanup();
		}
	}

	public void sendTokens(View view) {
		sendWithOtherThread("token");
	}

	public void sendTokens2(View view) {
		sendWithOtherThread2("token");
	}

	private void sendWithOtherThread(final String type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pushNotification(type);
			}
		}).start();
	}


	private void pushNotification(String type) {
		JSONObject jPayload = new JSONObject();
		JSONObject jNotification = new JSONObject();
		JSONObject jData = new JSONObject();
		try {
			jNotification.put("title", "กำลังเดินทางไปรับเด็กนักเรียน");
			jNotification.put("body", " ");
			jNotification.put("sound", "default");
			jNotification.put("badge", "1");
			jNotification.put("click_action", "OPEN_ACTIVITY_1");

			//jData.put("picture_url", "http://opsbug.com/static/google-io.jpg");

			switch(type) {
				case "token":
					JSONArray ja = new JSONArray();
					ja.put("ef8FdgSciCA:APA91bE8DbbBC_E0wWq4KVuR7d1P4Jly99lvYEmLwD8WmHAqjwp9h3964sFW8jXImWelnIxYGcPQGzj2Adbw7_oevNBapiPxqmuYF-DsmwJzrKXlI3nPblXTcLbw7np1wo5CvfGncrdL");
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


	private void sendWithOtherThread2(final String type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pushNotification2(type);
			}
		}).start();
	}


	private void pushNotification2(String type) {
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
					ja.put("ef8FdgSciCA:APA91bE8DbbBC_E0wWq4KVuR7d1P4Jly99lvYEmLwD8WmHAqjwp9h3964sFW8jXImWelnIxYGcPQGzj2Adbw7_oevNBapiPxqmuYF-DsmwJzrKXlI3nPblXTcLbw7np1wo5CvfGncrdL");
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

	public String getUid() {
		return FirebaseAuth.getInstance().getCurrentUser().getUid();
	}

	public abstract Query getQuery(DatabaseReference databaseReference);


}