package com.tk;

import io.searchbox.client.JestClient;
import io.searchbox.core.SearchResult;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;


import io.searchbox.core.Search;


import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


public class ElasticSearchTest {

    private TransportClient client;

    @Before
    public void init() throws Exception{
        //1.创建一个Setting对象，配置信息
        Settings settings = Settings.builder().put("cluster.name", "my-elasticsearch").build();
        //2.创建一个Client对象
       client=new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9300));
    }

    @Test
    //创建索引库
    public void createIndex() throws Exception{

        //3.使用client对象创建一个索引库
        client.admin().indices().prepareCreate("test2").get();
        //4.关闭client对象
        client.close();
    }

    @Test
    //设置maopping映射
    public void setMappings() throws Exception{

        //3.创建一个Mapping信息
        XContentBuilder builder= jsonBuilder()
                .startObject()
                    .startObject("hello")
                        .startObject("properties")
                            .startObject("id")
                                .field("type","long")
                                .field("store","true")
                            .endObject()
                            .startObject("title")
                                .field("type","text")
                                .field("store","true")
                                .field("analyzer","ik_smart")
                            .endObject()
                            .startObject("content")
                                .field("type","text")
                                .field("store","true")
                                .field("analyzer","ik_smart")
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject();
        client.admin().indices()
                .preparePutMapping("test2")
                .setType("hello")
                .setSource(builder)
                .get();
        client.close();
    }

    @Test
    public void createDocument() throws Exception{

            //创建一个文档对象
            XContentBuilder builder= jsonBuilder()
                    .startObject()
                    .field("id",13)
                    .field("title","教师入职要找35名领导干部签字？当事高校回应")
                    .field("content","　　都是美国内政，都与人事有关，都有点玄机，也都很精彩，并正在影响未来的美国政治版图。")
                    .endObject();
            //把文档添加到索引库
            client.prepareIndex()
                    //设置索引名称
                    .setIndex("test3")
                    //设置type
                    .setType("user")
                    //设置文档的id
                    .setId(13+"")
                    //设置文档信息
                    .setSource(builder)
                    //执行操作
                    .get();

        //关闭客户端
        client.close();

    }

//    @Test
//    public void createDocument2() throws Exception{
//        Article article=new Article();
//        article.setId(3l);
//        article.setTitle("测试title3");
//        article.setContent("测试content3");
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonDocument = objectMapper.writeValueAsString(article);
//        System.out.println(jsonDocument);
//        client.prepareIndex("hello","hello","3")
//                .setSource(jsonDocument, XContentType.JSON)
//                .get();
//        client.close();
//
//    }

    public void search(org.elasticsearch.index.query.QueryBuilder queryBuilder) throws Exception{
        //执行查询

        SearchResponse searchResponse = client.prepareSearch("test")
                .setTypes("hello")
                .setQuery(queryBuilder)
                .get();
        //取查询结果
        SearchHits searchHits = searchResponse.getHits();
        //取查询结果的记录数
        System.out.println(searchHits);
        //查询结果列表
        Iterator<SearchHit> iterator = searchHits.iterator();
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next();
            //打印文档对象
            System.out.println(searchHit.getSourceAsString());
            //取文档的属性

        }

        client.close();
        //关闭客户端
    }

    @Test
    public void searchDocumentById() throws Exception{
        //创建一个client对象
        //创建一个查询对象
        IdsQueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds("1", "3");
        search(queryBuilder);
    }

    @Test
    public void searchDocumentByTerm () throws Exception{
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("title", "title");
        search(queryBuilder);
    }


    /**
     * 获取所有index
     */
    public Set getAllIndices() {
        ActionFuture<IndicesStatsResponse> isr = client.admin().indices().stats(new IndicesStatsRequest().all());
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        Map<String, IndexStats> indexStatsMap = isr.actionGet().getIndices();
        Set<String> set = isr.actionGet().getIndices().keySet();
        return set;
    }



    @Test
    public void  getElasticClient() throws Exception {

        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();

        SearchResponse searchResponse = client.prepareSearch("*").get();

        SearchHits hits = searchResponse.getHits();
        for (SearchHit searchHit:hits) {
           System.out.println(searchHit.getSourceAsString());
        }
    }

    @Test
    public void searcByKeyWord() throws Exception{


        QueryStringQueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder("回应");

        SearchResponse response = client.prepareSearch("test2","test3","test4")
                .setQuery(queryStringQueryBuilder)
                .get();
        SearchHits searchHits = response.getHits();
        Iterator<SearchHit> iterator = searchHits.iterator();
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next();
            //打印文档对象
            System.out.println(searchHit.getIndex()+": "+ searchHit.getSourceAsString());
            //取文档的属性

        }



    }
    @Test
    public void deleteById () throws Exception{
            DeleteResponse deleteResponse = client.prepareDelete("test", "hello", "1D16pW4BzLEWXTGetRv2").get();

        System.out.println(deleteResponse.getId());
    }

    @Test
    public void update() throws Exception {
        client.prepareUpdate("hello", "hello", "1")
                .setDoc(jsonBuilder()
                        .startObject()
                        .field("title", "update")
                        .endObject())
                .get();
    }







}
