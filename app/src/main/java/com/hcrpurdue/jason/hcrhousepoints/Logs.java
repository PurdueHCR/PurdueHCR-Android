package com.hcrpurdue.jason.hcrhousepoints;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Logs extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.logs, container, false);
        try {
            Process process = Runtime.getRuntime().exec("logcat -e");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }
            TextView tv = view.findViewById(R.id.logsText);
            if (TextUtils.isEmpty(log))
                tv.setText("Nothing broke yet, yay!");
            else
                tv.setText(log.toString());
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error reading logs, this is real bad", Toast.LENGTH_LONG).show();
        }
        return view;
    }
}
