package com.myapp.timerecordingapp;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.myapp.model.DataBaseHandler;

class SendMailOnClickListener implements OnClickListener {
	private final int taskId;
	private final ArrayList<String> records;
	private final Context context;

	public SendMailOnClickListener(int taskId, ArrayList<String> records,
			Context context) {
		this.taskId = taskId;
		this.records = records;
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		DataBaseHandler db = DataBaseHandler.getInstance(context);

		String subject = "Records from task " + db.getTaskName(taskId);

		String body = "";
		for (String rec : records)
			body += rec + "\n\n";

		sendMail(subject, body);
		db.close();
	}

	public void sendMail(String subject, String body) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, body);

		Intent chooser = Intent.createChooser(emailIntent, "Sending email");
		chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		try {
			context.startActivity(chooser);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(context, "There is no email client installed.",
					Toast.LENGTH_SHORT).show();
		}
	}
}