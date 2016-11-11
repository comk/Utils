package com.mayhub.utils.volley;

import android.content.Context;

/**
 * 张兵
 * Created by Administrator on 2015/3/11.
 */
public class IRequest {


	/**
	 * 返回String post
	 * 
	 * @param context
	 * @param url
	 * @param params
	 * @param l
	 */
	public static void post(Context context, String url, RequestParams params,
							RequestListener l) {
		RequestManager.post(url, context, params, l);
	}


}
