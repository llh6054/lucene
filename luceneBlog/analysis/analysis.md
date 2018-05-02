​					     Lucene分词  

#### 1.  概念  
所有传递给Lucene进行索引的文本都需要经历一个过程----分词，即：将文本分割为一个个的足够小的词或者字。  包括但不限于：  

+ ##### 原型替换：将单词替换为它们的原型，例如用bike替换bikes,这样在搜索bike的时候bike、bikes都能够被搜索出来。

+ ##### 词过滤：文本中许多高频出现的词实际并无意义，例如“的”、“a”、“the”,剔除它们不仅能降低索引的空间，而且有助于提高索引的搜索效率和质量。

+ ##### 文本标准化：文本中时常会出现一些其他的东西，将文本标准化有助于提高搜索质量。

+ ##### 同义词扩展：进行同义词扩展有助于提高搜索质量，例如漂亮=美丽。  

#### 2. 负责分词的几个核心类、接口

##### Analyzer   
　　Analyzer的职责是为搜索、索引过程提供tokenStream，大部分时候可以实现为一个匿名子类　　

+ 主要方法和内部类:  
  　　static内部类TokenStreamComponents 对输入tokenizer和输出tokenStream进行了简单的封装。  
  　　static的抽象内部类ReuseStrategy定义了对TokenStreamComponents的重用策略。　
  　　GLOBAL_REUSE_STRATEGY和PER_FIELD_REUSE_STRATEGY　Analyzer.ReuseStrategy的两种实现，分别为共用一个TokenStreamComponents以及为每个field维护一TokenStreamComponents 
  　　TokenStream tokenStream(final String fieldName,final Reader reader)　tokenStream是Analyzer的入口。  
  　　抽象方法TokenStreamComponents createComponents(String fieldName)　实现一个Analyzer需要实现该方法，Analyzer的tokenStream方法会尝试从reuseStrategy中获取一个
  　　TokenStreamComponents，获取失败则会调用该方法生成一个并保存至reuseStrategy，最后从TokenStreamComponents中获取tokenStream。  

+ ##### Tokenizer
  　　Tokenizer是TokenStream的一个子类，它的主要职责是将输入文本分为一个个的token，大部分时候Analyzer会使用Tokenizer作为分词过程的第一步。  
+ 主要方法： 
  　　boolean incrementToken() Tokenizer和TokenFilter的incrementToken方法都定义在TokenStream中，但由于它们的不同职责实导致实现也不尽相同。由于Attribute的个个实现只会实例化一次，每生成下一个token时都需要调用AttributeResource类的clearAttributes()方法，清除上一个token的Attribute。  

+ ##### TokenFilter
  　　TokenFilter也是一个TokenStream的子类，它的主要职责的处理一个个已经被Tokenizer切开的token,包括但不限于：删除、填充、同义词插入等等。所以，TokenFilter不是必须的。  
+ 主要方法：  
  　　TokenFilter(TokenStream input)　构造函数，接收一个tokenStream，典型的装饰者模式，为incrementToken做准备。  
  　　boolean incrementToken()　过滤的关键，生成下一个token，由于使用的是一个装饰者模式，对token的Attribute进行操作之前，必须调用input.incrementToken。对于需要的token返回true，反之false。为了使其他的TokenFilter和消费者知道有哪些属性，Attribute必须在TokenFilter进行初始化之时就添加进来。同时在incrementToken方法中进行维护。  

+ ##### Attribute
  　　Attribute负责存储token的属性，例如：token的字符串，跨越的token个数，字符串的起始终止位置。这些都已经提供了实现，当然也可以实现自己的Attribute。


#### 3. 类图与分词流程

　　　　 ![类图](https://github.com/llh6054/BackUp/blob/master/lucene/analysis/class.png)   

Analyzer是分词的入口，首先需要实现一个Analyzer

```
Analyzer analyzer = new Analyzer() {
  @Override
   protected TokenStreamComponents createComponents(String fieldName) {
     Tokenizer source = new FooTokenizer(reader);
     TokenStream filter = new FooFilter(source);
     filter = new BarFilter(filter);
     return new TokenStreamComponents(source, filter);
   }
   @Override
   protected TokenStream normalize(TokenStream in) {
     // Assuming FooFilter is about normalization and BarFilter is about
     // stemming, only FooFilter should be applied
     return new FooFilter(in);
   }
 };
```

   从Analyzer得到一个TokenStream

```

/**
	 分词的入口是Analyzer，从Analyzer得到一个TokenStreamComponetns,然后从TokenStreamComponents中得的一个TokenStream
*/
public final TokenStream tokenStream(final String fieldName,
                                       final Reader reader) {
    TokenStreamComponents components = reuseStrategy.getReusableComponents(this, fieldName);
    final Reader r = initReader(fieldName, reader);
    if (components == null) {	//存在则获取，不存在则创建一个并缓存起来
      components = createComponents(fieldName);
      reuseStrategy.setReusableComponents(this, fieldName, components);
    }
    components.setReader(r);
    return components.getTokenStream();	//得到TokenStream
  }

/**
	抽象的Analyzer方法，需要具体的Analyzer去实现
*/
protected abstract TokenStreamComponents createComponents(String fieldName);
```

TokenStream的addAttribute(Class<T> clazz)为Token添加属性

```
/**
	为Token添加属性，不存在存在则添加，存在则直接返回
*/
 public final <T extends Attribute> T addAttribute(Class<T> attClass) {
    AttributeImpl attImpl = attributes.get(attClass);
    if (attImpl == null) {
      if (!(attClass.isInterface() && Attribute.class.isAssignableFrom(attClass))) {
        throw new IllegalArgumentException(
          "addAttribute() only accepts an interface that extends Attribute, but " +
          attClass.getName() + " does not fulfil this contract."
        );
      }
      addAttributeImpl(attImpl = this.factory.createAttributeInstance(attClass));
    }
    return attClass.cast(attImpl);
  }
  
/**
	真正的添加属性方法
*/
public final void addAttributeImpl(final AttributeImpl att) {
    final Class<? extends AttributeImpl> clazz = att.getClass();
    if (attributeImpls.containsKey(clazz)) return;
    
    // add all interfaces of this AttributeImpl to the maps
    for (final Class<? extends Attribute> curInterface : getAttributeInterfaces(clazz)) 	{
      // Attribute is a superclass of this interface
      if (!attributes.containsKey(curInterface)) {
        // invalidate state to force recomputation in captureState()
        this.currentState[0] = null;
        attributes.put(curInterface, att);
        attributeImpls.put(clazz, att);
      }
    }
  }
```

装饰者模式调用TokenStream的incrementToken方法

```
/**
LowerCaseFilter中的incrementToken
*/
@Override
  public final boolean incrementToken() throws IOException {
    if (input.incrementToken()) {
      CharacterUtils.toLowerCase(termAtt.buffer(), 0, termAtt.length()); //维护Attribute
      return true;
    } else
      return false;
  }
```

一个完整的流程

　　　　　　![流程](https://github.com/llh6054/BackUp/blob/master/lucene/analysis/AnalyseFlow.png)　　

#### 3. 几个常用的分词器  

![常用分词器](https://github.com/llh6054/BackUp/blob/master/lucene/analysis/commonAnalzer.jpg) 

   

#### 4. 实现自己的分词器

```
public class MyAnalyzer extends Analyzer {
 
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
      return new TokenStreamComponents(new WhitespaceTokenizer(matchVersion));
    }
     
}
```

#### 5. 七个实现的Attribute

 Lucene provides seven Attributes out of the box: 

| [`CharTermAttribute`](https://lucene.apache.org/core/7_1_0/core/org/apache/lucene/analysis/tokenattributes/CharTermAttribute.html) | The term text of a token.  Implements [`CharSequence`](https://docs.oracle.com/javase/8/docs/api/java/lang/CharSequence.html?is-external=true)        (providing methods length() and charAt(), and allowing e.g. for direct       use with regular expression [`Matcher`](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Matcher.html?is-external=true)s) and        [`Appendable`](https://docs.oracle.com/javase/8/docs/api/java/lang/Appendable.html?is-external=true) (allowing the term text to be appended to.) |
| ---------------------------------------- | :--------------------------------------- |
| [`OffsetAttribute`](https://lucene.apache.org/core/7_1_0/core/org/apache/lucene/analysis/tokenattributes/OffsetAttribute.html) | The start and end offset of a token in characters. |
| [`PositionIncrementAttribute`](https://lucene.apache.org/core/7_1_0/core/org/apache/lucene/analysis/tokenattributes/PositionIncrementAttribute.html) | See above for detailed information about position increment. |
| [`PositionLengthAttribute`](https://lucene.apache.org/core/7_1_0/core/org/apache/lucene/analysis/tokenattributes/PositionLengthAttribute.html) | The number of positions occupied by a token. |
| [`PayloadAttribute`](https://lucene.apache.org/core/7_1_0/core/org/apache/lucene/analysis/tokenattributes/PayloadAttribute.html) | The payload that a Token can optionally have. |
| [`TypeAttribute`](https://lucene.apache.org/core/7_1_0/core/org/apache/lucene/analysis/tokenattributes/TypeAttribute.html) | The type of the token. Default is 'word'. |
| [`FlagsAttribute`](https://lucene.apache.org/core/7_1_0/core/org/apache/lucene/analysis/tokenattributes/FlagsAttribute.html) | Optional flags a token can have.         |
| [`KeywordAttribute`](https://lucene.apache.org/core/7_1_0/core/org/apache/lucene/analysis/tokenattributes/KeywordAttribute.html) | Keyword-aware TokenStreams/-Filters skip modification of tokens that       return true from this attribute's isKeyword() method. |

##### 6. 实现并添加自己的Attribute  

```
 /**
	 接口定义
 */
 public interface PartOfSpeechAttribute extends Attribute {
     public static enum PartOfSpeech {
       Noun, Verb, Adjective, Adverb, Pronoun, Preposition, Conjunction, Article, Unknown
     }
   
     public void setPartOfSpeech(PartOfSpeech pos);
   
     public PartOfSpeech getPartOfSpeech();
   } 
```

```
/**
	继承AttributeImpl并实现已定义接口
*/
public final class PartOfSpeechAttributeImpl extends AttributeImpl 
                                   implements PartOfSpeechAttribute {
   
   private PartOfSpeech pos = PartOfSpeech.Unknown;
   
   public void setPartOfSpeech(PartOfSpeech pos) {
     this.pos = pos;
   }
   
   public PartOfSpeech getPartOfSpeech() {
     return pos;
   }
 
   @Override
   public void clear() {
     pos = PartOfSpeech.Unknown;
   }
 
   @Override
   public void copyTo(AttributeImpl target) {
     ((PartOfSpeechAttribute) target).setPartOfSpeech(pos);
   }
 }
```

```
/**
	添加Attribute到TokenFilter中
*/
public static class PartOfSpeechTaggingFilter extends TokenFilter {
     PartOfSpeechAttribute posAtt = addAttribute(PartOfSpeechAttribute.class);
     CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
     
     protected PartOfSpeechTaggingFilter(TokenStream input) {
       super(input);
     }
     
     public boolean incrementToken() throws IOException {
       if (!input.incrementToken()) {return false;}
       posAtt.setPartOfSpeech(determinePOS(termAtt.buffer(), 0, termAtt.length()));
       return true;
     }
     
     // determine the part of speech for the given term
     protected PartOfSpeech determinePOS(char[] term, int offset, int length) {
       // naive implementation that tags every uppercased word as noun
       if (length > 0 && Character.isUpperCase(term[0])) {
         return PartOfSpeech.Noun;
       }
       return PartOfSpeech.Unknown;
     }
   }
```