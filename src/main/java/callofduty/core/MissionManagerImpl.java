package callofduty.core;

import callofduty.entities.BaseAgent;
import callofduty.entities.MasterAgent;
import callofduty.entities.NoviceAgent;
import callofduty.interfaces.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MissionManagerImpl implements MissionManager {
    private Map<String, BaseAgent> noviceAgents;
    private Map<String, BaseAgent> masterAgents;
    private int assignedMissionCount;
    private MissionControl missionControl;

    public MissionManagerImpl() {
        this.noviceAgents = new LinkedHashMap<>();
        this.masterAgents = new LinkedHashMap<>();
        this.assignedMissionCount = 0;
        missionControl = new MissionControlImpl();
    }

    @Override
    public String agent(List<String> arguments) {
        BaseAgent noviceAgent = new NoviceAgent(arguments.get(1), arguments.get(2));
        noviceAgents.put(noviceAgent.getId(), noviceAgent);
        return String.format("Registered Agent - %s:%s", noviceAgent.getName(), noviceAgent.getId());
    }

    @Override
    public String request(List<String> arguments) {
        Mission mission = missionControl.generateMission(
                arguments.get(2),
                Double.parseDouble(arguments.get(3)),
                Double.parseDouble(arguments.get(4)));

        String missionType = null;

        switch (mission.getClass().getSimpleName()) {
            case "EscortMission":
                missionType = "Escort Mission";
                break;
            case "HuntMission":
                missionType = "Hunt Mission";
                break;
            case "SurveillanceMission":
                missionType = "Surveillance Mission";
                break;
        }

        if (this.noviceAgents.containsKey(arguments.get(1))) {
            noviceAgents.get(arguments.get(1)).acceptMission(mission);
            assignedMissionCount++;
            return String.format("Assigned %s - %s to Agent - %s", missionType, mission.getId(),
                    noviceAgents.get(arguments.get(1)).getName());
        }

        if (this.masterAgents.containsKey(arguments.get(1))) {
            masterAgents.get(arguments.get(1)).acceptMission(mission);
            assignedMissionCount++;
            return String.format("Assigned %s - %s to Agent - %s", missionType, mission.getId(),
                    masterAgents.get(arguments.get(1)).getName());
        }
        return null;
    }


    @Override
    public String complete(List<String> arguments) throws IllegalAccessException {

        String name = "";

        if (this.noviceAgents.containsKey(arguments.get(1))) {

            Agent currentNoviceAgent = noviceAgents.get(arguments.get(1));
            name = currentNoviceAgent.getName();

            currentNoviceAgent.completeMissions();


            Map<String, Mission> completedMissions = new LinkedHashMap<>();

            try {
                Field getCompletedMissions = BaseAgent.class.getDeclaredField("completedMissions");
                getCompletedMissions.setAccessible(true);
                completedMissions = (Map<String, Mission>) getCompletedMissions.get(currentNoviceAgent);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }


            if (completedMissions.size() >= 3) {
                BaseAgent newMasterAgent = new MasterAgent(currentNoviceAgent.getId(), currentNoviceAgent.getName());

                Field rating = null;
                Field compMissions = null;
                try {
                    rating = BaseAgent.class.getDeclaredField("rating");
                    rating.setAccessible(true);
                    rating.set(newMasterAgent, currentNoviceAgent.getRating());

                    compMissions = BaseAgent.class.getDeclaredField("completedMissions");
                    compMissions.setAccessible(true);
                    compMissions.set(newMasterAgent, completedMissions);

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }

                this.masterAgents.put(newMasterAgent.getId(), newMasterAgent);
                this.noviceAgents.remove(arguments.get(1));
            }
        }


        if (this.masterAgents.containsKey(arguments.get(1))) {

            Agent currentMasterAgent = masterAgents.get(arguments.get(1));
            name = currentMasterAgent.getName();

            currentMasterAgent.completeMissions();
        }

        return String.format("Agent - %s:%s has completed all assigned missions.", name, arguments.get(1));
    }

    @Override
    public String status(List<String> arguments) {

        if (this.noviceAgents.containsKey(arguments.get(1))) {
            return noviceAgents.get(arguments.get(1)).toString().trim();
        }

        if (this.masterAgents.containsKey(arguments.get(1))) {
            return masterAgents.get(arguments.get(1)).toString().trim();
        }

        String resultNovice = missionCheck(this.noviceAgents, arguments.get(1));
        String resultMaster = missionCheck(this.masterAgents, arguments.get(1));

        if (resultNovice != null ) {
            return resultNovice;
        }

        if (resultMaster != null ) {
            return resultMaster;
        }

        return "invalid id";
    }

    @Override
    public String over(List<String> arguments) {

        int assignededMissions = 0;
        int completedMissions = 0;
        double totalRating = 0;
        double totalBounty = 0;

        List<Double> noviceAgentsTotal = agentsCheck(this.noviceAgents);
        List<Double> masterAgentsTotal = agentsCheck(this.masterAgents);

        assignededMissions = (int) Math.floor(noviceAgentsTotal.get(0)) + (int) Math.floor(masterAgentsTotal.get(0));
        completedMissions = (int) Math.floor(noviceAgentsTotal.get(1)) + (int) Math.floor(masterAgentsTotal.get(1));
        totalRating = noviceAgentsTotal.get(2) + masterAgentsTotal.get(2);
        totalBounty = noviceAgentsTotal.get(3) + masterAgentsTotal.get(3);


        StringBuilder sb = new StringBuilder();

        sb
                .append(String.format("Novice Agents: %d", this.noviceAgents.size()))
                .append(System.lineSeparator())
                .append(String.format("Master Agents: %d", this.masterAgents.size()))
                .append(System.lineSeparator())
                .append(String.format("Assigned Missions: %d", this.assignedMissionCount))
                .append(System.lineSeparator())
                .append(String.format("Completed Missions: %d", completedMissions))
                .append(System.lineSeparator())
                .append(String.format("Total Rating Given: %.2f", totalRating))
                .append(System.lineSeparator())
                .append(String.format("Total Bounty Given: $%.2f", totalBounty))
                .append(System.lineSeparator());

        return sb.toString().trim();
    }

    private static String missionCheck(Map<String, BaseAgent> agents, String missionId){


        for (BaseAgent agent : agents.values()) {

            Field getAssignedMissions = null;
            Field getCompletedMissions = null;
            Map<String, Mission> assignedMissions = null;
            Map<String, Mission> completedMissions = null;

            try {
                getAssignedMissions = BaseAgent.class.getDeclaredField("assignedMissions");
                getAssignedMissions.setAccessible(true);
                assignedMissions = (Map<String, Mission>) getAssignedMissions.get(agent);

                getCompletedMissions = BaseAgent.class.getDeclaredField("completedMissions");
                getCompletedMissions.setAccessible(true);
                completedMissions = (Map<String, Mission>) getCompletedMissions.get(agent);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            if (assignedMissions != null && assignedMissions.containsKey(missionId)) {
                return assignedMissions.get(missionId).toString();
            }

            if (completedMissions != null && completedMissions.containsKey(missionId)) {
                return completedMissions.get(missionId).toString();
            }
        }

            return null;
    }




    private static List<Double> agentsCheck(Map<String, BaseAgent> agents){

        double rating = 0;
        double bounty = 0;
        double ass = 0;
        double comp = 0;

        for (BaseAgent agent : agents.values()) {

            rating += agent.getRating();

            if (agent.getClass().getSimpleName().equals("MasterAgent")){
                BountyAgent masterHui = (BountyAgent) agent;
                bounty += masterHui.getBounty();
            }


            Field getAssignedMissions = null;
            Field getCompletedMissions = null;
            Map<String, Mission> assignedMissions = null;
            Map<String, Mission> completedMissions = null;

            try {
                getAssignedMissions = BaseAgent.class.getDeclaredField("assignedMissions");
                getAssignedMissions.setAccessible(true);
                assignedMissions = (Map<String, Mission>) getAssignedMissions.get(agent);

                getCompletedMissions = BaseAgent.class.getDeclaredField("completedMissions");
                getCompletedMissions.setAccessible(true);
                completedMissions = (Map<String, Mission>) getCompletedMissions.get(agent);

                comp += completedMissions.size();
                ass += assignedMissions.size();

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }


        }

        List<Double> result = new ArrayList<>();
        result.add(0, ass);
        result.add(1, comp);
        result.add(2, rating);
        result.add(3, bounty);

        return result;
    }





}
