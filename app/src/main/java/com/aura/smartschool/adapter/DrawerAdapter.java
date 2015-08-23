package com.aura.smartschool.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aura.smartschool.Interface.DrawerSelectedListener;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-06-14.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    private static final int MAIN_MENU = 0;
    private static final int SETTING_MENU = 1;

    private static final int ALL = 0;
    private static final int PARENTS = 1;
    private static final int CHILD = 2;

    private ArrayList<DRAWER_MENU> mMenuList = new ArrayList<>();
    private DrawerSelectedListener mListener;

    private boolean subMenuVisibility;

    public DrawerAdapter(DrawerSelectedListener listener)  {
        mListener = listener;
    }

    public void setDrawerMenu(boolean isParent) {
        for(DRAWER_MENU menu:DRAWER_MENU.values()) {
            if (isParent) {
                if (menu.menuType == ALL || menu.menuType == PARENTS) {
                    mMenuList.add(menu);
                }
            } else {
                if (menu.menuType == ALL || menu.menuType == CHILD) {
                    mMenuList.add(menu);
                }
            }
        }
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        if (viewType == MAIN_MENU) {
            View itemView = vi.inflate(R.layout.drawer_menu_item, viewGroup, false);
            return new MainMenuViewHolder(itemView);
        } else {
            View itemView = vi.inflate(R.layout.drawer_setting_item, viewGroup, false);
            return new SubMenuViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder viewHolder, int position) {
        viewHolder.onBindViewHolder(mMenuList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return mMenuList.get(position).viewType;
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }

    public enum DRAWER_MENU {
        NOTICE(MAIN_MENU, R.string.drawer_notice, ALL),
        QnA(MAIN_MENU, R.string.drawer_qna, ALL),
        DEV_INFO(MAIN_MENU, R.string.drawer_dev_info, ALL),
        SERVICE_ASK(MAIN_MENU, R.string.drawer_ask, PARENTS),
        DROP_OUT(MAIN_MENU, R.string.drawer_drop_out, PARENTS),
//        LOCATION_INFO(MAIN_MENU, R.string.drawer_location_info_upload, ALL),
        SETTING(SETTING_MENU, R.string.drawer_settings, ALL);

        public int viewType;
        public int descId;
        public int menuType;

        private DRAWER_MENU(int viewType, int descriptionId, int menuType) {
            this.viewType = viewType;
            this.descId = descriptionId;
            this.menuType = menuType;
        }
    }

    public abstract class DrawerViewHolder extends RecyclerView.ViewHolder {
        public DrawerViewHolder(View itemView) {
            super(itemView);
        }
        public abstract void onBindViewHolder(DRAWER_MENU menu);
    }

    public class MainMenuViewHolder extends DrawerViewHolder implements View.OnClickListener {
        public final TextView mTextView;

        public MainMenuViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTextView = (TextView) v.findViewById(R.id.tv_menu_item);
        }

        @Override
        public void onBindViewHolder(DRAWER_MENU menu) {
            mTextView.setText(mTextView.getContext().getResources().getString(menu.descId));
        }

        @Override
        public void onClick(View v) {
            mListener.onSelected(mMenuList.get(getAdapterPosition()));
        }
    }

    public class SubMenuViewHolder extends DrawerViewHolder implements View.OnClickListener {
        public TextView mTextView;
        public View llSubMenu;
        public TextView tvHomeId;
        public View rlInOutAlarm;
        public ToggleButton tgInOutAlarm;
        public View rlActivityAlarm;
        public ToggleButton tgActivityAlarm;
        public View rlWidget;
        public ToggleButton tgWidget;

        public SubMenuViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tv_menu_item);
            llSubMenu = v.findViewById(R.id.ll_sub_menu);
            tvHomeId = (TextView) v.findViewById(R.id.tv_home_id);
            rlInOutAlarm = v.findViewById(R.id.rl_in_out_alarm);
            tgInOutAlarm = (ToggleButton) v.findViewById(R.id.tg_in_out_alarm);
            rlActivityAlarm = v.findViewById(R.id.rl_activity_alarm);
            tgActivityAlarm = (ToggleButton) v.findViewById(R.id.tg_activity_alarm);
            rlWidget = v.findViewById(R.id.rl_widget);
            tgWidget = (ToggleButton) v.findViewById(R.id.tg_widget);

            mTextView.setOnClickListener(this);
        }

        @Override
        public void onBindViewHolder(DRAWER_MENU menu) {
            mTextView.setText(mTextView.getContext().getResources().getString(menu.descId));
            if (LoginManager.getInstance().getLoginUser() != null) {
                tvHomeId.setText("Home ID  :  " + LoginManager.getInstance().getLoginUser().home_id);
            }
        }

        @Override
        public void onClick(View v) {
            llSubMenu.setVisibility(llSubMenu.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }
}
