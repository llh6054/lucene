package com.infores.gpdi.luce;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.infores.gpdi.lucene.CreateIndex;

public class IndexCreatorTest {
	CreateIndex ci;
	private static final String DIR = "E:/index/";
	
	@Before
	public void setUp() throws Exception {
		ci = new CreateIndex();
	}

	@Test
	public void testListFile() {
		File[] file = ci.listFile(DIR);
		assertTrue(file.length == 30);
	}

	@Test
	public void testBatchReadFile() {
		ci.listFile(DIR);
		ci.batchReadFile();
	}

}
