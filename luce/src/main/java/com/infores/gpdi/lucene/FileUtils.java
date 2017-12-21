package com.infores.gpdi.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {
	/**
	 * 获取文本内容
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getContent(File file) {
		FileInputStream fis;
		StringBuffer sb = new StringBuffer();
		try {
			fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String line;
			try {
				while((line = br.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println(sb.toString());
		return sb.toString();
	}
}
