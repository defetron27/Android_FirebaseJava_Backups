package com.max.def.firebasetutorial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.languageid.IdentifiedLanguage;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity
{
    private AppCompatEditText nameEdit;
    private AppCompatButton detectBtn;

    private AppCompatTextView detectedLanguages;

    private FirebaseAuth firebaseAuth;

    private StringBuilder stringBuilder = new StringBuilder();

    private FirebaseLanguageIdentification firebaseLanguageIdentification;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        nameEdit = findViewById(R.id.name_edit);

        detectBtn = findViewById(R.id.detect_btn);

        detectedLanguages = findViewById(R.id.lang);

        firebaseLanguageIdentification = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String name = nameEdit.getText().toString();

                if (TextUtils.isEmpty(name))
                {
                    Toast.makeText(MainActivity.this, "Please enter any name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseLanguageIdentification.identifyPossibleLanguages(name).addOnSuccessListener(new OnSuccessListener<List<IdentifiedLanguage>>()
                    {
                        @Override
                        public void onSuccess(List<IdentifiedLanguage> identifiedLanguages)
                        {
                            //this result gives all possible languages code

                            stringBuilder.append("Possible Languages : \n");

                            for (IdentifiedLanguage identifiedLanguage : identifiedLanguages)
                            {
                                stringBuilder.append(identifiedLanguage.getLanguageCode()).append(",\n");
                            }

                            detectedLanguages.setText(stringBuilder.toString());
                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null)
        {
            signInUser();
        }
    }

    private void signInUser()
    {
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
