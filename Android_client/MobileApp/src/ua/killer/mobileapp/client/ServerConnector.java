package ua.killer.mobileapp.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import ua.killer.mobileapp.application.Utils;
import ua.killer.mobileapp.listeners.OnServerConnectListener;
import android.os.AsyncTask;

public class ServerConnector {
	private Map<Object, Object> data = new HashMap<Object, Object>();
	private OnServerConnectListener listener;
	
	private Connecting connecting;
	
	public String url;
	
	public ServerConnector(Map<Object, Object> data, OnServerConnectListener _listener) {
		this.data = data;
		this.listener = _listener;
	}
	
	public void send(String _url) {
		url = _url;
		connecting = new Connecting();
		connecting.execute();
	}
	
	public void setOnServerConnectListener(OnServerConnectListener _listener) {
		this.listener = _listener;
	}
	
	private class Connecting extends AsyncTask<Void, Void, Void> {
		private String responseText;
		
		@Override
		protected void onPreExecute() {
			listener.onStartConnecting();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
//			try {
//				TimeUnit.SECONDS.sleep(20);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			HttpResponse response;
			HttpParams httpparams = new BasicHttpParams();
			HttpClientParams.setRedirecting(httpparams, true);
			HttpConnectionParams.setConnectionTimeout(httpparams, 10000);
			HttpConnectionParams.setSoTimeout(httpparams, 20000);
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setParams(httpparams);
			try {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				for (Entry<Object, Object> entry : data.entrySet()) {
					nameValuePairs.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
				}
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				response = httpclient.execute(httppost);
				responseText = Utils.getTextFromResponse(response);
			} catch (ConnectTimeoutException e) {
//				e.printStackTrace();
			} catch (SocketTimeoutException e) {
//				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
			} catch (ClientProtocolException e) {
//				e.printStackTrace();
			} catch (IOException e) {
//				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			listener.onFinishConnecting(responseText);
		}
	}

}
