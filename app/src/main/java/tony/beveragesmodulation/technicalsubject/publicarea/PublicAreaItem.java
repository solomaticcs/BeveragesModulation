package tony.beveragesmodulation.technicalsubject.publicarea;

/**
 * 公共材料區Item
 */
public class PublicAreaItem {
    private static final String TAG = "PublicAreaItem";
    private int id,groupID;
    private String groupName,itemName,useFunction,applicableTopicDescription,imgName,imgFullName;
    public PublicAreaItem(int id,int groupID,String groupName,String itemName,String useFunction,
                          String applicableTopicDescription,String imgName) {
        this.id = id;
        this.groupID = groupID;
        this.groupName = groupName;
        this.itemName = itemName;
        this.useFunction = useFunction;
        this.applicableTopicDescription = applicableTopicDescription;
        this.imgFullName = imgName.toLowerCase();
        processImgName(imgName);
    }

    /**
    * 處理圖片名稱(去除逗號與附檔名)
    */
    private void processImgName(String imgName) {
        if(imgName.equals("none")) {
            this.imgName = imgName;
        } else {
            // 切割字串 ex: A101.jpg --(以逗號切格取前面)--> A101 --(轉小寫)--> a101
            this.imgName = imgName.split("\\.")[0].toLowerCase();
        }
    }

    public int getId() {
        return id;
    }

    public int getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getUseFunction() {
        return useFunction;
    }

    public String getApplicableTopicDescription() {
        return applicableTopicDescription;
    }

    public String getImgName() {
        return imgName;
    }

    public String getImgFullName() {
        return imgFullName;
    }

    @Override
    public String toString() {
        String str = "編號: %1$d, 群組編號: %2$d, 群組名稱: %3$s, 項目名稱: %4$s, 用途: %5$s, 適用題項: %6$s, 圖片名稱: %7$s, 圖片完整名稱: %8$s";
        return String.format(str, id, groupID, groupName, itemName, useFunction, applicableTopicDescription, imgName, imgFullName);
    }
}
