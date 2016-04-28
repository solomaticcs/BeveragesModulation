package tony.beveragesmodulation.technicalsubject.preexercise;

/**
 * 前置操作-材料擺放
 */
public class PreExerciseItem {
    private static final String TAG = "PreExerciseItem";
    private int id, idOrder;
    private String groupID, name, ingredientDes, modulationFunctionDes, decorationsDes, cupUtensilsDes,
            materialAreaId, materialArea, decorationAreaId, decorationArea,
            coffeeAreaId, coffeeArea, workAreaId, workArea, mezzanineId, mezzanine,
            finishedAreaId, finishedArea, cupUtensilsId, cupUtensils;

    public PreExerciseItem(int id, int idOrder, String groupID, String name, String ingredientDes, String modulationFunctionDes, String decorationsDes, String cupUtensilsDes, String materialAreaId, String materialArea, String decorationAreaId, String decorationArea, String coffeeAreaId, String coffeeArea, String workAreaId, String workArea, String mezzanineId, String mezzanine, String finishedAreaId, String finishedArea, String cupUtensilsId, String cupUtensils) {
        this.id = id;
        this.idOrder = idOrder;
        this.groupID = groupID;
        this.name = name;
        this.ingredientDes = ingredientDes;
        this.modulationFunctionDes = modulationFunctionDes;
        this.decorationsDes = decorationsDes;
        this.cupUtensilsDes = cupUtensilsDes;
        this.materialAreaId = materialAreaId;
        this.materialArea = materialArea;
        this.decorationAreaId = decorationAreaId;
        this.decorationArea = decorationArea;
        this.coffeeAreaId = coffeeAreaId;
        this.coffeeArea = coffeeArea;
        this.workAreaId = workAreaId;
        this.workArea = workArea;
        this.mezzanineId = mezzanineId;
        this.mezzanine = mezzanine;
        this.finishedAreaId = finishedAreaId;
        this.finishedArea = finishedArea;
        this.cupUtensilsId = cupUtensilsId;
        this.cupUtensils = cupUtensils;
    }

    public int getId() {
        return id;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getName() {
        return name;
    }

    public String getIngredientDes() {
        return ingredientDes;
    }

    public String getModulationFunctionDes() {
        return modulationFunctionDes;
    }

    public String getDecorationsDes() {
        return decorationsDes;
    }

    public String getCupUtensilsDes() {
        return cupUtensilsDes;
    }

    public String getMaterialAreaId() {
        return materialAreaId;
    }

    public String getMaterialArea() {
        return materialArea;
    }

    public String getDecorationAreaId() {
        return decorationAreaId;
    }

    public String getDecorationArea() {
        return decorationArea;
    }

    public String getCoffeeAreaId() {
        return coffeeAreaId;
    }

    public String getCoffeeArea() {
        return coffeeArea;
    }

    public String getWorkAreaId() {
        return workAreaId;
    }

    public String getWorkArea() {
        return workArea;
    }

    public String getMezzanineId() {
        return mezzanineId;
    }

    public String getMezzanine() {
        return mezzanine;
    }

    public String getFinishedAreaId() {
        return finishedAreaId;
    }

    public String getFinishedArea() {
        return finishedArea;
    }

    public String getCupUtensilsId() {
        return cupUtensilsId;
    }

    public String getCupUtensils() {
        return cupUtensils;
    }

    @Override
    public String toString() {
        String str = "編號：%1$d, 群組代號：%2$s, 群組順序：%3$d, 名稱：%4$s, 成分：%5$s, 調製法：%6$s, 裝飾物：%7$s, 杯器皿：%8$s, 材料區對應編號：%9$s, 材料區：%10$s, 裝飾品區對應編號：%11$s, 裝飾品區：%12$s, 義式咖啡區對應編號：%13$s, 義式咖啡區：%14$s, 工作區對應編號：%15$s, 工作區：%16$s, 夾層對應編號：%17$s, 夾層：%18$s, 成品區對應編號：%19$s, 成品區：%20$s, 杯器皿區對應編號：%21$s, 杯器皿區：%22$s";
        return String.format(str, id, groupID, idOrder, name, ingredientDes, modulationFunctionDes, decorationsDes, cupUtensilsDes, materialAreaId, materialArea, decorationAreaId, decorationArea, coffeeAreaId, coffeeArea, workAreaId, workArea, mezzanineId, mezzanine, finishedAreaId, finishedArea, cupUtensilsId, cupUtensils);
    }
}
