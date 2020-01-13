package com.hcrpurdue.jason.hcrhousepoints.ListAdapters;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hcrpurdue.jason.hcrhousepoints.Fragments.PointLogDetailsFragment;
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.R;


import java.util.List;
import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

public class PointLogAdapter extends BaseAdapter  implements ListAdapter {
    private List<PointLog> list;
    private Context context;
    private CacheManager cacheManager;
    private Integer idToUse;

    /**
     * Create a Point Log Adapter
     * @param logs  Point logs to create this adapter with
     * @param c     Context
     * @param idToUse   R.id. id for the navigation menu so the app knows which page to return to
     */
    public PointLogAdapter(List<PointLog> logs, Context c, Integer idToUse ){
        list = logs;
        context = c;
        cacheManager = CacheManager.getInstance(context);
        this.idToUse = idToUse;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PointLog log = list.get(position);
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Objects.requireNonNull(inflater);
            view = inflater.inflate(R.layout.list_item_point_log, parent, false);
        }

        //Handle TextView and display string from your list
        TextView pointTypeLabel = view.findViewById(R.id.point_type_text_view);
        TextView nameLabel = view.findViewById(R.id.name_text_view);
        TextView pointDescriptionLabel = view.findViewById(R.id.description_text_view);
        //ImageView houseView = view.findViewById(R.id.message_image);
        TextView monthView = view.findViewById(R.id.month_text_view);
        TextView dateView = view.findViewById(R.id.month_date_view);

        View statusView = view.findViewById(R.id.status_bar_view);

        ImageView alertView = view.findViewById(R.id.notification_image_view);
        if(cacheManager.getPermissionLevel() != UserPermissionLevel.RESIDENT && log.getRhpNotifications() > 0){
            alertView.setVisibility(View.VISIBLE);
        }
        else if((cacheManager.getPermissionLevel() == UserPermissionLevel.RESIDENT || cacheManager.getUserId().equals(log.getResidentId())) && log.getResidentNotifications() > 0){
            alertView.setVisibility(View.VISIBLE);
        }
        else{
            alertView.setVisibility(View.GONE);
        }

        monthView.setText(DateFormat.format("MMM",log.getDateOccurred()));
        dateView.setText(DateFormat.format("dd",log.getDateOccurred()));
        pointTypeLabel.setText(log.getPointType().getName());
        nameLabel.setText(log.getResidentFirstName());
        pointDescriptionLabel.setText(log.getPointDescription());

        if(log.wasRejected()){
            //Rejected
            statusView.setBackgroundTintList(context.getResources().getColorStateList(R.color.reject_color));
        }
        else if(log.wasHandled()){
            //Approved
            statusView.setBackgroundTintList(context.getResources().getColorStateList(R.color.approve_color));
        }
        else{
            //Unhandled
            statusView.setBackgroundTintList(context.getResources().getColorStateList(R.color.unhandled_color));
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isHouseEnabled = cacheManager.getCachedSystemPreferences().isHouseEnabled();
                Bundle args = new Bundle();
                args.putSerializable("POINTLOG", log);

                //Create destination fragment
                Fragment fragment = new PointLogDetailsFragment();
                fragment.setArguments(args);

                //Create Fragment manager
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment, Integer.toString(R.id.nav_point_log_details));
                fragmentTransaction.addToBackStack(Integer.toString(idToUse));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
