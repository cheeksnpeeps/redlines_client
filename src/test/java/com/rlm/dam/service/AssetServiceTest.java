package com.rlm.dam.service;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.rlm.dam.model.Assets;

public class AssetServiceTest {

	@Test
	public void fetchAssets() {
		AssetServiceImpl service = new AssetServiceImpl(){

			@Override
			public List<Assets> fetchAssets(String string) {
				return new ArrayList<Assets>();
			}
			
		};
		List<Assets> assets = null;
		assets = service.fetchAssets("catalog_name");
		assertNotNull("NULL assets", assets);
	}

}
