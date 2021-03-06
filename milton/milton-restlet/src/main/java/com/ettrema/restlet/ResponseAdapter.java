package com.ettrema.restlet;

import com.bradmcevoy.http.AbstractResponse;
import com.bradmcevoy.http.Cookie;
import org.restlet.data.MediaType;
import org.restlet.engine.adapter.HttpResponse;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.representation.OutputRepresentation;
import org.restlet.util.Series;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ResponseAdapter extends AbstractResponse {

    final protected HttpResponse target;

    ResponseAdapter(HttpResponse target) {
        this.target = target;
    }

    public HttpResponse getTarget() {
        return target;
    }

    protected Series<org.restlet.engine.header.Header> getRawHeaders() {
        return getTarget().getHttpCall().getResponseHeaders();
    }

    @Override
    public void setStatus(Status status) {
        getTarget().setStatus(org.restlet.data.Status.valueOf(status.code));
    }

    @Override
    public Status getStatus() {
        return Status.SC_OK.fromCode(getTarget().getStatus().getCode());
    }

    @Override
    public Map<String, String> getHeaders() {
        return getRawHeaders().getValuesMap();
    }

    @Override
    public void setAuthenticateHeader(List<String> challenges) {
        for (String ch : challenges) {
            getRawHeaders().add(HeaderConstants.HEADER_WWW_AUTHENTICATE, ch);
        }
    }

    @Override
    public void setNonStandardHeader(String code, String value) {
        getRawHeaders().add(code, value);
    }

    @Override
    public String getNonStandardHeader(String code) {
        return getRawHeaders().getValues(code);
    }

    @Override
    public Cookie setCookie(Cookie cookie) {
        getTarget().getCookieSettings().add(new CookieAdapter(cookie).getTarget());
        return cookie;
    }

    @Override
    public Cookie setCookie(String name, String value) {
        CookieAdapter cookie = new CookieAdapter(name, value);
        getTarget().getCookieSettings().add(cookie.getTarget());
        return cookie;
    }

    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException(
           "No direct access to response streams in Restlet, set the response entity instead!"
        );
    }

    @Override
    public void close() {
        // Do nothing, job of the new EntityTransport
    }

    // Grab the Milton entity, wrap it in a Restlet entity, set it on target
    public void setTargetEntity() {
        final Entity entity;
        if ((entity = getEntity()) != null) {
            MediaType contentType = MediaType.valueOf(getContentTypeHeader());
            getTarget().setEntity(new OutputRepresentation(contentType) {

                @Override
                public void write(OutputStream outputStream) throws IOException {
                    try {
                        entity.write(ResponseAdapter.this, outputStream);
                    } catch (IOException ex) {
                        throw ex;
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }

}
