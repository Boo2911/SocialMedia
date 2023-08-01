package com.example.socialmedia.Fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.socialmedia.Adapters.PostsAdapter;
import com.example.socialmedia.Adapters.StoryAdapter;
import com.example.socialmedia.Models.Posts;
import com.example.socialmedia.Models.StoryModel;
import com.example.socialmedia.Models.UserStory;
import com.example.socialmedia.Models.Users;
import com.example.socialmedia.R;
import com.example.socialmedia.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    ShimmerRecyclerView shimmerRecyclerView;
    ArrayList<StoryModel> lists = new ArrayList<>();

    ArrayList<Posts> listPosts = new ArrayList<>();

    FirebaseDatabase database;
    FirebaseAuth auth;
    
    ProgressDialog dialog ;

    ActivityResultLauncher<String> galleryLaucher;

    FirebaseStorage storage;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.bind(root);


        shimmerRecyclerView = root.findViewById(R.id.posts_recyclerview);
        shimmerRecyclerView.showShimmerAdapter();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();


        database.getReference().child("users").child(auth.getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users user = snapshot.getValue(Users.class);
                        Picasso.get().load(user.getProfile_img()).placeholder(R.drawable.profileavatar)
                                .into(binding.profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Uploading Story");
        dialog.setMessage("Please wait..");
        dialog.setCancelable(false);

        StoryAdapter storyAdapter = new StoryAdapter(lists, getContext());
        binding.recyclerViewStory.setAdapter(storyAdapter);
        binding.recyclerViewStory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                lists.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    StoryModel storyModel = new StoryModel();
                    storyModel.setStoryBy(dataSnapshot.getKey());
                    storyModel.setStoryAt(dataSnapshot.child("postedBy").getValue(Long.class));

                    ArrayList<UserStory> stories = new ArrayList<>();
                    for(DataSnapshot dss1: dataSnapshot.child("userStories").getChildren()){

                        UserStory userStory = dss1.getValue(UserStory.class);
                        stories.add(userStory);

                    }
                    storyModel.setStories(stories);
                    lists.add(storyModel);
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        PostsAdapter postsAdapter = new PostsAdapter(listPosts, getContext());

       shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPosts.clear();
                for(DataSnapshot dss: snapshot.getChildren()){
                    Posts posts = dss.getValue(Posts.class);
                    posts.setPostId(dss.getKey());
                    listPosts.add(posts);
                }
                shimmerRecyclerView.setAdapter(postsAdapter);
                shimmerRecyclerView.hideShimmerAdapter();
                postsAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        binding.notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.linearLayout, new NotificationsFragment()).addToBackStack(null).commit();
            }
        });

        binding.addStoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLaucher.launch("image/*");
            }
        });



        galleryLaucher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                binding.storyMain.setImageURI(result);

                dialog.show();

                StorageReference reference = storage.getReference().child("stories")
                        .child(auth.getUid()).child(new Date().getTime()+"");

                reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                StoryModel story = new StoryModel();
                                story.setStoryAt(new Date().getTime());

                                Toast.makeText(getContext(), story.getStoryAt()+"", Toast.LENGTH_SHORT).show();

                                database.getReference().child("stories").child(auth.getUid())
                                        .child("postedBy")
                                        .setValue(story.getStoryAt())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                UserStory ustory = new UserStory(uri.toString(), story.getStoryAt());


                                                database.getReference().child("stories")
                                                        .child(auth.getUid())
                                                        .child("userStories")
                                                        .push()
                                                        .setValue(ustory).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                dialog.dismiss();
                                                                Toast.makeText(getContext(), "Story uploaded..", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });

                    }
                });

            }
        });

        return root;
    }


}