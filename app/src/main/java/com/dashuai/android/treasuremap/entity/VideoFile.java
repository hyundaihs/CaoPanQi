package com.dashuai.android.treasuremap.entity;

public class VideoFile {
	private String title;
	private String http_url;

	public VideoFile() {
		super();
	}

	public VideoFile(String title, String http_url) {
		super();
		this.title = title;
		this.http_url = http_url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHttp_url() {
		return http_url;
	}

	public void setHttp_url(String http_url) {
		this.http_url = http_url;
	}

	@Override
	public String toString() {
		return "VideoFile [title=" + title + ", http_url=" + http_url + "]";
	}

}
