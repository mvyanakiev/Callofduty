package callofduty;

import callofduty.core.MissionManagerImpl;
import callofduty.entities.NoviceAgent;
import callofduty.interfaces.Agent;
import callofduty.interfaces.InputReader;
import callofduty.interfaces.MissionManager;
import callofduty.interfaces.OutputWriter;
import callofduty.io.ConsoleInputReader;
import callofduty.io.ConsoleOutputWriter;

import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IllegalAccessException {
        InputReader reader = new ConsoleInputReader();
        OutputWriter writer = new ConsoleOutputWriter();
        MissionManager missionManager = new MissionManagerImpl();

        String input = reader.readLine();

        while (true) {

            List<String> commands = new LinkedList<>();

            String[] tokens = input.split("\\s+");

            for (String token : tokens) {
                commands.add(token);
            }

            switch (commands.get(0)) {

                case "Agent":
                    writer.println(missionManager.agent(commands));
                    break;

                case "Request":
                    writer.println(missionManager.request(commands));
                    break;

                case "Complete":
                    writer.println(missionManager.complete(commands));
                    break;

                case "Status":
                    writer.println(missionManager.status(commands));
                    break;

                case "Over":
                    writer.print(missionManager.over(commands));
                    return;
                    
                default:
                    System.out.println("invalid command");
                    break;
            }
            input = reader.readLine();
        }
    }
}




