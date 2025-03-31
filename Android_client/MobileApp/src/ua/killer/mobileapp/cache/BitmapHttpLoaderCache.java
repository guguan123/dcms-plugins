package ua.killer.mobileapp.cache;

import java.io.IOException;

import ua.killer.mobileapp.BitmapHttpLoader;
import ua.killer.mobileapp.listeners.BitmapHttpLoaderListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapHttpLoaderCache extends BitmapHttpLoader implements BitmapHttpLoaderListener {
	private BitmapHttpLoaderListener listener;
	private String action;
	private Cache cache;
	
	public BitmapHttpLoaderCache(String _action, String _url, Context _mContext) {
		super(_url, _mContext);
		action = _action;
		_init();
	}
	
	public BitmapHttpLoaderCache(String _action, String _url, Context _mContext, BitmapHttpLoaderListener _listener) {
		super(_url, _mContext);
		action = _action;
		this.listener = _listener;
		_init();
	}
	
	private void _init() {
		this.setOnLoadListener(this);
		cache = new Cache(action, mContext);
	}

	@Override
	public void onLoadStart() {
		listener.onLoadStart();
	}

	@Override
	public void onLoadFinish(Bitmap bitmap) throws IOException {
		cache.write(bitmap);
		listener.onLoadFinish(bitmap);
	}
	
	@Override
	public void load() throws IOException {
		if (cache.exists())
			listener.onLoadFinish(BitmapFactory.decodeStream(cache.get()));
		else {
			super.load();
		}
	}

}
