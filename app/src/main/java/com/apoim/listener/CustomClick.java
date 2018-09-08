package com.apoim.listener;

/**
 * Created by Anil on 24/5/18.
 */

public class CustomClick {

    private static CustomClick mInstance;
    public getToggleClick yourClick;

    public static CustomClick getmInstance (){ // for setInstance
        if(mInstance == null){
            mInstance = new CustomClick();
        }
        return mInstance;
    }

    public void getClick(getToggleClick toggleClick){ // call for getting click
        this.yourClick = toggleClick;
    }

    public void calltoSetListner(){ // need to call for set listner
        if(yourClick != null){
            yourClick.getToggleButton();
        }
    }

    public interface getToggleClick{
        void getToggleButton(); //  pass value if you need to travel value
    }
}
