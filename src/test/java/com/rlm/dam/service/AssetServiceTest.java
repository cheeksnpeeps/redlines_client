package com.rlm.dam.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import betamax.Betamax;
import betamax.Recorder;

import com.rlm.dam.dao.AssetDAO;
import com.rlm.dam.model.Asset;

public class AssetServiceTest {
	private static final String PUBLIC_CATALOG = "media/catalogs/public";
	
	@Rule
	public Recorder recorder = new Recorder();

	@Betamax(tape = "asset service search")
	@Test
	public void fetchAssets() {
		AssetServiceImpl service = new AssetServiceImpl();
		List<Asset> assets = null;
		assets = service.fetchAssets(PUBLIC_CATALOG);
		assertNotNull("NULL assets", assets);
		assertNotNull("NULL name from first index", assets.get(1).getName());
		System.out.println(assets);
	}
	
	@Test
	public void hasAssetDAO() {
		AssetServiceImpl service = new AssetServiceImpl(){

			@Override
			public AssetDAO getAssetDAO() {
				return new AssetDAO();
			}
			
		};
		AssetDAO dao = service.getAssetDAO();
		assertNotNull("NULL DAO", dao);
	}

}
