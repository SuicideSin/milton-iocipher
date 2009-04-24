package com.ettrema.http.fs;

import com.bradmcevoy.http.LockInfo;
import com.bradmcevoy.http.LockResult;
import com.bradmcevoy.http.LockTimeout;

/**
 *
 */
public interface  FsLockManager {

    LockResult lock(LockTimeout timeout, LockInfo lockInfo, FsResource resource);

    LockResult refresh(String token, FsResource resource);

    void unlock(String tokenId, FsResource resource);

}