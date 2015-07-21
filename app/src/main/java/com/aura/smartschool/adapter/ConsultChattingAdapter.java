package com.aura.smartschool.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.database.DBConsultChat;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.ConsultChatVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by SeungyeolBak on 15. 7. 11..
 */
public class ConsultChattingAdapter extends RecyclerView.Adapter<ConsultChattingAdapter.ConsultViewHolder> {

    private ArrayList<ConsultChatVO> chatMsgList;

    public ConsultChattingAdapter(ArrayList<ConsultChatVO> msgList) {
        this.chatMsgList = msgList;
    }

    @Override
    public ConsultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            View itemView = inflater.inflate(R.layout.item_consult_mine, parent, false);
            return new MineChattingViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.item_consult_other, parent, false);
            return new OtherChattingViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(ConsultViewHolder holder, int position) {
        boolean isFirstMsgOfDay = true;
        if (position > 0) {
            isFirstMsgOfDay = Util.isDifferentDay(chatMsgList.get(position).time, chatMsgList.get(position-1).time);
        }

        holder.llHeaderDate.setVisibility(isFirstMsgOfDay ? View.VISIBLE : View.GONE);
        holder.tvDate.setText(new SimpleDateFormat("yyyy년 MM월 dd일 E요일").format(chatMsgList.get(position).time));
        holder.tvConsultChat.setText(chatMsgList.get(position).msg);
        holder.tvTime.setText(new SimpleDateFormat("a hh:mm").format(chatMsgList.get(position).time));
        if (getItemViewType(position) == DBConsultChat.MSG_FROM_ME) {
        } else {
            ((OtherChattingViewHolder)holder).tvName.setText("선생님");
        }
    }

    @Override
    public int getItemCount() {
        return chatMsgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatMsgList.get(position).msgFrom;
    }

    public void addItem(ConsultChatVO msg) {
        chatMsgList.add(msg);
        notifyItemInserted(chatMsgList.size()-1);
    }

    // ---------------------- ViewHolder ---------------------

    public class ConsultViewHolder extends RecyclerView.ViewHolder {
        public View llHeaderDate;
        public TextView tvDate;
        public TextView tvConsultChat;
        public TextView tvTime;
        public ConsultViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class OtherChattingViewHolder extends ConsultViewHolder {
        public ImageView ivOtherProfile;
        public TextView tvName;

        public OtherChattingViewHolder(View itemView) {
            super(itemView);
            llHeaderDate = itemView.findViewById(R.id.ll_header_date);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            ivOtherProfile = (ImageView) itemView.findViewById(R.id.iv_other_profile);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvConsultChat = (TextView) itemView.findViewById(R.id.tv_consult_chat);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    public class MineChattingViewHolder extends ConsultViewHolder {

        public MineChattingViewHolder(View itemView) {
            super(itemView);
            llHeaderDate = itemView.findViewById(R.id.ll_header_date);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvConsultChat = (TextView) itemView.findViewById(R.id.tv_consult_chat);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

}
