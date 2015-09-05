package com.kokkinakis.service;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {

	private DrawerLayout mDrawerLayout;
	
	ArrayList<String> drawer_text=new ArrayList<String>();
	
	final String[] fragments ={
			"com.kokkinakis.service.services_brands",
			"com.kokkinakis.service.contact",
			"com.kokkinakis.service.articles_list",
			"com.kokkinakis.service.workshop",
			"com.kokkinakis.service.profile",
			"com.kokkinakis.service.appointment"};
	
	final Integer[] icons={R.drawable.euro,
						   R.drawable.map,
						   R.drawable.favicon1,
						   R.drawable.spanner,
						   R.drawable.profile_icon,
						   R.drawable.clock,
						   R.drawable.ic_menu_exit};
	//χρησιμοποιειται στο actionBarDrawerToggle() 
    private ActionBarDrawerToggle mDrawerToggle;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_main);

		 Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		 setSupportActionBar(toolbar);

		 final ActionBar ab = getSupportActionBar();
		 ab.setHomeAsUpIndicator(R.drawable.ic_menu);
		 ab.setDisplayHomeAsUpEnabled(true);

	 	 mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		 /*final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.main_content);*/
		 final ListView navList = (ListView) findViewById(R.id.drawer);
		 
		 Resources res = getResources();
		 final String[] data =res.getStringArray(R.array.drawer_items);
		 
		 for(int i=0;i<data.length;i++){drawer_text.add(data[i]);}
		 
		 
		 navList.setAdapter(new ImageAdapter(getBaseContext(),icons,2,drawer_text));
		 
		 navList.setOnItemClickListener(new OnItemClickListener(){
		         @Override
		         public void onItemClick(AdapterView<?> parent, View view, final int pos,long id){
		        	 if (pos==6) {finish();System.exit(0);} //Για την εξοδο
					 mDrawerLayout.setDrawerListener( new DrawerLayout.SimpleDrawerListener(){
		                         @Override
		                         public void onDrawerClosed(View drawerView){
		                                 super.onDrawerClosed(drawerView);
		                                 
		                                android.support.v4.app.FragmentManager fm = getSupportFragmentManager(); //αδειαζει ολο το backstack(χωρις pop)
		                         		fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); //αντι να το αντιγραφω σε καθε top fragment
		                         																		 //το βαζω πριν το mainactivity κανει το replace
		                                /* getSupportActionBar().setTitle(R.string.app_name)*/;//για να παραμεινει το ονομα εφαρμογης μετα το πρωτο ανοιγμα/κλεισιμο
		                                 FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
		                                 tx.replace(R.id.main, Fragment.instantiate(MainActivity.this, fragments[pos]));
		                                 tx.commit();
		                                 
		                         }
		                        /*public void onDrawerOpened(View drawerView) {//για να παραμεινει το ονομα μετα το πρωτο ανοιγμα/κλεισιμο
		         	                getSupportActionBar().setTitle(R.string.drawer_title);
		         	            }*/
		                 });
					 mDrawerLayout.closeDrawer(navList);
		         }
		 });
		 FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
		 tx.replace(R.id.main,Fragment.instantiate(MainActivity.this, fragments[4]));//το αρχικο fragment οταν ανοιγει η εφαρμογη(μπορει να ειναι οποιοδηποτε)
		 tx.commit();


		 //για το ανοιγμα/κλεισιμο
		 
		// mDrawerLayout = (DrawerLayout) findViewById(R.id.private DrawerLayout mDrawerLayout;ayout);
	        mDrawerToggle = new ActionBarDrawerToggle(	//απο το dev.android στο Tutorial για το navigationdrawer
	                this,                  /* host Activity */
					mDrawerLayout,         /* DrawerLayout object */
					toolbar,
	                  /* nav drawer icon to replace 'Up' caret */
	                R.string.drawer_open,  /* "open drawer" description */
	                R.string.drawer_close  /* "close drawer" description */
	                ) {

	            /** Called when a drawer has settled in a completely closed state. */
	            /*public void onDrawerClosed(View view) {//για το ονομα εφαρμογης πριν το πρωτο κλεισιμο(που ειναι μεσα onitemclick)
	                getSupportActionBar().setTitle(R.string.app_name);
	            }

	            *//** Called when a drawer has settled in a completely open state. *//*
	            public void onDrawerOpened(View drawerView) {////για να παραμεινει το ονομα πριν το πρωτο ανοιγμα/κλεισιμο(για τα επομενα ειναι μεσα στο onitemclick)
	                getSupportActionBar().setTitle(R.string.drawer_title);
	            }*/
	        };

	        // Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

	        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	        getSupportActionBar().setHomeButtonEnabled(true);*/




	    }

	    @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        //συνχρονισμος για οταν εχει συμβει το onRestoreInstanceState
	        mDrawerToggle.syncState();
	    }

	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        mDrawerToggle.onConfigurationChanged(newConfig);
	    }

	    /*@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // event για τα εικονιδια του menu της actionbar
	    	
	        if (mDrawerToggle.onOptionsItemSelected(item)) {
	          return true;
	        }

	        return super.onOptionsItemSelected(item);
	    }*/
	    
	    /*@Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Χρηση του layout menu/main.xml για να περιγραψει τα εικονιδια της actionbar 
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.main, menu);
	        return super.onCreateOptionsMenu(menu);
	    }*/
	    
	    
}