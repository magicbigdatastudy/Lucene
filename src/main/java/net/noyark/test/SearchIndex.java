package net.noyark.test;

import net.noyark.an.IKAnalyzer6x;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SearchIndex {


    public List<Document> searchMulti(Analyzer analyzer, String dir, String[] fields, String query, int nums) throws IOException, ParseException {
        Path path = Paths.get(dir);
        Directory directory = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields,analyzer);

        Query query1 = parser.parse(query);

        TopDocs doc = searcher.search(query1,nums);

        ScoreDoc[] docs = doc.scoreDocs;
        List<Document> documents = new ArrayList<>();

        for(ScoreDoc sdoc:docs){
            documents.add(searcher.doc(sdoc.doc));
        }
        return documents;
    }



    /**
     * 多域查询
     */
    @Test
    public void searchMulti() throws Exception{
        //1 路径
        Path path = Paths.get("index.dir");
        Directory directory = FSDirectory.open(path);
        //2 搜索对象创建searcher
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        //查询使用的分词器IK
        Analyzer analyzer = new IKAnalyzer6x();
        //3 创建查询条件
        String[] fields = {"title","sell_point"};
        //制定，查询多个域的名称
        //查询条件解析器，可以搜集查询的环境，生成查询条件对象
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields,analyzer);
        Query query = parser.parse("Galaxy");
        //这条件可以使用查询，只要两个域有一个包含词项，就会搜索到
        //System.out.println(query.toString());
        //4.获取数据for循环遍历；默认情况下，每个doc返回时
        //封装一个topDoc的对象中，底层包装了一个数组，评分
        //doc 循环数组，调用api获取数据
        TopDocs topDoc = searcher.search(query,10);//返回所有数据前十条
        //封装了获取doc的所有条件的docs对象
        ScoreDoc[] docs = topDoc.scoreDocs;
        for(int i = 0;i<docs.length;i++){
            Document document = searcher.doc(docs[i].doc);
            System.out.println("id:"+document.get("id"));
            System.out.println("title:"+document.get("title"));
            System.out.println("sell_point:"+document.get("sell_point"));
        }
    }
    /**
     * 词项查询
     */
    @Test
    public void termQuery() throws Exception {
        Path path = Paths.get("index.dir");
        Directory directory = FSDirectory.open(path);

        IndexReader reader = DirectoryReader.open(directory);

        IndexSearcher searcher = new IndexSearcher(reader);

        //--
        Term term = new Term("title","苹果");

        Query query = new TermQuery(term);
        //--
        System.out.println(query.toString());

        TopDocs topDocs =searcher.search(query,10);

        ScoreDoc[] docs = topDocs.scoreDocs;
        for(int i = 0;i<docs.length;i++){
            Document document = searcher.doc(docs[i].doc);
//            System.out.println("id:"+document.get("id"));
//            System.out.println("title:"+document.get("title"));
//            System.out.println("sell_point:"+document.get("sell_point"));
        }
    }


    @Test
    public void booleanSearch() throws Exception{
        Path path = Paths.get("index.dir");
        Directory directory = FSDirectory.open(path);

        IndexReader reader = DirectoryReader.open(directory);

        IndexSearcher searcher = new IndexSearcher(reader);

        Query query1 = new TermQuery(new Term("title","苹果"));

        Query query2 = new TermQuery(new Term("sell_point","平板"));

        //制定逻辑关系，title有苹果，sellpoint没平板
        BooleanClause bc1 = new BooleanClause(query1, BooleanClause.Occur.MUST);//必须有
        BooleanClause bc2 = new BooleanClause(query2, BooleanClause.Occur.MUST_NOT);//必须没有

        BooleanQuery query = new BooleanQuery.Builder().add(bc1).add(bc2).build();
        //--
        TopDocs topDocs =searcher.search(query,10);

        ScoreDoc[] docs = topDocs.scoreDocs;
        for(int i = 0;i<docs.length;i++){
            Document document = searcher.doc(docs[i].doc);
//            System.out.println("id:"+document.get("id"));
//            System.out.println("title:"+document.get("title"));
//            System.out.println("sell_point:"+document.get("sell_point"));
        }
    }

    /**
     * 范围查询，必须对应具有intPoint， longPoint等文档数据
     */
    @Test
    public void rangeSearch() throws Exception{
        Path path = Paths.get("index.dir");
        Directory directory = FSDirectory.open(path);

        IndexReader reader = DirectoryReader.open(directory);
        //搜索使用分词器IK
        IndexSearcher searcher = new IndexSearcher(reader);

        //TODO
        Query query = IntPoint.newRangeQuery("price",0,2500);

        //--
        TopDocs topDocs =searcher.search(query,10);

        ScoreDoc[] docs = topDocs.scoreDocs;
        for(int i = 0;i<docs.length;i++){
            Document document = searcher.doc(docs[i].doc);
//            System.out.println("id:"+document.get("id"));
//            System.out.println("title:"+document.get("title"));
//            System.out.println("sell_point:"+document.get("sell_point"));
        }
    }

    /**
     * 前缀查询
     */
    @Test
    public void prefixQuery() throws Exception{
        Path path = Paths.get("index.dir");
        Directory directory = FSDirectory.open(path);

        IndexReader reader = DirectoryReader.open(directory);
        //搜索使用分词器IK
        IndexSearcher searcher = new IndexSearcher(reader);

        Term term = new Term("title","三星");

        Query query = new PrefixQuery(term);
        //--
        TopDocs topDocs =searcher.search(query,10);

        ScoreDoc[] docs = topDocs.scoreDocs;
        for(int i = 0;i<docs.length;i++){
            Document document = searcher.doc(docs[i].doc);
//            System.out.println("id:"+document.get("id"));
//            System.out.println("title:"+document.get("title"));
//            System.out.println("sell_point:"+document.get("sell_point"));
        }
    }

    /**
     * 多关键字查询
     */
    @Test
    public void multiTermQuery() throws Exception{
        Path path = Paths.get("index.dir");
        Directory directory = FSDirectory.open(path);

        IndexReader reader = DirectoryReader.open(directory);
        //搜索使用分词器IK
        IndexSearcher searcher = new IndexSearcher(reader);


        PhraseQuery.Builder builder = new PhraseQuery.Builder();

        builder.add(new Term("title","三星"));
        builder.add(new Term("title","苹果"));
        PhraseQuery query= builder.build();
        //--
        TopDocs topDocs =searcher.search(query,10);

        ScoreDoc[] docs = topDocs.scoreDocs;
        for(int i = 0;i<docs.length;i++){
            Document document = searcher.doc(docs[i].doc);
//            System.out.println("id:"+document.get("id"));
//            System.out.println("title:"+document.get("title"));
//            System.out.println("sell_point:"+document.get("sell_point"));
        }
    }

}
