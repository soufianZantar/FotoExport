package com.example.sofian.fotoexport;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.sofian.fotoexport.listAdapter.AlbumListAdapter;
import com.example.sofian.fotoexport.listAdapter.GalleryListAdapter;
import com.example.sofian.fotoexport.model.Album;
import com.example.sofian.fotoexport.model.Photo;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Calendar;

public class GalleryActivity extends AppCompatActivity implements   ConnectionCallbacks,
        OnConnectionFailedListener {
    String albumId;
    ArrayList<Photo> photos;
    private GridView myGrid;
    private GalleryListAdapter gridAdapter;
    private Button btnExport;
    private ProgressBar progExport;
    private int nbPhoto;
    private int nbLoop=0;
    int myLastVisiblePos;
    private GoogleApiClient mGoogleApiClient;
    private Bitmap mBitmapToSave;
    private static final String TAG = "drive-google";
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private  int countPhoto=0;
    private int offset = 0;
    private int limit = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        albumId=getIntent().getStringExtra("albumId");
        countPhoto=getIntent().getIntExtra("countPhoto", 0);
        photos = new ArrayList<Photo>();
        myGrid = (GridView) findViewById(R.id.grid_gallery);
        btnExport =(Button) findViewById(R.id.btn_export);
        progExport=(ProgressBar) findViewById(R.id.prog_export);
        progExport.setMax(100);
         getAlbumPhotos(albumId, offset, limit);

        myGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Photo photo = (Photo) parent.getItemAtPosition(position);
                if (photo.isChecked() == false)
                    photo.setChecked(true);
                else photo.setChecked(false);
                gridAdapter.notifyDataSetChanged();
            }
        });

        myGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int currentFirstVisPos = view.getFirstVisiblePosition();
                    if (currentFirstVisPos > myLastVisiblePos) {
                        if (countPhoto > offset + limit) {
                            offset = offset + limit;
                            if (countPhoto - offset >= 12)
                                limit = 12;
                            else
                                limit = countPhoto - offset;
                            getAlbumPhotos(albumId, offset, limit);
                        }
                    }
                    myLastVisiblePos = currentFirstVisPos;

            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Photo> photsCheck = new ArrayList<Photo>() ;

                for(int i=0;i<photos.size();i++){
                   if(photos.get(i).isChecked()==true) {
                       photsCheck.add(photos.get(i));
                   }
                }
                nbPhoto=photsCheck.size();
                nbLoop=0;
                if(photsCheck.size()==0){
                    Toast.makeText(GalleryActivity.this, "Select one  photo or more!",
                            Toast.LENGTH_LONG).show();
                } else {
                    btnExport.setVisibility(View.GONE);
                    progExport.setVisibility(View.VISIBLE);
                    for (int i = 0; i < photsCheck.size(); i++) {
                        saveFileToDrive(photsCheck.get(i).getUrl(), photsCheck.get(i).getId() + i);
                    }
                }
            }
        });
    }

    private void saveFileToDrive(final String urlFoto, final String nameFoto) {
        Log.i(TAG, "Creating new contents.");
        final Bitmap image = mBitmapToSave;
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveContentsResult>() {

                    @Override
                    public void onResult(DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }
                        Log.i(TAG, "New contents created.");
                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        Bitmap image2 = null;
                        try {
                            URL url = new URL(urlFoto);
                            image2 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        } catch (IOException e) {
                            System.out.println(e);
                        }

                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        image2.compress(Bitmap.CompressFormat.JPEG, 100, bitmapStream);
                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            Log.i(TAG, "Unable to write file contents.");
                        }
                        Calendar c = Calendar.getInstance();
                        int seconds = c.get(Calendar.SECOND);
                        int min=c.get(Calendar.MINUTE);
                        int date=c.get(Calendar.DATE);

                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle("Photo-" + nameFoto +"-"+ date+"-"+ min+"-"+ seconds + ".jpg").build();

                        Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                .createFile(mGoogleApiClient, metadataChangeSet, result.getDriveContents())
                                .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                    @Override
                                    public void onResult(DriveFolder.DriveFileResult driveFileResult) {
                                        if (!driveFileResult.getStatus().isSuccess()) {
                                            Log.e(TAG, "Error Unable to create file.");
                                            return;
                                        }
                                        Log.v(TAG, "Created a file: " + driveFileResult.getDriveFile().getDriveId());
                                        nbLoop++;
                                        int progVal = ((nbLoop * 100) / nbPhoto);
                                        progExport.setProgress(progVal);
                                        if (nbLoop == nbPhoto) {
                                            Intent intent = new Intent(GalleryActivity.this, SuccessActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "API client connected.");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }
    private void getAlbumPhotos(final String albumId, int offsetValue, int limitValue){
        offset = offsetValue;
        limit = limitValue;
        Bundle parameters = new Bundle();
        parameters.putString("fields", "images");
        parameters.putString("offset", String.valueOf(offset));
        parameters.putString("limit", String.valueOf(limit));
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + albumId + "/photos",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.v("TAG", "Facebook Photos response: " + response);
                        try {
                            if (response.getError() == null) {
                                JSONObject joMain = response.getJSONObject();
                                if (joMain.has("data")) {
                                    JSONArray jaData = joMain.optJSONArray("data");
                                    for (int i = 0; i < jaData.length(); i++) {
                                        JSONObject joAlbum = jaData.getJSONObject(i);
                                        JSONArray jaImages = joAlbum.getJSONArray("images");
                                        if (jaImages.length() > 0) {
                                            photos.add(new Photo(""+i, jaImages.getJSONObject(0).getString("source"), false));
                                        }
                                    }
                                    if(offset==0) {
                                        gridAdapter = new GalleryListAdapter(GalleryActivity.this, R.layout.item_grid_gallery, photos);
                                        myGrid.setAdapter(gridAdapter);
                                    }
                                   else gridAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.v("TAG", response.getError().toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
}
