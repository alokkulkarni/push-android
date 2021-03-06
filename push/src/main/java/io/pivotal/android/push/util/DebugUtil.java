/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.push.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Debug utilities class
 */
public class DebugUtil {

	private static DebugUtil instance = null;

	public static DebugUtil getInstance(Context context) {
		if (instance == null) {
			instance = new DebugUtil(context);
		}
		return instance;
	}

	private boolean isDebuggable = false;

	private DebugUtil(Context context) {

		try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo applicationInfo;
			applicationInfo = pm.getApplicationInfo(context.getPackageName(), 0);
			isDebuggable = (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		} catch (NameNotFoundException e) {
			isDebuggable = false;
		} catch (NullPointerException e) {
			isDebuggable = false;
		}
	}

	/**
	 * Checks if the debuggable flag is set in the application's manifest file.
	 *
	 * @return Returns true if the application's manifest file has the debuggable flag set, else returns false.
	 */
	public boolean isDebuggable() {
		return isDebuggable;
	}
}
