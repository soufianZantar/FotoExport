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
import com.example.sofian.fotoexport.model.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sofian on 19/03/2017.
 */
public class GalleryListAdapter extends ArrayAdapter<Photo> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Photo> photos = new ArrayList<>();
    ImageView imageView,checked_image;

    public GalleryListAdapter(Context context, int layoutResourceId, ArrayList<Photo> photos) {
        super(context, layoutResourceId, photos);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.photos = photos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View v = inflater.inflate(layoutResourceId, parent, false);

        imageView = (ImageView) v.findViewById(R.id.image_gallery);
        checked_image = (ImageView) v.findViewById(R.id.image_checked);
        if(photos.get(position).isChecked()==true)
            checked_image.setVisibility(View.VISIBLE);
        else checked_image.setVisibility(View.GONE);
//        imageView.setTag(photos.get(position).getId());
        Log.d("///////////////////", photos.get(position).getUrl());

        try {
            Picasso.with(v.getContext()).load(photos.get(position).getUrl()).fit().centerCrop().into(imageView);
        }
        catch (Exception e) {

        }

        return v;
    }
}
