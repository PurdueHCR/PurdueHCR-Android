package com.hcrpurdue.jason.hcrhousepoints.ListAdapters;

import android.content.Context;
import android.os.Bundle;
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

import com.hcrpurdue.jason.hcrhousepoints.Fragments.SubmitPointsFragment;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.List;
import java.util.Objects;

public class PointTypeListAdapter extends BaseAdapter implements ListAdapter {

    private List<PointType> types;
    private Context context;

    public PointTypeListAdapter(List<PointType> types, Context context){
        this.types = types;
        this.context = context;
    }

    @Override
    public int getCount() {
        return types.size();
    }

    @Override
    public Object getItem(int i) {
        return types.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final PointType type = types.get(i);
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Objects.requireNonNull(inflater);
            view = inflater.inflate(R.layout.list_item_point_type,viewGroup, false);
        }

        //Handle TextView and display string from your list
        TextView typeTextView = view.findViewById(R.id.type_description_text_view);
        typeTextView.setText(type.getName());

        TextView pointValueTextView = view.findViewById(R.id.point_value_text_view);
        pointValueTextView.setText(type.getValue() + " Point"+((type.getValue() > 1)?"s":""));



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putSerializable("POINTTYPE", type);

                //Create destination fragment
                Fragment fragment = new SubmitPointsFragment();
                fragment.setArguments(args);

                //Create Fragment manager
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment, Integer.toString(R.id.nav_submit_point));
                fragmentTransaction.addToBackStack(Integer.toString(R.id.nav_point_type_list));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
