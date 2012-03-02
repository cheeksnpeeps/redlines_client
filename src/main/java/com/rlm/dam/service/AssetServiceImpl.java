package com.rlm.dam.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.rlm.dam.dao.AssetDAO;
import com.rlm.dam.model.Asset;

public class AssetServiceImpl {
	
	private AssetDAO assetDAO = new AssetDAO();

	public List<Asset> fetchAssets(String catalogName) {
		InputStream is = getAssetDAO().fetchAssets(catalogName);
		List<Map<String, String>> values = null;
		try {
			values = parseXml(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Asset> results = new ArrayList<Asset>();
		for (Map<String, String> map : values) {
			results.add(new Asset(map));
		}
		return results;
	}
	
	public AssetDAO getAssetDAO() {
		return assetDAO;
	}

	public void setAssetDAO(AssetDAO assetDAO) {
		this.assetDAO = assetDAO;
	}
	
	public List<Map<String, String>> parseXml(InputStream is) throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(is);
		String expression = "/rsp/hits/hit";

		NodeList nodes = (NodeList) XPathFactory.newInstance().newXPath().evaluate(expression, doc, XPathConstants.NODESET);
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		for (int i = 0, l = nodes.getLength(); i < l; i++) {
			Map<String, String> values = new HashMap<String, String>();
			Element node = (Element) nodes.item(i);
			NamedNodeMap map = node.getAttributes();
			int nodeMapcount = map.getLength();
			for(int j = 0; j < nodeMapcount; j++){
				values.put(map.item(j).getNodeName(), map.item(j).getTextContent());
			}
			values.put("thumb", node.getElementsByTagName("thumb").item(0).getTextContent());
			values.put("preview", node.getElementsByTagName("preview").item(0).getTextContent());
			values.put("original", node.getElementsByTagName("original").item(0).getTextContent());
			results.add(values);
		}
		return results;
	}


}
