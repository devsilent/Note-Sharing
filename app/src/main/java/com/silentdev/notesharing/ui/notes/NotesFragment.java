package com.silentdev.notesharing.ui.notes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.silentdev.notesharing.R;
import com.silentdev.notesharing.models.NotesModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NotesFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    FloatingActionButton fabAdd;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    String DATE_FORMAT = "h:mm a dd MMM yyyy";

    public NotesFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

       // Layouts
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        recyclerView = view.findViewById(R.id.recycler);
        progressBar = view.findViewById(R.id.progressBar);
        fabAdd = view.findViewById(R.id.floatingActionButton);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewNote.class));
            }
        });

        showNotes();

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                showNotes();
            }
        });

        // Hide show fab when scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fabAdd.getVisibility() == View.VISIBLE) {
                    fabAdd.hide();
                } else if (dy < 0 && fabAdd.getVisibility() != View.VISIBLE) {
                    fabAdd.show();
                }
            }
        });

        return view;
    }

    private void showNotes() {
        Query query = db.collection("notes").whereEqualTo("userId", mAuth.getCurrentUser().getUid());

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                int count = 0;
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        count++;
                    }
                    if (count == 0) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Empty Notes", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

            }
        });

        FirestoreRecyclerOptions<NotesModel> options = new FirestoreRecyclerOptions.Builder<NotesModel>()
                .setQuery(query, NotesModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<NotesModel, NotesFragment.NotesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotesFragment.NotesViewHolder holder, final int position, @NonNull NotesModel model) {

                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

                final String noteId = getSnapshots().getSnapshot(position).getId();

                holder.txtTitle.setText(model.getTitle());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.txtBody.setText(Html.fromHtml(model.getBody(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    holder.txtBody.setText(Html.fromHtml(model.getBody()));
                }

                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date date = model.getDate_added().toDate();

                holder.txtDate.setText(dateFormat.format(date)+"");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Intent intent = new Intent(getContext(), ApartmentView.class);
                        intent.putExtra("id", propertyId);
                        startActivity(intent);*/
                        //Toast.makeText(getContext(), propertyId, Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @NonNull
            @Override
            public NotesFragment.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //recyclerView.setVisibility(View.VISIBLE);
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notes, parent, false);
                return new NotesFragment.NotesViewHolder(view);
            }

        };

        adapter.startListening();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

    }

    //View Holder Class
    public class NotesViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtBody, txtDate;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.titleTxt);
            txtBody = itemView.findViewById(R.id.bodyTxt);
            txtDate = itemView.findViewById(R.id.dateTxt);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}