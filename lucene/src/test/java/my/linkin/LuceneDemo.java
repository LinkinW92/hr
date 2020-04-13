package my.linkin;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class LuceneDemo {

    private static String root = "d:\\dev\\lucene\\";

    public static void main(String[] args) throws Exception {
        index0();
    }

    /**
     * 创建索引库
     * 将Aritcle对象放入索引库中的原始记录表中，从而形成词汇表
     */
    private static void index0() throws IOException {
        //需要读入的文件目录
        Path fileDoc = Paths.get(root + "docs");
        //需要存储索引的目录，如果不存在，会主动创建
        Path index = Paths.get(root + "index");
        //FSDirectory打开索引的目录，FS是文件系统的意思,基于磁盘（还有一种基于内存的）
        Directory directory = FSDirectory.open(index);
        //分词器，标准的分词器，对英文能很好的分词，对于中文只能一个一个拆开（中文推荐使用IK分词器）
        Analyzer analyzer = new StandardAnalyzer();
        //写索引的配置类，配置使用的分词器为标准分词器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //IndexWriter是lucene的核心类，用于存储索引
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        //Files是NIO中操作文件的工具，推荐使用，很好用
        //Files.isDirectory(path)用于判断是否为目录，是返回true,否则返回false
        if (Files.isDirectory(fileDoc)) {
            //Files.walkFileTree是一个递归调用目录的方法
            // 两个参数： Path start  FileVisitor<? super Path> visitor
            //第一个是需要递归的目录，第二个是访问文件的接口（SimpleFileVisitor是其中一个实现类）
            Files.walkFileTree(fileDoc, new SimpleFileVisitor<Path>() {
                //重写visitFile的方法，这对于
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    //传入的file是一个文件类型的，attrs是该文件的一些属性
                    indexDocs(file,indexWriter);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            //存储索引的实践操作
            indexDocs(fileDoc, indexWriter);
        }
        //要关闭IndexWriter
        indexWriter.close();
    }

    private static void indexDocs(Path path, IndexWriter indexWriter) throws IOException {
        //将文件以类的方式读入
        InputStream inputStream = Files.newInputStream(path);
        //存入的文档
        Document document = new Document();
        //存入文档的属性,第一个是字段名，第二个是内容，第三个是否存储内容
        //Field有很多实现类，对于不同类型的字段，有不同的实现类来操作StringField是存储String类型的字段，不进行分词
        Field field = new StringField("fileName", path.getFileName().toString(), Field.Store.YES);
        //将属性加入文档中
        document.add(field);
        //TextField存入比较大的文本内容，要进行分词。一个是字段名，一个是Reader
        //new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf-8")))通过utf-8格式，获取带缓存的Reader
        document.add(new TextField("content",new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf-8")))));
        //LongPoint用于存储long类型数据，不分词
        document.add(new LongPoint("modified",Files.getLastModifiedTime(path).toMillis()));
        //在存入索引时，打出操作动作
        System.out.println("adding files:"+path);
        //添加文档
        indexWriter.addDocument(document);
        //显示关闭流
        inputStream.close();
    }
}
