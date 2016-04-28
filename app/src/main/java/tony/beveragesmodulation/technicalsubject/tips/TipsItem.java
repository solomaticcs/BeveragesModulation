package tony.beveragesmodulation.technicalsubject.tips;

/**
 * 應考錦囊項目
 */
public class TipsItem {

    private int id;
    private String functionName, tip;

    public TipsItem(int id, String functionName, String tip) {
        this.id = id;
        this.functionName = functionName;
        this.tip = tip;
    }

    public int getId() {
        return id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getTip() {
        return tip;
    }

    @Override
    public String toString() {
        return "TipsItem{" +
                "id=" + id +
                ", functionName='" + functionName + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
