package com.aura.smartschool.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.utils.Util;
import com.aura.smartschool.vo.ConsultVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by SeungyeolBak on 15. 7. 11..
 */
public class ConsultChattingAdapter extends RecyclerView.Adapter<ConsultChattingAdapter.ConsultViewHolder> {

    private ArrayList<ConsultVO> chatMsgList;
    private ArrayList<ConsultVO> failMsgList;

    private FailMessageManager failMessageManager;

    public interface FailMessageManager {
        void OnRetry(ConsultVO message);
        void OnRemove(ConsultVO message);
    }

    public ConsultChattingAdapter(ArrayList<ConsultVO> msgList, ArrayList<ConsultVO> failMsgList, FailMessageManager manager) {
        this.chatMsgList = msgList;
        this.failMsgList = failMsgList;
        this.failMessageManager = manager;
    }

    public void setConsultMessageList(ArrayList<ConsultVO> messageList) {
        this.chatMsgList = messageList;
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
    public void onBindViewHolder(final ConsultViewHolder holder, final int position) {
        boolean isFirstMsgOfDay = true;
        if (position > 0 && position < chatMsgList.size()) {
            isFirstMsgOfDay = Util.isDifferentDay(chatMsgList.get(position).created, chatMsgList.get(position-1).created);
        }
        if (position < chatMsgList.size()) {
            holder.onBindViewHolder(chatMsgList.get(position), isFirstMsgOfDay, false);
        } else {
            holder.onBindViewHolder(failMsgList.get(position - chatMsgList.size()), false, true);
        }

    }

    @Override
    public int getItemCount() {
        return chatMsgList.size() + failMsgList.size();
    }

    public int getChatMessageListItemCount() {
        return chatMsgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position < chatMsgList.size() ? chatMsgList.get(position).who : 0);
    }

    public void addItem(ConsultVO msg) {
        chatMsgList.add(msg);
        notifyItemInserted(chatMsgList.size() - 1);
    }

    public void addFailItem(ConsultVO msg) {
        failMsgList.add(msg);
        notifyItemInserted(chatMsgList.size() + failMsgList.size() - 1);
    }

    public void removeItem(ConsultVO msg) {
        for (int i = failMsgList.size()-1; i>=0; i--) {
            if (msg.consultId == failMsgList.get(i).consultId) {
                failMsgList.remove(i);
            }
        }
        notifyDataSetChanged();
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

        public void onBindViewHolder(ConsultVO msg, boolean isFirstMsgOfDay, boolean isFailList) {
            llHeaderDate.setVisibility(isFirstMsgOfDay ? View.VISIBLE : View.GONE);
            tvDate.setText(new SimpleDateFormat("yyyy년 MM월 dd일 E요일").format(msg.created));
            tvConsultChat.setText(msg.content);
            tvTime.setText(new SimpleDateFormat("a hh:mm").format(msg.created));
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

        @Override
        public void onBindViewHolder(ConsultVO msg, boolean isFirstMsgOfDay, boolean isFailList) {
            super.onBindViewHolder(msg, isFirstMsgOfDay, isFailList);
            tvName.setText("선생님");
        }
    }

    public class MineChattingViewHolder extends ConsultViewHolder {

        public ImageView ivFail;

        public MineChattingViewHolder(View itemView) {
            super(itemView);
            llHeaderDate = itemView.findViewById(R.id.ll_header_date);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvConsultChat = (TextView) itemView.findViewById(R.id.tv_consult_chat);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            ivFail = (ImageView) itemView.findViewById(R.id.iv_fail);
        }

        @Override
        public void onBindViewHolder(final ConsultVO msg, boolean isFirstMsgOfDay, boolean isFailList) {
            super.onBindViewHolder(msg, isFirstMsgOfDay, isFailList);
            if (!isFailList) {
                tvTime.setVisibility(View.VISIBLE);
                ivFail.setVisibility(View.GONE);
            } else {
                tvTime.setVisibility(View.GONE);
                ivFail.setVisibility(View.VISIBLE);
                ivFail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new RetryDialog(v.getContext())
                                .setRetryListener(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        failMessageManager.OnRetry(msg);
                                    }
                                })
                                .setRemoveListener(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        failMessageManager.OnRemove(msg);
                                    }
                                }).show(((FragmentActivity)v.getContext()).getSupportFragmentManager(), "retryDialog");
                    }
                });
            }
        }
    }

    public static class RetryDialog extends DialogFragment {
        private Context context;
        private DialogInterface.OnClickListener retryListener;
        private DialogInterface.OnClickListener removeListener;

        public RetryDialog () {}

        @SuppressLint("ValidFragment")
        public RetryDialog (Context context) {
            super();
            this.context = context;
            this.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        }

        RetryDialog setRetryListener(DialogInterface.OnClickListener retryListener) {
            this.retryListener = retryListener;
            return this;
        }

        RetryDialog setRemoveListener(DialogInterface.OnClickListener removeListener) {
            this.removeListener = removeListener;
            return this;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(context)
                    .setMessage("\n재전송 하시겠습니까?\n")
                    .setPositiveButton("확인", retryListener)
                    .setNegativeButton("삭제", removeListener)
                    .create();
        }
    }
}
