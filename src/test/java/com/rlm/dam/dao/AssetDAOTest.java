package com.rlm.dam.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.junit.Test;

import betamax.Betamax;

public class AssetDAOTest {

	private static final String PUBLIC_CATALOG = "media/catalogs/public";
	
	@Betamax(tape = "dao fetch assets")
	@Test
	public void fetchAssets() {
		InputStream stream = null;
		AssetDAO dao = new AssetDAO();
		stream = dao.fetchAssets(PUBLIC_CATALOG);
		assertNotNull("Null List of key values", stream);
	}
	
	@Test
	public void login_and_get_cookies() throws Exception{
		List<Cookie> cookies = null;
		cookies = new AssetDAO().login();
		assertNotNull(cookies);
		assertTrue("No Cookies", cookies.size()>=2);
	}

}
