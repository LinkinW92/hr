package my.linkin.elk;



import java.util.List;

/**
 * @Auther: chunhui.wu
 * @Date: 2019/5/30 14:34
 * @Description:
 */
public interface IElasticApiService {

    /*
     * 数据导入es
     * */
    boolean importEntity(String index, ElasticEntity entity);

    /*
     * multi_match查询高亮查询, 列表模式(预览模式)返回特定长度纯文本,无换行,过滤html标签
     * */
    Pagination<ElasticEntity> search(Class<? extends ElasticEntity> clz, String searchContent, List<String> ids, int pageNum, int pageSize);

    /*
     * ids 查询,返回富文本格式
     * */
    ElasticEntity searchByPrimaryKey(String id, Class<? extends ElasticEntity> clz);

    /*
     * 索引下指定记录是否存在
     * */
    boolean exist(String index, String id);

    /*
     * 创建索引,类似数据库
     * */
    boolean index(String index, ElasticEntity entity);

    /*
     * 索引是否存在
     * */
    boolean indexExist(String index);

    /*
     * 更新索引
     * */
    boolean updateIndex(String index, ElasticEntity entity);
}
