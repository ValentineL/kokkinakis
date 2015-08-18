package com.kokkinakis.service;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kokkinakis.service.R;
 
public class services_brands extends Fragment {
	
	
	static Integer[] brands_image={R.drawable.brands1,
							       R.drawable.brands2,
							       R.drawable.brands3,
							       R.drawable.brands4,
							       R.drawable.brands5,
							       R.drawable.brands6};
	
	static ArrayList<String> brand_names = new ArrayList<String>();
	private DataBaseHelper db;
	private Cursor c;
	
    public static Fragment newInstance(Context context) {
    	services_brands f = new services_brands();
 
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.services_brands, null);
                    
        
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
        
        c=db.getbrands();
        c.moveToFirst();
        for(int i=0;i<brands_image.length;i++)
        {
        	brand_names.add(c.getString(0));
        	c.moveToNext();
        
        }
        c.close();
        db.close();
        
        ListView listView=(ListView) root.findViewById(R.id.brands_listview);
        listView.setAdapter(new ImageAdapter(getActivity(),brands_image,1,brand_names));
        
       
		
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			    
			    final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
			    
			    
			    
			    ft.replace(((ViewGroup)getView().getParent()).getId(), new services_models(brand_names.get(position),position), "service_models");
			    
			    ft.addToBackStack(null);
			    
			    ft.commit();
			}
		});
    
    
    return root;
    }
    

 
}