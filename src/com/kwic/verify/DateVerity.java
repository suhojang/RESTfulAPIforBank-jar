package com.kwic.verify;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateVerity {
	public static boolean verify(){
		if (Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date())) > LibVerify.verify)
			return false;
		return true;
	}
}
