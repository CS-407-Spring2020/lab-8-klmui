package com.uw.lab8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageDrawable(null);
    }

    public void upload(View view) {

        try {
            //1. create a reference
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference meRef = storageRef.child("images/me.jpg");

            //2. convert image from Drawable resource to a byte stream
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.me);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] meByteStream = baos.toByteArray();

            //3. Start upload task
            UploadTask uploadTask = meRef.putBytes(meByteStream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("ImageUpload", "Image successfully uploaded to Firebase.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("UploadError", "Image upload failed");
        }
    }

    public void downloadAndSet(View view) {

        // 1. Ref to the object uploaded
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference meRef = storageReference.child("images/me.jpg");

        // 2. Get ImageView obj
        final ImageView imageView = findViewById(R.id.imageView);
        final long TWO_MEGABYTE = (1024 * 1024) * 2;

        // 3. Download the image into a byte stream
        meRef.getBytes(TWO_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // 4. Data for image is returned - get this into a bitmap obj
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // 5. Set the image in the imageView
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
                Log.i("DownloadError", "Image Download Failed");
            }
        });
    }
}
