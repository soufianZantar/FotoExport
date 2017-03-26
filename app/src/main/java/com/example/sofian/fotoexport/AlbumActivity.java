package com.example.sofian.fotoexport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.sofian.fotoexport.listAdapter.AlbumListAdapter;
import com.example.sofian.fotoexport.model.Album;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {
    ArrayList alFBAlbum = new ArrayList<>();
    ArrayList<Album> albums;
    private GridView myGrid;
    private AlbumListAdapter gridAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        albums = new ArrayList<Album>();

        myGrid = (GridView) findViewById(R.id.gridView);
        Bundle params = new Bundle();
        params.putString("fields", "picture,name,count");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + AccessToken.getCurrentAccessToken().getUserId() + "/albums",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            if (response.getError() == null) {
                                JSONObject joMain = response.getJSONObject();
                                if (joMain.has("data")) {
                                    JSONArray jaData = joMain.optJSONArray("data");
                                    alFBAlbum = new ArrayList<>();
                                    for (int i = 0; i < jaData.length(); i++) {
                                        JSONObject joAlbum = jaData.getJSONObject(i);
                                        Log.i("album", joAlbum.toString());
                                        albums.add(new Album(joAlbum.optString("id"), joAlbum.optString("name"), joAlbum.getJSONObject("picture").getJSONObject("data").getString("url"),joAlbum.optInt("count")));

                                    }
                                    gridAdapter = new AlbumListAdapter(AlbumActivity.this, R.layout.item_grid_album, albums);
                                    myGrid.setAdapter(gridAdapter);
                                }
                            } else {
                                Log.d("Test", response.getError().toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

        myGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = (Album) parent.getItemAtPosition(position);
                Intent intent = new Intent(AlbumActivity.this, GalleryActivity.class);
                Log.d("Album_id",album.getId());
                intent.putExtra("albumId", album.getId());
                intent.putExtra("countPhoto", album.getCount());
                startActivity(intent);
            }
        });
    }
}


