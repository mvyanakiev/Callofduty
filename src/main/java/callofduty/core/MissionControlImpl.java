package callofduty.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import callofduty.interfaces.Mission;
import callofduty.interfaces.MissionControl;

public class MissionControlImpl implements MissionControl {
    private static final Integer MISSION_ID_MAXIMUM_LENGTH = 8;

    private static final Double MISSION_RATING_MINIMUM_VALUE = 0D;

    private static final Double MISSION_RATING_MAXIMUM_VALUE = 100D;

    private static final Double MISSION_BOUNTY_MINIMUM_VALUE = 0D;

    private static final Double MISSION_BOUNTY_MAXIMUM_VALUE = 100000D;

    private Map<String, Class> missionClasses;

    private Iterator<Map.Entry<String, Class>> missionIterator;

    public MissionControlImpl() {
        this.initMissionClasses();
        this.missionIterator = this.missionClasses.entrySet().iterator();
    }

    private void initMissionClasses() {
        try {
            this.missionClasses = new LinkedHashMap<>() {

                {

                    put("EscortMission",
                            Class.forName("callofduty.domain.missions.EscortMission"));
                    put("HuntMission",
                            Class.forName("callofduty.domain.missions.HuntMission"));
                    put("SurveillanceMission",
                            Class.forName("callofduty.domain.missions.SurveillanceMission"));
                }
            };
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String checkAndReformMissionId(String missionId) {
        return missionId.length() > MISSION_ID_MAXIMUM_LENGTH
                ? missionId.substring(0, MISSION_ID_MAXIMUM_LENGTH)
                : missionId;
    }

    private Double checkAndReformMissionRating(Double missionRating) {

        Double result = -3.0;

        if (missionRating <= MISSION_RATING_MINIMUM_VALUE) {
            result = MISSION_RATING_MINIMUM_VALUE;
        } else if (missionRating >= MISSION_RATING_MAXIMUM_VALUE) {
            result = MISSION_RATING_MAXIMUM_VALUE;
        } else {
            result = missionRating;
        }

        return result;

    }

    private Double checkAndreformMissionBounty(Double missionBounty) {

        Double result = -3.0;

        if (missionBounty <= MISSION_BOUNTY_MINIMUM_VALUE) {
            result = MISSION_BOUNTY_MINIMUM_VALUE;
        } else if (missionBounty >= MISSION_BOUNTY_MAXIMUM_VALUE) {
            result = MISSION_BOUNTY_MAXIMUM_VALUE;
        } else {
            result = missionBounty;
        }

        return result;
    }

    private void updateMissionType() {
        this.missionIterator = this.missionClasses.entrySet().iterator();
    }


    private Class currentMission() {
        if (!this.missionIterator.hasNext()) {
            this.updateMissionType();
        }

        return this.missionIterator.next().getValue();
    }


    private Mission instantiateMissionObject(String missionId, Double missionRating, Double missionBounty) {
        Mission missionObject = null;

        try {
            missionObject = (Mission)
                    this.currentMission()
                            .getConstructor(String.class, Double.class, Double.class)
                            .newInstance(missionId, missionRating, missionBounty);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ignored) {

        }

        return missionObject;
    }

    @Override
    public Mission generateMission(String missionId, Double missionRating, Double missionBounty) {
        missionId = this.checkAndReformMissionId(missionId);
        missionRating = this.checkAndReformMissionRating(missionRating);
        missionBounty = this.checkAndreformMissionBounty(missionBounty);


        Mission generatedMission =
                this.instantiateMissionObject(missionId, missionRating, missionBounty);

        return generatedMission;
    }
}