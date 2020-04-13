package my.linkin.elk;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: haidong.feng
 * Date: 2018/10/18
 * Description:
 */
@Data
public class Pagination<T> implements Serializable {

    private static final long serialVersionUID = 5479043355479471450L;

    public static final int DEFAULT_PAGE_NO = 1;//默认页码
    public static final int DEFAULT_PAGE_SIZE = 25;//默认单页记录数

    //当前页
    private int pageNum;
    //每页的数量
    private int pageSize;
    //总记录数
    private long total;
    //总页数
    private int pages;
    //结果集
    private List<T> result;


    public Pagination() {
        result = Lists.newArrayList();
        this.pageNum = DEFAULT_PAGE_NO;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public static final Pagination build(PageInfo page) {
        Pagination pagination = new Pagination();
        pagination.setPageNum(page.getPageNum());
        pagination.setPageSize(page.getPageSize());
        pagination.setTotal(page.getTotal());
        pagination.setPages(page.getPages());
        if(!CollectionUtils.isEmpty(page.getList())) {
            pagination.setResult(page.getList());
        }
        return pagination;
    }
}
