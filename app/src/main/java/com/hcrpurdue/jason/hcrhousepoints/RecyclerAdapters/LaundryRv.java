package com.hcrpurdue.jason.hcrhousepoints.RecyclerAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hcrpurdue.jason.hcrhousepoints.Models.LaundryItem;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.List;

public class LaundryRv  extends RecyclerView.Adapter<LaundryRv.MyHolder> {

    List<LaundryItem> laundryItems;

public LaundryRv(List<LaundryItem> laundryItems) {
        this.laundryItems= laundryItems;
        }

@Override
public LaundryRv.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_laundry,parent,false);

       LaundryRv.MyHolder myHolder = new LaundryRv.MyHolder(view);
        return myHolder;
        }


public void onBindViewHolder(LaundryRv.MyHolder holder, final int position) {

    final LaundryItem data = laundryItems.get(position);
    holder.unit.setText(data.getName());
    //@TODO change value in equals("")
    if (data.getStatus().equals("Ready")) {
        //@TODO set text to avaiable and change color to green
    } else {

        holder.status.setText(R.string.unavailable);
        //@TODO add time to end as well
      //  holder.status.append();

    }



      /*  Context context = view.getContext();
        SharedPreferences mySharedPreferences = context.getSharedPreferences("WeekRef", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("WeekRef", weekReference).commit();
        Intent i = new Intent(context,c.class);
        context.startActivity(i);*?
        //  Context context = view.getContext();
        //  Intent intent = new Intent(context, Add_class_to_user.class);
        // intent.putExtra("date_class", listdata.get(position).getDate_class());
        //  intent.putExtra("teacher", listdata.get(position).getTeacher());
        // intent.putExtra("room_number", listdata.get(position).getRnumber());
        //intent.putExtra("post_key", listdata.get(position).getUid());

        // context.startActivity(intent);
        }
        });

        }
*/
        }


@Override
public int getItemCount() {
        return laundryItems.size();
        }


 class MyHolder extends RecyclerView.ViewHolder{

    TextView unit,status;

    public MyHolder(View itemView) {
        super(itemView);
       unit =  itemView.findViewById(R.id.name);
       status = itemView.findViewById(R.id.status);

    }
}

}

