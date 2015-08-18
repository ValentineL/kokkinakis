package com.kokkinakis.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

class FetchJSONForList extends AsyncTask<String, String, String> {
	
	public static ArrayList<Bitmap> pics = new ArrayList<Bitmap>(); //ετσι ωστε να φορτωνει και τις εικονες για τη λιστα
	ListView listview;
	Context context;
	private int animtime;
	JSONObject jsonobject;
    JSONArray jsonarray;
    public static ArrayList<Integer> ids = new ArrayList<Integer>();
    public static ArrayList<String> titles = new ArrayList<String>();
    public static ArrayList<String> previews = new ArrayList<String>();
    public static ArrayList<String> image_urls = new ArrayList<String>();
   
	
	public FetchJSONForList(Context context,ListView lst)
	{
		this.context = context;
		listview=lst;
		
	}
	
	
	 
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        animtime=context.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }
    
    @Override
	protected String doInBackground(String... uri){
    	
    	ids=new ArrayList<Integer>();
    	titles=new ArrayList<String>();
    	previews=new ArrayList<String>();
    	image_urls=new ArrayList<String>();
    	
        
       
        HTTPhandler httphandl = new HTTPhandler();

        
        String jsonStr = new String(httphandl.makeServiceCall(uri[0], HTTPhandler.GET));

        Log.d("Response: ", "> " + jsonStr);

        if (jsonStr != null) {
            try {
            	JSONArray json = new JSONArray(jsonStr);

                //Λουπα για ολες τις καταχωρησεις του JSON Array
                for (int i = 0; i < json.length(); i++) {
                	    
                    JSONObject e = json.getJSONObject(i);
                    
                    ids.add(e.getInt("id"));		
                    titles.add(e.getString("title"));
                    previews.add(e.getString("preview"));
                    image_urls.add(e.getString("image"));
                   
                }
                
                pics=new ArrayList<Bitmap>(); //Αν δεν γινει η μεταβλητη παραμενει ως εχει απο το προηγουμενο run
                							  //και στο επομενο κανει append με αποτελεσμα να εχουμε runtime γιατι φορτωνει πιο πολλες
                							  //εικονες απότι ειναι η λιστα
                
                for(int i=0;i < image_urls.size();i++)
        		{
        		 URL url = new URL(image_urls.get(i));
        		 HttpURLConnection connection = (HttpURLConnection) url.openConnection();		//Γεμιζει το array με εικονες Bitmap
        	                
        	        connection.setDoInput(true);
        	        connection.connect();
        	        InputStream input = (InputStream) connection.getContent();
        	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
        	        pics.add(myBitmap);
        		}
                
               
                
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
               
            } catch (IOException e) {
              
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }

        return null;
    }


    @Override
    protected void onPostExecute(String result) {
    	listview.animate().alpha(1f).setDuration(animtime).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                articles_list.spinner.setVisibility(View.GONE);		//τρεχουν και τα 2 απλως ο spinner ειναι αορατος
            }});
    	
    	ImageAdapter adapter = new ImageAdapter(context, titles, previews, pics, 4);
    	
    	listview.setAdapter(adapter);
    	
    	int i=listview.getCount();
	    if(i<10) articles_list.next_btn.setEnabled(false);
    	
    	
    }


}
