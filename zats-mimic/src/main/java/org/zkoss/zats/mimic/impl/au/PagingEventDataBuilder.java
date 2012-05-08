/* PagingEventDataBuilder.java

	Purpose:
		
	Description:
		
	History:
		2012/5/4 Created by Hawk

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zats.mimic.impl.au;

import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.event.PagingEvent;

/**
 * @author Hawk
 *
 */
public class PagingEventDataBuilder implements EventDataBuilder {

	public Map<String, Object> build(Event event, Map<String, Object> data) {
		PagingEvent evt = (PagingEvent) event;
		AuUtility.setEssential(data, "", evt.getActivePage());
		return data;
	}

}
