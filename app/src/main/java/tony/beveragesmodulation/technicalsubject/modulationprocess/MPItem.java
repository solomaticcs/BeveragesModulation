package tony.beveragesmodulation.technicalsubject.modulationprocess;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 調製流程項目
 */
public class MPItem {
    private static final String TAG = "MPItem";
    private int id;
    private String functionName,sampleName, procDescription;
    private ArrayList<HashMap<String,Object>> process;

    public MPItem(int id, String functionName,String sampleName, String procDescription) {
        this.id = id;
        this.functionName = functionName;
        this.sampleName = sampleName;
        this.procDescription = procDescription;
        initialized(procDescription);
    }

    private void initialized(String procDescription) {
        process = new ArrayList<>();
        int i = 0;
        while(true) {
            i++;
            String key = i + ".";
            String key2 = (i + 1) + ".";
            if(procDescription.contains(key)) { //如果有找到key (ex: 1.xxx or 2.xxx)
                int p1 = procDescription.indexOf(key); //取得該key的位置
                String str;
                if(procDescription.contains(key2)) {
                    //取得下一個key的位置
                    int p2 = procDescription.indexOf(key2);
                    str = procDescription.substring(p1,p2);
                } else {
                    str = procDescription.substring(p1);
                }
                String[] strSplit = str.split("\\.");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", Integer.valueOf(strSplit[0]));
                hashMap.put("text", strSplit[1]);
                process.add(hashMap);
            } else {
                break;
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getSampleName() {
        return sampleName;
    }

    public String getProcDescription() {
        return procDescription;
    }

    public ArrayList<HashMap<String, Object>> getProcess() {
        return process;
    }

    @Override
    public String toString() {
        String str = "編號: %1$d, 調製法: %2$s, 範例名稱: %3$s調製流程敘述: %4$s";
        return String.format(str, id, functionName, sampleName, procDescription);
    }
}
