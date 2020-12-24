package com.infomaximum.platform.component.frontend.engine.uploadfile;

import com.infomaximum.cluster.core.io.URIClusterFile;
import com.infomaximum.cluster.struct.Component;
import com.infomaximum.cluster.struct.storage.SourceClusterFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class FrontendMultipartSource implements SourceClusterFile {

	private final Component component;

	private final ConcurrentMap<String, MultipartFile> multipartFiles;

	private final AtomicLong uniqueIds;

	public FrontendMultipartSource(Component component) {
		this.component = component;
		this.multipartFiles = new ConcurrentHashMap<>();
		this.uniqueIds = new AtomicLong(0);
	}

	public URI put(MultipartFile multipartFile) {
		String clusterFileUUID = new UUID(uniqueIds.getAndIncrement(), System.currentTimeMillis()).toString();

		multipartFiles.put(clusterFileUUID, multipartFile);

		return URIClusterFile.createURI(component.getUniqueId(), clusterFileUUID);
	}

	public void remove(URI uri) throws IOException {
		String clusterFileUUID = URIClusterFile.getPathToFileUUID(uri);
		deleteIfExists(clusterFileUUID);
	}

	@Override
	public boolean contains(String clusterFileUUID) throws IOException {
		return multipartFiles.containsKey(clusterFileUUID);
	}

	@Override
	public long getSize(String clusterFileUUID) throws IOException {
		MultipartFile multipartFile = multipartFiles.get(clusterFileUUID);
		if (multipartFile == null) throw new FileNotFoundException(clusterFileUUID);
		return multipartFile.getSize();
	}

	@Override
	public InputStream getInputStream(String clusterFileUUID) throws IOException {
		MultipartFile multipartFile = multipartFiles.get(clusterFileUUID);
		return new BufferedInputStream(multipartFile.getInputStream());
	}

	@Override
	public void delete(String clusterFileUUID) throws IOException {
		deleteIfExists(clusterFileUUID);
	}

	@Override
	public void deleteIfExists(String clusterFileUUID) throws IOException {
		MultipartFile multipartFile = multipartFiles.remove(clusterFileUUID);
		if (multipartFile == null) return;
		CommonsMultipartFile commonsMultipartFile = (CommonsMultipartFile) multipartFile;
		commonsMultipartFile.getFileItem().delete();
	}
}
