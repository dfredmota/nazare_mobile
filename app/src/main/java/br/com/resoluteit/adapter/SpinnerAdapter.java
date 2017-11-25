package br.com.resoluteit.adapter;

/**
 * Created by fred on 24/11/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



import java.util.List;

import resoluteit.com.br.R;


public class SpinnerAdapter extends ArrayAdapter<String> {

    private List<String> mData;
    public Resources mResources;
    private LayoutInflater mInflater;


    public SpinnerAdapter(
            Activity activitySpinner,
            int textViewResourceId,
            List<String> objects,
            Resources resLocal
    ) {
        super(activitySpinner, textViewResourceId, objects);

        mData = objects;
        mResources = resLocal;
        mInflater = (LayoutInflater) activitySpinner.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = mInflater.inflate(R.layout.adapter_spinner, parent, false);
        TextView label = (TextView) row.findViewById(R.id.list_item);
        label.setText(mData.get(position).toString());

        label.setBackgroundResource(R.drawable.edittext_bg);


        return row;
    }
}