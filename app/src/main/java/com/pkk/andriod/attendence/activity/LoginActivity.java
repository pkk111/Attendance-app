package com.pkk.andriod.attendence.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.pkk.andriod.attendence.R;

public class LoginActivity extends AppCompatActivity {

    private boolean isTeacher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }

    void initialize() {
        Spinner spinner = findViewById(R.id.user_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_type, R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1)
                    isTeacher = true;
                else if (position == 0)
                    isTeacher = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button signin = findViewById(R.id.sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {

    }


}
