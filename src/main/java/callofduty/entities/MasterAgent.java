package callofduty.entities;

import callofduty.interfaces.Agent;
import callofduty.interfaces.BountyAgent;
import callofduty.interfaces.Mission;

import java.util.Map;

public class MasterAgent extends BaseAgent implements BountyAgent {
    private double bounty;


    public MasterAgent(String id, String name) {
        super(id, name);
        this.bounty = 0.0;
    }


    @Override
    public Double getBounty() {
        return this.bounty;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(super.toString())
                .append(String.format("Bounty Earned: $%.2f", this.getBounty()));

        return sb.toString();
    }
}
