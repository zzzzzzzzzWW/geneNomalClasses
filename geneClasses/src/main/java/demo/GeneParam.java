package demo;

public class GeneParam {

    /**
     * 项目的包结构
     * eg:com.project.demo
     * */
    private String bashdir;
    /**
     * 作者
     * */
    private String author;

    /**
     * 项目描述
     * */
    private String desc;

    /**
     * 生成文件的输出路径
     * */
    private String outputPath;

    /**
     * 是否为web结构 (是否需要生成rest类)
     * */
    private boolean isWeb;

    /**
     * 数据库的表名
     * */
    private String databasetTableName;

    /**
     * 数据库的url
     * */
    private String databaseUrl;

    /**
     * 数据库的用户
     * */
    private String databaseUser;

    /**
     * 数据库的密码
     * */
    private String databasePwd;


    public String getBashdir() {
        return bashdir;
    }

    public void setBashdir(String bashdir) {
        this.bashdir = bashdir;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public boolean isWeb() {
        return isWeb;
    }

    public void setWeb(boolean web) {
        isWeb = web;
    }

    public String getDatabasetTableName() {
        return databasetTableName;
    }

    public void setDatabasetTableName(String databasetTableName) {
        this.databasetTableName = databasetTableName;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    public String getDatabasePwd() {
        return databasePwd;
    }

    public void setDatabasePwd(String databasePwd) {
        this.databasePwd = databasePwd;
    }
}
