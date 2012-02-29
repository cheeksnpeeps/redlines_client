package com.rlm.dam;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

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
import org.apache.xerces.jaxp.SAXParserImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import betamax.Betamax;
import betamax.Recorder;

public class SimpleClientTest {
	private static final String PUBLIC_CATALOG = "media/catalogs/public";
	private static final String PATH = "/media/services/rest/";
	private static final String HOST = "http://demo.entermediasoftware.com";
	private static boolean isPrintOn = true;
	private static List<Cookie> cookies = null;

	private DefaultHttpClient client = null;
	private HttpResponse response = null;

	@Rule
	public Recorder recorder = new Recorder();

	@Before
	public void initSecurityCookies() throws Exception {
		client = new DefaultHttpClient();
		HttpRoutePlanner routePlanner = new ProxySelectorRoutePlanner(client.getConnectionManager().getSchemeRegistry(),
				ProxySelector.getDefault());
		client.setRoutePlanner(routePlanner);
		List<Cookie> securityCookies = getCookies();
		for (Cookie cookie : securityCookies) {
			client.getCookieStore().addCookie(cookie);
		}
	}

	@After
	public void flushInputStream() {
		InputStream input;
		BufferedReader br = null;
		try {
			input = response.getEntity().getContent();
			br = new BufferedReader(new InputStreamReader(input));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (isPrintOn)
					System.out.println(strLine);
			}
		} catch (IOException e) {
			// already flushed, don't care
		}
	}

	@Betamax(tape = "my tape")
	@Test
	public void testListCatalogs() throws ClientProtocolException, IOException {
		HttpGet getmethod = new HttpGet(HOST + PATH + "listcatalogs.xml");
		response = client.execute(getmethod);
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}

	@Betamax(tape = "asset search tape")
	@Test
	public void assetSearch() throws Exception {
		HttpPost httppost = new HttpPost(HOST + PATH + "assetsearch.xml");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("catalogid", PUBLIC_CATALOG));
		httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		response = client.execute(httppost);
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}

	@Betamax(tape = "asset details tape")
	@Test
	public void assetDetails() throws Exception {
		HttpPost httppost = new HttpPost(HOST + PATH + "assetdetails.xml");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("catalogid", PUBLIC_CATALOG));
		nvps.add(new BasicNameValuePair("id", "103"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		response = client.execute(httppost);
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}

	@Test
	public void editAssetMetaData() throws Exception {

		String updatedName = "test" + Math.random() + "";

		HttpPost httpSevePost = new HttpPost(HOST + PATH + "saveassetdetails.xml");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("catalogid", PUBLIC_CATALOG));
		nvps.add(new BasicNameValuePair("id", "103"));
		nvps.add(new BasicNameValuePair("field", "name"));
		nvps.add(new BasicNameValuePair("name.value", updatedName));
		httpSevePost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		response = client.execute(httpSevePost);
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		flushInputStream();

		HttpPost httppost = new HttpPost(HOST + PATH + "assetdetails.xml");
		nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("catalogid", PUBLIC_CATALOG));
		nvps.add(new BasicNameValuePair("id", "103"));
		httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		response = client.execute(httppost);
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		String name = getValueFromXML(response.getEntity().getContent(), "name");
		
		assertEquals(updatedName, name);
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

	public void parseXml(InputStream is) throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(is);
		String expression = "/rsp/property";

		NodeList nodes = (NodeList) XPathFactory.newInstance().newXPath().evaluate(expression, doc, XPathConstants.NODESET);
		for (int i = 0, l = nodes.getLength(); i < l; i++) {
			Element node = (Element) nodes.item(i);
			System.out.println(node.getAttribute("id") + " = " + node.getTextContent());
		}
	}

	public String getValueFromXML(InputStream is, String fieldName) throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		NodeList nodes = (NodeList) XPathFactory.newInstance().newXPath().evaluate("/rsp/property", db.parse(is), XPathConstants.NODESET);
		String result = null;
		for (int i = 0, l = nodes.getLength(); i < l; i++) {
			Element node = (Element) nodes.item(i);
			if (fieldName.equals(node.getAttribute("id")))
				return node.getTextContent();
		}
		return result;
	}
}
