package callofduty.entities;

import callofduty.domain.BaseMission;
import callofduty.interfaces.Agent;
import callofduty.interfaces.Mission;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseAgent implements Agent {
    private String id;
    private String name;
    private double rating;
    private Map<String, Mission> assignedMissions;
    private Map<String, Mission> completedMissions;

    protected BaseAgent(String id, String name) {
        this.id = id;
        this.name = name;
        this.rating = 0.0;
        this.assignedMissions = new LinkedHashMap<>();
        this.completedMissions = new LinkedHashMap<>();
    }

    @Override
    public void acceptMission(Mission mission) {
        this.assignedMissions.put(mission.getId(), mission);
    }


    @Override
    public void completeMissions() {

        double money = 0;

        for (Map.Entry<String, Mission> missionEntry : assignedMissions.entrySet()) {

            Mission mission = missionEntry.getValue();
            money += mission.getBounty();
            this.rating += mission.getRating();


            try {
                Field status = BaseMission.class.getDeclaredField("isOpen");
                status.setAccessible(true);
                status.set(mission, false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }


            this.completedMissions.put(missionEntry.getKey(), missionEntry.getValue());
        }

        if (this.getClass().getSimpleName().equals("MasterAgent")) {

            try {
                Field agentMoney = MasterAgent.class.getDeclaredField("bounty");
                agentMoney.setAccessible(true);
                agentMoney.set(this, money); // must add here, not replace
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }


        }

        this.assignedMissions.clear();
    }


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Double getRating() {
        return this.rating;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String agentType = "";

        if (this.getClass().getSimpleName().equals("NoviceAgent")) {
            agentType = "Novice Agent";
        } else {
            agentType = "Master Agent";
        }

        sb
                .append(String.format("%s - %s", agentType, this.getName()))
                .append(System.lineSeparator())
                .append(String.format("Personal Code: %s", this.getId()))
                .append(System.lineSeparator())
                .append(String.format("Assigned Missions: %d", this.assignedMissions.size()))
                .append(System.lineSeparator())
                .append(String.format("Completed Missions: %d", this.completedMissions.size()))
                .append(System.lineSeparator())
                .append(String.format("Rating: %.2f", this.getRating()))
                .append(System.lineSeparator());

        return sb.toString();
    }
}
