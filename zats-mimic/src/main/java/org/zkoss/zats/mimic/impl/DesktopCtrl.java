/* DesktopCtrl.java

	Purpose:
		
	Description:
		
	History:
		May 22, 2012 Created by pao

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zats.mimic.impl;

import org.zkoss.zats.mimic.Resource;

/**
 * The interface of desktop controller.
 * To provide more control of the desktop agent for developers.
 * @author pao
 */
public interface DesktopCtrl {
	
	/**
	 * setting current downloadable file.
	 * @param downloadable a downloadable resouce or null indicated there is no downloadable currently.
	 */
	void setDownloadable(Resource downloadable);

	/**
	 * Append a debug message of AuLog to current desktop.
	 * @param message a message.
	 * @since 2.0.1
	 */
	void appendZkLog(String message);
}
