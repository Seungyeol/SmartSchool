package com.aura.smartschool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aura.smartschool.Interface.DrawerSelectedListener;
import com.aura.smartschool.LoginManager;
import com.aura.smartschool.R;
import com.aura.smartschool.utils.PreferenceUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015-06-14.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    private static final int HOMEID_MENU = 0;
    private static final int PLAIN_MENU = 1;
    private static final int TOGGLE_MENU = 2;

    private static final int ALL = 0;
    private static final int PARENTS = 1;
    private static final int CHILD = 2;
    private static final int NUTRITIONIST = 3;

    private ArrayList<DRAWER_MENU> mMenuList = new ArrayList<>();
    private DrawerSelectedListener mListener;

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
                if ("영양사".equals(LoginManager.getInstance().getLoginUser().home_id)) {
                    if (menu.menuType == ALL || menu.menuType == NUTRITIONIST) {
                        mMenuList.add(menu);
                    }
                } else {
                    if (menu.menuType == ALL || menu.menuType == CHILD) {
                        mMenuList.add(menu);
                    }
                }
            }
        }
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        if (viewType == HOMEID_MENU) {
            View itemView = vi.inflate(R.layout.drawer_homeid_item, viewGroup, false);
            return new HomeIdMenuViewHolder(itemView);
        } else  if (viewType == TOGGLE_MENU) {
            View itemView = vi.inflate(R.layout.drawer_toggle_item, viewGroup, false);
            return new ToggleMenuViewHolder(itemView);
        } else {
            View itemView = vi.inflate(R.layout.drawer_plain_item, viewGroup, false);
            return new MainMenuViewHolder(itemView);
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
        HOMEID(HOMEID_MENU, R.string.drawer_setting_family_name, ALL),
        NOTICE(PLAIN_MENU, R.string.drawer_notice, ALL),
        QnA(PLAIN_MENU, R.string.drawer_qna, ALL),
        DEV_INFO(PLAIN_MENU, R.string.drawer_dev_info, ALL),
        DIET_MENU(PLAIN_MENU, R.string.drawer_diet_menu, NUTRITIONIST),
        SERVICE_ASK(PLAIN_MENU, R.string.drawer_ask, PARENTS),
        DROP_OUT(PLAIN_MENU, R.string.drawer_drop_out, PARENTS),
        SOS_ICON(TOGGLE_MENU, R.string.drawer_setting_widget, CHILD);

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

    public class HomeIdMenuViewHolder extends DrawerViewHolder implements View.OnClickListener {
        public TextView mTvHomeId;
        public TextView mTvModifyHomeId;

        public HomeIdMenuViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTvHomeId = (TextView) v.findViewById(R.id.tv_home_id);
            mTvModifyHomeId = (TextView) v.findViewById(R.id.tv_modify_home_id);

            mTvModifyHomeId.setOnClickListener(this);
        }

        @Override
        public void onBindViewHolder(DRAWER_MENU menu) {
            if (LoginManager.getInstance().getLoginUser() != null) {
                mTvHomeId.setText(LoginManager.getInstance().getLoginUser().home_id);
                mTvModifyHomeId.setVisibility(LoginManager.getInstance().getLoginUser().is_parent == 0 ? View.GONE : View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_modify_home_id) {
                mListener.onSelected(DRAWER_MENU.HOMEID);
            }
        }
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

    public class ToggleMenuViewHolder extends DrawerViewHolder {
        public TextView mTvMenu;
        public ToggleButton mTgBtn;

        private boolean isInBindEvent;

        private Context mContext;

        public ToggleMenuViewHolder(View v) {
            super(v);
            mContext = v.getContext();
            mTvMenu = (TextView) v.findViewById(R.id.tv_menu_item);
            mTgBtn = (ToggleButton) v.findViewById(R.id.tv_modify_home_id);

            mTvMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTgBtn.isChecked()) {
                        mListener.onSelected(mMenuList.get(getAdapterPosition()));
                    }
                }
            });

            mTgBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isInBindEvent) {
                        if (DRAWER_MENU.SOS_ICON == mMenuList.get(getAdapterPosition())) {
                            PreferenceUtil.getInstance(buttonView.getContext()).setSOSEnabled(isChecked);
                        }
                        if (isChecked) {
                            mListener.onSelected(mMenuList.get(getAdapterPosition()));
                        }
                    }
                }
            });
        }

        @Override
        public void onBindViewHolder(DRAWER_MENU menu) {
            isInBindEvent = true;
            mTvMenu.setText(mTvMenu.getContext().getResources().getString(menu.descId));
            mTgBtn.setChecked(PreferenceUtil.getInstance(mContext).isSOSEnabled());
            isInBindEvent = false;
        }
    }
}
