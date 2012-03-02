package com.rlm.dam.model;

import java.util.Map;

public class Asset {
	private String id;
	private String name;
	private String sourcepath;
	private String fileformatid;
	private String fileformat;
	private String thumb;
	private String preview;
	private String original;
	

	public Asset(Map<String, String> asset) {
		setId(asset.get("id"));
		setName(asset.get("name"));
		setSourcepath(asset.get("sourcepath"));
		setFileformat(asset.get("fileformat"));
		setFileformatid(asset.get("fileformatid"));
		setThumb(asset.get("thumb"));
		setPreview(asset.get("preview"));
		setOriginal(asset.get("original"));
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getSourcepath() {
		return sourcepath;
	}


	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}


	public String getFileformatid() {
		return fileformatid;
	}


	public void setFileformatid(String fileformatid) {
		this.fileformatid = fileformatid;
	}


	public String getFileformat() {
		return fileformat;
	}


	public void setFileformat(String fileformat) {
		this.fileformat = fileformat;
	}


	public String getThumb() {
		return thumb;
	}


	public void setThumb(String thumb) {
		this.thumb = thumb;
	}


	public String getPreview() {
		return preview;
	}


	public void setPreview(String preview) {
		this.preview = preview;
	}


	public String getOriginal() {
		return original;
	}


	public void setOriginal(String original) {
		this.original = original;
	}


	@Override
	public String toString() {
		return "Asset [id=" + id + ", name=" + name + ", sourcepath=" + sourcepath + ", fileformatid=" + fileformatid + ", fileformat="
				+ fileformat + ", thumb=" + thumb + ", preview=" + preview + ", original=" + original + "]";
	}

	
}
