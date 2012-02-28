package com.rlm.dam;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import betamax.Betamax;
import betamax.Recorder;

public class SimpleClientTest {
	private static final String PATH = "/media/services/rest/";
	private static final String HOST = "http://demo.entermediasoftware.com";
	private static boolean isPrintOn = false;
	private static List<Cookie> cookies = null;

	private DefaultHttpClient client = null;
	private HttpResponse response = null;

	@Before
	public void initSecurityCookies() throws Exception {
		client = new DefaultHttpClient();
		HttpRoutePlanner routePlanner = new ProxySelectorRoutePlanner(client
				.getConnectionManager().getSchemeRegistry(),
				ProxySelector.getDefault());
		client.setRoutePlanner(routePlanner);
		List<Cookie> securityCookies = getCookies();
		for (Cookie cookie : securityCookies) {
			client.getCookieStore().addCookie(cookie);
		}
	}

	@After
	public void flushInputStream() throws IOException {
		InputStream input = response.getEntity().getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		String strLine;
		while ((strLine = br.readLine()) != null) {
			if (isPrintOn)
				System.out.println(strLine);
		}
	}

	@Rule
	public Recorder recorder = new Recorder();

	@Betamax(tape = "my tape")
	@Test
	public void testListCatalogs() throws ClientProtocolException, IOException {
		HttpGet getmethod = new HttpGet(HOST + PATH + "listcatalogs.xml");
		response = client.execute(getmethod);
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}

	// HELPER METHODS

	private List<Cookie> getCookies() throws Exception {
		if (cookies == null)
			cookies = login();
		return cookies;
	}

	private List<Cookie> login() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = "http://demo.entermediasoftware.com/media/services/rest/login.xml";
		HttpPost httppost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("accountname", "admin"));
		nvps.add(new BasicNameValuePair("password", "admin"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		response = client.execute(httppost);
		flushInputStream();
		return client.getCookieStore().getCookies();
	}

}
