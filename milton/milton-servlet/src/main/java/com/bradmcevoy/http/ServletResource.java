package com.bradmcevoy.http;

import eu.medsea.mimeutil.MimeType;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import eu.medsea.mimeutil.MimeUtil;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Used to provide access to servlet resources via milton. This
 * just wraps the normal processing of the resource in a milton compatible
 * interface
 * 
 * 
 * @author brad
 */
public class ServletResource implements GetableResource {

	private final String localPath;
	private final File file;
	private final HttpServletRequest req;
	private final HttpServletResponse response;

	public ServletResource(File file, String localPath, HttpServletRequest req, HttpServletResponse response) {
		this.file = file;
		this.localPath = localPath;
		this.req = req;
		this.response = response;
		System.out.println("created servlet res: " + file.getAbsolutePath());
	}

	@Override
	public String getUniqueId() {
		return null;
	}

	public int compareTo(Resource res) {
		return this.getName().compareTo(res.getName());
	}

	@Override
	public void sendContent(OutputStream out, Range range, Map<String, String> params, String contentType) throws IOException {
		System.out.println("sendContent: " + localPath);
		try {
			MyResponse myResponse = new MyResponse(HttpManager.response(), out);
			req.getRequestDispatcher(localPath).include(req, myResponse);
		} catch (ServletException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public Object authenticate(String user, String password) {
		return "ok";
	}

	@Override
	public boolean authorise(Request request, Request.Method method, Auth auth) {
		return true;
	}

	@Override
	public String getRealm() {
		return "ettrema";   //TODO
	}

	@Override
	public Date getModifiedDate() {
		Date dt = new Date(file.lastModified());
		return dt;
	}

	@Override
	public Long getContentLength() {
		return null;
	}

	@Override
	public String getContentType(String preferredList) {
		Collection mimeTypes = MimeUtil.getMimeTypes(file);
		StringBuilder sb = null;
		for (Object o : mimeTypes) {
			MimeType mt = (MimeType) o;
			if (sb == null) {
				sb = new StringBuilder();
			} else {
				sb.append(",");
			}
			sb.append(mt.toString());
		}
		if (sb == null) {
			return null;
		}
		String mime = sb.toString();
		MimeType mt = MimeUtil.getPreferedMimeType(preferredList, mime);
		return mt.toString();
	}

	@Override
	public String checkRedirect(Request request) {
		return null;
	}

	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		Long ll = 315360000l; // immutable
		return ll;
	}

	public LockToken getLockToken() {
		return null;
	}

	private class MyResponse extends ServletOutputStream implements HttpServletResponse {

		private final Response response;
		private final OutputStream out;

		public MyResponse(Response response, OutputStream out) {
			this.response = response;
			this.out = out;
		}

		@Override
		public void addCookie(Cookie cookie) {
			response.setCookie(new ServletCookie(cookie));
		}

		@Override
		public boolean containsHeader(String name) {
			return response.getHeaders().containsKey(name);
		}

		@Override
		public String encodeURL(String url) {
			return MiltonServlet.response().encodeURL(url);
		}

		@Override
		public String encodeRedirectURL(String url) {
			return MiltonServlet.response().encodeRedirectURL(url);
		}

		@Override
		public String encodeUrl(String url) {
			return MiltonServlet.response().encodeUrl(url);
		}

		@Override
		public String encodeRedirectUrl(String url) {
			return MiltonServlet.response().encodeRedirectUrl(url);
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void sendError(int sc) throws IOException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void sendRedirect(String location) throws IOException {
			MiltonServlet.response().sendRedirect(location);
		}

		@Override
		public void setDateHeader(String name, long date) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void addDateHeader(String name, long date) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setHeader(String name, String value) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void addHeader(String name, String value) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setIntHeader(String name, int value) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void addIntHeader(String name, int value) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setStatus(int sc) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setStatus(int sc, String sm) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String getCharacterEncoding() {
			return MiltonServlet.response().getCharacterEncoding();
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return this;
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setContentLength(int len) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setContentType(String type) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setBufferSize(int size) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public int getBufferSize() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void flushBuffer() throws IOException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void resetBuffer() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isCommitted() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void reset() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void setLocale(Locale loc) {
			MiltonServlet.response().setLocale(loc);
		}

		@Override
		public Locale getLocale() {
			return MiltonServlet.response().getLocale();
		}

		@Override
		public void write(int b) throws IOException {
			out.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			out.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
		}
	}
}
