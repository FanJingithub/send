package com.fudan.helper;

/**
 * Created by FanJin on 2017/10/11.
 * It can listen the state of a HttpTask.
 */

public interface HttpListener {
    /**
     * It would be triggered if HttpTask finished.
     * @param state : values 1 if succeed, -1 if failed.
     * @param responseData : response data from the server.
     */
    void onHttpFinish(int state, String responseData);
}
