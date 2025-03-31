package ua.killer.mobileapp.listeners;

import java.io.IOException;

import android.graphics.Bitmap;

public interface BitmapHttpLoaderListener {
	public void onLoadStart();
	public void onLoadFinish(Bitmap bitmap) throws IOException;
}
