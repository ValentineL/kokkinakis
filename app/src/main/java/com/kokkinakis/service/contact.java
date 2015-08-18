package com.kokkinakis.service;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kokkinakis.service.R;

 
public class contact extends Fragment {
 
	private Cursor text;
    private DataBaseHelper db;
    EditText emailbody;
    static ArrayList<String> brand_names = new ArrayList<String>();
    static ArrayList<String> model_names = new ArrayList<String>();
    Spinner spinnerBrand, spinnerModel;
	
    public static Fragment newInstance(Context context) {
    	contact f = new contact();
 
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.contact, null);
        
        ImageView imageView=(ImageView) root.findViewById(R.id.logo);
        imageView.setImageResource(R.drawable.kok_logo); 
        
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

        
        text=db.getbrands();
        text.moveToFirst();
        
        for(int i=0;i<text.getCount()-1;i++)
        {
        	brand_names.add(text.getString(0));
        	text.moveToNext();
        
        }
        
        spinnerBrand = (Spinner) root.findViewById(R.id.spinnerBrand);
        spinnerModel = (Spinner) root.findViewById(R.id.spinnerModel);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, brand_names);
        spinnerBrand.setAdapter(adapter);
        
        text.close();
        spinnerBrand.setOnItemSelectedListener(new OnItemSelectedListener(){
        	
        	public void onItemSelected(AdapterView<?> parent, View view, int pos,
        			   long id) {
        		parent.getItemAtPosition(pos);
        		text=db.getmodels(brand_names.get(pos));
        		model_names.clear();
        		
        		for(int i=0;i<text.getCount()-1;i++)
                {
                	model_names.add(text.getString(0));
                	text.moveToNext();
                
                }
        		text.close();
        		
        		ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, model_names);
                spinnerModel.setAdapter(adapter);
        		
        	}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
				
			}	
        	
        });
        
        text=db.getcontacttext();
        text.moveToFirst();
        db.close();
       
        
       TextView tv=(TextView) root.findViewById(R.id.contact_info);
       tv.setText(text.getString(0));
        
        emailbody=(EditText) root.findViewById(R.id.email_body);
        
        
        Button mailbtn=(Button) root.findViewById(R.id.contact_mail);
        mailbtn.setOnClickListener(new View.OnClickListener(){
        			public void onClick(View v){
        				Intent send = new Intent(Intent.ACTION_SENDTO);
        				String uriText = "mailto:" + Uri.encode("valentine.longinidis@gmail.com") +
        				          "?subject=" + Uri.encode("test subject") + 
        				          "&body=" + Uri.encode(emailbody.getText().toString());
        				Uri uri = Uri.parse(uriText);

        				send.setData(uri);
        				startActivity(Intent.createChooser(send, "Send mail..."));}
        			});
        
        Button mapbtn=(Button) root.findViewById(R.id.contact_map);
        mapbtn.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v){
						
						
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:38.02400,23.74397?q=38.02400,23.74397(Kokkinakis Service)"));

						intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
						startActivity(intent);
					}
					});
        		
        		
        		
        		
        
        return root;
    }
 
}