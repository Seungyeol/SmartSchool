package com.aura.smartschool.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aura.smartschool.Interface.DrawerSelectedListener;
import com.aura.smartschool.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-06-14.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private ArrayList<String> mMenuList;
    private DrawerSelectedListener mListener;

    public DrawerAdapter(String[] menu, DrawerSelectedListener listener)  {
        ArrayList<String> menuList = new ArrayList<>();
        for(String s:menu) {
            menuList.add(s);
        }
        mMenuList = menuList;
        mListener = listener;
    }

    public DrawerAdapter(ArrayList<String> menu)  {
        mMenuList = menu;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View v = vi.inflate(R.layout.drawer_menu_item, viewGroup, false);
        TextView tv = (TextView) v.findViewById(R.id.tv_menu_item);

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            v.setOnClickListener(this);
            mTextView = v;
        }

        @Override
        public void onClick(View v) {
            mListener.onSelected(getAdapterPosition());
        }
    }
}
