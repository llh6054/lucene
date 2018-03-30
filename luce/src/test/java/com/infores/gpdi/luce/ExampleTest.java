package com.infores.gpdi.luce;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizerImpl;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;

public class ExampleTest {
	
	/**
	 * Analyzer中的attribute测试
	 * @throws IOException
	 */
	@Test
	public void analyzerTest() throws IOException {
	     @SuppressWarnings("resource")
		Analyzer analyzer = new StandardAnalyzer(); // or any other analyzer
	     TokenStream ts = analyzer.tokenStream("myfield", new StringReader("some text goes here"));
	     // The Analyzer class will construct the Tokenizer, TokenFilter(s), and CharFilter(s),
	     //   and pass the resulting Reader to the Tokenizer.
	     OffsetAttribute offsetAtt = ts.addAttribute(OffsetAttribute.class);
	     
	     try {
	       ts.reset(); // Resets this stream to the beginning. (Required)
	       while (ts.incrementToken()) {
	         // Use AttributeSource.reflectAsString(boolean)
	         // for token stream debugging.
	         System.out.println("token: " + ts.reflectAsString(true));
	 
	         System.out.println("token start offset: " + offsetAtt.startOffset());
	         System.out.println("  token end offset: " + offsetAtt.endOffset());
	       }
	       ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
	     } finally {
	       ts.close(); // Release resources associated with this stream.
	     }
	}
	
	/**
	 * standardTokenizerImpl测试，其中源码中的standardTokenizerImpl是根据JFlex去生成的
	 * @throws IOException
	 */
	@Test
	public void standardTokenizerImplTest() throws IOException {
	//	String s = "I'm LaiLongHui";
	//	String s = "我是赖龙辉";
		String s = "わたしは 頼です";
		StringReader reader = new StringReader(s);

		StandardTokenizerImpl impl = new StandardTokenizerImpl(reader);

		while(impl.getNextToken() != StandardTokenizerImpl.YYEOF){

			CharTermAttributeImpl ta = new CharTermAttributeImpl();

		    impl.getText(ta);

		    System.out.println(ta.toString());

		}
	}
}
