package com.example.leejaejun.scanner;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import static com.example.leejaejun.scanner.PlaceVO.EMPTY;
import static com.example.leejaejun.scanner.PlaceVO.WALL;

/**
 * Created by LeeJaeJun on 2017-05-22.
 */

public class Util {
    public int positionX = 0;
    public int positionY = 0;
    public boolean isFinished(PlaceVO placeVO) {
        if (placeVO.getEast() == null || placeVO.getWest() == null || placeVO.getSouth() == null || placeVO.getNorth() == null)
            return false;
        if (isFinished(placeVO.getEast()))
            return true;
        if (isFinished(placeVO.getWest()))
            return true;
        if (isFinished(placeVO.getSouth()))
            return true;
        if (isFinished(placeVO.getNorth()))
            return true;
        return false;
    }
    public Queue<PlaceVO> getPath(PlaceVO start, PlaceVO destination) {
        Stack<PlaceVO> stack = new Stack<PlaceVO>();
        Queue<PlaceVO> finalResult = new LinkedList<PlaceVO>();
        stack.add(start);
        findingPath(stack, finalResult, destination);
        return finalResult;
    }

    private void findingPath(Stack<PlaceVO> stack, Queue<PlaceVO> finalResult, PlaceVO destination) {
        if(finalResult.size()>0 && stack.size() >= finalResult.size())
            return;
        PlaceVO tempVO = stack.peek();
        Log.d("패쓰 어떻게 도는가", "["+tempVO.getX() +". "+tempVO.getY()+"]"+", ["+destination.getX()+", "+destination.getY()+"]");
        if (tempVO == destination) {
            if (finalResult.size() == 0 || stack.size() < finalResult.size()) {
                finalResult.clear();
                finalResult.addAll(stack);
            }
            return;
        }
        if (tempVO.getEast() != null && tempVO.getEast().getType() == EMPTY && !stack.contains(tempVO.getEast())) {
            stack.add(tempVO.getEast());
            findingPath(stack, finalResult, destination);
            stack.pop();
        }
        if (tempVO.getWest() != null && tempVO.getWest().getType() == EMPTY && !stack.contains(tempVO.getWest())) {
            stack.add(tempVO.getWest());
            findingPath(stack, finalResult, destination);
            stack.pop();
        }
        if (tempVO.getSouth() != null && tempVO.getSouth().getType() == EMPTY && !stack.contains(tempVO.getSouth())) {
            stack.add(tempVO.getSouth());
            findingPath(stack, finalResult, destination);
            stack.pop();
        }
        if (tempVO.getNorth() != null && tempVO.getNorth().getType() == EMPTY && !stack.contains(tempVO.getNorth())) {
            stack.add(tempVO.getNorth());
            findingPath(stack, finalResult, destination);
            stack.pop();
        }
    }
    public void displayMap(Table table){
        clearImageView(table);
        for(int i = 1; i<=11; i++){
            for(int z = 1; z<=11; z++){

                    PlaceVO placeVO = PlaceVO.contains(positionX - 6 + i, positionY - 6 + z);
                    if (placeVO != null) {
                        if (placeVO.getType() == EMPTY)
                            table.getImageView(z, i).setImageResource(R.drawable.bottom);
                        else {
                            placeVO.setType(WALL);
                            table.getImageView(z, i).setImageResource(R.drawable.wall);
                        }
                    }else{
                        table.getImageView(z, i).setImageResource(R.drawable.black);
                    }
                }
            }

    }

    public void displayNow(Table table, PlaceVO now) {
        clearImageView(table);
        //Stack<PlaceVO> stack = new Stack<PlaceVO>();
        display(table, now);
    }

    private void display(Table table, PlaceVO now) {
        positionX = now.getX();
        positionY = now.getY();
        for(int i = 1; i<=11; i++){
            for(int z = 1; z<=11; z++){
                if(i==6 && z==6)
                    table.getImageView(z, i).setImageResource(R.drawable.car);
                else {

                    PlaceVO placeVO = PlaceVO.contains(now.getX() - 6 + i, now.getY() - 6 + z);
                    if (placeVO != null) {
                        if (placeVO.getType() == EMPTY)
                            table.getImageView(z, i).setImageResource(R.drawable.bottom);
                        else {
                            placeVO.setType(WALL);
                            table.getImageView(z, i).setImageResource(R.drawable.wall);
                        }
                    }else{
                        table.getImageView(z, i).setImageResource(R.drawable.black);
                    }
                }
            }
        }
//        Log.d("그린다!! ", now.getX()+", "+now.getY());
//        int x = now.getY() - xy[0] + 6, y = now.getX() - xy[1] + 6;
//        if (x < 1 || x > 11 || y < 1 || y > 11)
//            return;
//        ImageView imageView = table.getImageView(now.getX() - xy[1], now.getY() - xy[0]);
//
//        switch (now.getType()) {
//            case EMPTY:
//                imageView.setImageResource(R.drawable.empty);
//                break;
//            case WALL:
//                imageView.setImageResource(R.drawable.wall);
//                break;
//        }
//        if (now.getEast() != null && now.getEast().getType() == EMPTY && !stack.contains(now)) {
//            stack.add(now.getEast());
//            display(table, stack, xy, now.getEast());
//            stack.pop();
//        }
//        if (now.getWest() != null && now.getWest().getType() == EMPTY && !stack.contains(now)) {
//            stack.add(now.getWest());
//            display(table, stack, xy, now.getWest());
//            stack.pop();
//        }
//        if (now.getSouth() != null && now.getSouth().getType() == EMPTY && !stack.contains(now)) {
//            stack.add(now.getSouth());
//            display(table, stack, xy, now.getSouth());
//            stack.pop();
//        }
//        if (now.getNorth() != null && now.getNorth().getType() == EMPTY && !stack.contains(now)) {
//            stack.add(now.getNorth());
//            display(table, stack, xy, now.getNorth());
//            stack.pop();
//        }
    }

    private int[] getStartGap(PlaceVO placeVO) {
        int[] xy = new int[2];
        xy[0] = 6 - placeVO.getX();
        xy[1] = 6 - placeVO.getY();
        return xy;
    }

    private void clearImageView(Table table) {
        for (int i = 1; i <= 11; i++)
            for (int z = 1; z <= 11; z++)
                table.getImageView(i, z).setImageResource(R.drawable.empty);
    }
    public boolean areThereNonSearchPlaces(PlaceVO placeVO){
        if(placeVO.getEast()==null || placeVO.getWest()==null || placeVO.getSouth()==null || placeVO.getNorth()==null)
            return true;
        return false;
    }
}