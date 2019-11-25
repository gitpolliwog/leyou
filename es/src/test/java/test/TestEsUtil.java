package test;

import com.tk.util.EsUtil;
import org.junit.Test;

public class TestEsUtil {

    @Test
   public void test() throws Exception{
       EsUtil util=new EsUtil();//初始化数据库
       util.init("127.0.0.1",9300);
       util.createIndex("test2");
       util.setMapping();//内容有点多建议在工具类中直接修改
       util.createDocument();//内容有点多建议在工具类中直接修改

        //按id查询
        //EsUtil.searchById("1");
        //关键字查询
        //EsUtil.searchByKeyWord("title","测试");
        //根据id删除
        //EsUtil.deleteById("test2","hello","1");
        //更新
       // EsUtil.updateByFieldName("test2","hello",1,"title","更新后的title");
   }
}
