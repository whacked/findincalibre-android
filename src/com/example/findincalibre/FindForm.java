package com.example.findincalibre;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FindForm extends Activity {

    private EditText mTitleText;
    private EditText mAuthorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_form);
        
        mTitleText = (EditText) findViewById(R.id.title);
        mAuthorText = (EditText) findViewById(R.id.author);
        Button mConfirmButton = (Button) findViewById(R.id.confirm);
        
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View view) {
        		Intent intent = new Intent();
        		String whereclause = "";
        		String author = mAuthorText.getText().toString();
        		if(author.length() > 0) {
    	            whereclause += "AND lower(b."+DbAdapter.KEY_AUTHOR+") LIKE '%"+author+"%' ";
    	            intent.putExtra("author", author);
        		}
        		String title = mTitleText.getText().toString();
        		if(title.length() > 0) {
    	            whereclause += "AND lower(b."+DbAdapter.KEY_TITLE+") LIKE '%"+title+"%' ";
    	            intent.putExtra("title", title);
        		}
        		intent.putExtra("filter", whereclause);
                setResult(RESULT_OK, intent);
        		
                finish();
            }

        });
    }
}