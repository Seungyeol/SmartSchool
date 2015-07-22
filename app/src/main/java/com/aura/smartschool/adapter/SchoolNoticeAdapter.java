package com.aura.smartschool.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.SchoolNotiVO;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-06-22.
 */
public class SchoolNoticeAdapter extends RecyclerView.Adapter<SchoolNoticeAdapter.ViewHolder> {
    private ArrayList<SchoolNotiVO> notiList;
    private MemberVO member;
    public SchoolNoticeAdapter(MemberVO member, ArrayList<SchoolNotiVO> notiList) {
        this.member = member;
        this.notiList = notiList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View itemView = vi.inflate(R.layout.item_school_notice, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.tvSchoolName.setText(member.mSchoolVO.school_name);
        viewHolder.tvNoticeName.setText(notiList.get(position).category == 1 ? "가정통신문" : "공지사항");
//      viewHolder.tvDay;
        viewHolder.tvDate.setText(notiList.get(position).notiDate);
        viewHolder.tvNoticeTitle.setText(notiList.get(position).title);
        viewHolder.tvNoticeBody.setText(notiList.get(position).content);
    }

    @Override
    public int getItemCount() {
        return notiList.size();
    }

    public void setNotiList(ArrayList<SchoolNotiVO> notiList) {
        this.notiList = notiList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivSchool;
        public TextView tvSchoolName;
        public TextView tvNoticeName;
        public TextView tvDay;
        public TextView tvDate;
        public TextView tvNoticeTitle;
        public TextView tvNoticeBody;
        public View llBtnLike;
        public View llBtnScrap;
        public View llBtnShare;

        public ViewHolder(View itemView) {
            super(itemView);
            ivSchool = (ImageView) itemView.findViewById(R.id.iv_school);
            tvSchoolName = (TextView) itemView.findViewById(R.id.tv_school_name);
            tvNoticeName = (TextView) itemView.findViewById(R.id.tv_notice_name);
            tvDay = (TextView) itemView.findViewById(R.id.tv_day);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvNoticeTitle = (TextView) itemView.findViewById(R.id.tv_notice_title);
            tvNoticeBody = (TextView) itemView.findViewById(R.id.tv_notice_body);
            llBtnLike = itemView.findViewById(R.id.ll_btn_like);
            llBtnScrap = itemView.findViewById(R.id.ll_btn_scrap);
            llBtnShare = itemView.findViewById(R.id.ll_btn_share);
        }
    }
}
