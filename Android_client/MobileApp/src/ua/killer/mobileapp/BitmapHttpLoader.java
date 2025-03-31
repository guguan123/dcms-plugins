package ua.killer.mobileapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ua.killer.mobileapp.listeners.BitmapHttpLoaderListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class BitmapHttpLoader extends AsyncTask<Void, Void, Bitmap> {
	protected String url;
	protected Context mContext;
	private BitmapHttpLoaderListener listener;
	
	public BitmapHttpLoader(String _url, Context _mContext) {
		super();
		this.url = _url;
		this.mContext = _mContext;
	}
	
	public BitmapHttpLoader(String _url, Context _mContext, BitmapHttpLoaderListener _listener) {
		super();
		this.url = _url;
		this.mContext = _mContext;
		this.listener = _listener;
	}
	
	public void setOnLoadListener(BitmapHttpLoaderListener _listener) {
		this.listener = _listener;
	}
	
	@Override
	protected void onPreExecute() {
		if (listener != null)
			listener.onLoadStart();
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
        try {
        	URL http = new URL(url);
        	HttpURLConnection connection = (HttpURLConnection) http.openConnection();
        	connection.setDoInput(true);
        	connection.connect();
        	return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (MalformedURLException e) {
//            e.printStackTrace();
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return null;
	}
	
	@Override
    protected void onPostExecute(Bitmap result) {
		if (listener != null)
			try {
				listener.onLoadFinish(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void load() throws FileNotFoundException, IOException {
		this.execute();
	}

}
