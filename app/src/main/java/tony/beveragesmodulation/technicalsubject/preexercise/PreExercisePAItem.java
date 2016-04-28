package tony.beveragesmodulation.technicalsubject.preexercise;

import tony.beveragesmodulation.technicalsubject.publicarea.PublicAreaItem;

/**
 * 前置操作-公共材料區項目(增加是否點選)
 */
public class PreExercisePAItem extends PublicAreaItem {

    private boolean isChecked = false; // 是否點選
    private boolean isCorrectAns = false; // 是否為正確答案
    private int placeAreaId = -1; // 0: 材料區 1: 裝飾物區 2:義式咖啡機 3:工作區 4:夾層 5:成品區 6:器皿區
    private boolean isCorrectPlaceArea = false; // 是否放置於正確位置

    public PreExercisePAItem(int id, int groupID, String groupName, String itemName, String useFunction, String applicableTopicDescription, String imgName) {
        super(id, groupID, groupName, itemName, useFunction, applicableTopicDescription, imgName);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isCorrectAns() {
        return isCorrectAns;
    }

    public void setIsCorrectAns(boolean isCorrectAns) {
        this.isCorrectAns = isCorrectAns;
    }

    public int getPlaceAreaId() {
        return placeAreaId;
    }

    public void setPlaceAreaId(int placeAreaId) {
        this.placeAreaId = placeAreaId;
    }

    public boolean isCorrectPlaceArea() {
        return isCorrectPlaceArea;
    }

    public void setIsCorrectPlaceArea(boolean isCorrectPlaceArea) {
        this.isCorrectPlaceArea = isCorrectPlaceArea;
    }

    @Override
    public String toString() {
        return super.toString() + ", 是否點選：" + isChecked + ", 是否為正確答案：" + isCorrectAns + ", 放置區域編號：" + placeAreaId + ", 是否放置於正確位置：" + isCorrectPlaceArea;
    }
}
