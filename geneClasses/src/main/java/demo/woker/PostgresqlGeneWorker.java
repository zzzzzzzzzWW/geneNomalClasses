package demo.woker;

import demo.domain.FieldVO;
import demo.util.CommonUtil;
import demo.util.JavaTypeUtil;
import demo.util.JdbcTypeUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PostgresqlGeneWorker extends GeneWorker {

    private static String driver = "com.pivotal.jdbc.GreenplumDriver";

    private static String sqlForColumns =
            "SELECT a.attnum," +
                    "a.attname AS Field," +
                    "t.typname AS Type," +
                    "a.attlen AS length," +
                    "a.atttypmod AS lengthvar," +
                    "a.attnotnull AS notnull," +
                    "b.description AS Comment " +
                    "FROM pg_class c," +
                    "pg_attribute a " +
                    "LEFT OUTER JOIN pg_description b ON a.attrelid=b.objoid AND a.attnum = b.objsubid," +
                    "pg_type t " +
                    "WHERE c.relname = '#tableName#' " +
                    "and a.attnum > 0 " +
                    "and a.attrelid = c.oid " +
                    "and a.atttypid = t.oid " +
                    "ORDER BY a.attnum ";

    private static  String primaryKeySql = "SELECT pg_attribute.attname AS colname " +
            "FROM pg_constraint " +
            "INNER JOIN pg_class ON pg_constraint.conrelid = pg_class.oid " +
            "INNER JOIN pg_attribute ON pg_attribute.attrelid = pg_class.oid " +
            "AND pg_attribute.attnum = pg_constraint.conkey [ 1 ] " +
            "INNER JOIN pg_type ON pg_type.oid = pg_attribute.atttypid " +
            "WHERE pg_class.relname = '#tableName#' " +
            "AND pg_constraint.contype = 'p' ";

    @Override
    protected void initFiledInfo() throws Exception {
        String url = geneParam.getDatabaseUrl();
        String pwd = geneParam.getDatabasePwd();
        String user = geneParam.getDatabaseUser();
        Class.forName(driver);
        Connection connection = DriverManager.getConnection(url, user, pwd);
        String idKey = getPrimarykey();
        //查出所有字段
        Statement statement = connection.createStatement();
        //结果集元数据
        ResultSet rs = statement.executeQuery(sqlForColumns.replace("#tableName#",geneParam.getDatabasetTableName()));
        while (rs.next()) {

            FieldVO fieldVO = new FieldVO();
            ;
            String fiedlName = rs.getString("Field");
            fieldVO.setColumName(fiedlName);
            fieldVO.setPropertyName(CommonUtil.camerName(fiedlName));
            //这个是mysql datatype
            String type = rs.getString("Type").replaceAll("\\(.*", "");
            String typeTranfer = JavaTypeUtil.tranferForPgSql(type);
            typeTranfer = CommonUtil.toObjName(typeTranfer);
            fieldVO.setJavaType(typeTranfer);
            fieldVO.setJdbcType(JdbcTypeUtil.getJdbcType(type));
            fieldVO.setCommont(rs.getString("Comment")==null?"":rs.getString("Comment"));
            fieldVO.setId(idKey.equals(fiedlName));
            if (JavaTypeUtil.isDate(fieldVO.getJavaType())) {
                //model 还需创建 xxxStart ,xxxEnd
                FieldVO fieldStart = new FieldVO();
                fieldStart.setPropertyName(CommonUtil.camerName(fiedlName) + "Start");
                fieldStart.setCommont(fieldVO.getCommont() + fiedlName +" 开始时间(日期查询用)");
                fieldStart.setJavaType(fieldVO.getJavaType());
                modelFields.add(fieldStart);

                //model 还需创建 xxxStart ,xxxEnd
                FieldVO fieldEnd = new FieldVO();
                fieldEnd.setPropertyName(CommonUtil.camerName(fiedlName) + "End");
                fieldEnd.setCommont(fieldVO.getCommont() + fiedlName +"结束时间(日期查询用)");
                fieldEnd.setJavaType(fieldVO.getJavaType());
                modelFields.add(fieldEnd);
            }
            dtoFields.add(fieldVO);
            voFields.add(fieldVO);
            modelFields.add(fieldVO);
        }
    }

    @Override
    protected  String getPrimarykey() throws Exception {
        String url = geneParam.getDatabaseUrl();
        String pwd = geneParam.getDatabasePwd();
        String user = geneParam.getDatabaseUser();
        Connection connection = DriverManager.getConnection(url, user, pwd);

        //找到主键
        Statement primaryKeyStatement = connection.createStatement();

        ResultSet executeQuery = primaryKeyStatement.executeQuery(primaryKeySql.replace("#tableName#",geneParam.getDatabasetTableName()));
        String idKey = null;
        while (executeQuery.next()) {
            idKey = executeQuery.getString("colname");
            return idKey;
        }
        if (idKey == null) {
            System.out.println("数据库没有主键..");
        }
        return null;
    }

}
