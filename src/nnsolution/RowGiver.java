package nnsolution;

public class RowGiver {
    private int index = 0;
    private String[] rows;
    private boolean shouldIGive;

    public RowGiver(String[] rows) {
        this.rows = rows;
        shouldIGive = true;
    }

    public String give() {
        if (shouldIGive)
            return rows[index++];
        else
            return "";
    }

    public void enableGiving() {
        shouldIGive = true;
    }

    public void disableGiving() {
        shouldIGive = false;
    }
}
