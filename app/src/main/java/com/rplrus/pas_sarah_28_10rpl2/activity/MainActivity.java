package com.rplrus.pas_sarah_28_10rpl2.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rplrus.pas_sarah_28_10rpl2.R;
import com.rplrus.pas_sarah_28_10rpl2.adapter.ContactAdapter;
import com.rplrus.pas_sarah_28_10rpl2.model.ContactModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 101;

    private ArrayList<ContactModel> arrayList;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private TextView tv_noContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab_add = findViewById(R.id.fab_add_main);
        recyclerView = findViewById(R.id.rv_contacts_main);
        tv_noContacts = findViewById(R.id.tv_noContacts_main);

        tv_noContacts.setVisibility(View.INVISIBLE);

        registerForContextMenu(recyclerView);

        addData();

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddContactActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addData() {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("contacts");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ContactModel ContactModel = dataSnapshot.getValue(ContactModel.class);
                    arrayList.add(ContactModel);
                }

                if (arrayList.size() == 0) tv_noContacts.setVisibility(View.VISIBLE);
                else tv_noContacts.setVisibility(View.INVISIBLE);

                adapter = new ContactAdapter(MainActivity.this, arrayList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                        myRef.orderByChild("phone").equalTo(arrayList.get(position).getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String key = "";
                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                    key = childSnapshot.getKey();
                                }

                                progressDialog.dismiss();

                                String name, email, phone, about, gender;
                                int age;

                                name = arrayList.get(position).getName();
                                email = arrayList.get(position).getEmail();
                                phone = arrayList.get(position).getPhone();
                                about = arrayList.get(position).getAbout();
                                gender = arrayList.get(position).getGender();
                                age = arrayList.get(position).getAge();

                                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("email", email);
                                intent.putExtra("phone", phone);
                                intent.putExtra("about", about);
                                intent.putExtra("gender", gender);
                                intent.putExtra("age", age);
                                intent.putExtra("key", key);
                                startActivityForResult(intent, REQUEST_CODE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) addData();
        }

    }
}