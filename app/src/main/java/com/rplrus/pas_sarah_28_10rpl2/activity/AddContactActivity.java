package com.rplrus.pas_sarah_28_10rpl2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rplrus.pas_sarah_28_10rpl2.R;
import com.rplrus.pas_sarah_28_10rpl2.model.ContactModel;

public class AddContactActivity extends AppCompatActivity {

    private String name, email, phone, about, gender, key;
    private int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("contacts");

        TextInputEditText txt_name = findViewById(R.id.txt_name_add);
        TextInputEditText txt_age = findViewById(R.id.txt_age_add);
        TextInputEditText txt_email = findViewById(R.id.txt_email_add);
        TextInputEditText txt_phone = findViewById(R.id.txt_phone_add);
        TextInputEditText txt_about = findViewById(R.id.txt_about_add);

        AutoCompleteTextView txt_gender = findViewById(R.id.txt_gender_add);

        Button btn_add = findViewById(R.id.btn_save_add);

        String[] items = new String[]{"Male", "Female", "Non-binary"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item, items);
        txt_gender.setAdapter(arrayAdapter);

        txt_gender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gender = items[position];
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = txt_name.getText().toString();
                String ageString = txt_age.getText().toString();
                email = txt_email.getText().toString();
                phone = txt_phone.getText().toString();
                about = txt_about.getText().toString();

                if (name.trim().isEmpty() || ageString.trim().isEmpty() || email.trim().isEmpty() || phone.trim().isEmpty() || about.trim().isEmpty() || gender.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                } else {
                    age = Integer.parseInt(txt_age.getText().toString());

                    String key = myRef.push().getKey();
                    ContactModel contactModel = new ContactModel(name, email, phone, gender, about, age);
                    myRef.child(key).setValue(contactModel);

                    txt_name.setText("");
                    txt_age.setText("");
                    txt_email.setText("");
                    txt_phone.setText("");
                    txt_gender.setText("");
                    txt_about.setText("");

                    Toast.makeText(getApplicationContext(), "Data has been successfully added!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}