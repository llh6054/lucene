﻿package com.infores.gpdi.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class CreateIndex {

	private static final String INDEX_DIR = "E:/index/";
	
	
	public static void main(String[] args) throws IOException, ParseException {
	//	CreateIndex ci = new CreateIndex();
		File file = new File("E:/index/1.txt");
	//	ci.indexFile(file);
//		Analyzer analyzer = new StandardAnalyzer();
//		QueryParser parser = new QueryParser("contents", analyzer);
//		Query query = parser.parse("test");
//		CreateIndex.searchIndex(query);
		CreateIndex.indexFile(file);
		CreateIndex.searchIndex();
		
	}
	
	/**
	 * 创建索引
	 * @param file
	 */
	public static void indexFile(File file) {
		
		Analyzer analyzer = new CJKAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		Directory dir;
		
		try {
			dir = FSDirectory.open(Paths.get(INDEX_DIR));
			IndexWriter iw = new IndexWriter(dir, iwc);
			try {
				Document doc = new Document();
				
				String content = CreateIndex.getContent(file);
				String name = file.getName();
				String path = file.getAbsolutePath();
				
				
				doc.add(new TextField("contents", content, Store.YES));
				doc.add(new TextField("path", path,  Store.YES));
				doc.add(new TextField("name", name, Store.YES));
				
				iw.addDocument(doc);
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				iw.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 搜索
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public  static void searchIndex() throws IOException {
		IndexReader ir = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_DIR)));
		Query query = new TermQuery(new Term("contents","am"));
		IndexSearcher searcher = new IndexSearcher(ir);
		TopDocs results = searcher.search(query, 10000);
		ScoreDoc[] hits = results.scoreDocs;
		
		System.out.println(hits.length);
		 for (ScoreDoc sd : hits) {   
	            Document d = searcher.doc(sd.doc);   
	            IndexableField[] contents = d.getFields("contents");
	            
	            System.out.println(contents[0].stringValue()); 
	         //   System.out.println(sd.doc);
	            System.out.println("d.get(\"path\")" + ":["+d.get("path")+"]");   
	     }  
	}
	
	public static void searchIndex(Query query) throws IOException, ParseException {
		System.out.println(Paths.get("index"));
	    Directory dire=FSDirectory.open(Paths.get("index"));  
        IndexReader ir=DirectoryReader.open(dire);  
        
        
        IndexSearcher is=new IndexSearcher(ir);  
        TopDocs td=is.search(query, 1000);  
      
        ScoreDoc[] sds =td.scoreDocs;  
        for (ScoreDoc sd : sds) {   
            Document d = is.doc(sd.doc);   
            System.out.println(d.getFields("contents")); 
         //   System.out.println(sd.doc);
            System.out.println("d.get(\"path\")" + ":["+d.get("path")+"]");   
        }  
        System.out.println();
        System.out.println("共为您查找到"+td.totalHits+"条结果");  
	}
	
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
		
		return sb.toString();
	}

}
