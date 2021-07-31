package entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "V_GD_NAV_ROAD_1")
public class start_end {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)   //主键生成策略

//    @GeneratedValue(generator="system_uuid") //另一种主键生成策略
//    @GenericGenerator(name="system_uuid",strategy="uuid")

    private String s;
    private String e;
    private String m;

    public start_end(){super();}
    public start_end(String s,String e){

        this.s=s;
        this.e=e;

    }
}
