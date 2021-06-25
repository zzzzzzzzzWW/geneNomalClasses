package demo.util;

/**
 * 对应的jdbctype
 *
 * */
public class JdbcTypeUtil {
    public static String getJdbcType(String type) {
        String str = "";
        switch (type.toUpperCase()) {
            case "DECIMAL":
                str = "DECIMAL";
                break;
            case "NUMERIC":
                str = "DECIMAL";
                break;
            case "TINYINT":
                str = "TINYINT";
                break;
            case "INT":
                str = "INTEGER";
                break;
            case "FLOAT":
                str = "FLOAT";
                break;
            case "DOUBLE":
                str = "DOUBLE";
                break;
            case "NULL":
                str = "NULL";
                break;
            case "TIMESTAMP":
                str = "TIMESTAMP";
                break;
            case "BIGINT":
                str = "BIGINT";
                break;
            case "MEDIUMINT":
                str = "";
                break;
            case "DATE":
                str = "DATE";
                break;
            case "TIME":
                str = "TIME";
                break;
            case "DATETIME":
                str = "DATE";
                break;
            case "YEAR":
                str = "";
                break;
            case "VARCHAR":
                str = "VARCHAR";
                break;
            case "BIT":
                str = "BIT";
                break;
            case "JSON":
                str = "";
                break;
            case "ENUM":
                str = "";
                break;
            case "SET":
                str = "";
                break;
            case "TINYBLOB":
                str = "";
                break;
            case "MEDIUMBLOB":
                str = "";
                break;
            case "LONGBLOB":
                str = "";
                break;
            case "BLOB":
                str = "BLOB";
                break;
            case "TEXT":
                str = "LONGNVARCHAR";
                break;
            case "VARBINARY":
                str = "VARBINARY";
                break;
            case "BINARY":
                str = "BINARY";
                break;
            case "CHAR":
                str = "CHAR";
                break;
            case "GEOMETRY":
                str = "";
                break;
            case "UNKNOWN":
                str = "";
                break;
            case "INT2":
                str = "INTEGER";
                break;
            case "INT4":
                str = "INTEGER";
                break;
            case "FLOAT8":
                str = "DOUBLE";
                break;
        }
        return str;
    }
}
