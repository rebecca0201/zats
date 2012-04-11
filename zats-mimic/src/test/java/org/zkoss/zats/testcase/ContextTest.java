/* ConversationTest.java

	Purpose:
		
	Description:
		
	History:
		2012/3/22 Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zats.testcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.Client;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.ClickAgent;
import org.zkoss.zul.Label;

/**
 * this is for local test, don't include it in other project
 * @author dennis
 *
 */
public class ContextTest {
	@BeforeClass
	public static void init()
	{
		Zats.init("./src/test/resources/web");
	}

	@AfterClass
	public static void end()
	{
		Zats.end();
	}

	@After
	public void after()
	{
		Zats.cleanup();
	}

	@Test
	public void testLoadLocal() {
		//to test open a local zul
		Client client = Zats.newClient();
		DesktopAgent desktop = client.connect("/basic/click.zul");
		assertEquals("Hello World!", desktop.query("#msg").as(Label.class).getValue());
		desktop.query("#btn").as(ClickAgent.class).click();
		assertEquals("Welcome", desktop.query("#msg").as(Label.class).getValue());
	}
	
	@Test
	public void test2Conversations(){
		DesktopAgent desktop1 = Zats.newClient().connect("/basic/click.zul");
		DesktopAgent desktop2 = Zats.newClient().connect("/basic/click.zul");
		assertNotSame(desktop1, desktop2);
		assertNotSame(desktop1.getSession().getId(), desktop2.getSession().getId());
		
		assertEquals("Hello World!", desktop1.query("#msg").as(Label.class).getValue());
		desktop1.query("#btn").as(ClickAgent.class).click();
		assertEquals("Welcome", desktop1.query("#msg").as(Label.class).getValue());
		
		assertEquals("Hello World!", desktop2.query("#msg").as(Label.class).getValue());
		desktop2.query("#btn").as(ClickAgent.class).click();
		assertEquals("Welcome", desktop2.query("#msg").as(Label.class).getValue());
	}
	
	//close conversation or desktop 
	@Test
	public void testDestroyDesktop() throws Exception{
		DesktopAgent desktop = Zats.newClient().connect("/basic/click.zul");
		desktop.getDesktop().getWebApp().getConfiguration().addListener(org.zkoss.zats.testapp.DesktopCleanListener.class);
		Zats.cleanup();
		Assert.assertFalse(desktop.getDesktop().isAlive());
		
		desktop = Zats.newClient().connect("/basic/click.zul");
		desktop.getClient().destroy();
		Assert.assertFalse(desktop.getDesktop().isAlive());
		
		desktop = Zats.newClient().connect("/basic/click.zul");
		desktop.destroy();
		Assert.assertFalse(desktop.getDesktop().isAlive());
	}

}