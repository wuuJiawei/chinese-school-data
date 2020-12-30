package xyz.easyboot;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全国幼儿园、小学、中学、职业学校、大学数据爬虫
 * @author wujiawei
 * @see
 * @since 2020/12/30 上午8:39
 */
public class SchoolSpider {
    
    private static final Log log = LogFactory.get();
    
    /**
     * 读取城市编码
     * @return
     */
    public List<String> getCityCode() {
        List<String> list = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceUtil.getStream("citycode.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                if (StrUtil.isEmpty(line) || list.contains(line)) {
                    continue;
                }
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * 执行
     * @param ak 高德地图AK
     * @param outputPath 数据文件生成路径
     */
    public void execute(String ak, String outputPath) {
        String[] types = new String[] {"幼儿园", "小学", "中学", "职业技术学校", "高等院校"};
        int page = 1;
        int offset = 20;
        String outputFilePath = outputPath + "/学校.json";
        String progressFilePath = outputPath + "/progress.txt";
        int totalSize = 0;
        boolean firstTime = true;
        
        FileUtil.touch(progressFilePath);
        FileUtil.touch(outputFilePath);
        FileUtil.writeString("[", outputFilePath, CharsetUtil.defaultCharset());
        
        List<String> cityCodes = getCityCode();
        log.info("共{}个地区", cityCodes.size());
        
        for (String type : types) {
            for (String area : cityCodes) {
                log.info(">>>>>>>>>>>>> [{}] [{}] <<<<<<<<<<<<<", type, area);
                boolean goNext;
                int areaCount = 0;
                do {
                    Map<String, Object> params = new HashMap<>();
                    params.put("key", ak);
                    params.put("types", type);
                    params.put("city", area);
                    params.put("children", "1");
                    params.put("offset", offset + "");
                    params.put("page", page + "");
                    
                    String resp = HttpUtil.get("https://restapi.amap.com/v3/place/text", params);
                    log.info(resp);
                    JSONObject respJSONObj = JSONUtil.parseObj(resp);
                    if (respJSONObj.getJSONArray("pois").size() == 0) {
                        goNext = false;
                        page = 1;
                    } else {
                        JSONArray poiJSONArray = respJSONObj.getJSONArray("pois");
                        for (int j = 0; j < poiJSONArray.size(); j++) {
                            JSONObject dataObj = poiJSONArray.getJSONObject(j);
                            
                            Dict dict = new Dict();
                            dict.put("name", dataObj.getStr("name"));
                            dict.put("cityname", dataObj.getStr("cityname"));
                            dict.put("pname", dataObj.getStr("pname"));
                            dict.put("adname", dataObj.getStr("adname"));
                            dict.put("address", dataObj.getStr("address"));
                            dict.put("location", dataObj.getStr("location"));
                            dict.put("type", type);
                            
                            if (firstTime) {
                                firstTime = false;
                            } else {
                                FileUtil.appendString(",", outputFilePath, CharsetUtil.defaultCharset());
                            }
                            
                            FileUtil.appendString(JSONUtil.toJsonStr(dict), outputFilePath,
                                    CharsetUtil.defaultCharset());
                            
                            totalSize++;
                            areaCount++;
                        }
                        
                        FileUtil.appendString(type + "\t" + area + "\t" + page + "\n", progressFilePath, CharsetUtil.defaultCharset());
                        
                        goNext = true;
                        page++;
                    }
                    
                    // 挂起20毫秒，保证调用频率不超过限额
                    ThreadUtil.sleep(20);
                } while (goNext);
                
                FileUtil.appendString("====== " + type + "\t" + area + "\t总计:" + areaCount + "======\n", progressFilePath, CharsetUtil.defaultCharset());
            }
        }
    
        FileUtil.appendString("]", outputFilePath, CharsetUtil.defaultCharset());
        log.info("总计: {}", totalSize);
    }
    
}
