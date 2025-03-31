package ua.killer.mobileapp.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;

public class Cache {
	public String action;
	public File cacheDir;
	private Context mContext;
	
	public Cache(String _action, Context _mContext) {
		this.action = _action;
		this.mContext = _mContext;
		cacheDir = mContext.getCacheDir();
	}
	
	public File getCacheFile() {
		return new File(cacheDir.getAbsolutePath() + "/" + action);
	}
	
	public boolean exists() {
		return getCacheFile().exists();
	}
	
	public void write(InputStream stream) throws IOException {
		FileOutputStream out = new FileOutputStream(getCacheFile());
		byte[] buf = new byte[0x1000];
	    while (true) {
	    	int r = stream.read(buf);
	    	if (r == -1) {
	    		break;
	    	}
	    	out.write(buf, 0, r);
	    }
	    out.close();
	}
	
	public void write(Bitmap bitmap) throws IOException {
		FileOutputStream out = new FileOutputStream(getCacheFile());
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
	}
	
	public InputStream get() throws FileNotFoundException {
		return new FileInputStream(getCacheFile());
	}
}
