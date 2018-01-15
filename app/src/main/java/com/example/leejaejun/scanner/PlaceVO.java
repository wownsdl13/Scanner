package com.example.leejaejun.scanner;

import android.util.Log;

import java.util.HashMap;
import java.util.InputMismatchException;

/**
 * Created by LeeJaeJun on 2017-05-22.
 */

public class PlaceVO {
    public static HashMap<Integer, HashMap<Integer, PlaceVO>> placeVOs = new HashMap<Integer, HashMap<Integer, PlaceVO>>();

    public static final int EMPTY = 0;
    public static final int WALL = 1;

    public static final int EAST = 1;
    public static final int WEST = 2;
    public static final int SOUTH = 3;
    public static final int NORTH = 4;

    private int type;

    private int x;
    private int y;

    public PlaceVO(){
        setX(0);
        setY(0);
        this.type = EMPTY;
        putOneVO(this);
    }

    public PlaceVO(int type, PlaceVO placeVO, int direction) {
        this.type = type;
        switch (direction){
            case EAST:
                setX(placeVO.getX()+1);
                setY(placeVO.getY());
                break;
            case WEST:
                setX(placeVO.getX()-1);
                setY(placeVO.getY());
                break;
            case SOUTH:
                setX(placeVO.getX());
                setY(placeVO.getY()-1);
                break;
            case NORTH:
                setX(placeVO.getX());
                setY(placeVO.getY()+1);
                break;
        }
    }

    public PlaceVO getNorth() {
        return getNext(NORTH, this);
    }
    public PlaceVO getSouth() {
        return getNext(SOUTH, this);
    }
    public PlaceVO getEast() {
        return getNext(EAST, this);
    }

    public PlaceVO getWest() {
        return getNext(WEST, this);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getType() {
        return type;
    }
    public void setType(int type){
        this.type = type;
    }
    public static void putOneVO(PlaceVO placeVO){
        Log.d("넣으려고 하는값 : ", placeVO.getX() +", "+placeVO.getY());
        if(!placeVOs.containsKey(placeVO.getX()))
            placeVOs.put(placeVO.getX(), new HashMap<Integer, PlaceVO>());
        if(!placeVOs.get(placeVO.getX()).containsKey(placeVO.getY()))
            placeVOs.get(placeVO.getX()).put(placeVO.getY(), placeVO);
        else
            Log.d("putOneVO", "위치에 이미 값이 존재한다.");
    }
    public static PlaceVO contains(int x, int y){
        if(!placeVOs.containsKey(x))
            return null;
        if(!placeVOs.get(x).containsKey(y))
            return null;
        return placeVOs.get(x).get(y);
    }
    public static PlaceVO getNext(int direction, PlaceVO placeVO){
        switch (direction){
            case EAST:
                if(placeVOs.containsKey(placeVO.getX()+1) && placeVOs.get(placeVO.getX()+1).containsKey(placeVO.getY()))
                    return placeVOs.get(placeVO.getX()+1).get(placeVO.getY());
                else return null;
            case WEST:
                if(placeVOs.containsKey(placeVO.getX()-1) && placeVOs.get(placeVO.getX()-1).containsKey(placeVO.getY()))
                    return placeVOs.get(placeVO.getX()-1).get(placeVO.getY());
                else return null;
            case SOUTH:
                if(placeVOs.containsKey(placeVO.getX()) && placeVOs.get(placeVO.getX()).containsKey(placeVO.getY()-1))
                    return placeVOs.get(placeVO.getX()).get(placeVO.getY()-1);
                else return null;
            case NORTH:
                if(placeVOs.containsKey(placeVO.getX()) && placeVOs.get(placeVO.getX()).containsKey(placeVO.getY()+1))
                    return placeVOs.get(placeVO.getX()).get(placeVO.getY()+1);
                else return null;
        }
        return null;
    }
}