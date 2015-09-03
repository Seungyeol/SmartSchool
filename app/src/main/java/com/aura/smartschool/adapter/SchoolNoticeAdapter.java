package com.aura.smartschool.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aura.smartschool.Constant;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.MemberVO;
import com.aura.smartschool.vo.SchoolNotiVO;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015-06-22.
 */
public class SchoolNoticeAdapter extends RecyclerView.Adapter<SchoolNoticeAdapter.ViewHolder> {
    private ArrayList<SchoolNotiVO> notiList;
    private ArrayList<SchoolNotiVO> scrapList = new ArrayList<>();
    private MemberVO member;

    private boolean showScrapOnly;

    private OnScrapChangedListener scrapChangedListener;

    public interface OnScrapChangedListener {
        void onScrapChanged(int notiSeq, boolean isSelected);
    }

    public SchoolNoticeAdapter(MemberVO member, ArrayList<SchoolNotiVO> notiList) {
        this.member = member;
        this.notiList = notiList;
        makeScrapList();
    }

    public void setNotiList(ArrayList<SchoolNotiVO> notiList) {
        this.notiList = notiList;
        makeScrapList();
    }

    public void setScrapChangedListener(OnScrapChangedListener listener) {
        this.scrapChangedListener = listener;
    }

    public void showScrapItemOnly(boolean selected) {
        showScrapOnly = selected;
        notifyDataSetChanged();
    }

    public void setScrap(int notiSeq, boolean isScraped) {
        for (SchoolNotiVO notiVO : notiList) {
            if (notiVO.notiSeq == notiSeq) {
                if (isScraped) {
                    notiVO.memberId = LoginManager.getInstance().getLoginUser().member_id;
                } else {
                    notiVO.memberId = 0;
                }
                break;
            }
        }
        makeScrapList();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View itemView = vi.inflate(R.layout.item_school_notice, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.onBindViewHolder(showScrapOnly ? scrapList.get(position) : notiList.get(position));
    }

    @Override
    public int getItemCount() {
        return (showScrapOnly ? scrapList.size() : notiList.size());
    }

    private void makeScrapList() {
        scrapList.clear();
        for (SchoolNotiVO notiVO : notiList) {
            if (notiVO.memberId > 0) {
                scrapList.add(notiVO);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivSchool;
        public TextView tvSchoolName;
        public TextView tvNoticeName;
        public TextView tvDay;
        public TextView tvDate;
        public TextView tvNoticeTitle;
        public TextView tvNoticeBody;
        public TextView tvFileName;
        public View llBtnLike;
        public View llBtnScrap;
        public View llBtnShare;

        private SchoolNotiVO notiVO;

        public ViewHolder(View itemView) {
            super(itemView);
            ivSchool = (ImageView) itemView.findViewById(R.id.iv_school);
            tvSchoolName = (TextView) itemView.findViewById(R.id.tv_school_name);
            tvNoticeName = (TextView) itemView.findViewById(R.id.tv_notice_name);
            tvDay = (TextView) itemView.findViewById(R.id.tv_day);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvNoticeTitle = (TextView) itemView.findViewById(R.id.tv_notice_title);
            tvNoticeBody = (TextView) itemView.findViewById(R.id.tv_notice_body);
            tvFileName = (TextView) itemView.findViewById(R.id.tv_attach_file);
            llBtnLike = itemView.findViewById(R.id.ll_btn_like);
            llBtnScrap = itemView.findViewById(R.id.ll_btn_scrap);
            llBtnShare = itemView.findViewById(R.id.ll_btn_share);

            llBtnScrap.setOnClickListener(clickListener);
            llBtnShare.setOnClickListener(clickListener);
        }

        public void onBindViewHolder(final SchoolNotiVO notiVO) {
            this.notiVO = notiVO;
            tvSchoolName.setText(member.mSchoolVO.school_name);
            tvNoticeName.setText(notiVO.category == 1 ? "가정통신문" : "공지사항");
//      viewHolder.tvDay;
            tvDate.setText(new SimpleDateFormat("yyyy년 MM월 dd일").format(Util.getDateFromString(notiVO.notiDate)));
            tvNoticeTitle.setText(notiVO.title);
            tvNoticeBody.setText(notiVO.content);

            if (!StringUtils.isBlank(notiVO.fileName) && !"null".equals(notiVO.fileName)) {
                tvFileName.setText(notiVO.fileName);
                tvFileName.setPaintFlags(tvFileName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                ((View) tvFileName.getParent()).setVisibility(View.VISIBLE);
                tvFileName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Constant.HOST + Constant.API_FILE + notiVO.fileName));
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, notiVO.fileName);
                        DownloadManager dm = (DownloadManager) v.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                    }
                });
            }

            if (notiVO.memberId > 0) {
                llBtnScrap.setSelected(true);
            } else {
                llBtnScrap.setSelected(false);
            }
        }

        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_btn_scrap:
                        if (scrapChangedListener != null) {
                            scrapChangedListener.onScrapChanged(notiVO.notiSeq, !llBtnScrap.isSelected());
                        }
                        break;
                    case R.id.ll_btn_share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getShareString());
                        sendIntent.setType("text/plain");
                        v.getContext().startActivity(sendIntent);
                        break;
                }
            }
        };

        private String getShareString() {
            StringBuilder sb = new StringBuilder();
            sb.append("<< ").append(tvNoticeName.getText()).append(" >>").append("\n")
                    .append("학교 : ").append(tvSchoolName.getText()).append("\n")
                    .append("일정 : ").append(tvDate.getText()).append("\n\n")
                    .append(tvNoticeTitle.getText()).append("\n")
                    .append(tvNoticeBody.getText()).append("\n");
            return sb.toString();
        }
    }
}
