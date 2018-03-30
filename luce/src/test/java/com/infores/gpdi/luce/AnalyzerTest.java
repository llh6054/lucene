package com.infores.gpdi.luce;

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Test;

public class AnalyzerTest {
	 	
		String enText = "IndexWriter addDocument's a javadoc.txt";  
	    String cnText = "我们是中国人";  
	      
	    //单字分词  
	    Analyzer analyzerStanderd = new StandardAnalyzer();  
	    //按停用词分词  
	    Analyzer analyzerSimple = new SimpleAnalyzer();  
	    //二分法分词  
	    Analyzer analyzerCJK = new CJKAnalyzer();   
	      
	    /** 
	     * 测试各种分词器的分词效果 
	     * @throws Exception 
	     */  
	    @Test  
	    public void testAnalyzer() throws Exception {  
	        //英文分词  
	        analyze(analyzerStanderd,enText);  
	        analyze(analyzerSimple,enText);  
	        analyze(analyzerCJK,enText);  
	          
	        //中文分词  
	        analyze(analyzerStanderd,cnText);  
	        analyze(analyzerSimple,cnText);  
	        analyze(analyzerCJK,cnText);  
	    }  
	      
	    /** 
	     * 将text文本内容进行分词,并将结果打印出来 
	     * @param analyzer 分词器 
	     * @param text     分词的文本 
	     * @throws Exception  
	     */  
	    public void analyze(Analyzer analyzer,String text) throws Exception {  
	        System.out.println("-----------分词器："+analyzer.getClass());  
	        //分词器将text文本的内容分词,并打印出来  
	        TokenStream tokensStream = analyzer.tokenStream("content",new StringReader(text));  
	        try {
	        	 	tokensStream.reset();  // Resets this stream to the beginning. (Required)
		 	        while(tokensStream.incrementToken()) {
		 	        	System.out.println("token: " + tokensStream.reflectAsString(false));
		 	        }
		 	       tokensStream.end();
	        }finally {
	        	tokensStream.close();
	        }
	    }  
}
