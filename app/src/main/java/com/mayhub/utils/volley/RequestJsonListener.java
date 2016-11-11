package com.mayhub.utils.volley;

import com.android.volley.VolleyError;

/**
 * 张兵
 * Created by Administrator on 2015/3/11.
 */
public interface RequestJsonListener<T> {
    /**
     * 成功
     *
     * @param
     */
    public void requestSuccess(T result);

    /**
     * 错误
     */
    public void requestError(VolleyError e);
}
