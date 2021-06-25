package demo.util;

/**
 * mybatis中的javatype
 * */
public class JavaTypeUtil {
    /**
     * 如果支持 null,说明是对象,需将基本数据类型转换为对象类型
     */
    public static String tranferForPgSql(String type) {
        String str = "";
        switch (type.toUpperCase()) {
            case "VARCHAR":
                str = "String";
                break;
            case "BPCHAR":
                str = "String";
                break;
            case "CIDR":
                str = "";
                break;
            case "INET":
                str = "";
                break;
            case "MACADDR":
                str = "";
                break;
            case "TEXT":
                str = "String";
                break;
            case "INT8":
                str = "Long";
                break;
            case "BOX":
                str = "";
                break;
            case "CIRCLE":
                str = "";
                break;
            case "FLOAT8":
                str = "Double";
                break;
            case "INTERVAL":
                str = "";
                break;
            case "LINE":
                str = "";
                break;
            case "LSEG":
                str = "String";
                break;
            case "MONEY":
                str = "Double";
                break;
            case "NUMERIC":
                str = "java.math.BigDecimal";
                break;
            case "PATH":
                str = "";
                break;
            case "POINT":
                str = "";
                break;
            case "POLYGON":
                str = "";
                break;
            case "FLOAT4":
                str = "Float";
                break;
            case "INT2":
                str = "Integer";
                break;
            case "INT4":
                str = "Integer";
                break;
            case "TIME":
                str = "LocalTime";
                break;
            case "TIMESTAMP":
                str = "Date";
                break;
            case "BIT":
                str = "Boolean";
                break;
            case "VARBIT":
                str = "";
                break;
            case "BOOL":
                str = "Boolean";
                break;
        }
        return str;
    }

    /**
     * 如果支持 null,说明是对象,需将基本数据类型转换为对象类型
     */
    public static String tranferForMysql(String type) {
        String str = "";
        switch (type.toUpperCase()) {
            case "DECIMAL":
                str = "java.math.BigDecimal";
                break;
            case "TINYINT":
                str = "int";
                break;
            case "INT":
                str = "int";
                break;
            case "FLOAT":
                str = "float";
                break;
            case "DOUBLE":
                str = "double";
                break;
            case "NULL":
                str = "";
                break;
            case "TIMESTAMP":
                str = "LocalDateTime";
                break;
            case "BIGINT":
                str = "BigInteger";
                break;
            case "MEDIUMINT":
                str = "int";
                break;
            case "DATE":
                str = "LocalDate";
                break;
            case "TIME":
                str = "LocalTime";
                break;
            case "DATETIME":
                str = "LocalDateTime";
                break;
            case "YEAR":
                str = "";
                break;
            case "VARCHAR":
                str = "String";
                break;
            case "BIT":
                str = "Boolean";
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
                str = "byte[]";
                break;
            case "MEDIUMBLOB":
                str = "byte[]";
                break;
            case "LONGBLOB":
                str = "byte[]";
                break;
            case "BLOB":
                str = "byte[]";
                break;
            case "TEXT":
                str = "String";
                break;
            case "VARBINARY":
                str = "String";
                break;
            case "BINARY":
                str = "";
                break;
            case "CHAR":
                str = "String";
                break;
            case "GEOMETRY":
                str = "";
                break;
            case "UNKNOWN":
                str = "";
        }
        return str;
    }

    public static boolean isDate(String fieldType) {
        return fieldType.equals("LocalDateTime") || fieldType.equals("LocalDate") || fieldType.equals("LocalTime") || fieldType.equals("Date");
    }

}
