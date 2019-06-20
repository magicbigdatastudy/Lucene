package net.noyark.test;


import net.noyark.an.IKAnalyzer6x;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.StringReader;

public class AnalyzerTest {

    //根据词条化的理解，源文件的内容
    //可以被词条化，这种计算在lucene，开放了一个接口
    //analyzer 只要实现这个接口，就可以将字符串进行流的计算
    //不同语言有各种各样analyzer--分词器
    //lucene自带的分词器 standardAnalyzer，whitespace... simple....

    //通过调用该方法，传入一个分词器
    //传入一个字符串
    //调用analyzer的代码，完成对msg分词计算后，的词项输出
    public static void printAnalyzerString(Analyzer analyzer,String msg) throws Exception{
        //将字符串读取流对象当中
        StringReader reader = new StringReader(msg);
        //analyzer实现的主要是靠一个叫做tokenStream的流实现的
        //作用就是把流对象转化成根据底层分词计算得到的数据
        //原有流的对象可以被其计算分成流的集合
        TokenStream token = analyzer.tokenStream("msg",reader);
        token.reset();
        //重置初始化信息，此项数据结构携带(位移，偏移量，频率)
        //重置可以是一些多余的信息从初始化开始计算，被携带
        //从token的流中获取我们真正想要的分词结果
        CharTermAttribute attribute = token.getAttribute(CharTermAttribute.class);
        while(token.incrementToken()){
            System.out.println(attribute.toString());
        }
    }

    /**
     * 分词器测试
     * @throws Exception
     */
    @Test
    public void testAnalyzer() throws Exception{
        String msg="许久不见，多多挂念，明日三更，松树林见";
        Analyzer analyzer1 = new SmartChineseAnalyzer();
        Analyzer analyzer2 = new SimpleAnalyzer();
        Analyzer analyzer = new StandardAnalyzer();

        System.out.println("我是智能中文分词器");

        printAnalyzerString(analyzer1,msg);

        System.out.println("简单分词器");

        printAnalyzerString(analyzer2,msg);

        System.out.println("标准分词器");

        printAnalyzerString(analyzer,msg);
    }

    @Test
    public void testIk() throws Exception{
        String msg="许久不见，多多挂念，明日三更，松树林见";
        Analyzer analyzer = new IKAnalyzer6x();
        printAnalyzerString(analyzer,msg);
    }
}
