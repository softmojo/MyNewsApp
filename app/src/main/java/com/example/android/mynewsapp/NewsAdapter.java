package com.example.android.mynewsapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pc on 7/30/2018.
 */

public class NewsAdapter extends ArrayAdapter<Newsfeeds> {
    private static final String DATE_TIME_SEPARATOR = "T";

    NewsAdapter(Activity context, ArrayList<Newsfeeds> newsfeeds) {
        super(context, 0, newsfeeds);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

            final Newsfeeds currentNewsfeed = getItem(position);

            TextView sectionView = listItemView.findViewById(R.id.section);
            sectionView.setText(currentNewsfeed.getSectionName());

            TextView webTitleView = listItemView.findViewById(R.id.web_title);
            webTitleView.setText(currentNewsfeed.getWebTitle());

            String dateTime = currentNewsfeed.getWebPublicationDate();
            String[] publicationDateTime = dateTime.split(DATE_TIME_SEPARATOR);
            String publicationDate = publicationDateTime[0];

            TextView publicationDateView = listItemView.findViewById(R.id.publication_date);
            publicationDateView.setText(publicationDate);

            ImageView thumbnailView = listItemView.findViewById(R.id.thumbnail_view);
            if(currentNewsfeed.getThumbnail() != null){
                thumbnailView.setImageBitmap(currentNewsfeed.getThumbnail());
            }

            TextView authorName = listItemView.findViewById(R.id.author_name);

            if(currentNewsfeed.getAuthorName()!= null){
                authorName.setText(currentNewsfeed.getAuthorName());
            }
            else{authorName.setVisibility(View.GONE);}

        }


        return listItemView;

    }

}
