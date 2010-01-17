package com.bradmcevoy.http.webdav;

import com.bradmcevoy.http.Resource;
import java.util.List;

/**
 * Applies a proppatch result to a resource
 *
 * This interface is only really needed to support updating properties via the
 * old PropPatchableResource.setFields() method. The more modern way of doing
 * things is through the PropertySource interface, which is symmetrical for
 * reading and writing properties.
 *
 *
 * @author brad
 */
public interface PropPatchSetter {


    /**
     * Update the given resource with the properties specified in the parseResult
     * and return appropriate responses
     *
     * @param href - the address of the resource being patched
     * @param parseResult - the list of properties to be mutated
     * @param r - the resource to be updated
     * @return - responses indicating success or otherwise for each field. Note
     * that success responses should not contain the value
     */
    List<PropFindResponse> setProperties(String href, PropPatchRequestParser.ParseResult parseResult, Resource r);
}