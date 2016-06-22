package com.dentool.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class KeyStoreService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String encodedKey;

	KeyStoreService() {
		// this.encodedKey = this.getKey();
		// if ("".equals(this.encodedKey)) {
		// this.encodedKey = null;
		// }
		String envVar = System.getenv("OPENSHIFT_DATA_DIR");
		final File f = new File(envVar);
		listFilesForFolder(f);
	}

	public String getEncodedKey() {
		return encodedKey;
	}

	public void setEncodedKey(String encodedKey) {
	}

	private void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				logger.info(fileEntry.getName());
			}
		}
	}

	private String getKey() {

		logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@ getkey() @@@@@@@@@@@@@@@@@@@@@@@@@");

		String k = "";
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader("/key");

			br = new BufferedReader(fr);

			while (br.ready()) {
				k = br.readLine();
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		return k;
	}
}
