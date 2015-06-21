package com.aura.smartschool.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.SchoolVO;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-06-22.
 */
public class SchoolListAdapter extends RecyclerView.Adapter<SchoolListAdapter.ViewHolder> {
    private ArrayList<SchoolVO> mSchoolList;

    public SchoolListAdapter() {
        mSchoolList = new ArrayList<>();
    }

    public void setSchoolList(ArrayList<SchoolVO> schoolList)  {
        mSchoolList = schoolList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View v = vi.inflate(R.layout.school_list_item, viewGroup, false);
        TextView tv = (TextView) v.findViewById(R.id.tv_school_item);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.mTextView.setText(mSchoolList.get(i).school_name);
    }

    @Override
    public int getItemCount() {
        return mSchoolList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }
}
