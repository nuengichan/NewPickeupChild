package com.example.database.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.database.R;
import com.example.database.models.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {
	public ImageView starView;
	private TextView authorView;
	private TextView bodyView;
	private TextView numStarsView;
	private TextView titleView;

	public PostViewHolder(View itemView) {
		super(itemView);
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
}