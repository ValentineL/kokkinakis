package com.kokkinakis.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class DataBaseHelper extends SQLiteOpenHelper{
 
    //Προεπιλεγμένη διαδρομή του συστήματος του Android για τη βάση δεδομένων της εφαρμογής 
    private String DB_PATH ;
 
    private static String DB_NAME = "kokkinakis_db";
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
    private static final int DATABASE_VERSION = 1;
 
    
    public DataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
        DB_PATH=context.getApplicationInfo().dataDir + "/databases/";
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//η βάση υπάρχει
    	}else{
 
    		//δημιουργία κενής βάσης
               
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 
    
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//η βάση δεν υπάρχει
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    	//return false;	
    }
 
   
    private void copyDataBase() throws IOException{
 
    	//άνοιγμα τοπικής βάσης για input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// διαδρομή στη μόλις δημιουργημένη κενή βάση
    	String outFileName = DB_PATH + DB_NAME;
 
    	//άνοιγμα κενής βάσης για output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//μεταφορά bytes από το inputfile στο outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//κλείσιμο των streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
 
    	//άνοιγμα βάσης
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
 
      
	
	public Cursor getworkshoptext() {

        //SQLiteDatabase db = getReadableDatabase();
		SQLiteDatabase db=myDataBase;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"text"}; 
        String sqlTables = "workshop";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, "_id=?", new String[]{"1"}, null, null, null);

        c.moveToFirst();
        return c;

}

	public Cursor getcontacttext() {

		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"text"}; 
		String sqlTables = "contact";

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, "_id=?", new String[]{"1"}, null, null, null);

		c.moveToFirst();
		return c;

	}

	public Cursor getbrands(){
	
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    
		String [] sqlSelect = {"name"};
		String sqlTables = "brands";
    
		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
		
		c.moveToFirst();
    
		return c;
	}

	public Cursor getmodels(String brandname){
	
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    
		String [] sqlSelect = {"model"};
		String sqlTables = "offers";
	
		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, "brand=?", new String[]{brandname}, null, null, null);
    
		c.moveToFirst();
		return c;
	
	}

	public Cursor getoffer(String brandname,String modelname){
	
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    
		String [] sqlSelect = {"text"};
		String sqlTables = "offers";
    
		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, "brand = ? AND model = ?",new String[] { brandname, modelname }, null, null, null);
		c.moveToFirst();
		
		return c;
	}
	
	public Cursor getprofile_info(){
		
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		String [] sqlSelect={"*"};
		String sqlTables= "profile_info";
		
		qb.setTables(sqlTables);
		Cursor c=qb.query(db, sqlSelect, null, null, null, null, null);
		c.moveToFirst();
		
		return c;
		
	}
	
	public void populate_profile_info(String name,String email,String cell_num,String uuid){
		
		SQLiteDatabase db=getWritableDatabase();
		ContentValues profinfo = new ContentValues();
		
		profinfo.put("uuid",uuid);
		profinfo.put("name",name); 
		profinfo.put("email",email);
		profinfo.put("cell_num",cell_num);
		db.insert("profile_info", null, profinfo);
		
		
	}
	
	public Cursor getmycars(){
		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		String [] sqlSelect={"*"};
		String sqlTables= "mycars";
		
		qb.setTables(sqlTables);
		Cursor c=qb.query(db, sqlSelect, null, null, null, null, null);
		c.moveToFirst();
		
		return c;
	}
	
	public void insert_car(String brand,String model,String wasmade,String km,String platenum,String engserial,String chassisnum){
		
		SQLiteDatabase db=getWritableDatabase();
		ContentValues car = new ContentValues();
		
		car.put("brand", brand);
		car.put("model", model);
		car.put("was_made", wasmade);
		car.put("kilometers", km);
		car.put("plate_num", platenum);
		car.put("engine_serial", engserial);
		car.put("chassis_num", chassisnum);
		
		db.insert("mycars", null, car);
		
	}
	
    public void removecar(Integer nth){
    	
    	SQLiteDatabase db=getWritableDatabase();
    	
    	String whereClause="plate_num"+"=?";
    	String sqlTables= "mycars";
    	
    	Cursor c=db.rawQuery("SELECT plate_num FROM mycars LIMIT 1 OFFSET ?", new String[] {nth.toString()}); 
    	c.moveToFirst();
    	String[] sel=new String[] {c.getString(0)};
    	db.delete(sqlTables, whereClause , sel);
    }
 
    public Cursor getcarbyorder(Integer nth){
    	SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		Cursor c=db.rawQuery("SELECT * FROM mycars LIMIT 1 OFFSET ?", new String[] {nth.toString()});
		c.moveToFirst();
    	
    	return c;
    }
}