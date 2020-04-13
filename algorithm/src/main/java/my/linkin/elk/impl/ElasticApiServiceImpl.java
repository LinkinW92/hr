//package my.linkin.elk.impl;
//
//
//import lombok.extern.slf4j.Slf4j;
//import my.linkin.elk.ElasticEntity;
//import my.linkin.elk.IElasticApiService;
//import my.linkin.elk.Pagination;
//import org.elasticsearch.action.ActionListener;
//import org.elasticsearch.action.DocWriteResponse;
//import org.elasticsearch.action.get.GetRequest;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.index.IndexResponse;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.update.UpdateRequest;
//import org.elasticsearch.action.update.UpdateResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.indices.GetIndexRequest;
//import org.elasticsearch.common.text.Text;
//import org.elasticsearch.common.xcontent.XContentBuilder;
//import org.elasticsearch.common.xcontent.XContentType;
//import org.elasticsearch.common.xcontent.json.JsonXContent;
//import org.elasticsearch.index.Index;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.rest.RestStatus;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
///**
// * @Auther: chunhui.wu
// * @Date: 2019/5/30 14:37
// * @Description:
// */
//@Slf4j
//@Service
//public class ElasticApiServiceImpl implements IElasticApiService {
//
//    @Autowired
//    private RestHighLevelClient client;
//
//    @Override
//    public boolean importEntity(String index, ElasticEntity entity) {
//        IndexRequest request = new IndexRequest(index);
//        if (exist(index, entity.getId())) {
//            return updateIndex(index, entity);//存在则更新
//        }
//        client.indexAsync(request.id(entity.getId()).source(JSON.toJSONString(entity), XContentType.JSON), RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
//            @Override
//            public void onResponse(IndexResponse indexResponse) {
//                if (Objects.equals(DocWriteResponse.Result.CREATED, indexResponse.getResult())) {
//                    log.info("新增ES记录成功, 此次新增id:{}", entity.getId());
//                }
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                throw new BizException(ResultCode.BIZ_EXCEPTION, "新增ES记录异常, id:{}".concat(entity.getId()));
//            }
//        });
//        return true;
//    }
//
//    @Override
//    public Pagination<ElasticEntity> search(@NotNull Class<? extends ElasticEntity> clz, @NotEmpty String searchContent, List<String> ids, int pageNum, int pageSize) {
//        String index = clz.getAnnotation(Index.class).index();
//        List<String> fields = Arrays.stream(clz.getDeclaredFields()).filter(e -> e.isAnnotationPresent(ElasticField.class)).map(Field::getName).collect(Collectors.toList());
//        //1.设置highlight
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        for (String field : fields) {
//            highlightBuilder.field(new HighlightBuilder.Field(field));
//        }
//        //2.配置搜索条件
//        SearchRequest req = new SearchRequest(index);
//        QueryBuilder idQuery = QueryBuilders.idsQuery().addIds(ids.toArray(new String[ids.size()]));
//        QueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(searchContent, fields.toArray(new String[fields.size()]));
//        QueryBuilder boolQuery = QueryBuilders.boolQuery().must(idQuery).must(multiMatchQuery);
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(boolQuery).highlighter(highlightBuilder).from(pageNum * pageSize).size(pageSize);
//        try {
//            SearchResponse response = client.search(req.source(sourceBuilder), RequestOptions.DEFAULT);
//            Pagination<ElasticEntity> pagination = new Pagination<>();
//            pagination.setResult(extractSearchResponse(response, clz, fields));
//            pagination.setPageSize(pageSize);
//            pagination.setPageNum(pageNum);
//            pagination.setTotal(response.getHits().getTotalHits().value);
//            return pagination;
//        } catch (Exception e) {
//            log.warn("index highlight query exception:{}", e);
//            throw new BizException(ResultCode.BIZ_EXCEPTION, "ES检索异常");
//        }
//    }
//
//    @Override
//    public ElasticEntity searchByPrimaryKey(@NotEmpty String id, @NotNull Class<? extends ElasticEntity> clz) {
//        String index = clz.getAnnotation(Index.class).index();
//        SearchRequest req = new SearchRequest(index);
//        QueryBuilder idQuery = QueryBuilders.idsQuery().addIds(id);
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(idQuery);
//        try {
//            SearchResponse response = client.search(req.source(sourceBuilder), RequestOptions.DEFAULT);
//            SearchHit[] hits = response.getHits().getHits();
//            return hits.length == 0 ? null : JSONObject.parseObject(hits[0].getSourceAsString(), clz);
//        } catch (IOException e) {
//            log.warn("index highlight query with id query exception:{}", e);
//            throw new BizException(ResultCode.BIZ_EXCEPTION, "ES检索异常");
//        }
//    }
//
//    @Override
//    public boolean exist(String index, String id) {
//        try {
//            return client.exists(new GetRequest().index(index).id(id), RequestOptions.DEFAULT);
//        } catch (IOException ioe) {
//            log.warn("判断指定索引下记录是否存在异常, msg:{}, index:{}, id:{}", ioe, index, id);
//        }
//        return false;
//    }
//
//    @Override
//    public boolean index(String index, ElasticEntity entity) {
//        try {
//            IndexRequest indexRequest = new IndexRequest(index);
//            XContentBuilder contentBuilder = JsonXContent.contentBuilder().startObject().startObject("mappings").startObject("doc").startObject("properties");
//            Field[] fields = entity.getClass().getDeclaredFields();
//            for (Field field : fields) {
//                if(!field.isAnnotationPresent(ElasticField.class)) {
//                    continue;
//                }
//                ElasticField elasticField = field.getDeclaredAnnotation(ElasticField.class);
//                contentBuilder.startObject(field.getName());
//                contentBuilder.field("type", elasticField.getClass()).field("analyzer", elasticField.analyzer());
//                contentBuilder.endObject();
//            }
//            contentBuilder.endObject().endObject().endObject();
//            contentBuilder.startObject("setting").field("number_of_shards", 3).field("number_of_replicas", 1).endObject().endObject();
//            indexRequest.source(contentBuilder);
//            client.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
//                @Override
//                public void onResponse(IndexResponse indexResponse) {
//                    if (!Objects.equals(RestStatus.CREATED, indexResponse.status())) {
//                        log.warn("创建索引失败:{}", indexResponse);
//                        throw new BizException(ResultCode.BIZ_EXCEPTION, "创建索引失败,index:".concat(index));
//                    }
//                    log.info("创建ES索引成功, index:{}", index);
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    log.warn("创建索引异常:{}", e);
//                    throw new BizException(ResultCode.BIZ_EXCEPTION, "创建索引异常,index:".concat(index));
//                }
//            });
//
//        } catch (IOException e) {
//            log.warn("error occurs when create index:{}, msg:{}", index, e);
//            throw new BizException(ResultCode.BIZ_EXCEPTION, "创建ES索引异常");
//        }
//        return true;
//    }
//
//    @Override
//    public boolean indexExist(String index) {
//        try {
//            boolean flag = client.indices().exists(new GetIndexRequest(index).humanReadable(true), RequestOptions.DEFAULT);
//            log.info("当前索引:{}, 是否存在:{}", index, flag);
//            return flag;
//        } catch (IOException e) {
//            log.warn("ES服务异常:{}", e);
//            throw new BizException(ResultCode.BIZ_EXCEPTION, "ES服务异常,请稍后重试");
//        }
//    }
//
//    @Override
//    public boolean updateIndex(String index, ElasticEntity entity) {
//        Asserts.checkArgs(entity != null && StringUtils.isNotEmpty(entity.getId()), "更新索引id不可为空");
//        UpdateRequest request = new UpdateRequest(index, entity.getId()).doc(JSON.toJSONString(entity), XContentType.JSON);
//        client.updateAsync(request, RequestOptions.DEFAULT, new ActionListener<UpdateResponse>() {
//            @Override
//            public void onResponse(UpdateResponse updateResponse) {
//                if (Objects.equals(RestStatus.OK, updateResponse.status())) {
//                    log.info("更新ES索引成功,更新记录id:{}", entity.getId());
//                }
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                throw new BizException(ResultCode.BIZ_EXCEPTION, "更新ES索引异常:".concat(e.getMessage()));
//            }
//        });
//        return true;
//    }
//
//    private static List<ElasticEntity> extractSearchResponse(SearchResponse response, Class<? extends ElasticEntity> clz, List<String> fields) {
//        List<ElasticEntity> result = Lists.newArrayList();
//        try {
//            SearchHit[] hits = response.getHits().getHits();
//            for (SearchHit hit : hits) {
//                ElasticEntity entity = JSONObject.parseObject(hit.getSourceAsString(), clz);
//                for (String field : fields) {
//                    HighlightField highlightField = hit.getHighlightFields().get(field);
//                    if (highlightField != null) {
//                        for (Field f : entity.getClass().getDeclaredFields()) {
//                            if (field.equals(f.getName())) {
//                                Method setter = entity.getClass().getDeclaredMethod("set" + field.substring(0, 1).toUpperCase().concat(field.substring(1)), String.class);
//                                setter.invoke(entity, mark(highlightField.getFragments()));
//                            }
//                        }
//                    } else {
//                        Method getter = entity.getClass().getDeclaredMethod("get" + field.substring(0, 1).toUpperCase().concat(field.substring(1)), null);
//                        Object value = getter.invoke(entity, null);
//                        if (null != value) {
//                            Method setter = entity.getClass().getDeclaredMethod("set" + field.substring(0, 1).toUpperCase().concat(field.substring(1)), String.class);
//                            setter.invoke(entity, JsoupUtil.emClean(value.toString()));
//                        }
//                    }
//                }
//                result.add(entity);
//            }
//        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            log.warn("解析ES搜索结果异常:{}", e);
//        }
//        return result;
//    }
//
//    /*
//     * 拼接fragments
//     * */
//    private static String mark(Text[] fragments) {
//        if (null == fragments || fragments.length == 0) {
//            return "";
//        }
//        StringBuilder content = new StringBuilder();
//        for (Text fragment : fragments) {
//            content.append(fragment.string());
//        }
//        String s1 = JsoupUtil.emClean(content.toString()).replaceAll("\n", "").replaceAll("<em>", "<span style=\"color: red\">");
//        return s1.replaceAll("</em>", "</span>");
//    }
//}
