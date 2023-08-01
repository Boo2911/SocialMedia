package com.example.socialmedia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Models.StoryModel;
import com.example.socialmedia.Models.UserStory;
import com.example.socialmedia.Models.Users;
import com.example.socialmedia.R;
import com.example.socialmedia.databinding.StorySampleViewBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.viewHolder>{

    ArrayList<StoryModel> lists;
    Context context;

    public StoryAdapter(ArrayList<StoryModel> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_sample_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        StoryModel models = lists.get(position);

        if(models.getStories().size()>0) {
            UserStory lastStory = models.getStories().get(models.getStories().size() - 1);

            Picasso.get().load(lastStory.getImage()).placeholder(R.drawable.profileavatar).into(holder.binding.storyProfile);
            holder.binding.statusCircle.setPortionsCount(models.getStories().size());

            FirebaseDatabase.getInstance().getReference().child("users")
                    .child(models.getStoryBy())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Users users = snapshot.getValue(Users.class);
                            Picasso.get().load(users.getProfile_img()).placeholder(R.drawable.profileavatar).into(holder.binding.story);
                            holder.binding.usernameStory.setText(users.getName());

                            holder.binding.storyProfile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ArrayList<MyStory> myStories = new ArrayList<>();

                                    for (UserStory story : models.getStories()) {
                                        myStories.add(new MyStory(story.getImage()));
                                    }

                                    new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                            .setStoriesList(myStories)
                                            .setStoryDuration(5000)
                                            .setTitleText(users.getName())
                                            .setSubtitleText("")
                                            .setTitleLogoUrl(users.getProfile_img())
                                            .setStoryClickListeners(new StoryClickListeners() {
                                                @Override
                                                public void onDescriptionClickListener(int position) {

                                                }

                                                @Override
                                                public void onTitleIconClickListener(int position) {
                                                }
                                            })
                                            .build()
                                            .show();

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        StorySampleViewBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = StorySampleViewBinding.bind(itemView);
        }
    }
}
