package com.myapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Record {
	private final int idTask;
	private final Long checkIn;
	private final Long checkOut;
	private static final String RECORD_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String UNDEF_VALUE = "#UNDEF";

	public Record(int idTask, Long checkIn, Long checkOut) {
		this.idTask = idTask;
		this.checkIn = checkIn;
		this.checkOut = checkOut;
	}

	public int getIdTask() {
		return idTask;
	}

	public String getCheckIn() {
		return getStringDateTime(checkIn);
	}

	public String getCheckOut() {
		return getStringDateTime(checkOut);
	}

	public String getStringDateTime(Long dateInMs) {
		if (dateInMs == 0)
			return UNDEF_VALUE;

		SimpleDateFormat sdfDateTime = new SimpleDateFormat(RECORD_DATE_FORMAT, Locale.US);
		String dateTime = sdfDateTime.format(new Date(dateInMs));

		return dateTime;
	}

	@Override
	public String toString() {
		return "Record: idTask=" + Integer.toString(idTask) +
				" ChIn=" + checkIn +
				" ChOu=" + checkOut;
	}
}
