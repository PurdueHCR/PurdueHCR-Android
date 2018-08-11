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

import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.ArrayList;

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
        house = h;
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
            assert inflater != null;
            view = inflater.inflate(R.layout.approve_list_item, null);
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

        approveBtn.setOnClickListener(v -> {
            spinner.setVisibility(View.VISIBLE);
            fbutil.handlePointLog(log, true, house, name, new FirebaseUtilInterface() {
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
            fbutil.handlePointLog(log, false, house, name, new FirebaseUtilInterface() {
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
