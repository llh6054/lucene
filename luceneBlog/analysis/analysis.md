					### Lucene分词  
1. #### 概念  
所有传递给Lucene进行索引的文本都需要经历一个过程----分词，即：将文本分割为一个个的足够小的字或者词。  
包括但不限于：  
+ ##### 原型替换：将单词替换为它们的原型，例如用bike替换bikes,这样在搜索bike的时候bike、bikes都能够被搜索出来。
+ ##### 词过滤：文本中许多高频出现的词实际并无意义，例如“的”、“a”、“the”,剔除它们不仅能降低索引的空间，而且有助于提高索引的搜索效率和质量。
+ ##### 文本标准化：文本中时常会出现一些其他的东西，将文本标准化有助于提高搜索质量。
+ ##### 同义词扩展：进行同义词扩展有助于提高搜索质量，例如漂亮=美丽。  
2. #### 负责分词的几个核心类、接口
+ ##### Analyzer   
Analyzer的职责是为搜索、索引过程提供tokenStream，大部分时候可以实现为一个匿名子类
+ ##### Tokenizer
Tokenizer是TokenStream的一个子类，它的主要职责是将输入文本分为一个个的token，大部分时候Analyzer会使用Tokenizer作为分词过程的第一步。
+ ##### TokenFilter
TokenFilter也是一个TokenStream的子类，它的主要职责的处理一个个已经被Tokenizer切开的token,包括但不限于：删除、填充、同义词插入等等。所以，
TokenFilter不是必须的。
+ ##### Attribute
Attribute负责存储token的属性，例如：token的字符串，跨越的token个数，字符串的起始终止位置。这些都已经提供了实现，当然也可以实现自己的Attribute。


                                        