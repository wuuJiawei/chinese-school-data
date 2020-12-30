package xyz.easyboot;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wujiawei
 * @see
 * @since 2020/12/30 上午8:59
 */
public class DbRunner {
    
    public void JSONToDb(String inputPath) throws SQLException {
        String jsonPath = inputPath + "/学校.json";
        String jsonString = FileUtil.readString(jsonPath, CharsetUtil.defaultCharset());
        JSONArray jsonArray = JSONUtil.parseArray(jsonString);
        
        Db db = Db.use();
        List<String> errorNames = new ArrayList<>();
        
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String location = jsonObject.getStr("location");
            String[] locationArr = StrUtil.split(location, ",");
            
            int rows = db.insert(Entity.create("saas_school")
                    .set("name", jsonObject.getStr("name"))
                    .set("province", jsonObject.getStr("pname"))
                    .set("city", jsonObject.getStr("cityname"))
                    .set("area", jsonObject.getStr("adname"))
                    .set("address", jsonObject.getStr("address"))
                    .set("lat", locationArr[1])
                    .set("lng", locationArr[0])
                    .set("level", jsonObject.getStr("type"))
            );
            if (rows == 0) {
                errorNames.add(jsonObject.getStr("name"));
            }
        }
        
        errorNames.forEach(System.out::println);
    }
    
}
