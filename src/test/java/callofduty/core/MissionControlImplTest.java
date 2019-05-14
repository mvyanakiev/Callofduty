package callofduty.core;

import callofduty.domain.missions.HuntMission;
import callofduty.domain.missions.SurveillanceMission;
import callofduty.interfaces.Mission;
import callofduty.interfaces.MissionControl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MissionControlImplTest {

    MissionControl missionControl;

    @Before
    public void setUp() throws Exception {
        this.missionControl = new MissionControlImpl();
    }

    @Test
    public void missionIdEscort() {
      Mission testMission =  missionControl.generateMission("abc", 10.0, 10.0);
        Assert.assertEquals("abc", testMission.getId());
    }

    @Test
    public void bigMissionIdEscort() {
        Mission testMission =  missionControl.generateMission("ab_ab_ab_abc", 10.0, 10.0);
        Assert.assertEquals("ab_ab_ab", testMission.getId());
    }

    @Test
    public void missionRatingEscort() {
        Mission testMission =  missionControl.generateMission("cdcd", 10.0, 10.0);
        Assert.assertEquals(7.5, testMission.getRating(), 0.1);
    }

//    @Test // broke N1
//    public void missionBountyEscort() {
//        Mission testMission =  missionControl.generateMission("asd", 10.0, 10.0);
//        Assert.assertEquals(7.5, testMission.getBounty(), 0.1);
//    }

    @Test
    public void missionEscortBigRating() {
        Mission testMission =  missionControl.generateMission("iuyhg", 101.0, 10.0);
        Assert.assertEquals(75, testMission.getRating(), 0.1);
    }

    @Test
    public void missionEscortSmallRating() {
        Mission testMission =  missionControl.generateMission("fdcv", -2.0, 10.0);
        Assert.assertEquals(0, testMission.getRating(), 0.1);
    }

    @Test
    public void missionEscortSmallBounty() {
        Mission testMission =  missionControl.generateMission("abc", -11.0, -10.0);
        Assert.assertEquals(0, testMission.getBounty(), 0.1);
    }

    @Test
    public void missionH() {
        Mission testMission =  new HuntMission("abc", 10.0, 10.0);
        Assert.assertEquals(15, testMission.getRating(), 0.1);
    }

    @Test
    public void missionS() {
        Mission testMission =  new SurveillanceMission( "abc", 10.0, 10.0);
        Assert.assertEquals(2.5, testMission.getRating(), 0.1);
    }




//    @Test //broke n1
//    public void missionEscortBigBounty() {
//        Mission testMission =  missionControl.generateMission("abc", 23.0, 234000.0);
//        Assert.assertEquals(75000.0, testMission.getBounty(), 0.1);
//    }






    @Test // do not give a points
    public void missionType() {
        Mission testMission1 =  missionControl.generateMission("fdcv", 11D, 10.0);
        Mission testMission2 =  missionControl.generateMission("fdcv", 11D, 10.0);
        Mission testMission3 =  missionControl.generateMission("fdcv", 11D, 10.0);

        Assert.assertEquals("EscortMission", testMission1.getClass().getSimpleName());
        Assert.assertEquals("HuntMission", testMission2.getClass().getSimpleName());
        Assert.assertEquals("SurveillanceMission", testMission3.getClass().getSimpleName());
    }
}