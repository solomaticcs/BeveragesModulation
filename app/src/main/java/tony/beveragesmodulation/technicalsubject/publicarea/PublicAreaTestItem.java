package tony.beveragesmodulation.technicalsubject.publicarea;

public class PublicAreaTestItem extends PublicAreaItem {
    private static final String TAG = "PublicAreaTestItem";
    private boolean alreadyAnswer = false; //是否已經作答過(預設false)
    private int answerPosition = -1; //放在第幾個答案(預設為0)
    public PublicAreaTestItem(int id, int groupID, String groupName, String itemName, String useFunction, String applicableTopicDescription, String imgName) {
        super(id, groupID, groupName, itemName, useFunction, applicableTopicDescription, imgName);
    }

    public boolean isAlreadyAnswer() {
        return alreadyAnswer;
    }

    public void setAlreadyAnswer(boolean alreadyAnswer) {
        this.alreadyAnswer = alreadyAnswer;
    }

    public int getAnswerPosition() {
        return answerPosition;
    }

    public void setAnswerPosition(int answerPosition) {
        this.answerPosition = answerPosition;
    }

    @Override
    public String toString() {
        return super.toString() + " 是否作答：" + alreadyAnswer + " 答案放置位置：" + answerPosition;
//        return "PublicAreaTestItem{" +
//                "alreadyAnswer=" + alreadyAnswer +
//                ", answerPosition=" + answerPosition +
//                '}';
    }
}
