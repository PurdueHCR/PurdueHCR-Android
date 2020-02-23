package com.hcrpurdue.jason.hcrhousepoints.Views;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hcrpurdue.jason.hcrhousepoints.Fragments.PersonalPointLogListFragment;
import com.hcrpurdue.jason.hcrhousepoints.ListAdapters.PointLogAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

import java.util.Collections;
import java.util.List;

public class RecentPointSubmissionCard {
    private TextView emptyMessageTextView;
    private ListView recentSubmissionListView;

    private Button viewMyPointsButton;
    private PointLogAdapter adapter;
    private Context context;
    private CacheManager cacheManager;

    public RecentPointSubmissionCard(Context context, View parentView){
        recentSubmissionListView = parentView.findViewById(R.id.recent_submission_list_view);
        emptyMessageTextView = parentView.findViewById(R.id.recent_submissions_empty_message_text_view);
        viewMyPointsButton = parentView.findViewById(R.id.more_button);

        this.context = context;
        this.cacheManager = CacheManager.getInstance(context);

        viewMyPointsButton.setOnClickListener(view1 -> {
            //Create destination fragment
            Fragment fragment = new PersonalPointLogListFragment();

            //Create Fragment manager
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment, Integer.toString(R.id.nav_personal_point_log_list));
            fragmentTransaction.addToBackStack(Integer.toString(R.id.nav_personal_point_log_list));
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        });

        handlePersonalPointLogUpdate();
    }

    private void createAdapter(List<PointLog> logs){
        Collections.sort(logs);
        if(logs == null || logs.size() == 0){
            emptyMessageTextView.setVisibility(View.VISIBLE);
            emptyMessageTextView.setText(R.string.empty_recent_submission);
            return;
        }
        else{
            emptyMessageTextView.setVisibility(View.GONE);
        }
        if(logs.size() > 3){
            logs = logs.subList(0,3);
        }
        adapter = new PointLogAdapter(logs ,context, R.id.nav_personal_point_log_list);
        recentSubmissionListView.setAdapter(adapter);
    }

    /**
     * Call this to respond to the Personal Point Log Listener
     */
    public void handlePersonalPointLogUpdate(){
        createAdapter(cacheManager.getPersonalPointLogs());
    }

    /**
     * Call this to respond to a notifications listener
     */
    public void handleNotificationsUpdate(){
        createAdapter(cacheManager.getPersonalPointLogs());
    }
}
