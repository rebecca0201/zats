/* GenericPagingAgentBuilder.java

	Purpose:
		
	Description:
		
	History:
		2012/5/3 Created by Hawk

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zats.mimic.impl.operation;

import java.lang.reflect.Method;
import java.util.Map;

import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.impl.ClientCtrl;
import org.zkoss.zats.mimic.impl.au.EventDataManager;
import org.zkoss.zats.mimic.operation.PagingAgent;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.event.ZulEvents;

/**
 * @author Hawk
 *
 */
public class GenericPagingAgentBuilder implements OperationAgentBuilder<PagingAgent>{
	public PagingAgent getOperation(final ComponentAgent target) {
		return new PagingAgentImpl(target);
	}

	class PagingAgentImpl extends AgentDelegator implements PagingAgent{
		public PagingAgentImpl(ComponentAgent target) {
			super(target);
		}

		/* (non-Javadoc)
		 * @see org.zkoss.zats.mimic.operation.PagingAgent#goTo(int)
		 */
		public void goTo(int page) {
			String desktopId = target.getDesktop().getId();
			String cmd = ZulEvents.ON_PAGING;
			//determine instanceof Paging then get paging's uuid by reflection
			Map<String, Object> data = EventDataManager.build(new PagingEvent(cmd, (Component)target.getDelegatee(),page));

			//get Paging for sending its UUID
			Paging paging = null;
			if (target.getDelegatee() instanceof Paging){
				paging = (Paging)target.getDelegatee();
			}else{
				try{
					Method getPagingChild = target.getDelegatee().getClass().getMethod("getPagingChild");
					paging = (Paging)getPagingChild.invoke(target.getDelegatee());
				}catch(Exception e){
					throw new ZatsException(" cannot retrieve Paging component from "+getDelegatee().getClass().getName(), e);
				}
			}
			
			((ClientCtrl)target.getClient()).postUpdate(desktopId, paging.getUuid(), cmd, data);
			
		}
	}

}
