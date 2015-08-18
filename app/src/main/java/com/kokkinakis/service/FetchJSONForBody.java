package com.kokkinakis.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

class FetchJSONForBody extends AsyncTask<String, String, String> {
	
	Bitmap pic; //ετσι ωστε να φορτωνει και την εικονα 
	
	TextView article_title;
	TextView article_body;
	
	ImageView article_image;
	
	Context context;
	JSONObject jsonobject;
    JSONArray jsonarray;
    
    Integer id; 
    String title;
    String body;
    String image_url;
	

	public FetchJSONForBody(Context context,TextView tit,TextView bod,ImageView im)
	{
		this.context = context;
		article_title=tit;
		article_body=bod;
		article_image=im;
		
	}

	 @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }


	 @Override
		protected String doInBackground(String... uri){
	    	
	    	
	    	
	        
	     
	        HTTPhandler httphandl = new HTTPhandler();

	   
	        String jsonStr = httphandl.makeServiceCall(uri[0], HTTPhandler.GET);

	        Log.d("Response: ", "> " + jsonStr);

	        if (jsonStr != null) {
	            try {
	            	JSONArray json = new JSONArray(jsonStr);

	                //Λουπα για ολες τις καταχωρησεις του JSON Array
	                for (int i = 0; i < json.length(); i++) {
	                	    
	                    JSONObject e = json.getJSONObject(i);
	                    
	                    id=e.getInt("id");		
	                    title=e.getString("title");
	                    body=e.getString("body");
	                    image_url=e.getString("image");
	                   
	                }
	                
	               
	                
	                
	        		 URL url = new URL(image_url);
	        		 HttpURLConnection connection = (HttpURLConnection) url.openConnection();		//Γεμιζει το array με εικονες Bitmap
	        	                
	        	        connection.setDoInput(true);
	        	        connection.connect();
	        	        InputStream input = (InputStream) connection.getContent();
	        	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        	        pic=myBitmap;
	        		
	                
	               
	                
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
	    	
		 article_title.setText(title);
		 article_body.setText(body);
			
	     article_image.setImageBitmap(pic);
	    	
	    	
	    }
}
