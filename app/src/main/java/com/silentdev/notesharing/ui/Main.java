package com.silentdev.notesharing.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.silentdev.notesharing.R;
import com.silentdev.notesharing.myClass.AuthClass;
import com.silentdev.notesharing.ui.account.Account;
import com.silentdev.notesharing.ui.favorites.FavoritesFragment;
import com.silentdev.notesharing.ui.friends.FriendsFragment;
import com.silentdev.notesharing.ui.notes.NotesFragment;
import com.silentdev.notesharing.ui.notifications.NotificationsFragment;
import com.silentdev.notesharing.ui.shared.SharedFragment;
import com.squareup.picasso.Picasso;

public class Main extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView barTitle;
    ImageView barImg;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Custom Actionbar
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_app_bar);
        getSupportActionBar().setElevation(0);

        View appBar = getSupportActionBar().getCustomView();
        barTitle = appBar.findViewById(R.id.titleTxt);
        barImg = appBar.findViewById(R.id.profileImg);

        //loading the default fragment
        barTitle.setText("Notes");
        loadFragment(new NotesFragment());

        // Display avatar
        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String img_url = document.getString("img_url");
                        Picasso.get().load(img_url).placeholder(R.drawable.ic_baseline_person_24).into(barImg);
                    }
                }
            }
        });

        barImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Account.class));
            }
        });

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        if(user == null){
            startActivity(new Intent(Main.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.notes_item:
                barTitle.setText("Notes");
                fragment = new NotesFragment();
                break;

            case R.id.favorites_item:
                barTitle.setText("Favorites");
                fragment = new FavoritesFragment();
                break;

            case R.id.friends_item:
                barTitle.setText("Friends");
                fragment = new FriendsFragment();
                break;

            case R.id.notif_item:
                barTitle.setText("Notifications");
                fragment = new NotificationsFragment();
                break;

        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}