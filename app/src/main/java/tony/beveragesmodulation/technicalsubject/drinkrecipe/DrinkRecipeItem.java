package tony.beveragesmodulation.technicalsubject.drinkrecipe;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 飲品配方項目
 */
public class DrinkRecipeItem {
    private static final String TAG = "DrinkRecipeItem";
    private Context cxt;
    private int id, idOrder, imageResource;
    private String groupId, name, ingredient, modulation, decorations, cupUtensils, imgName, ingredientIds, modulationIds;

    private static final String NULLSTR = "無";

    public DrinkRecipeItem(Context cxt,int id, String groupId, int idOrder, String name,
                           String ingredientIds, String ingredient,String modulationIds,String modulation,String decorations,String cupUtensils,String imgName) {
        this.cxt = cxt;
        this.id = id;
        this.groupId = check(groupId);
        this.idOrder = idOrder;
        this.name = check(name);
        this.ingredientIds = check(ingredientIds);
        this.ingredient = check(ingredient);
        this.modulationIds = check(modulationIds);
        this.modulation = check(modulation);
        this.decorations = check(decorations);
        this.cupUtensils = check(cupUtensils);
        this.imgName = check(imgName);

        Initialize();
    }

    //檢查是否為空字串，如為空字串則補上文字
    private String check(String s) {
        if(s.trim().equals("")) {
            return NULLSTR;
        }
        return s;
    }

    private void Initialize() {

        /*
            圖片Drawable
         */

        // 切割字串 ex: A101.jpg --(以逗號切格取前面)--> A101 --(轉小寫)--> a101
        String uri = "drawable/" + getImgName().split("\\.")[0].toLowerCase();
        imageResource = cxt.getResources().getIdentifier(uri, null, cxt.getPackageName());
//        Drawable image = cxt.getResources().getDrawable(imageResource);
//        //設定Drawable
//        setDrawable(image);

//        //新增detailMap用來儲存調飲配方的詳細資訊
//        HashMap<String, ArrayList<String>> detailMap = new HashMap<>();
//
//        /*
//        『成份』
//         */
//
//        if(ingredient != null && !ingredient.equals("")) {
//            //切割字串，檢查是否有「、」存在
//            if(ingredient.indexOf("、") != -1) {
//                detailMap.put(KEY_INGREDIENT,
//                        new ArrayList<String>(Arrays.asList(ingredient.split("、"))));
//            } else {
//                detailMap.put(KEY_INGREDIENT,new ArrayList<String>(){{
//                    add(ingredient);
//                }});
//            }
//        } else {
//            detailMap.put(KEY_INGREDIENT,new ArrayList<String>(){{
//                add(NULLSTR);
//            }});
//        }
//
//        /*
//        『調製法』
//         */
//
//        if(modulation != null && !modulation.equals("")) {
//            // 因為調製法只有一項便直接新增
//            detailMap.put(KEY_MODULATION, new ArrayList<String>() {{
//                add(modulation);
//            }});
//        } else {
//            detailMap.put(KEY_MODULATION,new ArrayList<String>(){{
//                add(NULLSTR);
//            }});
//        }
//
//        /*
//        『裝飾物』
//         */
//
//        if(decorations != null && !decorations.equals("")) {
//            //切割字串，檢查是否有「、」存在
//            if(decorations.indexOf("、") != -1) {
//                detailMap.put(KEY_DECORATIONS,
//                        new ArrayList<String>(Arrays.asList(decorations.split("、"))));
//            } else {
//                detailMap.put(KEY_DECORATIONS, new ArrayList<String>(){{
//                    add(decorations);
//                }});
//            }
//        } else {
//            detailMap.put(KEY_DECORATIONS,new ArrayList<String>(){{
//                add(NULLSTR);
//            }});
//        }
//
//        /*
//        『杯器皿』
//         */
//
//        if(cupUtensils != null && !cupUtensils.equals("")) {
//            //切割字串，檢查是否有「/」存在
//            if(cupUtensils.indexOf("/") != -1) {
//                detailMap.put(KEY_CUPUTENSILS,
//                        new ArrayList<String>(Arrays.asList(cupUtensils.split("/"))));
//            } else {
//                detailMap.put(KEY_CUPUTENSILS, new ArrayList<String>(){{
//                    add(cupUtensils);
//                }});
//            }
//        } else {
//            detailMap.put(KEY_CUPUTENSILS,new ArrayList<String>(){{
//                add(NULLSTR);
//            }});
//        }
//        setDetailMap(detailMap);

        Log.i(TAG, "" + toString());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getModulation() {
        return modulation;
    }

    public void setModulation(String modulation) {
        this.modulation = modulation;
    }

    public String getDecorations() {
        return decorations;
    }

    public void setDecorations(String decorations) {
        this.decorations = decorations;
    }

    public String getCupUtensils() {
        return cupUtensils;
    }

    public void setCupUtensils(String cupUtensils) {
        this.cupUtensils = cupUtensils;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public int getImageResource() {
        return imageResource;
    }

    @Override
    public String toString() {
        String str = "編號: %1$s, 類別代號: %2$s, 類別編號: %3$s, 名稱: %4$s, " +
                "成份: %5$s, 調製法: %6$s, 裝飾品: %7$s, 杯器皿: %8$s, 圖片名稱: %9$s。";
//                "成份陣列: %10$s, 裝飾物陣列: %11$s, 杯器皿陣列: %12$s";
        return String.format(str,str(id),groupId,str(idOrder),name,
                ingredient,modulation,decorations,cupUtensils,imgName);
    }

    private String str(Object o) {
        return String.valueOf(o);
    }

    private String arrStr(String[] arr) {
        return Arrays.deepToString(arr);
    }

    private String arrStr(ArrayList<String> arrayList) {
        StringBuilder sb = new StringBuilder();
        for(String s : arrayList) {
            sb.append(s);
            sb.append("\t");
        }
        return sb.toString();
    }

    public String getIngredientIds() {
        return ingredientIds;
    }

    public String getModulationIds() {
        return modulationIds;
    }
}
