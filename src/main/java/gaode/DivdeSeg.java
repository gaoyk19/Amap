package gaode;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vividsolutions.jts.io.ParseException;
import entity.GdNaviLinkTest;
import entity.start_end;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.JPAUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gaode.GetPathTest.getHttpReq;


//
public class DivdeSeg {

    public static void main(String[] args) throws ParseException {
            Map<String, Object> link = getWhole();
            // 发送http请求
            String content = getHttpReq(link);
            parseContent(content);
    }

    //从数据库表 V_GD_NAV_ROAD 读取起点、终点、途径点
    public static Map<String, Object> getWhole() {
        Session session = JPAUtil.getSession();
        List<Object[]> objects = session.createSQLQuery("SELECT * FROM V_GD_NAV_ROAD").addScalar("s").addScalar("e")
                .addScalar("m").list();
        session.clear();
        session.close();

        Map<String, Object> map = new HashMap<String, Object>();
        map = new HashMap<String, Object>();
        map.put("S", objects.get(0)[0]);
        map.put("E", objects.get(0)[1]);
        map.put("M", objects.get(0)[2]);
        return map;
    }

    //解析http返回的消息实体(即：response_example.json文件中的内容)
    public static void parseContent(String content) throws ParseException {
        JsonParser parser = new JsonParser();
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
            return ;
        }

        JsonArray pathList = result.get("route").getAsJsonObject().get("paths").getAsJsonArray();//得到多条路线
        System.out.println("path的个数："+pathList.size());

        JsonArray steps = pathList.get(0).getAsJsonObject().get("steps").getAsJsonArray();
        System.out.println("第一条path中steps大小：" + steps.size() );

/************************************************************************************************************/
        //path中所有step的坐标点串,将它转化为起点，终点 存储到数据库中
        {
            Session sessionStartEnd = JPAUtil.getSession();
            Transaction tx_StartEnd = sessionStartEnd.beginTransaction();

            List<String> polyLineList=new ArrayList<>();
            String res="";
            for(int stepID=0;stepID<steps.size();++stepID) {
                JsonObject step = steps.get(stepID).getAsJsonObject();
                String polyline = step.get("polyline").getAsString();
//               System.out.println(polyline);
                System.out.println("-----------------------------------------------------------------------------------");
                String[] temp = polyline.split(";");
                for (int i = 0; i < temp.length; ++i) {
                    if(stepID!=0 && i==0) continue;
                    polyLineList.add(temp[i]);
                    System.out.println(temp[i]);
                }
            }
                System.out.println("polyLineList: "+polyLineList.size());

            //save the start point to Mysql
            start_end realStart=new start_end("125.32591,43.855156",polyLineList.get(0));
            sessionStartEnd.save(realStart);
            System.out.println("125.32591,43.855156 | "+polyLineList.get(0));

            //make the whole path to multiple segments,and to save every segements start /end point in Mysql;
            int length=polyLineList.size();
            for(int pointIndex=0;pointIndex<length-1;pointIndex++){
                System.out.println(polyLineList.get(pointIndex)+" | "+polyLineList.get(pointIndex+1));
                start_end originEnd=new start_end(polyLineList.get(pointIndex),polyLineList.get(pointIndex+1));
                sessionStartEnd.save(originEnd);//将起点/终点存储到数据库中
            }

            //save the end point
            start_end realEnd=new start_end(polyLineList.get(polyLineList.size()-1),"125.28951,43.824303");
            sessionStartEnd.save(realEnd);
            System.out.println(polyLineList.get(polyLineList.size()-1)+" | 125.28951,43.824303");

            sessionStartEnd.flush();
            sessionStartEnd.clear();
            tx_StartEnd.commit();
            sessionStartEnd.close();
            JPAUtil.close();//必须要有
        }
    }

}
