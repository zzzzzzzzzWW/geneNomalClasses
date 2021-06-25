package demo.woker;

import demo.domain.FieldVO;
import demo.util.CommonUtil;
import demo.util.JavaTypeUtil;
import demo.util.JdbcTypeUtil;

import java.sql.*;

public class MysqlGeneWorker extends GeneWorker {

    private String driver = "com.mysql.jdbc.Driver";
    private String sqlForColumns = "show full columns from #tableName#";


    @Override
    protected void initFiledInfo() throws Exception {
        String url = geneParam.getDatabaseUrl();
        String pwd = geneParam.getDatabasePwd();
        String user = geneParam.getDatabaseUser();
        Class.forName(driver);
        Connection connection = DriverManager.getConnection(url, user, pwd);

        String idKey = getPrimarykey();

        //结果集元数据
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sqlForColumns.replace("#tableName#",geneParam.getDatabasetTableName()));
        while (rs.next()) {

            FieldVO fieldVO = new FieldVO();
            ;
            String fiedlName = rs.getString("Field");
            fieldVO.setColumName(fiedlName);
            fieldVO.setPropertyName(CommonUtil.camerName(fiedlName));
            //这个是mysql datatype
            String type = rs.getString("Type").replaceAll("\\(.*", "");
            String typeTranfer = JavaTypeUtil.tranferForMysql(type);
            typeTranfer = CommonUtil.toObjName(typeTranfer);
            fieldVO.setJavaType(typeTranfer);
            fieldVO.setJdbcType(JdbcTypeUtil.getJdbcType(type));
            fieldVO.setCommont(rs.getString("Comment"));
            fieldVO.setId(idKey.equals(fiedlName));
            if (JavaTypeUtil.isDate(fieldVO.getJavaType())) {
                //model 还需创建 xxxStart ,xxxEnd
                FieldVO fieldStart = new FieldVO();
                fieldStart.setPropertyName(CommonUtil.camerName(fiedlName) + "Start");
                fieldStart.setCommont(fieldVO.getCommont() + "开始时间(日期查询用)");
                fieldStart.setJavaType(fieldVO.getJavaType());
                modelFields.add(fieldStart);

                //model 还需创建 xxxStart ,xxxEnd
                FieldVO fieldEnd = new FieldVO();
                fieldEnd.setPropertyName(CommonUtil.camerName(fiedlName) + "End");
                fieldEnd.setCommont(fieldVO.getCommont() + "结束时间(日期查询用)");
                fieldEnd.setJavaType(fieldVO.getJavaType());
                modelFields.add(fieldEnd);
            }
            dtoFields.add(fieldVO);
            voFields.add(fieldVO);
            modelFields.add(fieldVO);
        }
    }



    @Override
    protected String getPrimarykey() throws Exception {
        String url = geneParam.getDatabaseUrl();
        String pwd = geneParam.getDatabasePwd();
        String user = geneParam.getDatabaseUser();
        Class.forName(driver);
        Connection connection = DriverManager.getConnection(url, user, pwd);
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet primaryKeys = metaData.getPrimaryKeys(connection.getCatalog().toUpperCase(),

                null, geneParam.getDatabasetTableName().toUpperCase());
        //找到主键
        String idKey = null;
        while (primaryKeys.next()) {
            idKey = primaryKeys.getString("COLUMN_NAME");
            return idKey;
        }
        if (idKey == null) {
//            System.out.println("数据库没有主键..");
//            System.out.println("程序退出...");
        }
        return null;
    }

}
