package com.infores.gpdi.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * 文件操作工具类
 * @author chubby
 *
 */
public class FileUtils {
	
	private static final List<File> fileList = new LinkedList<File>();
	
	
	public static List<File> getFileList() {
		return fileList;
	}
	
	/**
	 * 获取文本内容
	 * @param file
	 * @return
	 */
	public static String getContent(File file) {
		FileInputStream fis;
		StringBuffer sb = new StringBuffer();
		try {
			System.out.println(file.getAbsolutePath());
			fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
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
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	
	/**
	 * 获取路径下的文件
	 * @param path
	 */
	public static void listFile(String path) {
		listFile(path, null);
	}
	
	/**
	 * 带fileNameFilter的获取文件
	 * @param path
	 * @param fileNameFilter
	 */
	public static void listFile(String path, FilenameFilter fileNameFilter) {
		File[] files = null;
		
		File curFile = new File(path);
		if(!curFile.isDirectory()) {
			fileList.add(curFile);
		}
			
		if(curFile.isDirectory()) {
			files = curFile.listFiles();
			for(File file: files) {
				if(file.isDirectory()) {
					listFile(file.getAbsolutePath());
				}
				else {
					fileList.add(file);
				}
					
			}
		}
	}
	
	/**
	 * 正向过滤器
	 * @author chubby
	 *
	 */
	public class IncludeFileNameFilter implements FilenameFilter {
		
		public IncludeFileNameFilter(String include) {
			
		}

		@Override
		public boolean accept(File dir, String name) {
			return false;
		}
		
	}
	
	/**
	 * 反向过滤器
	 * @author chubby
	 *
	 */
	public class ExcludeFileFilter implements FilenameFilter {
		
		public ExcludeFileFilter(String exclude) {
		}

		@Override
		public boolean accept(File dir, String name) {
			return false;
		}
		
	}
	
	
}