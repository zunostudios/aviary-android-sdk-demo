package com.dnuon.aviarydemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aviary.android.feather.library.Constants;
import com.aviary.android.feather.sdk.FeatherActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {

    private static final int RESULT_LOAD_IMAGE = 0x10;
    private static final int REUSLT_INVOKE_EDITOR = 0x20 ;
    File mPhotoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // After getting the photo from the camera
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            // Start up Aviary
            Intent newIntent = new Intent( this, FeatherActivity.class );
            newIntent.setData( Uri.fromFile(mPhotoFile) );
            newIntent.putExtra( Constants.EXTRA_IN_API_KEY_SECRET, "YOUR_SECRET_HERE" );
            startActivityForResult( newIntent, REUSLT_INVOKE_EDITOR );

        }

        // After getting the image result from Aviary
        if(requestCode == REUSLT_INVOKE_EDITOR && resultCode == RESULT_OK) {
            Uri theURI = data.getData();
            Toast.makeText(this, "Got Aviary!: " + theURI, Toast.LENGTH_SHORT).show();
            ImageView theImage = (ImageView) findViewById(R.id.image);
            theImage.setImageURI(theURI);
        }
    }


    /**
     * Creates a temporary file that the camera can use to save
     * @return File
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    /**
     * Callback method for our button
     * @param view
     */
    public void invokeEditor(View view) {

        try {
            mPhotoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(mPhotoFile == null) {
            Toast.makeText(this, "Unable to create file!", Toast.LENGTH_SHORT).show();
        } else {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE);
            }
        }


    }
}
