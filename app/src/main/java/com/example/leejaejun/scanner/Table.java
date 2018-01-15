package com.example.leejaejun.scanner;

import android.app.Activity;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;

/**
 * Created by LeeJaeJun on 2017-05-22.
 */

public class Table {
    HashMap<Integer, HashMap<Integer, ImageView>> hashMapHashMap = new HashMap<Integer, HashMap<Integer, ImageView>>();
    int [] rows = new int[]{-1, R.id.row11, R.id.row10, R.id.row9, R.id.row8, R.id.row7, R.id.row6, R.id.row5, R.id.row4, R.id.row3, R.id.row2, R.id.row1};

    public Table(Activity activity){
        TableLayout tableLayout;
        tableLayout = (TableLayout)activity.findViewById(R.id.tableLayout);

        for(int i = 1; i<=11; i++){
            for(int z = 1; z<=11; z++){
                TableRow tableRow = (TableRow)tableLayout.findViewById(rows[i]);
                ImageView imageView = new ImageView(activity);
                imageView.setImageResource(R.drawable.black);
                tableRow.addView(imageView);

                addImageView(i, z, imageView);
            }
        }
    }

    void addImageView(int row, int col, ImageView imageView){
        if(!hashMapHashMap.containsKey(row))
            hashMapHashMap.put(row, new HashMap<Integer, ImageView>());
        hashMapHashMap.get(row).put(col, imageView);
    }

    ImageView getImageView(int row, int col){
        return hashMapHashMap.get(row).get(col);
    }
}
