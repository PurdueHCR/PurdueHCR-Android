package Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Bundle;

import com.hcrpurdue.jason.hcrhousepoints.PointLogDetailsFragment;
import com.hcrpurdue.jason.hcrhousepoints.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import java.util.ArrayList;
import java.util.Objects;

import Models.PointLog;

public class ApprovePointListAdapter extends BaseAdapter implements ListAdapter {
    private FirebaseUtil fbutil = new FirebaseUtil();
    private String house;
    private ArrayList<PointLog> list;
    private Context context;
    private String name;
    private final ProgressBar spinner;

    public ApprovePointListAdapter(ArrayList<PointLog> logs, Context c, String h, String n, ProgressBar s){
        list = logs;
        context = c;
        name = n;
        spinner = s;
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
            view = inflater.inflate(R.layout.approve_list_item, parent, false);
        }

        //Handle TextView and display string from your list
        TextView text1 = view.findViewById(R.id.text1);
        TextView text2 = view.findViewById(R.id.text2);
        String text1String = log.getResident() + " - " +  log.getType().getPointDescription();
        text1.setText(text1String);
        text2.setText(log.getPointDescription());

        //Handle buttons and add onClickListeners
        ImageButton approveBtn = view.findViewById(R.id.approveButton);
        ImageButton denyBtn = view.findViewById(R.id.denyButton);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putSerializable("POINTLOG", log);

                //Create destination fragment
                Fragment fragment = new PointLogDetailsFragment();
                fragment.setArguments(args);

                //Create Fragment manager
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment, Integer.toString(R.id.nav_point_log_details));
                fragmentTransaction.addToBackStack(Integer.toString(R.id.nav_approve));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
        });

        approveBtn.setOnClickListener(v -> {
            spinner.setVisibility(View.VISIBLE);
            Singleton.getInstance(context).handlePointLog(log, true,false, new SingletonInterface() {
                @Override
                public void onSuccess() {
                    list.remove(position);
                    notifyDataSetChanged();
                    spinner.setVisibility(View.GONE);
                }
            });
        });
        denyBtn.setOnClickListener(v -> {
            spinner.setVisibility(View.VISIBLE);
            Singleton.getInstance(context).handlePointLog(log, false,false, new SingletonInterface() {
                @Override
                public void onSuccess() {
                    list.remove(position);
                    notifyDataSetChanged();
                    spinner.setVisibility(View.GONE);
                }
            });
        });

        return view;
    }
}
