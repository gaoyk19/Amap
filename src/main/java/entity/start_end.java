package entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "V_GD_NAV_ROAD_1")
public class start_end {
    //@GeneratedValue(strategy = GenerationType.AUTO)   //主键生成策略
    //@GeneratedValue(generator="system_uuid") //另一种主键生成策略
    //@GenericGenerator(name="system_uuid",strategy="uuid")

    //@GeneratedValue(strategy = GenerationType.AUTO,generator="id")
    //@SequenceGenerator(name="id",sequenceName = "S_QUEENS",allocationSize = 1)

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    private String s;
    private String e;
    private String m;//is useful ??

    public start_end(){super();}

    public start_end(String s,String e){
        this.s=s;
        this.e=e;
    }

    public start_end(int id,String s,String e){
        this.id=id;
        this.s=s;
        this.e=e;
    }
}
