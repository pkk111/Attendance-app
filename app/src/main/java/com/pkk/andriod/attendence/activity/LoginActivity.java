package com.pkk.andriod.attendence.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.pkk.andriod.attendence.R;

public class LoginActivity extends AppCompatActivity {

    private boolean isTeacher = false;
    private ImageButton student;
    private ImageButton teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }

    void initialize() {
        student = findViewById(R.id.student_login);
        teacher = findViewById(R.id.teacher_login);
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentLogin();
            }
        });
        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teacherLogin();
            }
        });
    }

    private void studentLogin() {
        Intent i = new Intent(this, StudentActivity.class);
        startActivity(i);
    }

    private void teacherLogin() {
        Intent i = new Intent(this, TeacherActivity.class);
        startActivity(i);
    }
    
}
