package com.kokkinakis.service;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.manuelpeinado.fadingactionbar.FadingActionBarHelperBase;
import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;

public class services_offer extends Fragment{
	
	public String brand;
	public String model;
	public Integer position;
	private DataBaseHelper db;
	private Cursor c;
	
	static Integer[] audi={ R.drawable.audi_a1,
		   					R.drawable.audi_a3,
		   					R.drawable.audi_a4,
		   					R.drawable.audi_q3,
							R.drawable.audi_q5};
	
	static Integer[] vw={R.drawable.vw_polo,
						 R.drawable.vw_golf,
						 R.drawable.vw_beetle,
						 R.drawable.vw_scirocco,
						 R.drawable.vw_passat,
						 R.drawable.vw_jetta};
	
	static Integer[] seat={R.drawable.seat_ibiza,
						   R.drawable.seat_leon,
						   R.drawable.seat_toledo,
						   R.drawable.seat_cordoba};
	
	static Integer[] skoda={R.drawable.skoda_fabia,
		   				   R.drawable.skoda_octavia,
		   				   R.drawable.skoda_yeti};
	
	static Integer[] subaru={R.drawable.subaru_impreza,
		   					 R.drawable.subaru_forester};
	
	static Integer[] opel={R.drawable.opel_corsa,
							 R.drawable.opel_astra};
	/*private FadingActionBarHelperBase mFadingHelper;*/

	public services_offer(String brd,String mdl,Integer p) {
    	brand= brd;
		model= mdl;
    	position=p;
	}

	public static Fragment newInstance(Context context,String brd,String mod,Integer in) {
    	services_offer f = new services_offer(brd,mod,in);
        return f;
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		/*View root = mFadingHelper.createView(inflater);*/

		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_detail, null);

		final Toolbar toolbar = (Toolbar)root.findViewById(R.id.toolbar);
		((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		CollapsingToolbarLayout collapsingToolbar =
				(CollapsingToolbarLayout)root.findViewById(R.id.collapsing_toolbar);


		loadBackdrop();

        Integer image = null ;
        if(brand.equals("Audi")) {image=audi[position];}	
        	else if (brand.equals("Volkswagen")){image=vw[position];}
        		else if (brand.equals("Seat")){image=seat[position];}
        			else if (brand.equals("Skoda")){image=skoda[position];}
        				else if (brand.equals("Subaru")){image=subaru[position];}
        					else if (brand.equals("Opel")){image=opel[position];}

        ImageView img = (ImageView) root.findViewById(R.id.image_header);
        img.setImageResource(image);


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
       
       c=db.getoffer(brand, model);
       c.moveToFirst(); 
       String a=new String(c.getString(0));
       
       /*WebView webview= (WebView) root.findViewById(R.id.offer_webview);
       webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
       webview.getSettings().setJavaScriptEnabled(false);
       webview.loadDataWithBaseURL(null, a, "text/html", "utf-8", null);*/
       
        return root;
	}
    
	/*@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        mFadingHelper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.black)
                .headerLayout(R.layout.header_light)
                .contentLayout(R.layout.services_offer);
        mFadingHelper.initActionBar(getActivity());
	}*/

	private void loadBackdrop() {
		final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
		Glide.with(this).load(Cheeses.getRandomCheeseDrawable()).centerCrop().into(imageView);
	}

}
