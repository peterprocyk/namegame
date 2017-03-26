package com.willowtreeapps.namegame.core;

/**
 * Created by SuperPowerUser on 3/25/2017.
 */

public enum GameMode {
    normal,hint,reverse,matt;

    public static GameMode getState(String s){
        if(s.startsWith("Normal")){
            return normal;
        }else if(s.startsWith("Hint")){
            return hint;
        }else if(s.startsWith("Reverse")){
            return reverse;
        }else{
            return matt;
        }

    }
}
