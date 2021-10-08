package com.max.def.firebasetutorial;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
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
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private AppCompatImageView imageView;

    private AppCompatButton pickImageBtn;

    private AppCompatTextView detectedText;

    private FirebaseAuth firebaseAuth;

    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.image_view);

        pickImageBtn = findViewById(R.id.pick_image_btn);

        detectedText = findViewById(R.id.detected_text);

        pickImageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == 1)
            {
                Uri uri = null;

                if (data != null)
                {
                    uri = data.getData();
                }

                // convert the uri to correct gallery filepath

                if (uri != null)
                {
                    String[] filePath = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri,filePath,null,null,null);

                    if (cursor != null)
                    {
                        cursor.moveToFirst();

                        int index = cursor.getColumnIndex(filePath[0]);

                        String finalFilePath = cursor.getString(index);

                        cursor.close();

                        imageView.setImageBitmap(BitmapFactory.decodeFile(finalFilePath));

                        // now detect the text from the image

                        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(BitmapFactory.decodeFile(finalFilePath));

                        // firebase vision text recognizer is to recognize the text from the image

                        // now we change the text to face recognizer

                        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector();

                        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>()
                        {
                            @Override
                            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces)
                            {
                                stringBuilder.append("Detected Faces Details : ").append(TextUtils.join(",",firebaseVisionFaces)).append("\n");

                                detectedText.setText(stringBuilder.toString());
                            }
                        });

                    }
                }

            }
        }
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
