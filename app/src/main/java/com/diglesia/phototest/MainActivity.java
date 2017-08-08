package com.diglesia.phototest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private File mPhotoFile;
    private ImageView mPhotoView;
    private static final int REQUEST_PHOTO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhotoView = (ImageView)findViewById(R.id.image_view);

        final ImageButton photoButton = (ImageButton) findViewById(R.id.photo_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File filesDir = getFilesDir();
                mPhotoFile = new File(filesDir, "IMG_foo.jpg");

                Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.diglesia.phototest.fileprovider", mPhotoFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // skipped checking with packagemanager and resolver
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PHOTO) {
            Log.i("DEI", mPhotoFile.toString());
            Uri uri = FileProvider.getUriForFile(this, "com.diglesia.phototest.fileprovider", mPhotoFile);
            revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            // old way - uses up a lot of RAM!
            /*Bitmap bm = BitmapFactory.decodeFile(mPhotoFile.getPath());
            Log.i("DEI", bm.getByteCount()+" "+bm.getAllocationByteCount());
            mPhotoView.setImageBitmap(bm);
*/

            // New way - get size of top-level view, use that as max height/width. Can also send any other values for desired height/width.
            View rootView = findViewById(R.id.root_view);
            Log.i("DEI", "display:"+rootView.getWidth()+" "+rootView.getHeight());

            Bitmap bm = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), rootView.getWidth(), rootView.getHeight() );
            Log.i("DEI", bm.getByteCount()+" "+bm.getAllocationByteCount()+" "+bm.getWidth()+" "+bm.getHeight());
            mPhotoView.setImageBitmap(bm);

        }
    }
}
