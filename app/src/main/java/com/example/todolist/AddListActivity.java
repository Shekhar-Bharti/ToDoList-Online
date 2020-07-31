package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.CaseMap;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddListActivity extends AppCompatActivity {
    private EditText editList;
    private EditText editDescription;
    private Button buttonSave;
    private Button buttonDiscard;
    private FirebaseFirestore fstore;
    private String userID;
    private String currentDate;
    private final String DATE = "Date";
    private final String TITLE = "Title";
    private final String DESCRIPTION = "Description";
    private String TAG="Add List";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        buttonDiscard = findViewById(R.id.buttonDiscard);
        buttonSave = findViewById(R.id.buttonSave);
        editList = findViewById(R.id.titleText);
        editDescription = findViewById(R.id.descriptionText);
        userID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        fstore=FirebaseFirestore.getInstance();
    }

    public void save(View view)
    {
        String title = editList.getText().toString();
        if(title.length()<3) {
            editList.setError("Too Short");
            return;
        }
        if(title.length()>20) {
            editList.setError("Too Long");
            return;
        }

        currentDate= DateFormat.getDateInstance().format(Calendar.getInstance().getTime());
        DocumentReference documentReference = fstore.document("users/"+userID+"/information/"+editList.getText().toString());
        //DocumentReference documentReference = fstore.document("users/"+userID);
        Map<String,Object> data= new HashMap<>();
        data.put(TITLE,editList.getText().toString());
        data.put(DESCRIPTION,editDescription.getText().toString());
        data.put(DATE,currentDate);
        documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Successfully added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed");
            }
        });
        startActivity(new Intent(getApplicationContext(),ToDoListActivity.class));
        finish();
    }

    public void discard(View view)
    {
        startActivity(new Intent(getApplicationContext(),ToDoListActivity.class));
        finish();
    }
}
