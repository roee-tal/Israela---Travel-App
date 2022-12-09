package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class DetailActivity extends AppCompatActivity
{
    // creating object of ViewPager
    ViewPager mViewPager;

    // images array
    int[] images = {R.drawable.facebook, R.drawable.pic, R.drawable.pic2, R.drawable.facebook,
            R.drawable.pic, R.drawable.pic2, R.drawable.facebook, R.drawable.pic};

    // Creating Object of ViewPagerAdapter
    ViewPagerAdapter mViewPagerAdapter;

    Site selectedShape;
    private StorageReference myStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail);
//        getSelectedShape();
//        setValues();

        setContentView(R.layout.activity_detail);

        // Initializing the ViewPager Object
        mViewPager = (ViewPager)findViewById(R.id.viewPagerMain);

        // Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapter(DetailActivity.this, images);

        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

    }

    private void getSelectedShape()
    {
        Intent previousIntent = getIntent();
        String parsedStringID = previousIntent.getStringExtra("id");
        selectedShape = getParsedShape(parsedStringID);
    }

    private Site getParsedShape(String parsedID)
    {
        for (Site site : MainActivity.shapeList)
        {
            if(site.getId().equals(parsedID))
                return site;
        }
        return null;
    }

    private void setValues()
    {
        TextView tv = (TextView) findViewById(R.id.shapeName);
        ImageView iv = (ImageView) findViewById(R.id.mainImage);

        tv.setText(selectedShape.getId() + " - " + selectedShape.getName());
        this.setimageView(selectedShape.getImage());
//        iv.setImageResource(selectedShape.getImage());
    }

    private void setimageView(String pictureName) {
        pictureName = "shvil.jpg";
        myStorage = FirebaseStorage.getInstance().getReference().child("picture/"+pictureName);
        try {
            final File localTempFile = File.createTempFile("shvil", "jpg");
            myStorage.getFile(localTempFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(DetailActivity.this, "Picture Retrieved",Toast.LENGTH_SHORT).show();
                            Bitmap bitmap = BitmapFactory.decodeFile(localTempFile.getAbsolutePath());
                            ((ImageView) findViewById(R.id.mainImage)).setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailActivity.this, "Error During Picture Retrieved",Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}