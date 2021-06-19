package com.silentdev.notesharing.ui.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.silentdev.notesharing.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

public class NewNote extends AppCompatActivity {

    RichEditor editor;
    LinearLayout layoutFormat;
    EditText editTitle;
    ImageView imgBold, imgItalic, imgUnderline, imgBullet;
    String body = "";
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Layouts
        editor = findViewById(R.id.editor);
        layoutFormat = findViewById(R.id.formatLayout);
        editTitle = findViewById(R.id.titleEdit);
        imgBold = findViewById(R.id.boldImg);
        imgItalic = findViewById(R.id.italicImg);
        imgUnderline = findViewById(R.id.underlineImg);
        imgBullet = findViewById(R.id.bulletImg);

        layoutFormat.setVisibility(View.GONE);

        // Editor settings
        editor.setPadding(10, 10, 10, 10);
        editor.setPlaceholder("Enter your note here...");

        editor. setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                // Do Something
                body = text;
                Log.d("RichEditor", "Preview " + body);
            }
        });

        // Hide format layout when touched
        formatHide();

        // Format clicks
        imgBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.setBold();
            }
        });

        imgItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.setItalic();
            }
        });

        imgUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.setUnderline();
            }
        });

        imgBullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.setBullets();
            }
        });

    }

    private void saveToDb(String body) {

        ProgressDialog progressDialog = new ProgressDialog(NewNote.this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String title = editTitle.getText().toString();
        Date date = new Date();

        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("body", body);
        note.put("userId", mAuth.getCurrentUser().getUid());
        note.put("date_added", date);

        db.collection("notes").add(note).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(getApplicationContext(),"Note Added!", Toast.LENGTH_SHORT).show();
                finish();
                progressDialog.dismiss();
            }
        });

    }

    private void formatHide() {
        editTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                layoutFormat.setVisibility(View.GONE);
                return false;
            }
        });

        editor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                layoutFormat.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.undo_item:
                editor.undo();
                return true;
            case R.id.redo_item:
                editor.redo();
                return true;
            case R.id.save_item:
                saveToDb(body);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}