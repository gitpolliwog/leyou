package com.tk.util;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;

import java.net.InetAddress;
import java.util.Iterator;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public  class EsUtil {

    private static TransportClient client;


    @Before
    public    void init(String ippAddress,int portNumber) throws Exception{
        //1.创建一个Setting对象，配置信息
        Settings settings = Settings.builder().put("cluster.name", "my-elasticsearch").build();
        //2.创建一个Client对象
        client=new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName(ippAddress),portNumber));
    }

    //创建索引库
    public   void createIndex(String indexName) throws Exception{

        //3.使用client对象创建一个索引库
        client.admin().indices().prepareCreate(indexName).get();
        //4.关闭client对象
//        client.close();
    }

    //设置mapping映射
    public  void setMapping() throws Exception{

//        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9300));
        //3.创建一个Mapping信息
        XContentBuilder builder= XContentFactory.jsonBuilder()
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
                                   .field("analyzer","standard")
                              .endObject()
                            .startObject("content")
                                .field("type","text")
                                .field("store","true")
                                .field("analyzer","standard")
                             .endObject()
                        .endObject()
                    .endObject()
                .endObject();
                client.admin().indices()
                        .preparePutMapping("hello")
                        .setType("hello")
                        .setSource(builder)
                        .get();
//                client.close();
            }
    public  void createDocument() throws Exception{
        //创建一个文档对象
        XContentBuilder builder=XContentFactory.jsonBuilder()
                .startObject()
                    .field("id",1)
                    .field("title","测试title")
                    .field("content","测试content")
                .endObject();
        //把文档添加到索引库
        client.prepareIndex()
                //设置索引名称
                .setIndex("hello")
                //设置type
                .setType("hello")
                //设置文档的id
                .setId("1")
                //设置文档信息
                .setSource(builder)
                //执行操作
                .get();
        //关闭客户端
//        client.close();

    }
    public static void search(org.elasticsearch.index.query.QueryBuilder queryBuilder) throws Exception{
        //执行查询
        SearchResponse searchResponse = client.prepareSearch("hello")
                .setTypes("hello")
                .setQuery(queryBuilder)
                .get();
        //取查询结果
        SearchHits searchHits = searchResponse.getHits();
        //查询结果列表
        Iterator<SearchHit> iterator = searchHits.iterator();
        while (iterator.hasNext()) {
             SearchHit searchHit = iterator.next();
            //打印文档对象
            System.out.println(searchHit.getSourceAsString());
        }

        client.close();
        //关闭客户端
    }
    public static void searchById(String id) throws Exception{
        //创建一个client对象
        //创建一个查询对象
        IdsQueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(id);
        search(queryBuilder);
    }
    public static void searchByKeyWord (String propertyName,String keyword) throws Exception{
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery(propertyName, keyword);
        search(queryBuilder);
    }
    public static void deleteById (String indexName,String typeName,String id) throws Exception{
        DeleteResponse deleteResponse = client.prepareDelete(indexName, typeName, id).get();
        System.out.println(deleteResponse.getId());
    }
    public static void updateByFieldName(String indexName,String typeName,String id,String fieldName,String fieldValue) throws Exception {
        client.prepareUpdate(indexName, typeName, id)
                .setDoc(jsonBuilder()
                        .startObject()
                        .field(fieldName, fieldValue)
                        .endObject())
                .get();
    }






}
