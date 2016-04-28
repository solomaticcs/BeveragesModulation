package tony.beveragesmodulation.utils;

public class Util {
    public static final boolean TESTDEBUG = true; //是否為測試狀態?

    /**
     * 將文字以題號做切割
     */
    // 切割『適用題項』的標題, 1. ... 2. ... 3. ...
    public static String getIdCutText(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while(true) {
            i++;
            String key = i + ".";
            String key2 = (i + 1) + ".";
            if(text.contains(key)) { //如果有找到key (ex: 1.xxx or 2.xxx)
                int p1 = text.indexOf(key); //取得該key的位置
                if(text.contains(key2)) {
                    //取得下一個key的位置
                    int p2 = text.indexOf(key2);
                    String str = text.substring(p1,p2);
                    stringBuilder.append(str).append("\n");
                } else {
                    String str = text.substring(p1);
                    stringBuilder.append(str);
                }
            } else {
                break;
            }
        }
        return stringBuilder.toString();
    }

    /**
     *  將文字以頓號切割「、」
     */
    public static String getCutDotText(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] strs = text.split("、");
        for(int i = 0; i<strs.length;i++) {
            stringBuilder.append(strs[i]);
            if(i != strs.length -1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
