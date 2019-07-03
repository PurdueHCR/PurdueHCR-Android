package com.hcrpurdue.jason.hcrhousepoints.ListAdapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcrpurdue.jason.hcrhousepoints.R;

import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;

public class PointLogMessageAdapter extends RecyclerView.Adapter<PointLogMessageAdapter.PointLogMessageHolder> {



    private PointLog log;
    private Context context;
    private Singleton singleton;

    public PointLogMessageAdapter (PointLog log, Context context){
        this.log = log;
        this.context = context;
        this.singleton = Singleton.getInstance(context);
    }

    public class PointLogMessageHolder extends RecyclerView.ViewHolder{

        public TextView firstNameView, lastNameView, descriptionView, pointTypeView,descriptionLabel,pointTypeLabel;
        public ImageView imageView;

        public PointLogMessageHolder(@NonNull View itemView){
            super(itemView);

            firstNameView = itemView.findViewById(R.id.point_log_first_name);
            lastNameView = itemView.findViewById(R.id.point_log_last_name);
            descriptionView = itemView.findViewById(R.id.message_log_text);
            pointTypeView = itemView.findViewById(R.id.message_log_type_text);
            imageView = itemView.findViewById(R.id.message_image);
            descriptionLabel = itemView.findViewById(R.id.description_text);
            pointTypeLabel = itemView.findViewById(R.id.point_type_text);
        }
    }

    @NonNull
    @Override
    public PointLogMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_point_log_overview,parent,false);

        return new PointLogMessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointLogMessageHolder holder, int position) {

        if(position == 0) {
            holder.descriptionLabel.setVisibility(View.VISIBLE);
            holder.pointTypeLabel.setVisibility(View.VISIBLE);
            holder.pointTypeView.setVisibility(View.VISIBLE);
            holder.firstNameView.setText( log.getResident().split(" ")[0]);
            holder.lastNameView.setText( log.getResident().split(" ")[1]);
            int drawableID = context.getResources().getIdentifier(singleton.getHouse().toLowerCase(), "drawable", context.getPackageName());
            holder.imageView.setImageResource(drawableID);
            holder.pointTypeView.setText(log.getPointType().getPointDescription());
            holder.descriptionView.setText(log.getPointDescription());
        }
        else{
            holder.descriptionLabel.setVisibility(View.GONE);
            holder.pointTypeLabel.setVisibility(View.GONE);
            holder.pointTypeView.setVisibility(View.GONE);
            holder.firstNameView.setText( log.getMessages().get(position-1).getSenderFirstName());
            holder.lastNameView.setText( log.getMessages().get(position-1).getSenderLastName());
            holder.descriptionView.setText(log.getMessages().get(position-1).getMessage());
            int drawableID = context.getResources().getIdentifier(singleton.getHouse().toLowerCase(), "drawable", context.getPackageName());
            holder.imageView.setImageResource(drawableID);
        }
    }

    @Override
    public int getItemCount() {
        if(log.getMessages() == null){
            return 1;
        }
        else{
            return 1 + log.getMessages().size();
        }
    }



}
