package net.minecraft.src;

import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;

class ThreadDownloadImage extends Thread {
	final String location;
	final ImageBuffer buffer;
	final ThreadDownloadImageData imageData;

	ThreadDownloadImage(ThreadDownloadImageData downloadImageData, String location, ImageBuffer imageBuffer) {
		this.imageData = downloadImageData;
		this.location = location;
		this.buffer = imageBuffer;
	}

	public void run() {
		HttpURLConnection httpURLConnection1 = null;

		try {
			URL uRL2 = new URL(this.location);
			httpURLConnection1 = (HttpURLConnection)uRL2.openConnection();
			httpURLConnection1.setDoInput(true);
			httpURLConnection1.setDoOutput(false);
			httpURLConnection1.connect();
			if(httpURLConnection1.getResponseCode() == 404) {
				return;
			}

			if(this.buffer == null) {
				this.imageData.image = ImageIO.read(httpURLConnection1.getInputStream());
			} else {
				this.imageData.image = this.buffer.parseUserSkin(ImageIO.read(httpURLConnection1.getInputStream()));
			}
		} catch (Exception exception6) {
			exception6.printStackTrace();
		} finally {
			httpURLConnection1.disconnect();
		}

	}
}
