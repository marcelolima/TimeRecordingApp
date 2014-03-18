package com.myapp.timerecordingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.myapp.model.DataBaseHandler;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button buttonNewTask = (Button) findViewById(R.id.button_new_task);
		Button buttonYourTasks = (Button) findViewById(R.id.button_show_tasks);
		Button buttonResetDatabase = (Button) findViewById(R.id.button_reset_database);

		buttonNewTask.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.myapp.timerecordingapp.NEWTASK");
				startActivity(intent);
			}
		});

		buttonYourTasks.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("com.myapp.timerecordingapp.SHOWTASKS");
				startActivity(intent);
			}
		});

		buttonResetDatabase.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alertMessage();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void alertMessage() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which ==  DialogInterface.BUTTON_POSITIVE) {
					DataBaseHandler db = DataBaseHandler.getInstance(getApplicationContext());
					db.clearAllData();
					db.close();
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("ARE YOU SURE??")
		.setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener)
		.show();
	}
}
