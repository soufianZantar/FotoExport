package com.example.sofian.fotoexport.listAdapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sofian.fotoexport.R;
import com.example.sofian.fotoexport.model.Album;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sofian on 18/03/2017.
 */
public class AlbumListAdapter  extends ArrayAdapter<Album> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Album> albums = new ArrayList<>();
    ImageView imageView;
    TextView textView;

    public AlbumListAdapter(Context context, int layoutResourceId, ArrayList<Album> albums) {
        super(context, layoutResourceId, albums);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.albums = albums;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View v = inflater.inflate(layoutResourceId, parent, false);

        imageView = (ImageView) v.findViewById(R.id.image_album);
        textView = (TextView) v.findViewById(R.id.album_name);
        imageView.setTag(albums.get(position).getId());
        textView.setText(albums.get(position).getName());

        try {
            Picasso.with(v.getContext()).load(albums.get(position).getPhoto()).fit().centerCrop().into(imageView);
        }
        catch (Exception e) {

        }

        return v;
    }
}
