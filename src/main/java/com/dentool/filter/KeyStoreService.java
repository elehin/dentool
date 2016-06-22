package com.dentool.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

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
		final File f = new File(envVar + "/key");
		List<String> lines = Arrays.asList("BBpMqmy6BG+F2yd+/tzZ3g==");
		Path file = Paths.get(envVar);
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public String getEncodedKey() {
		return encodedKey;
	}

	public void setEncodedKey(String encodedKey) {
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
