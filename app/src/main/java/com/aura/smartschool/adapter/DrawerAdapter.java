package com.aura.smartschool.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-06-14.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private ArrayList<String> mMenuList;

    public DrawerAdapter(String[] menu)  {
        ArrayList<String> menuList = new ArrayList<>();
        for(String s:menu) {
            menuList.add(s);
        }
        mMenuList = menuList;
    }

    public DrawerAdapter(ArrayList<String> menu)  {
        mMenuList = menu;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View v = vi.inflate(android.R.layout.test_list_item, viewGroup, false);
        TextView tv = (TextView) v.findViewById(android.R.id.text1);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.mTextView.setText(mMenuList.get(i));
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }
}
