package com.aura.smartschool.adapter;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.AppNoticeVO;

import java.text.SimpleDateFormat;
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
        private TextView tvShowContent;
        private TextView tvCreated;
        private View contentLayout;

        public AppNoticeViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvShowContent = (TextView) itemView.findViewById(R.id.tv_show_contents);
            tvCreated = (TextView) itemView.findViewById(R.id.tv_create_date);
            contentLayout = itemView.findViewById(R.id.content_layout);

            tvShowContent.getPaint().setFlags(tvShowContent.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            tvTitle.setOnClickListener(this);
        }

        public void onBindViewHolder(AppNoticeVO noti) {
            tvTitle.setText(noti.title);
            tvContent.setText(noti.content);
            contentLayout.setVisibility(View.GONE);
            tvCreated.setText(new SimpleDateFormat("yyyy년 MM월 dd일").format(Util.getDateFromString(noti.created)));
        }

        @Override
        public void onClick(View v) {
            contentLayout.setVisibility(contentLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }
}
