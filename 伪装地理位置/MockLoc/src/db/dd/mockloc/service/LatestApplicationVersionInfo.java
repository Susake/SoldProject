package db.dd.mockloc.service;

import java.io.Serializable;

/**
 * DTO class containing latest application info received from update server.
 * @author robert
 *
 */
public class LatestApplicationVersionInfo implements Serializable {
	private String url;
	private String version;
	private String message;
	private String device;
	private String app;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
}
