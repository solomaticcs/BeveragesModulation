package tony.beveragesmodulation;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import tony.beveragesmodulation.db.DatabaseDAO;
import tony.beveragesmodulation.sciencesubject.TopicItem;
import tony.beveragesmodulation.technicalsubject.drinkrecipe.DrinkRecipeItem;
import tony.beveragesmodulation.technicalsubject.preexercise.PreExerciseItem;
import tony.beveragesmodulation.technicalsubject.preexercise.PreExercisePAItem;

public class MainApp extends Application {

    private static final String TAG = "MainApp";
    private static Context cxt;
    //-----Shared Preference-----
    private static SharedPreferences sharedPreferences;
    // Data Name
    private static final String data = "DATA";
    // 使用者名稱
    private static final String USERNAME = "username";
    //「模擬測驗」狀態 -1:尚未開始 (0: 尚未完成 1~7關)
    private static final String TG_MOCK_STATUS = "tg_mock_status";
    // 第一～第七關的 ArrayList<TopicItem>
    private static final String TG_MOCK_GROUP_ARRAYLIST = "tg_mock_group_arraylist";
    // 依飲調群組代號的該組通過杯數情形
    private static final String PE_GROUP_STATUS = "pe_group_status";
    // 依飲調群組代號、第幾個項目、群組編號的PreExercisePAItem
    private static final String PE_PA_ARRAYLIST = "pe_pa_arraylist";
    // 前置操作於公共材料區的擺放位置的PreExerciseItem
    private static final String PE_OBJECT = "pe_object";
    // 前置操作於公共材料區需要的材料(尚未擺放)
    private static final String PE_PA_NEED_ARRAYLIST = "pe_pa_need_arraylist";
    // 前置操作於公共材料區需要的材料(已放置)
    private static final String PE_PA_PLACE_ARRAYLIST = "pe_pa_need_place_arraylist";
    //------------------------------------------

    // 取得使用者名稱
    public static String getUserNameField() {
        return sharedPreferences.getString(USERNAME, "匿名");
    }

    // 編輯使用者名稱
    public static void editUserNameField(String username) {
        sharedPreferences.edit().putString(USERNAME, username).apply();
    }

    // 取得模擬考通過幾關，如果無法取得status，就回傳-1，表示測驗尚未開始
    public static int getTgMockStatusField() {
        return sharedPreferences.getInt(TG_MOCK_STATUS, -1);
    }

    public static void editTgMockStatusField(int status) {
        sharedPreferences.edit().putInt(TG_MOCK_STATUS,status).apply();
    }

    // 依照群組ID取得該群組TopicItems
    public static ArrayList<TopicItem> getTgMockGroupArrayList(int mockGroupId) {
        ArrayList<TopicItem> arrayList;
        Gson gson = new Gson();
        String json = sharedPreferences.getString(TG_MOCK_GROUP_ARRAYLIST + "_" + mockGroupId, "");
        if(json.isEmpty()) {
            arrayList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<TopicItem>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
        }
        return arrayList;
    }

    // 依照群組ID與給予ArrayList陣列轉為json儲存至SharedPreferences，或是直接以string儲存
    public static void editTgMockGroupArrayList(int mockGroupId, ArrayList<TopicItem> arrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        sharedPreferences.edit()
                .putString(TG_MOCK_GROUP_ARRAYLIST + "_" + mockGroupId, json).apply();
    }
    public static void editTgMockGroupArrayList(int mockGroupId, String str) {
        sharedPreferences.edit()
                .putString(TG_MOCK_GROUP_ARRAYLIST + "_" + mockGroupId, str).apply();
    }

    // 取得前置操作練習的某個群組的進度（幾杯），-1表示還沒開始
    public static int getPeGroupStatusField(String tgGroupId) {
        return sharedPreferences.getInt(PE_GROUP_STATUS + "_" + tgGroupId, -1);
    }

    // 編輯前置操作練習的某個群組的通過杯數
    public static void editPeGroupStatusField(String tgGroupId, int status) {
        sharedPreferences.edit().putInt(PE_GROUP_STATUS + "_" + tgGroupId, status).apply();
    }

    /**
     * 取得飲品配方擺放的位置(群組類別位置) or (指定ID)
     */
    public static PreExerciseItem getPreExerciseItem(String tgGroupId, int orderOrId) {
        Gson gson = new Gson();
        PreExerciseItem preExerciseItem = gson.fromJson(sharedPreferences.getString(PE_OBJECT + "_" + tgGroupId + "_" + orderOrId, ""), PreExerciseItem.class);
        return preExerciseItem;
    }
    public static void editPreExerciseItem(String tgGroupId, int orderOrId, PreExerciseItem preExerciseItem) {
        Gson gson = new Gson();
        String json = gson.toJson(preExerciseItem);
        sharedPreferences.edit()
                .putString(PE_OBJECT + "_" + tgGroupId + "_" + orderOrId, json).apply();
    }
    public static void editPreExerciseItem(String tgGroupId, int orderOrId, String str) {
        sharedPreferences.edit()
                .putString(PE_OBJECT + "_" + tgGroupId + "_" + orderOrId, str).apply();
    }

    /**
     * 取得飲品配方擺放的位置(隨機)
     */
    public static PreExerciseItem getPreExerciseItem() {
        Gson gson = new Gson();
        PreExerciseItem preExerciseItem = gson.fromJson(
                sharedPreferences.getString(PE_OBJECT + "_RANDOM", ""), PreExerciseItem.class);
        return preExerciseItem;
    }
    public static void editPreExerciseItem(PreExerciseItem preExerciseItem) {
        Gson gson = new Gson();
        String json = gson.toJson(preExerciseItem);
        sharedPreferences.edit()
                .putString(PE_OBJECT + "_RANDOM", json).apply();
    }

    /**
     * 儲存該項需要材料
     */
    public static ArrayList<PreExercisePAItem> getNeedPEPAList(String tgGroupId, int orderOrId) {
        ArrayList<PreExercisePAItem> arrayList;
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PE_PA_NEED_ARRAYLIST + "_" + tgGroupId + "_" + orderOrId, "");
        if(json.isEmpty()) {
            arrayList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<PreExercisePAItem>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
        }
        return arrayList;
    }

    public static void editNeedPEPAList(String tgGroupId, int orderOrId, ArrayList<PreExercisePAItem> arrayList) {
        Log.e(TAG, "editNeedPEPAList");
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        sharedPreferences.edit()
                .putString(PE_PA_NEED_ARRAYLIST + "_" + tgGroupId + "_" + orderOrId, json).apply();
    }

    public static void editNeedPEPAList(String tgGroupId, int orderOrId, String str) {
        Log.e(TAG, "editNeedPEPAList");
        sharedPreferences.edit()
                .putString(PE_PA_NEED_ARRAYLIST + "_" + tgGroupId + "_" + orderOrId, str).apply();
    }

    public static ArrayList<PreExercisePAItem> getNeedPEPAList() {
        ArrayList<PreExercisePAItem> arrayList;
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PE_PA_NEED_ARRAYLIST, "");
        if(json.isEmpty()) {
            arrayList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<PreExercisePAItem>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
        }
        return arrayList;
    }

    public static void editNeedPEPAList(ArrayList<PreExercisePAItem> arrayList) {
        Log.e(TAG, "editNeedPEPAList");
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        sharedPreferences.edit()
                .putString(PE_PA_NEED_ARRAYLIST, json).apply();
    }

    public static void editNeedPEPAList(String str) {
        sharedPreferences.edit()
                .putString(PE_PA_NEED_ARRAYLIST, str).apply();
    }

    /**
     * place列表
     */

    public static ArrayList<PreExercisePAItem> getPlaceList(String tgGroupId, int orderOrId) {
        ArrayList<PreExercisePAItem> arrayList;
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PE_PA_PLACE_ARRAYLIST + "_" + tgGroupId + "_" + orderOrId, "");
        if(json.isEmpty()) {
            arrayList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<PreExercisePAItem>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
        }
        return arrayList;
    }

    public static void editPlaceList(String tgGroupId, int orderOrId, ArrayList<PreExercisePAItem> arrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        sharedPreferences.edit()
                .putString(PE_PA_PLACE_ARRAYLIST + "_" + tgGroupId + "_" + orderOrId, json).apply();
    }

    public static void editPlaceList(String tgGroupId, int orderOrId, String str) {
        sharedPreferences.edit()
                .putString(PE_PA_PLACE_ARRAYLIST + "_" + tgGroupId + "_" + orderOrId, str).apply();
    }

    public static ArrayList<PreExercisePAItem> getPlaceList() {
        ArrayList<PreExercisePAItem> arrayList;
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PE_PA_PLACE_ARRAYLIST, "");
        if(json.isEmpty()) {
            arrayList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<PreExercisePAItem>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
        }
        return arrayList;
    }

    public static void editPlaceList(ArrayList<PreExercisePAItem> arrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        sharedPreferences.edit()
                .putString(PE_PA_PLACE_ARRAYLIST, json).apply();
    }

    public static void editPlaceList(String str) {
        sharedPreferences.edit()
                .putString(PE_PA_PLACE_ARRAYLIST, str).apply();
    }

    /**
     * 根據"飲調題組" (A1 , A2, ... C18)
     * "第幾杯" (6杯 1~6)
     * "材料題組代號" (1, 2, 3 ,4 ,5 , ... 9)
     * 取得已經選取的前置-公共材料區資料
     */
    public static ArrayList<PreExercisePAItem> getPEPAArrayList(String tgGroupId, int orderOrId, int amGroupId) {
        ArrayList<PreExercisePAItem> arrayList;
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PE_PA_ARRAYLIST + "_" + tgGroupId + "_" + orderOrId + "_" + amGroupId, "");
        if(json.isEmpty()) {
            arrayList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<PreExercisePAItem>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
        }
        return arrayList;
    }
    public static void editPEPAArrayList(String tgGroupId, int orderOrId, int amGroupId, ArrayList<PreExercisePAItem> arrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        sharedPreferences.edit()
                .putString(PE_PA_ARRAYLIST + "_" + tgGroupId + "_" + orderOrId + "_" + amGroupId, json).apply();
    }
    public static void editPEPAArrayList(String tgGroupId, int orderOrId, int amGroupId, String str) {
        sharedPreferences.edit()
                .putString(PE_PA_ARRAYLIST + "_" + tgGroupId + "_" + orderOrId + "_" + amGroupId, str).apply();
    }

    /**
     * 取得隨機題組選擇的前置-公共材料
     */
    public static ArrayList<PreExercisePAItem> getPEPAArrayList(int amGroupId) {
        ArrayList<PreExercisePAItem> arrayList;
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PE_PA_ARRAYLIST + "_" + amGroupId, "");
        if(json.isEmpty()) {
            arrayList = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<PreExercisePAItem>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
        }
        return arrayList;
    }
    public static void editPEPAArrayList(int amGroupId, ArrayList<PreExercisePAItem> arrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        sharedPreferences.edit()
                .putString(PE_PA_ARRAYLIST + "_" + amGroupId, json).apply();
    }

    private static DatabaseDAO databaseDAO;

    public static DatabaseDAO getDatabaseDAO() {
        return databaseDAO;
    }

    public static DrinkRecipeItem drinkRecipeItem = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // Context
        cxt = getApplicationContext();
        // Initialize SharedPreference
        sharedPreferences = cxt.getSharedPreferences(data,0);
        // Initialize Database
        databaseDAO = new DatabaseDAO(getApplicationContext());
        databaseDAO.copyToDatabasesFile();
        databaseDAO.OpenDB();
    }


}
