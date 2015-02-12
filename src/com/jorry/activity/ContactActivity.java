package com.jorry.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ContactActivity extends Activity{

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    ListView list = new ListView(this);
  }
}
