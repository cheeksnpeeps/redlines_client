package com.rlm.dam.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class AssetDAO {
	private static final String ASSET_SEARCH = "assetsearch.xml";
	private String host = "http://demo.entermediasoftware.com";
	private String path = "/media/services/rest/";
	private DefaultHttpClient client;
	private HttpResponse response;
	private static List<Cookie> cookies = null;
	private String username = "admin";
	private String password = "admin";

	public InputStream fetchAssets(String catalog) {
		HttpPost httppost = new HttpPost(getHost() + getPath() + ASSET_SEARCH);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("catalogid", catalog));
		InputStream result = null;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			response = getClient().execute(httppost);
			result = response.getEntity().getContent();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public DefaultHttpClient getClient() throws ClientProtocolException, IOException {
		client = new DefaultHttpClient();
		HttpRoutePlanner routePlanner = new ProxySelectorRoutePlanner(client.getConnectionManager().getSchemeRegistry(),
				ProxySelector.getDefault());
		client.setRoutePlanner(routePlanner);
		List<Cookie> securityCookies = getCookies();
		for (Cookie cookie : securityCookies) {
			client.getCookieStore().addCookie(cookie);
		}
		return client;
	}

	private List<Cookie> getCookies() throws ClientProtocolException, IOException {
		if (cookies == null)
			cookies = login();
		return cookies;
	}

	List<Cookie> login() throws ClientProtocolException, IOException {
		DefaultHttpClient loginClient = new DefaultHttpClient();
		String url = getHost() + getPath() + "login.xml";
		HttpPost httppost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("accountname", getUsername()));
		nvps.add(new BasicNameValuePair("password", getPassword()));
		httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		response = loginClient.execute(httppost);
		flushInputStream();
		return loginClient.getCookieStore().getCookies();
	}

	private void flushInputStream() {
		InputStream input;
		BufferedReader br = null;
		try {
			input = response.getEntity().getContent();
			br = new BufferedReader(new InputStreamReader(input));
			String strLine;
			while ((strLine = br.readLine()) != null) {
			}
		} catch (IOException e) {
			// already flushed, don't care
		}
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
