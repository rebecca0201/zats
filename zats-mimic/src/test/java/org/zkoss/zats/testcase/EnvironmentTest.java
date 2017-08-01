/* ConversationTest.java

	Purpose:
		
	Description:
		
	History:
		2012/3/22 Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zats.testcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Test;
import org.zkoss.zats.mimic.Client;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DefaultZatsEnvironment;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zats.mimic.impl.DesktopCtrl;
import org.zkoss.zats.mimic.impl.LayoutResponseHandler;
import org.zkoss.zats.mimic.impl.ResponseHandlerManager;
import org.zkoss.zats.mimic.impl.UpdateResponseHandler;
import org.zkoss.zats.mimic.operation.ClickAgent;
import org.zkoss.zats.mimic.operation.InputAgent;
import org.zkoss.zats.mimic.operation.SortAgent;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zul.Label;

/**
 * this is for local test, don't include it in other project
 * @author dennis
 *
 */
public class EnvironmentTest {
	
	@Test
	public void testNested() {
		//to test open a local zul
		DefaultZatsEnvironment ctx = new DefaultZatsEnvironment();
		try{
			ctx.init("./src/test/resources/web");
			Client client = ctx.newClient();
			DesktopAgent desktop = client.connect("/basic/click.zul");
			assertEquals("Hello World!", desktop.query("#msg").as(Label.class).getValue());
			desktop.query("#btn").as(ClickAgent.class).click();
			assertEquals("Welcome", desktop.query("#msg").as(Label.class).getValue());
		
		
			DefaultZatsEnvironment ctx2 = new DefaultZatsEnvironment();
			try{
				ctx2.init("./src/test/resources/web");
				Client client2 = ctx2.newClient();
				DesktopAgent desktop2 = client2.connect("/basic/click.zul");
				assertEquals("Hello World!", desktop2.query("#msg").as(Label.class).getValue());
				desktop2.query("#btn").as(ClickAgent.class).click();
				assertEquals("Welcome", desktop2.query("#msg").as(Label.class).getValue());
				

				Assert.assertFalse(desktop.getId().equals(desktop2.getId()));
				Assert.assertFalse(desktop.query("#btn").getUuid().equals(desktop2.query("#btn").getUuid()));
				
			}finally{
				ctx2.destroy();
			}
		}finally{
			ctx.destroy();
		}
	}
	
	@Test
	public void testCustomConfig() {
		//to test open a local zul
		DefaultZatsEnvironment ctx = new DefaultZatsEnvironment();//test first, the zk-library property is loaded in static
		try {
			ctx.init("./src/test/resources/web");
			Client client = ctx.newClient();
			DesktopAgent desktop = client.connect("/basic/custom-config.zul");
			//default config
			assertEquals("", desktop.query("#msg").as(Label.class).getValue());
		} finally {
			ctx.destroy();
		}
				
		//to test open a local zul
		ctx = new DefaultZatsEnvironment("./src/test/resources/web/WEB-INF/custom");
		try{
			ctx.init("./src/test/resources/web");
			Client client = ctx.newClient();
			DesktopAgent desktop = client.connect("/basic/custom-config.zul");
			//custom config
			assertEquals("hello zats", desktop.query("#msg").as(Label.class).getValue());
		}finally{
			ctx.destroy();
		}
	}
	
	@Test
	public void testContextPath() {
		DefaultZatsEnvironment ctx = null;		
		//to test open a local zul
		ctx = new DefaultZatsEnvironment("./src/test/resources/web/WEB-INF");
		try{
			ctx.init("./src/test/resources/web");
			Client client = ctx.newClient();
			DesktopAgent desktop = client.connect("/basic/contextpath.zul");
			//custom config
			assertEquals("", desktop.query("#msg").as(Label.class).getValue());
		}finally{
			ctx.destroy();
		}
		
		ctx = new DefaultZatsEnvironment("./src/test/resources/web/WEB-INF","/myapp");
		try{
			ctx.init("./src/test/resources/web");
			Client client = ctx.newClient();
			DesktopAgent desktop = client.connect("/basic/contextpath.zul");
			//custom config
			assertEquals("/myapp", desktop.query("#msg").as(Label.class).getValue());
		}finally{
			ctx.destroy();
		}
	}
	
	@Test
	public void test2Conversations(){
		DefaultZatsEnvironment ctx = new DefaultZatsEnvironment();
		try{
			ctx.init("./src/test/resources/web");
			
			DesktopAgent desktop1 = ctx.newClient().connect("/basic/click.zul");
			DesktopAgent desktop2 = ctx.newClient().connect("/basic/click.zul");
			assertNotSame(desktop1, desktop2);
			assertNotSame(((HttpSession)((Desktop)desktop1.getDelegatee()).getSession().getNativeSession()).getId(),
					((HttpSession)((Desktop)desktop2.getDelegatee()).getSession().getNativeSession()).getId());
			
			assertEquals("Hello World!", desktop1.query("#msg").as(Label.class).getValue());
			desktop1.query("#btn").as(ClickAgent.class).click();
			assertEquals("Welcome", desktop1.query("#msg").as(Label.class).getValue());
			
			assertEquals("Hello World!", desktop2.query("#msg").as(Label.class).getValue());
			desktop2.query("#btn").as(ClickAgent.class).click();
			assertEquals("Welcome", desktop2.query("#msg").as(Label.class).getValue());
		}finally{
			ctx.destroy();
		}
	}
	
	//close conversation or desktop 
	@Test
	public void testDestroyDesktop() throws Exception{
		DefaultZatsEnvironment ctx = new DefaultZatsEnvironment();
		
		try{
			ctx.init("./src/test/resources/web");
			
			DesktopAgent desktop = ctx.newClient().connect("/basic/click.zul");
			((Desktop)desktop.getDelegatee()).getWebApp().getConfiguration().addListener(org.zkoss.zats.testapp.DesktopCleanListener.class);
			ctx.cleanup();
			Assert.assertFalse(((Desktop)desktop.getDelegatee()).isAlive());
			
			desktop = ctx.newClient().connect("/basic/click.zul");
			desktop.getClient().destroy();
			Assert.assertFalse(((Desktop)desktop.getDelegatee()).isAlive());
			
			desktop = ctx.newClient().connect("/basic/click.zul");
			desktop.destroy();
			Assert.assertFalse(((Desktop)desktop.getDelegatee()).isAlive());
		}finally{
			ctx.destroy();
		}
	}
	
	@Test
	public void testCookies() {
		Zats.init(".");
		try {
			Client client = Zats.newClient();
			Map<String, String> cookies = client.getCookies();
			assertEquals("{}", cookies.toString());

			DesktopAgent desktop = client.connect("/~./basic/cookie.zul");
			cookies = client.getCookies();
			assertFalse("{}".equals(cookies.toString()));
			Label msg = desktop.query("#msg").as(Label.class);
			assertEquals("", msg.getValue());

			// set by server
			desktop.query("#myCookie").as(InputAgent.class).type("testing");
			desktop.query("#set").click();
			assertEquals("testing", client.getCookie("myCookie"));
			desktop.query("#show").click();
			assertTrue(msg.getValue().contains("myCookie=testing"));

			// set by server again
			desktop.query("#myCookie").as(InputAgent.class).type("zk");
			desktop.query("#set").click();
			assertEquals("zk", client.getCookie("myCookie"));
			desktop.query("#show").click();
			assertTrue(msg.getValue().contains("myCookie=zk"));

			// erase by server
			desktop.query("#delete").click();
			assertTrue(client.getCookie("myCookie") == null);
			desktop.query("#show").click();
			assertFalse(msg.getValue().contains("myCookie"));
			
			// set by client
			client.setCookie("hello", "world");
			assertEquals("world" , client.getCookie("hello"));
			desktop.query("#show").click();
			assertTrue(msg.getValue().contains("hello=world"));
			
			// set by client again
			client.setCookie("hello", "zk");
			assertEquals("zk" , client.getCookie("hello"));
			desktop.query("#show").click();
			assertTrue(msg.getValue().contains("hello=zk"));
			
			// erase by client
			client.setCookie("hello" , null);
			assertTrue(client.getCookie("hello") == null);
			desktop.query("#show").click();
			assertFalse(msg.getValue().contains("hello"));
			
			// special case
			
			// set empty cookie
			desktop.query("#myCookie").as(InputAgent.class).type("");
			desktop.query("#set").click();
			assertEquals("", client.getCookie("myCookie"));
			desktop.query("#show").click();
			assertTrue(msg.getValue().contains("myCookie=;"));

			// illegal cookie name
			try {
				client.setCookie(null, "zk");
				Assert.fail();
			} catch (Throwable e) {
			}
			try {
				client.setCookie("$hello", "zk");
				Assert.fail();
			} catch (Throwable e) {
			}			
			
		} finally {
			Zats.cleanup();
			Zats.end();
		}
	}
	
	@Test
	public void testConcurrentPostAU() {
		Zats.init(".");
		try {

			// exceptions handler
			final Queue<Throwable> exceptions = new ConcurrentLinkedQueue<Throwable>();
			UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
				public void uncaughtException(Thread t, Throwable e) {
					exceptions.add(e);
				}
			};

			// concurrent test with different desktops
			final DesktopAgent[] desktops = { 
					Zats.newClient().connect("/~./basic/group-sort.zul"),
					Zats.newClient().connect("/~./basic/group-sort.zul") 
			};
			Runnable work = new Runnable() {
				public void run() {
					int index = Integer.parseInt(Thread.currentThread().getName());
					DesktopAgent desktop = desktops[index];
					for (int i = 0; i < 100; ++i) {
						Label status = desktop.query("#sortStatus").as(Label.class);
						ComponentAgent column = desktop.query("column[label='Author']");
						column.as(SortAgent.class).sort(false);
						assertEquals(i + " rounds", "Author,false", status.getValue());
						column.as(SortAgent.class).sort(true);
						assertEquals(i + " rounds", "Author,true", status.getValue());
					}
				}
			};
			Thread[] threads = { 
					new Thread(work, "0"), 
					new Thread(work, "1") 
			};
			for (Thread t : threads) {
				t.setUncaughtExceptionHandler(handler);
				t.start();
			}
			for (Thread t : threads)
				t.join();
			for(DesktopAgent d : desktops)
				d.destroy();
			if(!exceptions.isEmpty())
				throw exceptions.remove();
			
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			Zats.cleanup();
			Zats.end();
		}
	}
	
	public static class LayoutHandlerImpl implements LayoutResponseHandler {
		public static boolean enabled = true; // avoid other test cases
		public static int count = 0;

		public void process(DesktopAgent desktop, String response) {
			if (!enabled)
				return;
			++count;
			assertTrue(desktop != null);
			assertTrue(desktop instanceof DesktopCtrl);
			assertTrue(response != null);
			assertTrue(response.indexOf("html") >= 0);
			assertTrue(response.indexOf("ZK") >= 0);
			assertTrue(response.indexOf("Hello World") >= 0);
		}
	}
	
	public static class UpdateHandlerImpl implements UpdateResponseHandler {
		public static boolean enabled = true; // avoid other test cases
		public static int count = 0;

		public void process(DesktopAgent desktop, Map<String, Object> jsonObject) {
			if (!enabled)
				return;
			++count;
			assertTrue(desktop != null);
			assertTrue(desktop instanceof DesktopCtrl);
			assertTrue(jsonObject != null);
			assertTrue(jsonObject.containsKey("rid"));
			assertTrue(jsonObject.containsKey("rs"));
			List<?> list = (List<?>) jsonObject.get("rs");
			list = (List<?>) list.get(0);
			Assert.assertEquals("setAttr", list.get(0));
			list = (List<?>) list.get(1);
			assertEquals("value", list.get(1));
			assertEquals("Welcome", list.get(2));
		}
	}

	@Test
	public void testResponseHandler() {
		// test handlers register
		ResponseHandlerManager manager = ResponseHandlerManager.getInstance();
		// layout handlers
		manager.registerHandler("*", "*", "foo0", LayoutHandlerImpl.class.getName());
		manager.registerHandler("*", "*", "foo1", new LayoutHandlerImpl());
		manager.registerHandler("*", "*", "foo1", new LayoutHandlerImpl()); // test duplicate
		manager.registerHandler("9.9.9", "*", "foo2", new LayoutHandlerImpl());
		// update handlers
		manager.registerHandler("*", "*", "bar0", UpdateHandlerImpl.class.getName());
		manager.registerHandler("*", "*", "bar1", new UpdateHandlerImpl());
		manager.registerHandler("*", "*", "bar1", new UpdateHandlerImpl()); // test duplicate
		manager.registerHandler("9.9.9", "*", "bar2", new UpdateHandlerImpl());

		Zats.init(".");
		try {
			DesktopAgent desktopAgent = Zats.newClient().connect("/~./basic/click.zul");
			assertEquals("Hello World!", desktopAgent.query("#msg").as(Label.class).getValue());
			desktopAgent.query("#btn").as(ClickAgent.class).click();
			assertEquals("Welcome", desktopAgent.query("#msg").as(Label.class).getValue());
			assertEquals(2 , LayoutHandlerImpl.count);
			assertEquals(2 , UpdateHandlerImpl.count);
		} finally {
			LayoutHandlerImpl.enabled = false;
			UpdateHandlerImpl.enabled = false;
			Zats.cleanup();
			Zats.end();
		}
	}
	
	@Test
	public void testSameSession() {
		Zats.init(".");
		try {
			String zul = "/~./basic/click.zul";
			
			Client c = Zats.newClient();
			DesktopAgent desktop1 = c.connect(zul);
			String s1 = ((HttpSession) ((Desktop) desktop1.getDelegatee()).getSession().getNativeSession()).getId();
			DesktopAgent desktop2 = c.connect(zul);
			String s2 = ((HttpSession) ((Desktop) desktop2.getDelegatee()).getSession().getNativeSession()).getId();
			assertFalse(desktop1.getId().equals(desktop2.getId()));
			assertEquals(s1, s2);
			
			DesktopAgent desktop3 = Zats.newClient().connect(zul);
			String s3 = ((HttpSession) ((Desktop) desktop3.getDelegatee()).getSession().getNativeSession()).getId();
			assertFalse(desktop1.getId().equals(desktop3.getId()));
			assertFalse(desktop2.getId().equals(desktop3.getId()));
			assertFalse(desktop1.getId().equals(desktop2.getId()));
			assertEquals(s1, s2);
			assertFalse(s1.equals(s3));
			assertFalse(s2.equals(s3));
			
		} finally {
			Zats.cleanup();
			Zats.end();
		}
	}
	
	@Test
	public void testConnectAsIncluded1() {
		Zats.init(".");
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			for (int i = 0; i < 10; ++i)
				args.put("k" + i, "v" + i);

			Client client = Zats.newClient();
			DesktopAgent desktop = client.connectAsIncluded("/~./basic/include.zul", args);
			Assert.assertNotNull(desktop.query("#content"));
			List<ComponentAgent> labels = desktop.queryAll("#content label");
			Assert.assertEquals(args.size(), labels.size());

			Map<String, Object> results = new HashMap<String, Object>();
			for (ComponentAgent label : labels) {
				String[] tokens = label.as(Label.class).getValue().split("=");
				results.put(tokens[0], tokens[1]);
			}
			Assert.assertEquals(args, results);
			
			// test exceptions
			try {
				client.connect(null);
				Assert.fail("should throw an exception");
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
			try {
				client.connectAsIncluded(null, args);
				Assert.fail("should throw an exception");
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}

		} finally {
			Zats.end();
		}
	}
	
	@Test
	public void testConnectAsIncluded2() {

		Zats.init(".");
		try {
			Map<String, Object> args = new HashMap<String, Object>();
			for (int i = 0; i < 5; ++i)
				args.put("key" + i, "value" + i);

			Client client = Zats.newClient();
			DesktopAgent desktop = client.connect("/~./basic/include-outer.zul");
			Assert.assertNotNull(desktop.query("#inner #content"));
			List<ComponentAgent> labels = desktop.queryAll("#inner #content label");
			Assert.assertEquals(args.size(), labels.size());

			Map<String, Object> results = new HashMap<String, Object>();
			for (ComponentAgent label : labels) {
				String[] tokens = label.as(Label.class).getValue().split("=");
				results.put(tokens[0], tokens[1]);
			}
			Assert.assertEquals(args, results);

		} finally {
			Zats.end();
		}

	}
}
