package com.example.database.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.database.R;
import com.example.database.models.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import static com.example.database.R.string.firebase_database_url;
import static com.example.database.R.string.post;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
public class PostViewHolder extends RecyclerView.ViewHolder {
	public ImageView starView;
	public ImageView  imageView;
	private TextView authorView;
	private TextView bodyView;
	private TextView numStarsView;
	private TextView titleView;
	public ImageView downloadeUrl ;
	private DatabaseReference mDatabase;


	View mView ;

	public PostViewHolder(View itemView) {
		super(itemView);
	mView = itemView  ;
		mDatabase = FirebaseDatabase.getInstance().getReference();
		titleView = (TextView) itemView.findViewById(R.id.post_title);
		authorView = (TextView) itemView.findViewById(R.id.post_author);
		starView = (ImageView) itemView.findViewById(R.id.star);
		numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
		bodyView = (TextView) itemView.findViewById(R.id.post_body);

	}

	public void bindToPost(Post post, View.OnClickListener starClickListener) {
		titleView.setText(post.title);
		authorView.setText(post.author);
		numStarsView.setText(String.valueOf(post.starCount));
		bodyView.setText(post.body);
		starView.setOnClickListener(starClickListener);

	}

	public void setImage(Context ctx,String downloadeUrl){

		ImageView post_image = (ImageView) mView.findViewById(R.id.ImageViewPho);
		Picasso.with(ctx).load(downloadeUrl).into(post_image);
		Log.wtf("Testing valid URL", "|"+downloadeUrl+"|");



	}


    public void bindToPost2(Post post, View.OnClickListener starClickListener) {

		titleView.setText(post.title);
		authorView.setText(post.author);
		numStarsView.setText(String.valueOf(post.starCount));
		bodyView.setText(post.body);
		starView.setOnClickListener(starClickListener);
    }
}