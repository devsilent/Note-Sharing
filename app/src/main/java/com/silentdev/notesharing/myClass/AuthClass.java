package com.silentdev.notesharing.myClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.silentdev.notesharing.ui.Login;
import com.silentdev.notesharing.ui.Main;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthClass {

    String TAG = "wampipti";

    public void signOutGoogle(Context context, FirebaseAuth mAuth) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Signing-out");
        progressDialog.setCancelable(false);
        progressDialog.show();

        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();

        GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(context ,gso);
        googleSignInClient.signOut();
        mAuth.signOut();
        context.startActivity(new Intent(context, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        ((Activity)context).finish();

        progressDialog.dismiss();
    }

    public void createUserGoogle(String uid, Context context, FirebaseAuth mAuth) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Signing-in");
        progressDialog.setCancelable(false);
        progressDialog.show();

        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        context.startActivity(new Intent(context, Main.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        String msg = "User signed-in";
                        displayToast(msg, context);
                        ((Activity)context).finish();
                        progressDialog.dismiss();
                    } else {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Date date = new Date();
                        String url = user.getPhotoUrl().toString();

                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("name", user.getDisplayName());
                        userInfo.put("email", user.getEmail());
                        userInfo.put("phone", user.getPhoneNumber());
                        userInfo.put("img_url", url);
                        userInfo.put("date_joined", date);

                        db.collection("users").document(uid)
                                .set(userInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        context.startActivity(new Intent(context, Main.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                        String msg = "User signed-in";
                                        displayToast(msg, context);
                                        ((Activity)context).finish();
                                        progressDialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }
                }
            }
        });
    }

    private void displayToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
