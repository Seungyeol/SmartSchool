package com.aura.smartschool.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
    private SchoolItemClickListener listener;
    public interface SchoolItemClickListener {
        void onItemClicked(SchoolVO school);
    }

    public SchoolListAdapter() {
        mSchoolList = new ArrayList<>();
    }

    public void setSchoolList(ArrayList<SchoolVO> schoolList)  {
        mSchoolList = schoolList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View itemView = vi.inflate(R.layout.school_list_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.mSchoolName.setText(mSchoolList.get(position).school_name);

        //구주소가 비어있으면 도로명주소로 대체
        String address = mSchoolList.get(position).address;
        if (TextUtils.isEmpty(address)) {
            address = mSchoolList.get(position).new_address;
        }
        viewHolder.mSchoolAddr.setText(address);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClicked(mSchoolList.get(position));
                }
            }
        });
    }

    public void setOnItemClickListener(SchoolItemClickListener listener)  {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mSchoolList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mSchoolName;
        public final TextView mSchoolAddr;

        public ViewHolder(View itemView) {
            super(itemView);
            mSchoolName = (TextView) itemView.findViewById(R.id.tv_school_name);
            mSchoolAddr = (TextView) itemView.findViewById(R.id.tv_school_addr);
        }
    }
}
