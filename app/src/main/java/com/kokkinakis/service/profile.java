package com.kokkinakis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kokkinakis.service.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class profile extends Fragment {
	
	private DataBaseHelper db;
	private Cursor profile_info;
	FetchJSONforRegistration task;
	ListView listview;
	private Cursor text;
	static ArrayList<String> brand_names = new ArrayList<String>();
    static ArrayList<String> model_names = new ArrayList<String>(); 
    static ArrayList<String> cars;
    
    GoogleCloudMessaging gcm;
    String regId;
    Boolean flag=false;
    
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";
   
    static final String TAG = "Profile fragment: ";
    
    static final String GOOGLE_PROJECT_ID = "352376564896"; //απο το google apis
    static final String MESSAGE_KEY = "message";
    static final String APP_SERVER_URL = "http://jimmylabros.comoj.com/appointments/gcm.php?shareRegId=1"; 

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
       
		regId = registerGCM();
		
		String mail=new String();
		String number=new String();
		final String password=Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID); //Το documentation λεει πως ειναι μοναδικο μερικες φορες ακομα και σε reset συσκευης
		
		final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.profile, null);
		
		final EditText users_name_edit = (EditText) root.findViewById(R.id.users_name);
		final EditText users_mail_edit = (EditText) root.findViewById(R.id.users_mail);
		final EditText users_cell_edit = (EditText) root.findViewById(R.id.users_num);
		
		final Button submit_profile = (Button) root.findViewById(R.id.submit_info);
		
		//String[] cars = null;
        
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
		
        profile_info=db.getprofile_info();
        
        if (!(profile_info.moveToFirst()) || profile_info.getCount() ==0){	//Ελεγχος αν ο cursor ειναι αδειος(Δηλαδη ειναι το πρωτο τρεξιμο)
           
        	TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
    		number = tm.getLine1Number();
    		
    		
    		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
    		Account[] accounts = AccountManager.get(getActivity()).getAccountsByType("com.google");
    		
    		for (Account account : accounts) {
    			if (emailPattern.matcher(account.name).matches()) {
    				mail = account.name;
    			}
    		}
    		
    		users_mail_edit.setText(mail);
    		users_cell_edit.setText(number);
       }
        else{
        	users_name_edit.setFocusable(false);
        	users_mail_edit.setFocusable(false);
        	users_cell_edit.setFocusable(false);
        	submit_profile.setEnabled(false);
        	
        	users_name_edit.setTextColor(Color.GRAY);
        	users_mail_edit.setTextColor(Color.GRAY);
        	users_cell_edit.setTextColor(Color.GRAY);
        	
        	String db_name=profile_info.getString(profile_info.getColumnIndex("name"));
            String db_mail=profile_info.getString(profile_info.getColumnIndex("email"));
            String db_number=profile_info.getString(profile_info.getColumnIndex("cell_num"));
            
            users_name_edit.setText(db_name);
            users_mail_edit.setText(db_mail);
            users_cell_edit.setText(db_number);
            
        }
		
		
        submit_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	final String strname = users_name_edit.getText().toString();
            	final String strmail = users_mail_edit.getText().toString();
            	final String strcell = users_cell_edit.getText().toString();
            	
            	
                if(TextUtils.isEmpty(strname)){
                	Toast toast = Toast.makeText(getActivity(), "Το ονομα ειναι κενο!", Toast.LENGTH_SHORT);
                	toast.show();
                }
                else if (TextUtils.isEmpty(strmail)){
                	Toast toast = Toast.makeText(getActivity(), "Το mail ειναι κενο!", Toast.LENGTH_SHORT);
                	toast.show();
                }
                else if (TextUtils.isEmpty(strcell)){
                	Toast toast = Toast.makeText(getActivity(), "Ο αριθμος ειναι κενος!", Toast.LENGTH_SHORT);
                	toast.show();
                }
                else{
                	
                	InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE); 
                	inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                	//αλλιως μενει το πληκτρολογιο ανοικτο
                	
                	
                	final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); 
                    builder.setMessage("Τα δεδομενα χρηστη δεν θα μπορουν να αλλαξουν. Συνεχεια;");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Ναι",
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           
                           
                           task=new FetchJSONforRegistration(db);
                 	       task.execute("http://jimmylabros.comoj.com/appointments/add_user.php?name="+strname+"&email="+strmail+"&cell_num="+strcell+"&password="+password+"&gcmID="+regId);
                 	       //Η αποθηκευση στη βαση γινεται στο async για να εχουμε ελεγχο για τα λαθη
                 	       
                 	       
                            users_name_edit.setFocusable(false);
                            users_mail_edit.setFocusable(false);	//τα κανει γκρι και να μην μπορουν να πατηθουν
                            users_cell_edit.setFocusable(false);
                            submit_profile.setEnabled(false);
                    	
                            users_name_edit.setTextColor(Color.GRAY);
                            users_mail_edit.setTextColor(Color.GRAY);
                            users_cell_edit.setTextColor(Color.GRAY);
                            
                        }
                    });
                    builder.setNegativeButton("Οχι",
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            
                        }
                    });
                AlertDialog alert = builder.create();
                alert.show();
                    
            	}
                
            }
        });
		
        listview=(ListView) root.findViewById(R.id.users_cars);
		
        updatelistview(listview,db);
        
        
		
		
		Button add_car = (Button) root.findViewById(R.id.add_car);
		add_car.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	final Dialog dialog = new Dialog(getActivity());
            	dialog.setCancelable(true);
    			dialog.setCanceledOnTouchOutside(true);
    			dialog.setContentView(R.layout.dialog_custom);
    			dialog.setTitle("Προσθηκη Οχηματος");
    			
    			
    			text=db.getbrands();
    	        text.moveToFirst();
    	        
    	        brand_names=new ArrayList<String>(); //για την περιπτωση που ο χρηστης κλεισει το dialog χωρις
    	        model_names=new ArrayList<String>(); //να κανει υποβολη(εκανε append) στο spinnerBrand!
    	        
    	        for(int i=0;i<text.getCount()-1;i++)
    	        {
    	        	brand_names.add(text.getString(0));
    	        	text.moveToNext();
    	        
    	        }
    			
    			final Spinner spinnerBrand= (Spinner) dialog.findViewById(R.id.spBrand);
    			final Spinner spinnerModel = (Spinner) dialog.findViewById(R.id.spModel);
    			
    			final EditText was_made=(EditText) dialog.findViewById(R.id.yearofpro);
    			final EditText totalkm=(EditText) dialog.findViewById(R.id.km);
    			final EditText plate_num=(EditText) dialog.findViewById(R.id.licencepl);
    			final EditText engine_serial=(EditText) dialog.findViewById(R.id.engcode);
    			final EditText chasis_num=(EditText) dialog.findViewById(R.id.framenum);
    			
    			final ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item, brand_names);
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
    			
    			Button okbtn=(Button) dialog.findViewById(R.id.dialogbuttonok);
    			okbtn.setOnClickListener(new View.OnClickListener() {
    	            public void onClick(View v) {
    	            	if(TextUtils.isEmpty(was_made.getText().toString())){
    	                	Toast toast = Toast.makeText(getActivity(), "Η χρονολογια ειναι κενη!", Toast.LENGTH_SHORT);
    	                	toast.show();
    	                }
    	            	else if(TextUtils.isEmpty(totalkm.getText().toString())){
    	                	Toast toast = Toast.makeText(getActivity(), "Tα χιλιομετρα ειναι κενα!", Toast.LENGTH_SHORT);
    	                	toast.show();
    	                }
    	            	else if(TextUtils.isEmpty(plate_num.getText().toString())){
    	                	Toast toast = Toast.makeText(getActivity(), "Ο αριθμος πινακιδας ειναι κενος!", Toast.LENGTH_SHORT);
    	                	toast.show();
    	                }
    	            	else if(TextUtils.isEmpty(engine_serial.getText().toString())){
    	                	Toast toast = Toast.makeText(getActivity(), "Ο κωδικος κινητηρα ειναι κενος!", Toast.LENGTH_SHORT);
    	                	toast.show();
    	                }
    	            	else if(TextUtils.isEmpty(chasis_num.getText().toString())){
    	                	Toast toast = Toast.makeText(getActivity(), "Ο αριθμος πλαισιου ειναι κενος!", Toast.LENGTH_SHORT);
    	                	toast.show();
    	                }
    	            	else{
    	            		//αποθηκευση στη βαση και κλεισιμο dialog
    	            		db.insert_car(spinnerBrand.getSelectedItem().toString(), spinnerModel.getSelectedItem().toString(), was_made.getText().toString(), totalkm.getText().toString(), plate_num.getText().toString(), engine_serial.getText().toString(), chasis_num.getText().toString());
    	            		dialog.dismiss();
    	            		
    	            		updatelistview(listview,db);
    	            	}
    	            	
    	            }
    			
    			});
    			
    			dialog.show();
    			
            
            }
            
		});
		
		
		db.close();
		return root;
	}
	
	
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	  if (v.getId()==R.id.users_cars ) { //αν προερχεται απο το Listview μας
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	    if(cars.get(info.position)!="Δεν εχετε προσθεσει οχηματα"){ //TODO Οπως και στα αλλα να το παιρνει απο ενα string και οχι στατικα
	    															//Δεν θελουμε να βγαζει το menu οταν εχει το μηνυμα για αδειο
	    	menu.setHeaderTitle(cars.get(info.position));
	    	String[] menuItems = {"Διαγραφη"};	//ισως να χρειαστει να προσθεσουμε επιλογες στο μελλον TODO να τα παιρνει απο ενα array απο τα strings
	    	for (int i = 0; i<menuItems.length; i++) {
	    		menu.add(Menu.NONE, i, i, menuItems[i]);
	    	}
	    }
	  }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
	  String[] menuItems = {"Διαγραφη"};
	  String menuItemName = menuItems[menuItemIndex];
	  String listItemName = cars.get(info.position);
	  
	  if(menuItemIndex==0)
	  {
		  db.removecar(info.position);
		  updatelistview(listview,db);
	  }

	  return true;
	}
	
	
	void updatelistview(ListView lstview, DataBaseHelper datab){ //για καποιο λογο το notifyDataSetChanged() δεν δουλευε οπως πρεπει
																 //οποτε αναγκαστηκα να κανω αυτη την υπορουτινα
		ArrayAdapter<String> list;
		Cursor cars_list;
		
		cars_list=datab.getmycars();
		cars_list.moveToFirst();
		cars=new ArrayList<String>();
		
		if(!(cars_list.moveToFirst()) || cars_list.getCount() ==0){ //δηλαδη δεν εχουμε οχημα στη βαση
			
			cars.add("Δεν εχετε προσθεσει οχηματα");
			
		}
		else{
			for(int i=0;i<cars_list.getCount();i++)
			{
				cars.add(cars_list.getString(0)+" "+cars_list.getString(1)+" "+cars_list.getString(4));
				cars_list.moveToNext();
			}	
		}
		
		
		list = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,cars); 
		//Κατι αστειο: Αν για context βαλουμε getActivity().getContext() η listview τρεχει αλλα το xml simple_list_item_1 δεν
		//			   ξερει τι theme εχει οποτε βαζει default λευκα γραμματα!!!Με το getActivity() δουλευει κανονικα (undocumented btw)
		lstview.setAdapter(list);
		registerForContextMenu(lstview);

	}
	
	public String registerGCM() {
		 
	    gcm = GoogleCloudMessaging.getInstance(getActivity().getApplicationContext());
	    regId = getRegistrationId(getActivity().getApplicationContext());
	 
	    if (TextUtils.isEmpty(regId)) {
	 
	      registerInBackground();
	 
	      Log.d("RegisterActivity",
	          "registerGCM - successfully registered with GCM server - regId: "
	              + regId);
	    } /*else {
	      Toast.makeText(getActivity().getApplicationContext(),
	          "RegId already available. RegId: " + regId,
	          Toast.LENGTH_LONG).show();
	    }*/
	    return regId;
	  }
	
	
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = this.getActivity().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	    String registrationId = prefs.getString(REG_ID, "");
	    if (registrationId.isEmpty()) {
	      Log.i(TAG, "Registration not found.");
	      return "";
	    }
	    int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	      Log.i(TAG, "App version changed.");
	      return "";
	    }
	    return registrationId;
	  }
	
	private void registerInBackground() {
	    new AsyncTask<Void, Void, String>() {
	      @Override
	      protected String doInBackground(Void... params) {
	        String msg = "";
	        try {
	          if (gcm == null) {
	            gcm = GoogleCloudMessaging.getInstance(getActivity().getApplicationContext()); //τα γνωστα περι fragment και context...
	          }
	          regId = gcm.register(GOOGLE_PROJECT_ID);
	          Log.d("RegisterActivity", "registerInBackground - regId: "
	              + regId);
	          msg = "Device registered, registration ID=" + regId;
	 
	         storeRegistrationId(getActivity().getApplicationContext(), regId);
	        } catch (IOException ex) {
	          msg = "Error :" + ex.getMessage();
	          Log.d("RegisterActivity", "Error: " + msg);
	        }
	        Log.d("RegisterActivity", "AsyncTask completed: " + msg);
	        return msg;
	      }
	 
	      @Override
	      protected void onPostExecute(String msg) {
	       /* Toast.makeText(getActivity().getApplicationContext(),
	            "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
	            .show();*/
	        flag=true;
	      }
	    }.execute(null, null, null);
	  }
	
	
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = this.getActivity().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);	//λογω fragmant δεν μπορουμε χωρις this.getActivity()
	    int appVersion = getAppVersion(context);
	    Log.d(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(REG_ID, regId);
	    editor.putInt(APP_VERSION, appVersion);
	    editor.commit();
	  }
	
	private static int getAppVersion(Context context) {
	    try {
	      PackageInfo packageInfo = context.getPackageManager()
	          .getPackageInfo(context.getPackageName(), 0);
	      return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	      Log.d("RegisterActivity",
	          "I never expected this! Going down, going down!" + e);
	      throw new RuntimeException(e);
	    }
	  }
	
}

