package com.aura.smartschool.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.AppNoticeVO;

import java.util.ArrayList;

/**
 * Created by SeungyeolBak on 15. 8. 11..
 */
public class AppNoticeAdapter extends RecyclerView.Adapter<AppNoticeAdapter.AppNoticeViewHolder> {

    private ArrayList<AppNoticeVO> notiList = new ArrayList<>();

    public AppNoticeAdapter() {}

    public void setNotiList(ArrayList<AppNoticeVO> notiList) {
        this.notiList = notiList;
    }

    @Override
    public AppNoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_app_notice, parent, false);
        return new AppNoticeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AppNoticeViewHolder holder, int position) {
        holder.onBindViewHolder(notiList.get(position));
    }

    @Override
    public int getItemCount() {
        return notiList.size();
    }

    public class AppNoticeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle;
        private TextView tvContent;

        public AppNoticeViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);

            tvTitle.setOnClickListener(this);
        }

        public void onBindViewHolder(AppNoticeVO noti) {
            tvTitle.setText(noti.title);
            tvContent.setText(noti.content);
        }

        @Override
        public void onClick(View v) {
            tvContent.setVisibility(tvContent.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }
}
