package Utils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hcrpurdue.jason.hcrhousepoints.PointLogDetailsFragment;
import com.hcrpurdue.jason.hcrhousepoints.R;


import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

import Models.PointLog;

public class HouseSubmissionHistoryAdapter extends BaseAdapter  implements ListAdapter {
    private List<PointLog> list;
    private Context context;
    private final ProgressBar spinner;
    private Singleton singleton;

    public HouseSubmissionHistoryAdapter(List<PointLog> logs, Context c, ProgressBar s){
        list = logs;
        context = c;
        spinner = s;
        singleton = Singleton.getInstance(context);
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
            view = inflater.inflate(R.layout.list_item_point_log_overview, parent, false);
        }

        //Handle TextView and display string from your list
        TextView pointTypeLabel = view.findViewById(R.id.message_log_type_text);
        TextView nameLabel = view.findViewById(R.id.point_log_first_name);
        TextView lastNameLabel = view.findViewById(R.id.point_log_last_name);
        TextView pointDescriptionLabel = view.findViewById(R.id.message_log_text);
        ImageView houseView = view.findViewById(R.id.message_image);


        pointTypeLabel.setText(log.getPointType().getPointDescription());
        nameLabel.setText(log.getResident().split( " ")[0]);
        lastNameLabel.setText(log.getResident().split( " ")[1]);
        pointDescriptionLabel.setText(log.getPointDescription());

        int drawableID = context.getResources().getIdentifier(singleton.getHouse().toLowerCase(), "drawable", context.getPackageName());
        houseView.setImageResource(drawableID);

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
                fragmentTransaction.addToBackStack(Integer.toString(R.id.point_history));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
