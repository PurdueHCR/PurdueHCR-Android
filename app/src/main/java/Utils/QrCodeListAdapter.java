package Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hcrpurdue.jason.hcrhousepoints.QrCodeDisplay;
import com.hcrpurdue.jason.hcrhousepoints.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Models.Link;

public class QrCodeListAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<Link> qrCodeList;
    private Context context;

    public QrCodeListAdapter(ArrayList<Link> qrCodeList, Context context){
        this.qrCodeList = qrCodeList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return qrCodeList.size();
    }

    @Override
    public Object getItem(int i) {
        return qrCodeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Link qrCode = qrCodeList.get(position);
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Objects.requireNonNull(inflater);
            view = inflater.inflate(R.layout.qr_code_list_item, parent, false);
        }

        TextView titleTextView = view.findViewById(R.id.title);
        titleTextView.setText(qrCode.getDescription());

        TextView typeTextView = view.findViewById(R.id.type);
        typeTextView.setText(qrCode.getPointType(context).getPointDescription());
        Switch codeActiveSwitch = view.findViewById(R.id.qr_code_switch);
        codeActiveSwitch.setChecked(qrCode.isEnabled());

        codeActiveSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Singleton.getInstance(context).setQRCodeEnabledStatus(qrCodeList.get(position), codeActiveSwitch.isChecked(), new SingletonInterface() {
                    @Override
                    public void onSuccess() {
                        if (codeActiveSwitch.isChecked()) {
                            Toast.makeText(context, "Code has been activated", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Code has been deactivated", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(Exception e, Context context) {
                        codeActiveSwitch.setChecked(!codeActiveSwitch.isChecked());
                        qrCodeList.get(position).setEnabled(!codeActiveSwitch.isChecked());
                        Toast.makeText(context, "Code could not be updated.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });




        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putSerializable("QRCODE", qrCode);

                //Create destination fragment
                Fragment fragment = new QrCodeDisplay();
                fragment.setArguments(args);

                //Create Fragment manager
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment, Integer.toString(R.id.nav_qr_code_display));
                fragmentTransaction.addToBackStack(Integer.toString(R.id.nav_qr_code_list));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
        });

        return view;
    }


}
