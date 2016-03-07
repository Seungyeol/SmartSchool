package com.aura.smartschool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aura.smartschool.R;
import com.aura.smartschool.vo.GeofenceVO;

import java.util.List;

/**
 * Created by eastflag on 2016-03-07.
 */
public class GeofenceAdapter extends RecyclerView.Adapter<GeofenceAdapter.ViewHolder> {

    private List<GeofenceVO> mGeofenceList;

    public GeofenceAdapter(List<GeofenceVO> geofenceList) {
        mGeofenceList = geofenceList;
    }

    public void setData(List<GeofenceVO> geofenceList) {
        mGeofenceList = geofenceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        View itemView = vi.inflate(R.layout.item_geofence, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GeofenceVO geofenceVO = mGeofenceList.get(position);
        String title;
        String sex = "M".equals(geofenceVO.sex) ? "군": "양";
        String typeString;
        if (geofenceVO.type == 1) {
            title = "등교알림";
            typeString = "들어왔습니다";
        } else {
            title = "하교알림";
            typeString = "벗어났습니다.";
        }
        holder.title.setText(title);
        holder.created.setText(geofenceVO.created);
        holder.content.setText(String.format("%s%s이(가) %s 반경 100m를 %s", geofenceVO.name,
                sex, geofenceVO.school_name, typeString));
    }

    @Override
    public int getItemCount() {
        return mGeofenceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        TextView created;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            created = (TextView) itemView.findViewById(R.id.created);
        }
    }
}
