package utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;

    /*
    * （1）JPA(java persistence API) 是负责 对象持久化 的应用程序编程接口，定义一系列注释；给实体类添加适当的注释可以在程序运行时，告诉hibernate如何将
    * 一个实体类 保存到数据库中 以及如何将数据一对象的形式从数据库中读取出来；
    * （2）JPA是一种规范，而hibernate是一种ORM框架，是JPA的具体实现；
    * （3）JDBC由不同的数据库厂商（mysql、oracle等）各自提供相应的实现类，打包成jar包(数据库驱动），因此java应用程序只需要调用jdbc接口就可以了；
    * （4）JPA是与JDBC类似的东西，它为（hibernate、TopLink）等ORM框架提供接口；
    *
    * */
public class JPAUtil {
    private static final EntityManagerFactory emFactory;
    static {
        try {
            //SPATIAL-JPA可以在resources/META-INF/persistence.xml文件的第7行可以看到
            //参考链接：https://www.kejisen.com/article/161331290.html
            emFactory = Persistence.createEntityManagerFactory("SPATIAL-JPA");
        }catch(Throwable ex){
            System.err.println("Cannot create EntityManagerFactory.");
            throw new ExceptionInInitializerError(ex);
        }
    }
    public static EntityManager createEntityManager() {
        return emFactory.createEntityManager();
    }

    public static void close(){
        emFactory.close();
    }


    //为了在JPA环境中使用hibernate API,此步骤获取hibernate原生session
    public static Session getSession() {
    	//EntityManager em = JPAUtil.createEntityManager();
		//Session session = em.unwrap(Session.class);
    	return emFactory.createEntityManager().unwrap(Session.class);
    }
}
