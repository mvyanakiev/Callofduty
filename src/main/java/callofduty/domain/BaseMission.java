package callofduty.domain;

import callofduty.interfaces.Mission;

public abstract class BaseMission implements Mission {
    private String id;
    private boolean isOpen;


    protected BaseMission(String id) {
        this.id = id;
        this.isOpen = true;
    }


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {

        String status;
        if (this.isOpen) {
            status = "Open";
        } else {
            status = "Completed";
        }
        return String.format("Status: %s", status);
    }
}
