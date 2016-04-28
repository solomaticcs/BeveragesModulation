package tony.beveragesmodulation.technicalsubject.modulation;

public class ModulationItem {
    private static final String TAG = "ModulationItem";
    private int id;
    private String function, example, videoURL, define;

    public ModulationItem(int id, String function, String example, String videoURL, String define) {
        this.id = id;
        this.function = function;
        this.example = example;
        this.videoURL = videoURL;
        this.define = define;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public int getId() {
        return id;
    }

    public String getFunction() {
        return function;
    }

    public String getDefine() {
        return define;
    }

    public String getExample() {
        return example;
    }

    @Override
    public String toString() {
        String str = "編號: %1$d, 調製法方式: %2$s, 調製法範例: %3$s, 調製法影片網址: %4$s, 定義: %5$s";
        return String.format(str, id, function, example, videoURL, define);
    }
}
