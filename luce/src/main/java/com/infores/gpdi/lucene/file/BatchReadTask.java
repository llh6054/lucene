package com.infores.gpdi.lucene.file;

import java.io.File;
import java.util.List;

/**
 * 分组批量读取任务
 *
 */
public class BatchReadTask implements Runnable {
	
	private List<File> fileList;
	private int begin = 0;
	private int end = 0;
	
	public BatchReadTask(List<File> fileList, int begin, int end) {
		this.fileList = fileList;
		this.begin = begin;
		this.end = end;
	}
	
	public void run() {
		while(begin <= end) {
			File file = fileList.get(begin);
			FileUtils.getContent(file);
			begin++;
		}
	}
	
}
	
	

