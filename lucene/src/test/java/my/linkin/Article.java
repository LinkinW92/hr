package my.linkin;

public class Article {
    //编号
    private Integer id;
    //标题
    private String title;
    //内容
    private String content;
    public Article(){}
    public Article(Integer id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "编号:" + id+"\n标题：" + title + "\n内容:" + content;
    }
}
