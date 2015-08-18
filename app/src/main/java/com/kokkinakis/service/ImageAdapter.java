package com.kokkinakis.service;


import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kokkinakis.service.R;

public class ImageAdapter extends BaseAdapter
{	private Integer[] imageIDs;
	private Integer flag;	//Ο adapter γραφτηκε για να υποστηριζει ολα τα imageview της εφαρμογης που φορτωνουν πολλες εικονες τα flags ειναι 
						    //0:εικονες χωρις κειμενο
						    //1:εικονες με κειμενο(χρησιμοποιει το icon.xml για layout)	
							//2:εικονες με κειμενο (για το navigationdrawer)
							//3:animations(εικονες με animation)
	//TODO					//4:ειναι για το listview των αρθρων που ολα ειναι φορτωμενα απο το internet(2 κειμενα και εικονα απο το article_icon.xml)
	private ArrayList<String> icon_text;
	
	private ArrayList<String> titles;
	private Bitmap[] pics;
	
	
	private Context context;
	
	static class ViewHolder {
	    public TextView hold_title;
	    public TextView hold_icon_text;
	    public ImageView image;
	  }
	
	
	public ImageAdapter(Context c,ArrayList<String> ttl,ArrayList<String> txt_preview,ArrayList<Bitmap> thump,Integer flg) //για flag=4
	{
		context =c;
		flag=flg;
		titles=ttl;
		icon_text=txt_preview;
		pics=new Bitmap[thump.size()];
		for(int i = 0;i<thump.size();i++)
		{
			pics[i]=thump.get(i);
		}
	}
	
	public ImageAdapter(Context c,Integer[] imglnk,Integer flg) //constructor  για 0 ή 3(μονο εικονες)
	{
		context = c;
		imageIDs=imglnk;
		flag=flg;
	}
	
	public ImageAdapter(Context c,Integer[] imglnk,Integer flg,ArrayList<String> db_names)//constructor για flag=1 ή 2 (image + text)
	{
		context = c;
		imageIDs=imglnk;
		flag=flg;
		icon_text=db_names;
	}
	
	

	//το imageIDs ειναι ο πινακας με τα Link στις εικονες
	public int getCount() {
		if (flag==4)
			{return pics.length; }
		return imageIDs.length;
	}
	
	
	public Object getItem(int position) {
		return position;
	}
	
	
	public long getItemId(int position) {
		return position;
	}

	//τα flags του constructor χρησιμοποιουνται στο switch case εδω.Γυριζει ενα imageview το οποιο συνδεεται στο fragment
	public View getView(int position, View convertView,ViewGroup parent)
	{
		ViewHolder holder = new ViewHolder(); //Χρησιμοποιείται για garbage management :-(
		
		switch(flag){
			case 0:	ImageView imageView;
				if (convertView == null) {
				imageView = new ImageView(context);
				} 
				else {
					imageView = (ImageView) convertView;}

				imageView.setImageResource(imageIDs[position]);
				return imageView;
			case 1: View v;
				if (convertView == null) {  
					LayoutInflater li = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
					v = li.inflate(R.layout.icon, null);
					TextView tv = (TextView)v.findViewById(R.id.icon_text);    
					tv.setText(""+icon_text.get(position));
					ImageView iv = (ImageView)v.findViewById(R.id.icon_image);
					iv.setImageResource(imageIDs[position]);} 
				else {
					v = (View) convertView;
				}
				return v;	
			case 2: View v1;//θα μπορουσα να βαλω View v αλλα δεν αρεσει στο eclipse(error)
				if (convertView == null){
					LayoutInflater li =(LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
					v1 = li.inflate(R.layout.drawer_icon, null);
					TextView tv = (TextView)v1.findViewById(R.id.drawer_icon_text);
					tv.setText(""+icon_text.get(position));
					ImageView iv = (ImageView)v1.findViewById(R.id.drawer_icon_image);
					iv.setImageResource(imageIDs[position]);
				}
				else{
					v1=(View) convertView;
				}
				return v1;
			/* 	
			case 3: ImageView imageView2;
					if (convertView == null) {
						imageView2 = new ImageView(context);
					} 
					else {
						imageView2 = (ImageView) convertView;}

					imageView2.setImageResource(imageIDs[position]);
					final Animation zoomin = AnimationUtils.loadAnimation(context, R.anim.zoomin);
					final Animation zoomout = AnimationUtils.loadAnimation(context, R.anim.zoomout);
					imageView2.setAnimation(zoomin);
					imageView2.setAnimation(zoomout);
					imageView2.setOnClickListener(new View.OnClickListener(){
						public void onClick(View v) {
							boolean pressed=v.isPressed();
							if(!pressed) {
								v.startAnimation(zoomin);
								pressed = !pressed;
							} else {
								v.startAnimation(zoomout);
							    pressed = !pressed;
							}
						}
					});
					return imageView2;	
			*/
			
			case 4:
				if (convertView == null){
					LayoutInflater li =(LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
					convertView = li.inflate(R.layout.article_icon, null);
					holder.hold_title = (TextView)convertView.findViewById(R.id.articles_title_text);
					holder.hold_icon_text =  (TextView)convertView.findViewById(R.id.articles_icon_text);
					
					
					holder.image = (ImageView)convertView.findViewById(R.id.articles_icon_image);
					
					convertView.setTag(holder);
				}
				else{
					holder=(ViewHolder) convertView.getTag();
				}
				
				String title=new String(titles.get(position));
				String articl_prev=new String(icon_text.get(position));
				Bitmap thump=pics[position];
				
				
				holder.hold_title.setText(title);
				holder.hold_icon_text.setText(articl_prev);
				holder.image.setImageBitmap(thump);
				
				this.notifyDataSetChanged();
				return convertView;									
//αυτο εγινε επειδη για να φορτωσει ολη τη λιστα σωστα(λογω του Garbage management της listview/android)
//Αν δεν χρησιμοποιησουμε αλλη κλαση φορτωνει πολλες φορες τα ιδια αντικειμενα επειδη οτι φευγει απο την οθονη μπαινει σε stack για να μην βαραινει τη μνημη
//ετσι οταν παει κατι καινουριο να φορτωθει αν δεν κανουμε ελεγχο φορτωνει το πρωτο απο αυτη τη stack

				
				
				
		}
		return convertView; //κανονικα δεν πρεπει ποτε να φτανεις σε αυτο το return.Μπηκε απλα για να μην παραπονιεται το eclipse
							
	
	}	

}
