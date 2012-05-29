/* AuUtility.java

	Purpose:
		
	Description:
		
	History:
		2012/3/22 Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zats.mimic.impl.au;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zats.mimic.AgentException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;

/**
 * A utility for AU.
 * 
 * @author dennis
 */
public class AuUtility {

	/**
	 * lookup the event target of a component, it look up the component and its
	 * ancient. use this for search the actual target what will receive a event
	 * for a action on a component-agent
	 * <p/>
	 * Currently, i get it by server side directly
	 */
	public static ComponentAgent lookupEventTarget(ComponentAgent c, String evtname) {
		if (c == null)
			return null;
		Component comp = (Component)c.getDelegatee();
		if (Events.isListened(comp, evtname, true)) {
			return c;
		}
		return lookupEventTarget(c.getParent(), evtname);

	}

	static void setEssential(Map<String,Object> data,String key, Object obj){
		setEssential(data,key,obj,false);
	}
	static void setEssential(Map<String,Object> data,String key, Object obj, boolean nullable){
		if(obj==null&&!nullable) throw new AgentException("data of "+key+" is null");
		data.put(key, toSafeJsonObject(obj));
	}

	static void setOptional(Map<String,Object> data,String key, Object obj){
		if(obj==null) return;
		data.put(key, toSafeJsonObject(obj));
	}

	static void setReference(Map<String,Object> data,Component comp){
		if(comp==null) return;
		data.put("reference", comp.getUuid());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static private Object toSafeJsonObject(Object obj){
		if(obj instanceof Set){
			//exception if data is Set
			//>>Unexpected character (n) at position 10.
			//>>	at org.zkoss.json.parser.Yylex.yylex(Yylex.java:610)
			//>>	at org.zkoss.json.parser.JSONParser.nextToken(JSONParser.java:270)
			return new ArrayList((Set)obj);
		}
		return obj;
	}
	
	/**
	 * convert JSON object of AU. response to AuResponse list. 
	 * @param jsonObject AU. response. If null, throw null point exception.
	 * @return list of AuResponse if the format of object is valid, or null if otherwise.
	 */
	public static List<AuResponse> convertToResponses(Map<String, Object> jsonObject) {
		// check argument
		if (jsonObject == null)
			throw new NullPointerException("input object can't be null");
		Object responses = jsonObject.get("rs");
		if (!(responses instanceof List))
			return null;

		// fetch all response
		ArrayList<AuResponse> list = new ArrayList<AuResponse>();
		for (Object response : (List<?>) responses) {
			if (response instanceof List) {
				List<?> resp = (List<?>) response;
				if (resp.size() == 2) {
					// create response
					String cmd = resp.get(0).toString();
					Object data = resp.get(1);
					if (data instanceof List)
						list.add(new AuResponse(cmd, ((List<?>) data).toArray()));
					else
						list.add(new AuResponse(cmd, data));
					continue;
				}
			}
			// format is invalid
			return null;
		}
		return list;
	}
}
