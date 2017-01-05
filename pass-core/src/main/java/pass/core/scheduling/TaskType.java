package pass.core.scheduling;

public enum TaskType
{
    EVALUATION("E"),
    CLEANUP("C"),
    ZIP("Z");

    private final String shortName;

    private TaskType(String shortName)
    {
        this.shortName = shortName;
    }

    public String getShortName()
    {
        return shortName;
    }
}
