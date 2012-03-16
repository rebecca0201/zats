package org.zkoss.zats.core.component.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.zkoss.zats.core.Conversation;
import org.zkoss.zats.core.component.DesktopNode;
import org.zkoss.zats.core.component.PageNode;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;

public class DefaultDesktopNode implements DesktopNode
{
	private Conversation conversation;
	private Desktop desktop;

	public DefaultDesktopNode(Conversation conversation, Desktop desktop)
	{
		this.conversation = conversation;
		this.desktop = desktop;
	}

	public String getId()
	{
		return desktop.getId();
	}

	public String getType()
	{
		return "desktop";
	}

	public Object getAttribute(String name)
	{
		return desktop.getAttribute(name);
	}

	public Map<String, Object> getAttributes()
	{
		return desktop.getAttributes();
	}

	public Conversation getConversation()
	{
		return conversation;
	}

	public List<PageNode> getPages()
	{
		List<PageNode> nodes = new ArrayList<PageNode>();
		Iterator<?> iter = desktop.getPages().iterator();
		while(iter.hasNext())
			nodes.add(new DefaultPageNode(this, (Page)iter.next()));
		return nodes;
	}

	public Desktop cast()
	{
		return desktop;
	}
}
