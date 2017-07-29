package com.example.umamaheshwari.mygooglemaps.Interfaces;

import android.database.Cursor;

/**
 * Created by user1 on 24-Apr-17.
 */

public interface getSelectedDateListener {
    public void getSelectedDateResult(Cursor String);
    //  public Cursor getSelectedDateResult();

    public void getSelDateErrorResult(Cursor String);
    public void getRecentAddedLocation();

}