package com.example.database;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.database.models.Post;
import com.example.database.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends BaseActivity {
	private DatabaseReference mDatabase ;
	private EditText mTitleField, mBodyField;
	private FloatingActionButton mSubmitButton;
	public  ImageView imageView;
	private ImageButton mSelectImage ;
	private  static  final  int GALLERY_REQUEST = 1;
	private Uri mImageUri = null;
	private StorageReference mStorage ;
	private ProgressDialog mProgress;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_post);
		mTitleField = (EditText) findViewById(R.id.field_title);
		mBodyField = (EditText) findViewById(R.id.field_body);
		mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_post);
		imageView = (ImageView) findViewById(R.id.ImageViewPho);

		////// เลือกรูป
		mSelectImage =(ImageButton) findViewById(R.id.imageSelect);
		mSelectImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
				galleryIntent.setType("image/*");
				startActivityForResult(galleryIntent , GALLERY_REQUEST);
			}
		});

		mStorage = FirebaseStorage.getInstance().getReference();
		mDatabase = FirebaseDatabase.getInstance().getReference();

		mProgress = new ProgressDialog(this);

		mSubmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submitPost();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

			mImageUri  = data.getData();
			mSelectImage.setImageURI(mImageUri);
		}
	}

	private boolean validateForm(String title, String body) {
		if (TextUtils.isEmpty(title)) {
			mTitleField.setError(getString(R.string.required));
			return false;
		} else if (TextUtils.isEmpty(body)) {
			mBodyField.setError(getString(R.string.required));
			return false;
		} else {
			mTitleField.setError(null);
			mBodyField.setError(null);
			return true;
		}
	}

	private void submitPost() {

		final String title = mTitleField.getText().toString().trim();
		final String body = mBodyField.getText().toString().trim();
		final String userId = getUid();


		if (validateForm(title, body) && mImageUri != null) {
			// Disable button so there are no multi-posts
			setEditingEnabled(false);

			StorageReference filepath = mStorage.child("Student").child(mImageUri.getLastPathSegment());
			filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

					final Uri downloadUrl = taskSnapshot.getDownloadUrl();
					//showProgressDialog();

					Log.wtf("Testing valid URL", "|"+downloadUrl+"|");

					//Picasso.with(mActivity.getApplicationContext()).load(model.getImage()).resize(50, 50).into(viewHolder.imageView);


					mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							User user = dataSnapshot.getValue(User.class);
							if (user == null) {
								Toast.makeText(NewPostActivity.this, "Error: could not fetch user.", Toast.LENGTH_LONG).show();
							} else {


								writeNewPost(userId, user.username, title, body , downloadUrl.toString());
							}
							setEditingEnabled(true);
							finish();
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {
							setEditingEnabled(true);
							Toast.makeText(NewPostActivity.this, "onCancelled: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
						}
					});



				}
			});


		}
	}

	private void setEditingEnabled(boolean enabled) {
		mTitleField.setEnabled(enabled);
		mBodyField.setEnabled(enabled);
		if (enabled) {
			mSubmitButton.setVisibility(View.VISIBLE);
		} else {
			mSubmitButton.setVisibility(View.GONE);
		}
	}

	private void writeNewPost(String userId, String username, String title, String body , String downloadeUrl ) {
		// Create new post at /user-posts/$userid/$postid
		// and at /posts/$postid simultaneously
		String key = mDatabase.child("students").push().getKey();
		Post post = new Post(userId, username, title, body , downloadeUrl);
		Map<String, Object> postValues = post.toMap();

		Map<String, Object> childUpdates = new HashMap<>();
		childUpdates.put("/students/" + key, postValues);
		childUpdates.put("/user-students/" + userId + "/" + key, postValues);

		mDatabase.updateChildren(childUpdates);
	}


}