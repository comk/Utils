package com.mayhub.utils.volley;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mayhub.utils.MyApplication;

import java.io.UnsupportedEncodingException;

@SuppressLint("NewApi")
public class RequestManager
{
	public static RequestQueue mRequestQueue = Volley
			.newRequestQueue(MyApplication.getInstance());

	private RequestManager()
	{
	}


	/**
	 * 返回String
	 * 
	 * @param url
	 *            接口
	 * @param tag
	 *            上下文
	 * @param params
	 *            post需要传的参数
	 * @param listener
	 *            回调
	 */
	public static void post(String url, Object tag, RequestParams params,
							RequestListener listener)
	{
		ByteArrayRequest request = new ByteArrayRequest(Request.Method.POST,
				url, params, responseListener(listener, false),
				responseError(listener, false));
		addRequest(request, tag);
	}



	/**
	 * 成功消息监听 返回String
	 * 
	 * @param l
	 *            String 接口
	 * @param flag
	 *            true 带进度条 flase不带进度条
	 * @return
	 */
	protected static Response.Listener<byte[]> responseListener(
			final RequestListener l, final boolean flag)
	{
		return new Response.Listener<byte[]>()
		{
			@Override
			public void onResponse(byte[] arg0)
			{
				String data = null;
				try
				{
					data = new String(arg0, "UTF-8");
				}
				catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
				l.requestSuccess(data);

			}
		};
	}

	/**
	 * String 返回错误监听
	 * 
	 * @param l
	 *            String 接口
	 * @param flag
	 *            true 带进度条 flase不带进度条
	 * @return
	 */
	protected static Response.ErrorListener responseError(
			final RequestListener l, final boolean flag)
	{
		return new Response.ErrorListener()
		{

			@Override
			public void onErrorResponse(VolleyError e)
			{
				l.requestError(e);

			}
		};
	}

	public static void addRequest(Request<?> request, Object tag)
	{
		if (tag != null)
		{
			request.setTag(tag);
		}
		request.setShouldCache(false);
		request.setRetryPolicy(new DefaultRetryPolicy(
				30000,
				0,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(request);
	}

	/**
	 * 当主页面调用协议 在结束该页面调用此方法
	 * 
	 * @param tag
	 */
	public static void cancelAll(Object tag)
	{
		mRequestQueue.cancelAll(tag);
	}
}
