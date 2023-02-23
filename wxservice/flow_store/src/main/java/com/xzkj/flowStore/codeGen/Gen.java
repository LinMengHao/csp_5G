package com.xzkj.flowStore.codeGen;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @author dashan
 * @date 2018/10/11 下午5:40
 */
public class Gen {

    public static void main(String[] args) {
        String packageName = "com.xzkj.flowStore";
        String dirPath = "";
        generateByTables(dirPath, packageName, "", "user_rights");
    }

    private static void generateByTables(String dirPath, String packageName, String tablePrefix, String... tableNames) {
        GlobalConfig config = new GlobalConfig();
        String dbUrl = "jdbc:mysql://rm-2zeob8sd0a19h5a732o.mysql.rds.aliyuncs.com:3306/vpn?tinyInt1isBit=false&useSSL=true";
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL).setUrl(dbUrl).
                setUsername("fangao_test").
                setPassword("a3HyVIHepanOoLEZ").
                setDriverName("com.mysql.jdbc.Driver");

        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setCapitalMode(true)
                .setRestControllerStyle(true)
                .setEntityLombokModel(false)
                .setTablePrefix("ds_")
//                .setDbColumnUnderline(true)
                .setNaming(NamingStrategy.underline_to_camel)
                .entityTableFieldAnnotationEnable(true)
                .setInclude(tableNames);

        config.setActiveRecord(false)
                .setAuthor("dashan")
                .setOutputDir("/Users/lizhiguo/dashan-project/vpn/src/main/java/")
                .setFileOverride(true)
                .setBaseColumnList(true)
                .setBaseResultMap(true)
                .setOpen(false);

        boolean serviceNameStartWithI = false;
        if (!serviceNameStartWithI) {
            config.setServiceName("%sService");
        }
        new AutoGenerator().setGlobalConfig(config).setDataSource(dataSourceConfig).setStrategy(strategyConfig)
                .setPackageInfo(new PackageConfig().setParent(packageName)
                        .setController("controller")
                        .setEntity("entity")).execute();
    }


}
