package com.kokkinakis.service;

import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.kokkinakis.service.R;
 
public class workshop extends Fragment {
 
	private Cursor text;
    private DataBaseHelper db;
    static Integer[] imageIds={R.drawable.workshop1,
							   R.drawable.workshop2,
							   R.drawable.workshop3,
							   R.drawable.workshop4,
							   R.drawable.workshop5,
							   R.drawable.workshop6,
							   R.drawable.workshop7,
							   R.drawable.workshop8,};
	
    public static Fragment newInstance(Context context) {
    	workshop f = new workshop();
 
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.workshop, null);      
        
        db = new DataBaseHelper(getActivity());
       
        try {
 
        	db.createDataBase();
 
        } 
        catch (IOException ioe) {
 
        	throw new Error("Unable to create database");
 
        }
 
        try {
 
        	db.openDataBase();
 
        }catch(SQLException sqle){
 
        	throw sqle;
 
        }
        
        
        text = db.getworkshoptext();
        text.moveToFirst();
        
 
       TextView t = (TextView) root.findViewById(R.id.workshop_text);	
       t.setText(text.getString(0));									
       
       GridView gridView = (GridView) root.findViewById(R.id.gridview);
       gridView.setAdapter(new ImageAdapter(getActivity(),imageIds,0));
       
       text.close();
       db.close();

        
        
        return root;
    }
 
}