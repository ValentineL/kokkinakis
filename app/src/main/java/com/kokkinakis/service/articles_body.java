package com.kokkinakis.service;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kokkinakis.service.R;

public class articles_body extends Fragment{
	
	public Integer position;
	public Integer article_page;
	FetchJSONForBody task;
	
	
	public articles_body(Integer p,Integer ap) {
    	
    	position=p;
    	article_page=ap;
	}
	
	public static Fragment newInstance(Context context,Integer p,Integer ap) {
    	articles_body f = new articles_body(p,ap);
        return f;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.articles_body, null);
        
        TextView tv_title = (TextView) root.findViewById(R.id.articles_body_title);
        
         TextView tv_article_body = (TextView) root.findViewById(R.id.articles_body);
         
         ImageView iv_article = (ImageView) root.findViewById(R.id.articles_body_image);
       
        
        task=new FetchJSONForBody(getActivity(),tv_title,tv_article_body,iv_article);
        Integer id=((article_page-1)*10)+position+1;
	    task.execute("http://jimmylabros.comoj.com/article_pull.php?id="+id);
        
        return root;
	}
}
