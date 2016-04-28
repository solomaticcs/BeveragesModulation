package tony.beveragesmodulation.sciencesubject;

/**
 *  學科項目
 */
public class TopicItem {
    private static final String TAG = "TopicItem";

    private int id, correctAns, userAns;
    private String question;
    private String[] ans = new String[4];
    private boolean isAlreadyAnswer = false; //是否已經作答

    public TopicItem(int id, int correctAns, String question,String ans1,String ans2,String ans3,String ans4) {
        this.id = id;
        this.correctAns = correctAns;
        this.question = question;
        ans[0] = ans1;
        ans[1] = ans2;
        ans[2] = ans3;
        ans[3] = ans4;
    }

    public int getId() {
        return id;
    }

    public int getCorrectAns() {
        return correctAns;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getAns() {
        return ans;
    }

    @Override
    public String toString() {
//        return super.toString();
        String str = "編號: %1$d, 正確答案編號: %2$d, 題目問題: %3$s, 答案: %4$s, 答案2: %5$s, 答案3: %6$s, 答案4: %7$s, 是否作答: %8$s";
        return String.format(str,id, correctAns,question,ans[0],ans[1],ans[2],ans[3],isAlreadyAnswer);
    }

    public boolean isAlreadyAnswer() {
        return isAlreadyAnswer;
    }

    public void setIsAlreadyAnswer(boolean isAlreadyAnswer) {
        this.isAlreadyAnswer = isAlreadyAnswer;
    }

    public int getUserAns() {
        return userAns;
    }

    public void setUserAns(int userans) {
        this.userAns = userans;
    }
}
