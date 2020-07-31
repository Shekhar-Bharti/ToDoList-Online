package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.Context;

public class ToDoListActivity extends AppCompatActivity {
    private ArrayList<E_Item> arrayList;
   // private ArrayList<String> array=new ArrayList<>();
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore fstore;
    private FirebaseAuth mAuth;
    private final String TAG = "ToDoListActivity";
    private String userId;
    private final String DATE = "Date";
    private final String TITLE = "Title";
    private final String DESCRIPTION = "Description";
    private String date;
    private String title;
    private String description;
    public EditText descriptionText;
    public EditText titleText;
    public TextView dateText;
    public int position1;
    private ImageButton deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mAuth = FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        descriptionText=findViewById(R.id.descriptionEditText);
        dateText=findViewById(R.id.dateText);
        titleText=findViewById(R.id.title);
        deleteBtn=findViewById(R.id.deleteButton);
        readData();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title!=null){
                    DocumentReference db=fstore.document("users/"+userId+"/information/"+title);
                    db.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),title+"Deleted",Toast.LENGTH_LONG);
                        }
                    });
                    arrayList.remove(position1);
                    clearData();
                    initiateRecyclerView();
                }
            }
        });
    }

    private void initiateRecyclerView(){
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerViewAdapter adapter=new RecyclerViewAdapter(this,arrayList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListner(new RecyclerViewAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                //Toast.makeText( getApplicationContext(),"clicked on"+ arrayList.get(position).getDate(),Toast.LENGTH_LONG).show();
                descriptionText.setText(arrayList.get(position).getDescription());
                titleText.setText(arrayList.get(position).getTitle());
                dateText.setText(arrayList.get(position).getDate());
                position1=position;
            }
        });
    }

    private void readData(){
        CollectionReference collectionReference=fstore.collection("users/"+userId+"/information");
        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<E_Item> aList=new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                title=document.getString(TITLE);
                                description=document.getString(DESCRIPTION);
                                date=document.getString(DATE);
                                E_Item e_item=new E_Item(title,description,date);
                                aList.add(e_item);
                            }
                            CollectData(aList);
                        } else {
                            Toast.makeText(ToDoListActivity.this, "doesNot Exist", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void CollectData(ArrayList<E_Item> aList){
        arrayList=new ArrayList<>(aList);
        initiateRecyclerView();
    }

    void clearData(){
        descriptionText.setText("");
        titleText.setText("");
        dateText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logOut:   signOut();
                                return true;

            case R.id.addList:  addList();
                                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);
        googleSignInClient.signOut();
        startActivity(new Intent(getApplicationContext(),SignInActivity.class));
        finish();
    }

    public void addList() {
        startActivity(new Intent(getApplicationContext(),AddListActivity.class));
    }
}
