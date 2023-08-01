package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.socialmedia.Fragments.AddFragment;
import com.example.socialmedia.Fragments.HomeFragment;
import com.example.socialmedia.Fragments.ProfileFragment;
import com.example.socialmedia.Fragments.ReelFragment;
import com.example.socialmedia.Fragments.SearchFragment;
import com.example.socialmedia.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        openFragment(new HomeFragment());

        setSupportActionBar(binding.toolbar);
        MainActivity.this.setTitle("My Profile");

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                if (item.getItemId() == R.id.nav_home) {
                    openFragment(new HomeFragment());
                }else if(item.getItemId()==R.id.nav_search){
                    openFragment(new SearchFragment());
                }else if(item.getItemId()==R.id.nav_more){
                    openFragment(new AddFragment());

                }else if(item.getItemId()==R.id.nav_reel){
                    openFragment(new ReelFragment());

                }else if(item.getItemId()==R.id.nav_profile){
                    openFragment(new ProfileFragment());
                    binding.toolbar.setVisibility(View.VISIBLE);

                }else {
                    openFragment(new HomeFragment());

                }

                return true;
            }
        });



    }


    private void openFragment(final Fragment fragment)   {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.linearLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        binding.toolbar.setVisibility(View.GONE);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.settings){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}