package gaode;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import entity.GdNavLinkNJ;
import entity.GdNaviLinkTest;
import entity.start_end;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HttpClientResult;
import utils.HttpClientUtils;
import utils.JPAUtil;

import javax.sql.rowset.spi.SyncResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPathTest {
//    private List<Map> links; // 需要导航的OD对

    public static void main(String[] args) throws Exception {
        Session session = JPAUtil.getSession();
        Transaction tx = session.beginTransaction();

        GdNaviLinkTest path = new GdNaviLinkTest();
        List<Map> links = getLinks();

        for (int i = 0; i < links.size(); i++) {
//        int i=0;
            Map<String, Object> link = links.get(i);
            System.out.println("get response:" + (i + 1) + "/" + links.size());
            // 发送http请求
            String content = getHttpReq(link);
            if(content==null)
                continue;
            path=parseContent(content);
            session.save(path);
        }


        session.flush();
        session.clear();
        tx.commit();
        session.close();
        JPAUtil.close();//必须要有
    }

    //从数据库表 V_GD_NAV_ROAD 读取起点、终点、途径点
    public static List<Map> getLinks() {
        Session session = JPAUtil.getSession();
        List<Object[]> objects = session.createSQLQuery("SELECT * FROM V_GD_NAV_ROAD_1").addScalar("s").addScalar("e")
                .addScalar("m").list();
        session.clear();
        session.close();

        Map<String, Object> map = new HashMap<String, Object>();
        List<Map> linkList = new ArrayList<Map>();
        for (Object[] obj : objects) {
            map = new HashMap<String, Object>();
            map.put("S", obj[0]);
            map.put("E", obj[1]);
            map.put("M", obj[2]);
            linkList.add(map);
        }
        System.out.println("待解析的OD数: " + linkList.size());
        return linkList;
    }

    //向高德地图发送http请求，得到http响应
    public static String getHttpReq(Map<String, Object> map) {
        String url = "https://restapi.amap.com/v3/direction/driving";
        //String url = "https://restapi.amap.com/v5/direction/driving";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", "139a3f9066d939b983dc4a8dd6487578"); //这里需要修改申请得到的key
        params.put("extensions", "all");
        params.put("strategy", "5"); // 10默认多路径;2单路径,距离最短
        params.put("origin", (String) map.get("S"));
        params.put("destination", (String) map.get("E"));
        // 中途点
//        params.put("waypoints", (String) map.get("M"));

        HttpClientResult result;
        try {
            result = HttpClientUtils.doGet(url, params);
//            result = HttpClientUtils.doPost(url, params);
            System.out.println(result.getContent());

        } catch (Exception e) {
            System.out.println("访问接口出错了,忽略本次请求...");
            System.out.println("S: "+(String) map.get("S")+"; E: "+(String) map.get("E"));

            return null;
        }

        if (result.getCode() != 200) {
            System.out.println("访问API失败: " + result.getCode());
            return null;
        }

        return result.getContent();
    }


    //解析http返回的消息实体(即：response_example.json文件中的内容)
    public static GdNaviLinkTest parseContent(String content) throws ParseException {
        JsonParser parser = new JsonParser();
        //System.out.println(content);
        JsonObject result = (JsonObject) parser.parse(content);
        // 导航是否成功,处理失败的情景
        int status = 0, count = 0;
        if (result.get("status") != null) {
            status = result.get("status").getAsInt();
        }
        if (result.get("count") != null) {
            count = result.get("count").getAsInt();
        }
        String info = result.get("info").getAsString();
        if (status == 0 || count == 0) {
            System.out.println("请求导航服务引擎失败:" + info);
            return null;
        }

        int pathID;
        Double distance;
        Double duration;
        Double speed;
        GdNaviLinkTest singlePath;

        JsonArray pathList = result.get("route").getAsJsonObject().get("paths").getAsJsonArray();//得到多条路线
        System.out.println("path的个数："+pathList.size());

        JsonArray steps = pathList.get(0).getAsJsonObject().get("steps").getAsJsonArray();
        System.out.println("第一条path中steps大小：" + steps.size() );

//        for(int id=0;id<pathList.size();++id){
        int id=0;
        JsonObject path = pathList.get(id).getAsJsonObject();
        pathID=id;
        distance = path.get("distance").getAsDouble();
        duration = path.get("duration").getAsDouble();
        System.out.println("distance: "+distance+" ;duration: "+duration);
        speed = new Double(distance * 3.6 / duration).doubleValue();
        singlePath=new GdNaviLinkTest(pathID,distance,duration,speed);
        return singlePath;
//    }

    }
}

