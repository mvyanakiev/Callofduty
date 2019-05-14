package callofduty.domain.missions;


import callofduty.domain.BaseMission;

public class SurveillanceMission extends BaseMission {
    private Double rating;
    private Double bounty;

    public SurveillanceMission(String id, Double rating, Double bounty) {
        super(id);
        this.rating = rating - (rating / 100 * 75);
        this.bounty = bounty * 1.5;
    }

    @Override
    public Double getBounty() {
        return this.bounty;
    }

    @Override
    public Double getRating() {
        return this.rating;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb
                .append(String.format("Surveillance Mission - %s", this.getId()))
                .append(System.lineSeparator())
                .append(super.toString())
                .append(System.lineSeparator())
                .append(String.format("Rating: %.2f", this.rating))
                .append(System.lineSeparator())
                .append(String.format("Bounty: %.2f", this.bounty));

        return sb.toString();
    }
}
