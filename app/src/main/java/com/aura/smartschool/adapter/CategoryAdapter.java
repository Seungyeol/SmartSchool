package com.aura.smartschool.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aura.smartschool.R;

/**
 * Created by eastflag on 2015-07-23.
 */
public class CategoryAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private String [] mStrings;
    private int [] mImages = {R.drawable.map_0, R.drawable.map_1, R.drawable.map_2, R.drawable.map_3,
            R.drawable.map_4, R.drawable.map_5, R.drawable.map_6};

    public CategoryAdapter(Context context, int resource, String [] objects) {
        super(context, resource, objects);
        mContext = context;
        mStrings = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = View.inflate(mContext, R.layout.spinner_category, null);
        TextView label=(TextView)row.findViewById(R.id.tv_map);
        label.setText(mStrings[position]);

        ImageView icon=(ImageView)row.findViewById(R.id.iv_map);
        icon.setImageResource(mImages[position]);

        return row;
    }
}
