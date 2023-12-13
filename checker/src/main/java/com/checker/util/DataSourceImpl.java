package com.checker.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class DataSourceImpl implements javax.activation.DataSource {
	
	final String contentType;

	public DataSourceImpl(StringWriter textWriter, String contentType) {
		super();
		this.textWriter = textWriter;
		this.contentType = contentType;
	}

	final StringWriter textWriter;

	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(textWriter.toString().getBytes(StandardCharsets.UTF_8));
	}

	public OutputStream getOutputStream() throws IOException {
		throw new IOException("Read-only data");
	}

	public String getContentType() {
		return this.contentType;
	}

	public String getName() {
		return "main";
	}

}
