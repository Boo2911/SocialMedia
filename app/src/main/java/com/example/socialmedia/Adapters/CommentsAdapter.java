package com.example.socialmedia.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Models.CommentsModel;
import com.example.socialmedia.Models.Users;
import com.example.socialmedia.R;
import com.example.socialmedia.databinding.SampleCommentsBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.viewHolder> {

    ArrayList<CommentsModel> commentList;
    Context context;

    public CommentsAdapter(ArrayList<CommentsModel> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_comments, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        CommentsModel model = commentList.get(position);
        holder.binding.commentTime.setText(TimeAgo.using(model.getCommentedAt()));

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(model.getCommentedBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Users user = snapshot.getValue(Users.class);
                        Picasso.get().load(user.getProfile_img())
                                .placeholder(R.drawable.profileavatar)
                                .into(holder.binding.profileCommentor);
                        holder.binding.commentwithname.setText(Html.fromHtml("<b>"+user.getName()+"</b>"+" "+ model.getCommentBody()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {

        SampleCommentsBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleCommentsBinding.bind(itemView);
        }
    }
}
