package com.homedepot.ngfp.session.service;

import com.homedepot.ngfp.session.support.SharedDeviceSessionData;
import com.homedepot.ngfp.session.support.SharedUserSessionData;

/**
 * Created by Matt Mehalso on 6/19/13.  Remote data service to obtain device session data and user
 * session data.
 */
interface IRemoteSessionService {
    /**
     * Remote method to obtain device session data.  Will only be executed when caller has a permission
     * specific to this method call.
     */
    SharedDeviceSessionData getDeviceData();

    /**
     * Remote method to obtain user session data.  Will only be executed when caller has a permission
     * specific to this method call.
     */
    SharedUserSessionData getUserData();
}
