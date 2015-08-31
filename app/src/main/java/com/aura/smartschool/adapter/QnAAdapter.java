package com.aura.smartschool.adapter;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.BoardVO;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by SeungyeolBak on 15. 8. 11..
 */
public class QnAAdapter extends RecyclerView.Adapter<QnAAdapter.QnAViewHolder> {

    private ArrayList<BoardVO> qnaList = new ArrayList<>();

    public QnAAdapter() {}

    public void setQnAList(ArrayList<BoardVO> qnaList) {
        this.qnaList = qnaList;
    }

    @Override
    public QnAViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_qna, parent, false);
        return new QnAViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QnAViewHolder holder, int position) {
        holder.onBindViewHolder(qnaList.get(position));
    }

    @Override
    public int getItemCount() {
        return qnaList.size();
    }

    public class QnAViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle;
        private TextView tvShowAnswer;
        private View tvBoardArrow;
        private TextView tvCreated;
        private TextView tvContent;
        private TextView tvAnswer;
        private View answerLayout;

        public QnAViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvShowAnswer = (TextView) itemView.findViewById(R.id.tv_show_answer);
            tvBoardArrow = itemView.findViewById(R.id.iv_board_arrow);
            tvCreated = (TextView) itemView.findViewById(R.id.tv_create_date);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvAnswer = (TextView) itemView.findViewById(R.id.tv_answer);
            answerLayout = itemView.findViewById(R.id.answer_layout);

            tvShowAnswer.getPaint().setFlags(tvShowAnswer.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            tvTitle.setOnClickListener(this);
        }

        public void onBindViewHolder(BoardVO qna) {
            tvTitle.setText(qna.title);
            tvContent.setText(qna.content);
            tvCreated.setText(new SimpleDateFormat("yyyy년 MM월 dd일").format(Util.getDateFromString(qna.created)));

            answerLayout.setVisibility(View.GONE);
            if (!StringUtils.isBlank(qna.answer) && !"null".equals(qna.answer)) {
                tvBoardArrow.setVisibility(View.VISIBLE);
                tvShowAnswer.setVisibility(View.VISIBLE);
                tvAnswer.setText(qna.answer);
            } else {
                tvBoardArrow.setVisibility(View.GONE);
                tvShowAnswer.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if (tvShowAnswer.getVisibility() == View.VISIBLE) {
                answerLayout.setVisibility(answerLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        }
    }
}
