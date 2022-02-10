/* SelectboxSelectByIndexAgentBuilder.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 4 12:46:50 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zats.mimic.impl.operation.select;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zats.mimic.AgentException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.impl.ClientCtrl;
import org.zkoss.zats.mimic.impl.OperationAgentBuilder;
import org.zkoss.zats.mimic.impl.operation.AgentDelegator;
import org.zkoss.zats.mimic.operation.SelectByIndexAgent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Selectbox;

/**
 * A builder for selection by index agent at Selectbox component.
 * @author pao
 */
public class SelectboxSelectByIndexAgentBuilder implements OperationAgentBuilder<ComponentAgent, SelectByIndexAgent> {

	public SelectByIndexAgent getOperation(final ComponentAgent target) {
		return new SelectByIndexAgentImpl(target);
	}

	public Class<SelectByIndexAgent> getOperationClass() {
		return SelectByIndexAgent.class;
	}

	class SelectByIndexAgentImpl extends AgentDelegator<ComponentAgent> implements SelectByIndexAgent {

		public SelectByIndexAgentImpl(ComponentAgent target) {
			super(target);
		}

		public void select(int index) {
			// check target
			if (!target.is(Selectbox.class))
				throw new AgentException("target is not a Selectbox");

			// check bounds
			Selectbox sb = target.as(Selectbox.class);
			if (index < 0 || index >= sb.getModel().getSize())
				throw new AgentException("index out of bounds: " + index);

			// post AU
			String cmd = Events.ON_SELECT;
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("", index);
			ClientCtrl ctrl = (ClientCtrl) target.getClient();
			String desktopId = target.getDesktop().getId();
			ctrl.postUpdate(desktopId, target.getUuid(), cmd, data, false);
			ctrl.flush(desktopId);
		}
	}
}
