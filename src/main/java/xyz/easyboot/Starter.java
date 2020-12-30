package xyz.easyboot;

import java.sql.SQLException;

/**
 * @author wujiawei
 * @see
 * @since 2020/12/30 上午8:44
 */
public class Starter {
    
    public static void main(String[] args) throws SQLException {
        // 高德AK
        String ak = "***";
        
        // 文件生成路径
        String outputPath = "/Users/wujiawei/Demo/other-data";
        
        // 获取全国院校数据
        SchoolSpider spider = new SchoolSpider();
        spider.execute(ak, outputPath);
        
        // 存储到数据库
//        DbRunner runner = new DbRunner();
//        runner.JSONToDb(outputPath);
    }
    
}
