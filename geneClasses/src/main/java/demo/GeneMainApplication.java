package demo;

import demo.domain.GeneParam;
import demo.woker.GeneWorker;

public class GeneMainApplication {

    public static void main(String[] args) throws Exception {
        GeneParam geneParam = new GeneParam();
        geneParam.setAuthor("zw");
        geneParam.setBashdir("com.piesat.worker.demo");
        geneParam.setDesc("用户关注的站点");
        geneParam.setOutputPath("C:\\Users\\wei\\Desktop\\zw");
        geneParam.setWeb(true);
//        geneParam.setDatabasetTableName("t_main_station");
//        geneParam.setDatabasePwd("123456");
//        geneParam.setDatabaseUrl("jdbc:mysql://192.168.2.62:3306/xxl_job?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowMultiQueries=true");
//        geneParam.setDatabaseUser("root");
//        geneParam.setQueyrUrl("/sys");
        geneParam.setDatabasetTableName("t_health_advice");
        geneParam.setDatabasePwd("gpadmin");
        geneParam.setDatabaseUrl("jdbc:pivotal:greenplum://192.168.1.146:5432;DatabaseName=gpdb");
        geneParam.setDatabaseUser("gpadmin");
        geneParam.setQueyrUrl("/sys");
        GeneWorker worker = GeneWorker.getWorker(GeneWorker.TYPE_PGSQL);
        worker.setGeneParam(geneParam);
        worker.gene();

    }


}

