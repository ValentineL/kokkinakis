package com.kokkinakis.service;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.os.AsyncTask;

public class FetchJSONForAppointment extends AsyncTask<String, String, String> {

	Context context;
	DataBaseHelper db;
	List<NameValuePair> data;

	public FetchJSONForAppointment(DataBaseHelper database,List<NameValuePair> params )
	{
		this.context = context;
		db=database;
		data=params;
		
	}
	
	protected String doInBackground(String... uri) {
		
		HTTPhandler httphandl = new HTTPhandler();
		
		String jsonStr = httphandl.makeServiceCall(uri[0], HTTPhandler.POST, data);
		
		return null;
	}

	
	@Override
    protected void onPostExecute(String result){
		
		
	}

	

	
}
