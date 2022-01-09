package entity;

import javax.persistence.*;

@Entity
@Table(name = "GD_NAV_TRAFFIC_Test")
public class GdNaviLinkTest {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int pathID;
    private Double distance;
    private Double duration;
    private Double speed;

    public GdNaviLinkTest() {
        super();
    }
    public GdNaviLinkTest(int pathID,Double distance,Double duration,Double speed){
        this.pathID=pathID;
        this.distance=distance;
        this.duration=duration;
        this.speed=speed;
    }
}
