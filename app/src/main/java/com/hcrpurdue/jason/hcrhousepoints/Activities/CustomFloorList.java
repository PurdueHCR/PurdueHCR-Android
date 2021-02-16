package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.hcrpurdue.jason.hcrhousepoints.R;

public class CustomFloorList extends AppCompatActivity {
    ListView listView;
    Button back;
    SparseBooleanArray sparseBooleanArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_floor_list);
        back = findViewById(R.id.setCustom);
        listView = findViewById(R.id.floorList);
        //@TODO save selected items so if users select leave and come back they are still showing

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, getResources().getStringArray(R.array.floorList)));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
       // listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
       //     @Override
       //     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //        if (listView.isItemChecked(position)) {
        //            listView.setItemChecked(position,false);
        //        }
        //    }
       // });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomFloorList.this, createEvent.class);
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                StringBuilder stringBuilder = new StringBuilder();
                System.out.println(checked.size());
                if (checked.size() == 0) {
                    intent.putExtra("floorList", "");
                } else {
                   String[] floorList= getResources().getStringArray(R.array.floorList);
                    for (int i = 0; i < checked.size(); i++) {
                        if (checked.valueAt(i)) {
                            stringBuilder.append(floorList[checked.keyAt(i)]);
                            stringBuilder.append(",");
                        }


                    }
                    if (stringBuilder.length() > 1) {
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        intent.putExtra("floorList", stringBuilder.toString());

                    }else {
                        intent.putExtra("floorList", "");
                    }

                }
                startActivity(intent);
            }
        });

    }
}