package com.kokkinakis.service;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.kokkinakis.service.R;

public class articles_list extends Fragment
{
	int article_page=1;
	public static View spinner;
	public static Button next_btn;
	ListView listview;
	FetchJSONForList task;
	
	 @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    return;}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.articles_list, null);
        
	       listview=(ListView) root.findViewById(R.id.articles_listview); //Τα υπολοιπα φορτωνονται μεσα στο async
	       
	       
	       spinner=root.findViewById(R.id.spinner);
	      
	       
	       task=new FetchJSONForList(getActivity(),listview);
	       task.execute("http://jimmylabros.comoj.com/data_pull.php?page="+article_page);
	       
	       next_btn=new Button(getActivity());
	       next_btn=(Button) root.findViewById(R.id.articles_nextbtn);
	       final Button back_btn=(Button) root.findViewById(R.id.articles_backbtn);
	       
	       if(article_page==1)back_btn.setEnabled(false);
	        back_btn.setOnClickListener(new View.OnClickListener(){
	        			public void onClick(View v){
	        				
	                        next_btn.setEnabled(true);
	        				
	        				if(article_page>1){
	        					article_page--;
	        					if(article_page==1) back_btn.setEnabled(false);
	        					task=new FetchJSONForList(getActivity(),listview);
	        					task.execute("http://jimmylabros.comoj.com/data_pull.php?page="+article_page);}
	        				else
	        					back_btn.setEnabled(false);
	        			       }
	        });
	        
	        
	        next_btn.setOnClickListener(new View.OnClickListener(){
	        			public void onClick(View v){
	        				
	        				
	        				
	        				back_btn.setEnabled(true);
	        				
	        				article_page++;
	        				task=new FetchJSONForList(getActivity(),listview);
	        			    task.execute("http://jimmylabros.comoj.com/data_pull.php?page="+article_page);
	        			    
	        			    
	        			}
	        			    
	        			       
	        });
	       
	        
	        listview.setOnItemClickListener(new OnItemClickListener()
	        {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				    
				    final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
				    
				    
				    
				    ft.replace(((ViewGroup)getView().getParent()).getId(), new articles_body(position,article_page), "articles_body");
				    
				    ft.addToBackStack(null);
				    
				    ft.commit();
				}
			});
	        
	       return root;
	}

}
