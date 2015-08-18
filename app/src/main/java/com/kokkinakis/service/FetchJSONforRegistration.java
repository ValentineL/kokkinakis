package com.kokkinakis.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

class FetchJSONforRegistration extends AsyncTask<String, String, String> {

	Context context;
	String name;
	String email;
	String cell_num;
	String uuid;
	DataBaseHelper db;
	
	public FetchJSONforRegistration(DataBaseHelper database)
	{
		this.context = context;
		db=database;
		
	}
	
	@Override
	protected String doInBackground(String... uri) {
		
		uri[0]=uri[0].replace(" ", "%20"); //Αν εχει space δημιουργει runtime. Ετσι το αντικαθιστω με τον ASCII κωδικο για το space 
		
        HTTPhandler httphandl = new HTTPhandler();

       
        String jsonStr = httphandl.makeServiceCall(uri[0], HTTPhandler.GET);
        jsonStr = jsonStr.trim();	//για καποιο λογο βγαζει spaces στην αρχη
        
        Log.d("Response: ", "> " + jsonStr);
        
        if (jsonStr != null) {
            try {

                //Η Απαντηση για το registration εχει μονο μια καταχωρηση
                
                	    
                    JSONObject e = new JSONObject(jsonStr);
                    
                    name=new String(e.getString("name"));
                    email=new String(e.getString("email"));
                    cell_num=new String(e.getString("cell_num"));
                    uuid=new String(e.getString("uuid"));
                            
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
		
		return null;
	}
	
	@Override
    protected void onPostExecute(String result) {
    	
		
		db.populate_profile_info(name, email, cell_num,uuid);
    	
    	
    }

}
