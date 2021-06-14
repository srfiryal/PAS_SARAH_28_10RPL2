package com.rplrus.pas_sarah_28_10rpl2.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rplrus.pas_sarah_28_10rpl2.R;
import com.rplrus.pas_sarah_28_10rpl2.model.ContactModel;

public class EditActivity extends AppCompatActivity {
    private String name, email, phone, about, gender, key;
    private int age;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Casting Component
        TextInputEditText txt_name = findViewById(R.id.txt_name_edit);
        TextInputEditText txt_age = findViewById(R.id.txt_age_edit);
        TextInputEditText txt_email = findViewById(R.id.txt_email_edit);
        TextInputEditText txt_phone = findViewById(R.id.txt_phone_edit);
        TextInputEditText txt_about = findViewById(R.id.txt_about_edit);

        AutoCompleteTextView txt_gender = findViewById(R.id.txt_gender_edit);

        Button btn_save = findViewById(R.id.btn_save_edit);

        // Spinner Adapter
        String[] items = new String[]{"Male", "Female", "Non-binary"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item, items);
        txt_gender.setAdapter(arrayAdapter);

        // Spinner OnItemClick
        txt_gender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gender = items[position];
                Toast.makeText(getApplicationContext(), gender, Toast.LENGTH_SHORT).show();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            email = bundle.getString("email");
            phone = bundle.getString("phone");
            about = bundle.getString("about");
            gender = bundle.getString("gender");
            key = bundle.getString("key");
            age = bundle.getInt("age");

            txt_name.setText(name);
            txt_email.setText(email);
            txt_phone.setText(phone);
            txt_about.setText(about);
            txt_gender.setText(gender, false);
            txt_age.setText(String.valueOf(age), TextView.BufferType.EDITABLE);
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference().child("contacts").child(key);

                name = txt_name.getText().toString();
                email = txt_email.getText().toString();
                phone = txt_phone.getText().toString();
                about = txt_about.getText().toString();
                age = Integer.parseInt(txt_age.getText().toString());

                ContactModel contactModel = new ContactModel(name, email, phone, gender, about, age);
                reference.setValue(contactModel);
                Toast.makeText(getApplicationContext(), "Data has been edited", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}