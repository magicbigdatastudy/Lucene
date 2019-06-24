package net.noyark.test;


import net.noyark.an.IKAnalyzer6x;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class IndexCreate {
    /**
     * 创建索引，基本单位文档，只要有文档对象
     * 索引就可以创建，手动拼接文档数据
     */
    @Test
    public void createIndex() throws Exception{
        //第一步:指向一个索引文件的目录(文件夹)，写出的索引
        //文件都保存在这个目录
        Path indexPath = Paths.get("index.dir");
        //lucene的dir对象可以实现索引的流输出
        Directory dir = FSDirectory.open(indexPath);
        //第二步：引入一个创建索引计算分词的分词器IK
        Analyzer analyzer = new IKAnalyzer6x();
        //将analyzer添加到索引创建的配置对象中
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //写出的indexdir文件在第二次创建时，会被覆盖
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        //第三步，生产索引文件保存的document文档对象
        Document doc1 = new Document();
        Document doc2 = new Document();
        //其中添加写数据，3-5域 字段值
        //id表示当前域的名称，相当于字段名，100表示值
        //Store.YES 文档对象一旦存储到索引文件，占用空间
        //对于一些没有必要存储在文件中的数据可以调用Store.NO
        //在创建索引文件时，该字段的值，不会保存到文档中，即使搜到
        //了文档对象，也不能获取
        //域的类型，String--varchar--StringField/TextField
        doc1.add(new StringField("id","100", Field.Store.NO));
        doc1.add(new TextField("title","三星 Galaxy S 轻奢版", Field.Store.YES));
        doc1.add(new TextField("sell_point","白条12期免息", Field.Store.YES));
        doc2.add(new StringField("id","100", Field.Store.NO));
        doc2.add(new TextField("title","苹果 Galaxy S 轻奢版", Field.Store.YES));
        doc2.add(new TextField("sell_point","不要钱", Field.Store.YES));
        //第四步，将文档数据输出到索引文件
        IndexWriter writer = new IndexWriter(dir,config);
        //将文档数据添加到输出流中
        writer.addDocument(doc1);
        writer.addDocument(doc2);
        writer.commit();
        writer.close();
        dir.close();

    }
    //更新索引
    @Test
    public void updateIndex() throws Exception{
        Path indexPath = Paths.get("index.dir");
        Directory directory = FSDirectory.open(indexPath);
        Analyzer analyzer = new IKAnalyzer6x();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory,config);
        Document doc = new Document();
        doc.add(new StringField("id","2", Field.Store.YES));
        doc.add(new TextField("title","华夏大平板", Field.Store.YES));
        doc.add(new TextField("sell_point","好用", Field.Store.YES));
        //第二步，根据条件更新覆盖原文档对象
        //参与Term：
        // 参数1:域名，根据哪个项目更新
        // 参数：域数据的分词词项，三星，搜索带有三星分词词项
        //第一个document对象进行覆盖
        //参数doc数据替代者
        writer.updateDocument(new Term("title","苹果"),doc);

        writer.commit();
        writer.close();
        directory.close();
    }



    
    //删除索引
    @Test
    public void deleteIndex() throws Exception{
        Path indexPath = Paths.get("index.dir");
        Directory directory = FSDirectory.open(indexPath);
        Analyzer analyzer = new IKAnalyzer6x();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory,config);
        //删除 deleteDocuments的方法参数不同
        //词项对比删除：必须词项完全匹配，才删除
        writer.deleteDocuments(new Term("sell_point","不要钱"));
        //删除完成
    }


}
