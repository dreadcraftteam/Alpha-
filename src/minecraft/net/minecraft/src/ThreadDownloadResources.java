package net.minecraft.src;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;

public class ThreadDownloadResources extends Thread {
	public File resourcesFolder;
	private Minecraft mc;
	private boolean closing = false;

	public ThreadDownloadResources(File file, Minecraft minecraft) {
		this.mc = minecraft;
		this.setName("Resource download thread");
		this.setDaemon(true);
		this.resourcesFolder = new File(file, "resources/");
		if(!this.resourcesFolder.exists() && !this.resourcesFolder.mkdirs()) {
			throw new RuntimeException("The working directory could not be created: " + this.resourcesFolder);
		}
	}

	public void run() {
		try {
			ArrayList arrayList1 = new ArrayList();
			URL uRL2 = new URL("http://www.minecraft.net/resources/");
			BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(uRL2.openStream()));
			String string4 = "";

			while((string4 = bufferedReader3.readLine()) != null) {
				arrayList1.add(string4);
			}

			bufferedReader3.close();

			for(int i5 = 0; i5 < 2; ++i5) {
				for(int i6 = 0; i6 < arrayList1.size(); ++i6) {
					this.downloadAndInstallResource(uRL2, (String)arrayList1.get(i6), i5);
					if(this.closing) {
						return;
					}
				}
			}
		} catch (IOException iOException7) {
			this.loadResource(this.resourcesFolder, "");
			iOException7.printStackTrace();
		}

	}

	public void closeMinecraft() {
		this.loadResource(this.resourcesFolder, "");
	}

	private void loadResource(File file, String path) {
		File[] file3 = file.listFiles();

		for(int i4 = 0; i4 < file3.length; ++i4) {
			if(file3[i4].isDirectory()) {
				this.loadResource(file3[i4], path + file3[i4].getName() + "/");
			} else {
				try {
					this.mc.installResource(path + file3[i4].getName(), file3[i4]);
				} catch (Exception exception6) {
					System.out.println("Failed to add " + path + file3[i4].getName());
				}
			}
		}

	}

	private void downloadAndInstallResource(URL url, String key, int size) {
		try {
			String[] string4 = key.split(",");
			String string5 = string4[0];
			int i6 = string5.indexOf("/");
			String string7 = string5.substring(0, i6);
			if(!string7.equals("sound") && !string7.equals("newsound")) {
				if(size != 1) {
					return;
				}
			} else if(size != 0) {
				return;
			}

			int i8 = Integer.parseInt(string4[1]);
			long j9 = Long.parseLong(string4[2]);
			j9 /= 2L;
			File file11 = new File(this.resourcesFolder, string5);
			if(!file11.exists() || file11.length() != (long)i8) {
				file11.getParentFile().mkdirs();
				String string12 = string5.replaceAll(" ", "%20");
				this.downloadResource(new URL(url, string12), file11, i8);
				if(this.closing) {
					return;
				}
			}

			this.mc.installResource(string5, file11);
		} catch (Exception exception13) {
			exception13.printStackTrace();
		}

	}

	private void downloadResource(URL url, File file, int size) throws IOException {
		byte[] b4 = new byte[4096];
		DataInputStream dataInputStream5 = new DataInputStream(url.openStream());
		DataOutputStream dataOutputStream6 = new DataOutputStream(new FileOutputStream(file));
		boolean z7 = false;

		do {
			int i8;
			if((i8 = dataInputStream5.read(b4)) < 0) {
				dataInputStream5.close();
				dataOutputStream6.close();
				return;
			}

			dataOutputStream6.write(b4, 0, i8);
		} while(!this.closing);

	}

	public void b() {
		this.closing = true;
	}
}
