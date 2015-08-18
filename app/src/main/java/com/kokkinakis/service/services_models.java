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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.kokkinakis.service.R;
 
public class services_models extends Fragment {
	public String brand;
	public Integer position;
	static ArrayList<String> model_names = new ArrayList<String>();
	private DataBaseHelper db;
	private Cursor c;
	static Integer[] brands_image={R.drawable.brands1,
		   						   R.drawable.brands2,
		   						   R.drawable.brands3,
		   						   R.drawable.brands4,
		   						   R.drawable.brands5,
		   						   R.drawable.brands6};
	
	
    public services_models(String brd,Integer p) {
    	brand=new String(brd);
    	position=p;
	}

	public static Fragment newInstance(Context context,String brd,Integer in) {
    	services_models f = new services_models(brd,in);
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.services_models, null);
        
        ImageView imageView=(ImageView) root.findViewById(R.id.brand_image);
        imageView.setImageResource(brands_image[position]);
        
        //db = new DataBaseHelper(getActivity());
        
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
        
        c=db.getmodels(brand);
        c.moveToFirst();
        model_names.clear(); 
        do{
        	model_names.add(c.getString(0));
        }while(c.moveToNext());
        
        c.close();
        db.close();
        
        ListView listView=(ListView) root.findViewById(R.id.models_listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.list_items,R.id.text1, model_names );
        adapter.notifyDataSetChanged(); 
        listView.setAdapter(adapter);
       
        listView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			    
			    final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
			    ft.replace(((ViewGroup)getView().getParent()).getId(), new services_offer(brand,model_names.get(position),position), "service_offer");
			    
			    ft.addToBackStack(null);
			    
			    ft.commit();
			}
		});
        
        
        return root;
    }
    
}