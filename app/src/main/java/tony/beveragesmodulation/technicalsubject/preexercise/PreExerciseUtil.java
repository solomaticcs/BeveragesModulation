package tony.beveragesmodulation.technicalsubject.preexercise;

import java.util.ArrayList;

import tony.beveragesmodulation.MainApp;

public class PreExerciseUtil {

    private static PreExerciseService mService = null;

    private static PreExerciseReceiver myReceiver = null;

    /**
     * 取得公共材料區(PEPA)的所有項目 (隨機)
     */
    public static ArrayList<PreExercisePAItem> getAllAMGroupItems() {
        ArrayList<PreExercisePAItem> paItems = new ArrayList<>();
        for(int i = 1; i <= 9;i ++) {
            ArrayList<PreExercisePAItem> preExercisePAItems = MainApp.getPEPAArrayList(i);
            paItems.addAll(preExercisePAItems);
        }
        return paItems;
    }

    /**
     * 取得公共材料區(PEPA)的所有項目 (根據群組編號、順序)
     * (只有該類別儲存後才會出現)
     */
    public static ArrayList<PreExercisePAItem> getAllAMGroupItems(String tgGroupID, int orderID) {
        ArrayList<PreExercisePAItem> paItems = new ArrayList<>();
        for(int i = 1; i <= 9;i ++) {
            ArrayList<PreExercisePAItem> preExercisePAItems = MainApp.getPEPAArrayList(tgGroupID, orderID, i);
            paItems.addAll(preExercisePAItems);
        }
        return paItems;
    }

    /**
     * 取得公共材料區(PEPA)被選取的項目
     */
    public static ArrayList<PreExercisePAItem> getBeChoicePEPAItems(ArrayList<PreExercisePAItem> paItemArrayList) {
        ArrayList<PreExercisePAItem> beChoicePEPAItems = new ArrayList<>();
        for(int i = 0; i < paItemArrayList.size(); i++) {
            PreExercisePAItem preExercisePAItem = paItemArrayList.get(i);
            if(preExercisePAItem.isChecked()) {
                beChoicePEPAItems.add(preExercisePAItem);
            }
        }
        return beChoicePEPAItems;
    }

    /**
     * 取得每個項目的Ids，使用","做切割，回傳int陣列
     */
    public static int[] getAreaIds(String str) {
        if(str.equals("")) {
            return new int[0];
        } else {
            String[] strs = str.split(",");
            int[] ints = new int[strs.length];
            for(int i = 0; i < strs.length; i++) {
                ints[i] = Integer.valueOf(strs[i]);
            }
            return ints;
        }
    }

    /**
     * 加入Ids陣列
     */
    public static void addAreaIdsToArrayList(ArrayList<Integer> integerArrayList, int[]... lotOfInts) {
        for(int[] ints : lotOfInts) {
            for (int j : ints) {
                // 如果不重複再加入
                if (!checkAreaIdsRepeat(integerArrayList, j)) {
                    integerArrayList.add(j);
                }
            }
        }
    }

    /**
     * 確認preExerciseItemIds是否有重複的ID
     */
    private static boolean checkAreaIdsRepeat(ArrayList<Integer> arrayList, int n) {
        for(int i=0;i<arrayList.size();i++) {
            if(arrayList.get(i) == n) {
                return true;
            }
        }
        return false;
    }

    public static PreExerciseService getTimerService() {
        return mService;
    }

    public static void setTimerService(PreExerciseService mService) {
        PreExerciseUtil.mService = mService;
    }

    public static PreExerciseReceiver getMyReceiver() {
        return myReceiver;
    }

    public static void setMyReceiver(PreExerciseReceiver myReceiver) {
        PreExerciseUtil.myReceiver = myReceiver;
    }

    /**
     * 刷新隨機上的項目選擇紀錄與需要材料與擺放位置紀錄
     */
    public static void refreshRandomRecord() {
        ArrayList<PreExercisePAItem> arrayList = new ArrayList<>();
        for(int i = 1; i <= 9; i++) {
            MainApp.editPEPAArrayList(i, arrayList);
        }
        MainApp.editNeedPEPAList("");
        MainApp.editPlaceList("");
    }

    /**
     * 當完成題目時，用來清除item的項目
     * 1. 選擇那些材料
     * 2. 需要的材料
     * 3. 需要的材料放置的位置
     */

    public static void clearItemList() {
        ArrayList<PreExercisePAItem> arrayList = new ArrayList<>();
        for(int i = 1; i <= 9; i++) {
            MainApp.editPEPAArrayList(i, arrayList);
        }
        MainApp.editNeedPEPAList("");
        MainApp.editPlaceList("");
    }

    public static void clearItemList(String tgGroupID, int orderOrId) {
        ArrayList<PreExercisePAItem> arrayList = new ArrayList<>();
        for(int i = 1; i <= 9; i++) {
            MainApp.editPEPAArrayList(tgGroupID, orderOrId, i, arrayList);
        }
        MainApp.editNeedPEPAList(tgGroupID, orderOrId, "");
        MainApp.editPlaceList(tgGroupID, orderOrId, "");
    }

}
