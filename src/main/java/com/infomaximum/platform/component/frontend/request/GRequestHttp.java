package com.infomaximum.platform.component.frontend.request;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.struct.GRequest;

import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class GRequestHttp extends GRequest {

	private final HashMap<String, String[]> parameters;
	private final HashMap<String, String[]> attributes;

	private final Cookie[] cookies;

	private final ArrayList<UploadFile> uploadFiles;

	public GRequestHttp(Instant instant, RemoteAddress remoteAddress, String query, HashMap<String, Serializable> queryVariables, HashMap<String, String[]> parameters, HashMap<String, String[]> attributes, Cookie[] cookies, ArrayList<UploadFile> uploadFiles) {
		super(instant, remoteAddress, query, queryVariables);

		this.parameters = parameters;

		this.attributes = attributes;

		this.cookies = cookies;

		this.uploadFiles = uploadFiles;
	}

	public String getParameter(String name) {
		String[] values = getParameters(name);
		return (values == null) ? null : values[0];
	}

	public String[] getParameters(String name) {
		return parameters.get(name);
	}

	public String[] getAttributes(String name) {
		return attributes.get(name);
	}

	public Cookie getCookie(String name) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) return cookie;
			}
		}
		return null;
	}

	public ArrayList<UploadFile> getUploadFiles() {
		return uploadFiles;
	}

	public static class UploadFile implements RemoteObject {

		public final String fieldname;
		public final String filename;
		public final URI uri;

		/**
		 * Если файл небольших размеров, то он будет хранить в оперативке, без скидывания на диск
		 */
		public final boolean isInMemory;
		public final long size;

		public UploadFile(String fieldname, String filename, URI uri, boolean isInMemory, long size) {
			this.fieldname = fieldname;
			this.filename = filename;
			this.uri = uri;
			this.isInMemory = isInMemory;
			this.size = size;
		}
	}
}
