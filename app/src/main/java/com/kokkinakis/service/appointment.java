package com.kokkinakis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Dialog;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kokkinakis.service.R;

public class appointment extends Fragment  {
	
	FetchJSONForAppointment task;
	TimePicker timepicker;
	DatePicker datepicker;
	EditText appoint_comm;
	private DataBaseHelper db;
	Cursor sel_car;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.appointment, null);
		
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
		
		
		timepicker=(TimePicker) root.findViewById(R.id.appoint_time);
		
		datepicker = (DatePicker) root.findViewById(R.id.appoint_date);
		
		appoint_comm = (EditText) root.findViewById(R.id.appoint_comments);
		
		//Δεν θα βαλω Listview οπως στο fragment profile.java επειδη το android δεν υποστηριζει
		//οποιουδηποτε ειδους scroll μεσα σε ενα αλλο scroll.Αντ'αυτου θα φτιαξω προγραμματιστικα radiobuttons
		
		final RadioGroup rg = (RadioGroup) root.findViewById(R.id.radiogroup); //Αυτο γινεται μεσω του radiogroup που εχουμε ορισει στο xml
	    rg.setOrientation(RadioGroup.VERTICAL);
		
		createradiobuttons(rg,db);
		
		
		Button submit_appointment = (Button) root.findViewById(R.id.submit_appointment);
		submit_appointment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Integer id=rg.getCheckedRadioButtonId();
            	if(id==-1){//(απο το documentation) οταν δεν εχει επιλεγει τιποτα
            		Toast toast = Toast.makeText(getActivity(), "Δεν εχετε επιλεξει οχημα", Toast.LENGTH_SHORT);
            		toast.show();
            	}
            	else{
            		
            		
            		sel_car=db.getcarbyorder(id);
            		sel_car.moveToFirst();
            		
            		final Dialog dialog = new Dialog(getActivity());
                	dialog.setCancelable(true);
        			dialog.setCanceledOnTouchOutside(true);
        			dialog.setContentView(R.layout.dialog_custom_appointment);
        			dialog.setTitle("Επιβεβαιωση Αιτησης Ραντεβου");
        			
        			TextView textdatetime=(TextView) dialog.findViewById(R.id.dialog_datetime);
        			TextView textcar=(TextView) dialog.findViewById(R.id.dialog_car);
        			
        			String datetime=new String();
        			
        			final String date=datepicker.getDayOfMonth()+"/"+(datepicker.getMonth()+1)+"/"+datepicker.getYear();
        			
        			String pickerminute=new String();
        			String pickerhour=new String();
        			
        			if(timepicker.getCurrentMinute()<10)
        				pickerminute="0"+timepicker.getCurrentMinute().toString(); //εχουμε συνηθισει να βλεπουμε 02:09 οχι 2:9
        			else
        				pickerminute=timepicker.getCurrentMinute().toString();
        			if(timepicker.getCurrentHour()<10)
        				pickerhour="0"+timepicker.getCurrentHour().toString();
        			else
        				pickerhour=timepicker.getCurrentHour().toString();
        			
        			final String time=pickerhour+":"+pickerminute;
        			
        			final String comments=appoint_comm.getText().toString();
        			datetime=date+" Ωρα:"+time;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
        			
        			textdatetime.setText(datetime);
        			
        			String car=sel_car.getString(0)+" "+sel_car.getString(1)+" "+sel_car.getString(4);
        			textcar.setText(car);
        			
        			Button btnok=(Button) dialog.findViewById(R.id.dialog_appointment_ok);
        			btnok.setOnClickListener(new View.OnClickListener() {
        				public void onClick(View v) {
        					rg.clearCheck();
        					//στελνει μεσω jsonhandler το ραντεβου με post
        					List<NameValuePair> params = new ArrayList<NameValuePair>();
        					params=fillparampairs(db,date,time,comments);

        					task=new FetchJSONForAppointment(db,params);
        					task.execute("http://jimmylabros.comoj.com/appointments/appoint_send.php"); 
        					dialog.cancel();
        				}
    	            });
        			
        			dialog.show();
            	}
            }
		});
		
		
		db.close();
		return root;
	}
	
	List<NameValuePair> fillparampairs(DataBaseHelper database,String d,String t,String com){
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Cursor c=db.getprofile_info();
		
        String db_mail=c.getString(c.getColumnIndex("email"));
        String password=Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID);
        String db_uuid=c.getString(c.getColumnIndex("uuid"));
		
		params.add(new BasicNameValuePair("email", db_mail));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("uuid", db_uuid));
		
		params.add(new BasicNameValuePair("brand", sel_car.getString(0)));  
		params.add(new BasicNameValuePair("model", sel_car.getString(1)));	
		params.add(new BasicNameValuePair("was_made", sel_car.getString(2)));
		params.add(new BasicNameValuePair("kilometers", sel_car.getString(3)));
		params.add(new BasicNameValuePair("plate_num", sel_car.getString(4)));
		params.add(new BasicNameValuePair("engine_serial", sel_car.getString(5)));
		params.add(new BasicNameValuePair("chassis_num", sel_car.getString(6)));
		
		params.add(new BasicNameValuePair("date", d));
		params.add(new BasicNameValuePair("time", t));
		params.add(new BasicNameValuePair("comments", com));
		
		return params;
	}

	void createradiobuttons(RadioGroup radiogroup,DataBaseHelper datab){//παραλλαγη του updatelistview απο
																		//το profile.java
		Cursor cars_list;
		cars_list=datab.getmycars();
		cars_list.moveToFirst();
		
		if(!(cars_list.moveToFirst()) || cars_list.getCount() ==0){ //δηλαδη δεν εχουμε οχημα στη βαση
			
			RadioButton empty=new RadioButton(getActivity());
			radiogroup.addView(empty);
			empty.setText("Δεν εχετε προσθεσει οχηματα");
			empty.setEnabled(false);
		}
		else{
			final RadioButton[] rb = new RadioButton[cars_list.getCount()];
			for(int i=0; i<cars_list.getCount(); i++)
			{
				rb[i]  = new RadioButton(getActivity());
				radiogroup.addView(rb[i]); 
				rb[i].setText(cars_list.getString(0)+" "+cars_list.getString(1)+" "+cars_list.getString(4));
				rb[i].setId(i); //Για να βρουμε ποιο ειναι ποιο στην υποβολη 
								  //θα επρεπε να ειναι i+1 αλλα η sqlite παιρνει το ΕΠΟΜΕΝΟ της σειρας του offset
				cars_list.moveToNext();
			}
			
		}
		
	}
	
	
}
