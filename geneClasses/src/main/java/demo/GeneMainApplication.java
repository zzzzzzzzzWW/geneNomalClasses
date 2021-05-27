package demo;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneMainApplication {

    public static ArrayList<FieldVO> dtoFields = new ArrayList<>();
    public static ArrayList<FieldVO> voFields = new ArrayList<>();
    public static ArrayList<FieldVO> modelFields = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        GeneParam geneParam = new GeneParam();
        geneParam.setAuthor("zw");
        geneParam.setBashdir("co.zw.demo");
        geneParam.setDesc("字典信息");
        geneParam.setOutputPath("D:\\tmp\\test");
        geneParam.setWeb(true);
        geneParam.setDatabasetTableName("dic_data");
        geneParam.setDatabasePwd("123456");
        geneParam.setDatabaseUrl("jdbc:mysql://192.168.2.62:3306/test?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowMultiQueries=true");
        geneParam.setDatabaseUser("root");

        initFiledInfo(geneParam);

        initBaseStr(geneParam);

        gene(geneParam);
    }

    /**
     * 生成代码的入口
     */
    public static void gene(GeneParam geneParam) throws Exception {
        //找到模板
        String outputPath = geneParam.getOutputPath();
        URL resource = GeneMainApplication.class.getClassLoader().getResource("");
        String path = resource.getPath();
        String file = path + "/tem";
        String dto = file + "/domain/dto/DTO.tem";
        String vo = file + "/domain/vo/VO.tem";
        String model = file + "/domain/model/Model.tem";
        String service = file + "/service/Service.tem";
        String serviceImpl = file + "/service/impl/ServiceImpl.tem";
        String rest = file + "/rest/Controller.tem";
        String mapper = file + "/mapper/Mapper.tem";
        String pageModel = file + "/domain/model/PageModel.tem";
        String returnObj = file + "/domain/ReturnObj.tem";
        String baseRest = file + "/rest/BaseRest.tem";
        String pageVo = file + "/domain/vo/PageVO.tem";


        String bashDir = varMap.get("#project.bashdir#");
        bashDir = bashDir.replaceAll("\\.", "/");
        geneDomain(dto, "dto", outputPath + "/" + bashDir + "/domain/dto/" + varMap.get("#dto_up#") + ".java");
        geneDomain(model, "model", outputPath + "/" + bashDir + "/domain/model/" + varMap.get("#model_up#") + ".java");
        geneDomain(vo, "vo", outputPath + "/" + bashDir + "/domain/vo/" + varMap.get("#vo_up#") + ".java");
        geneOther(service, outputPath + "/" + bashDir + "/service/" + varMap.get("#service_up#") + ".java", geneParam.isWeb());
        geneOther(serviceImpl, outputPath + "/" + bashDir + "/service/impl/" + varMap.get("#serviceImpl_up#") + ".java", geneParam.isWeb());
        geneOther(pageModel, outputPath + "/" + bashDir + "/domain/model/PageModel.java", geneParam.isWeb());
        if (geneParam.isWeb()) {
            geneOtherIfNotWeb(rest, outputPath + "/" + bashDir + "/rest/" + varMap.get("#controller#") + ".java");
            geneOtherIfNotWeb(returnObj, outputPath + "/" + bashDir + "/domain/ReturnObj.java");
            geneOtherIfNotWeb(baseRest, outputPath + "/" + bashDir + "/rest/BaseRest.java");
            geneOtherIfNotWeb(pageVo, outputPath + "/" + bashDir + "/domain/vo/PageVO.java");
        }
        geneMapper(mapper, outputPath + "/" + bashDir + "/mapper/" + varMap.get("#mapper_up#") + ".java");
    }


    /**
     * 获取表中各个字段
     */
    public static void initFiledInfo(GeneParam geneParam) throws Exception {
        String driver = "com.mysql.jdbc.Driver";
        String url = geneParam.getDatabaseUrl();
        String tableName = geneParam.getDatabasetTableName();
        String pwd = geneParam.getDatabasePwd();
        String user = geneParam.getDatabaseUser();
        Class.forName(driver);
        Connection connection = DriverManager.getConnection(url, user, pwd);
        String sqlForColumns = "show full columns from " + tableName;
        makeupVarMap(connection, sqlForColumns, tableName);
    }

    /**
     * 组装各个变量
     * */
    public static void makeupVarMap(Connection connection, String sql, String tableName) throws Exception {
        Statement statement = connection.createStatement();
        //结果集元数据
        ResultSet rs = statement.executeQuery(sql);
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet primaryKeys = metaData.getPrimaryKeys(connection.getCatalog().toUpperCase(),

                null, tableName.toUpperCase());
        //找到主键
        String idKey = null;
        String idKeyProperty = "";
        String idKeyJavaType = "";
        while (primaryKeys.next()) {
            idKey = primaryKeys.getString("COLUMN_NAME");
        }
        if (idKey == null) {
            System.out.println("数据库没有主键..");
            System.out.println("程序退出...");
        }
        //查出所有字段
        String allFiledStr = "";
        //插入数据时需要
        String insertFiledStr = "";
        StringBuffer insertProperties = new StringBuffer();
        while (rs.next()) {

            FieldVO fieldVO = new GeneMainApplication().new FieldVO();;
            String fiedlName = rs.getString("Field");
            fieldVO.setColumName(fiedlName);
            fieldVO.setPropertyName(camerName(fiedlName));
            //这个是mysql datatype
            String type = rs.getString("Type").replaceAll("\\(.*", "");
            String typeTranfer = jdbcTypeTranfer(type);
            typeTranfer = toObjName(typeTranfer);
            fieldVO.setJavaType(typeTranfer);
            fieldVO.setJdbcType(transferSqlType(type));
            fieldVO.setCommont(rs.getString("Comment"));
            fieldVO.setId(idKey.equals(fiedlName));
            if (isDate(fieldVO.getJavaType())) {
                //model 还需创建 xxxStart ,xxxEnd
                FieldVO fieldStart = new GeneMainApplication().new FieldVO();
                fieldStart.setPropertyName(camerName(fiedlName) + "Start");
                fieldStart.setCommont(fieldVO.getCommont() + "开始时间(日期查询用)");
                fieldStart.setJavaType(fieldVO.getJavaType());
                modelFields.add(fieldStart);

                //model 还需创建 xxxStart ,xxxEnd
                FieldVO fieldEnd = new GeneMainApplication().new FieldVO();
                fieldEnd.setPropertyName(camerName(fiedlName) + "End");
                fieldEnd.setCommont(fieldVO.getCommont() + "结束时间(日期查询用)");
                fieldEnd.setJavaType(fieldVO.getJavaType());
                modelFields.add(fieldEnd);
            }
            dtoFields.add(fieldVO);
            voFields.add(fieldVO);
            modelFields.add(fieldVO);
            allFiledStr += "," + fiedlName;
            if (!fieldVO.isId()) {
                insertFiledStr += "," + fiedlName;
                insertProperties.append(",#{item.");
                insertProperties.append(fieldVO.getPropertyName());
                insertProperties.append("}");
            } else {
                idKeyProperty = camerName(fieldVO.getPropertyName());
                idKeyJavaType = toObjName(fieldVO.getJavaType());
            }
            System.out.println(fieldVO);
        }
        varMap.put("#all_fields#", allFiledStr.replaceFirst(",", ""));
        varMap.put("#idKey_property#", idKeyProperty);
        varMap.put("#idKey_column#", idKey);
        varMap.put("#idKey_javaType#", idKeyJavaType);
        varMap.put("#insert_fields#", insertFiledStr.replaceFirst(",", ""));
        varMap.put("#insert_properties#", insertProperties.toString().replaceFirst(",", ""));
    }


    //z换换为
    private static String transferSqlType(String type) {
        String str = "";
        switch (type.toUpperCase()) {
            case "DECIMAL":
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
        }
        return str;
    }


    /**
     * 如果支持 null,说明是对象,需将基本数据类型转换为对象类型
     */
    private static String jdbcTypeTranfer(String type) {
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

    private static String toObjName(String str) {
        switch (str) {
            case "int":
                str = "Integer";
                break;
            case "float":
                str = "Float";
                break;
            case "byte":
                str = "Byte";
                break;
            case "boolean":
                str = "Boolean";
                break;
            case "char":
                str = "Char";
                break;
            case "short":
                str = "Short";
                break;
            case "long":
                str = "Long";
                break;
            case "double":
                str = "Double";
                break;
        }
        return str;
    }


    //生成mapper
    private static void geneMapper(String filepath, String outputPath) throws Exception {
        FileReader fileReader = new FileReader(filepath);
        BufferedReader br = new BufferedReader(fileReader);
        List<String> collect = br.lines().collect(Collectors.toList());
        List<FieldVO> fields = dtoFields;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < collect.size(); i++) {
            String s = collect.get(i);
            //result 填充
            if (s.trim().equals("#result_start#")) {
                //从下一行开始
                i++;
                s = collect.get(i);
                for (int i1 = 0; i1 < fields.size(); i1++) {
                    FieldVO field = fields.get(i1);
                    String jdbcType = field.getJdbcType();
                    String javaType = field.getJavaType();
                    String propertyName = field.getPropertyName();
                    String columName = field.getColumName();
                    //填充每个字段
                    String replace = "";
                    replace = s.replace("#propertyName#", propertyName).replace("#columName#", columName)
                            .replace("#javaType#", javaType).replace("#jdbcType#", jdbcType);
                    if (field.isId()) {
                        replace = replace.replace("#{id}", ",id=true");
                    } else {
                        replace = replace.replace("#{id}", "");
                    }
                    sb.append(replace);
                    sb.append("\r");
                    sb.append("\n");
                }
                //跳过 "#result_end#
                i++;
                continue;
            }
            //查询 条件 填充
            if (s.trim().equals("#condition_start#")) {
                //从下一行开始
                ++i;
                s = collect.get(i);
                for (int i1 = 0; i1 < fields.size(); i1++) {
                    FieldVO field = fields.get(i1);
                    if (field.isId()) {
                        //id 字段不参与
                        continue;
                    }
                    String javaType = field.getJavaType();
                    String propertyName = field.getPropertyName();
                    String columName = field.getColumName();
                    String replace = "";
                    if (isStr(javaType)) {
                        replace = s.replace("#propertyName#", propertyName).replace("#columName#", columName);
                        //and dto.#propertyName#.trim()!=\"\"
                        replace = replace.replace("#str_condition#", "and dto." + propertyName + "!=\\\"\\\"");
                    } else {
                        replace = s.replace("#propertyName#", propertyName).replace("#columName#", columName).replace("#str_condition#", "");
                    }
                    sb.append(replace);
                    sb.append("\r");
                    sb.append("\n");
                }
                //跳过 "#result_end#
                i++;
                continue;
            }
            //查询 日期条件 填充
            if (s.trim().equals("#condition_date_start#")) {
                //从下一行开始
                ++i;
                s = collect.get(i);
                for (int i1 = 0; i1 < fields.size(); i1++) {
                    FieldVO field = fields.get(i1);
                    if (field.isId()) {
                        //id 字段不参与
                        continue;
                    }
                    String javaType = field.getJavaType();
                    String propertyName = field.getPropertyName();
                    String columName = field.getColumName();
                    String replace = "";
                    if (isDate(javaType)) {
                        replace = s.replace("#propertyName#", propertyName).replace("#columName#", columName);
                        //"<if test='dto.#startDate#!=null and dto.#endDate#!=null'> and #columName#=between #{dto.#startDate#} and #{dto.#endDate#}}</if>" +
                        replace = replace.replace("#startDate#", propertyName+"Start").replace("#endDate#",propertyName+"End").replace("#columName#", columName);
                        sb.append(replace);
                        sb.append("\r");
                        sb.append("\n");
                    }
                }
                //跳过 "#result_end#
                i++;
                continue;
            }

            //update 条件 填充
            if (s.trim().equals("#condition_update_start#")) {
                //从下一行开始
                ++i;
                s = collect.get(i);
                for (int i1 = 0; i1 < fields.size(); i1++) {
                    FieldVO field = fields.get(i1);
                    if (field.isId()) {
                        //id 字段不参与
                        continue;
                    }
                    String javaType = field.getJavaType();
                    String propertyName = field.getPropertyName();
                    String columName = field.getColumName();
                    String replace = "";
                    replace = s.replace("#propertyName#", propertyName).replace("#columName#", columName);
                    if (isStr(javaType)) {
                        //and dto.#propertyName#.trim()!=\"\"
                        replace = replace.replace("#str_condition#", "and dto." + propertyName + ".trim()!=\\\"\\\"");
                    } else {
                        replace = replace.replace("#str_condition#", "");
                    }
                    sb.append(replace);
                    sb.append("\r");
                    sb.append("\n");
                }
                //跳过 "#result_end#
                i++;
                continue;
            }

            if (s.indexOf("#") != -1 || s.indexOf("【") != -1) {
                sb.append(replaceStr(s));
            } else {
                sb.append(s);
            }
            sb.append("\r");
            sb.append("\n");
        }
        fileReader.close();
        br.close();
        writeFile(sb, outputPath);
    }

    private static boolean isStr(String javaType) {
        return javaType.equals("String");
    }


    //生成service
    private static void geneOtherIfNotWeb(String filepath, String outputPath) throws Exception {
        geneOther(filepath, outputPath, false);
    }

    //生成 controller
    private static void geneOther(String filepath, String outputPath, boolean web) throws Exception {
        FileReader fileReader = new FileReader(filepath);
        BufferedReader br = new BufferedReader(fileReader);
        List<String> collect = br.lines().collect(Collectors.toList());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < collect.size(); i++) {
            String s = collect.get(i);
            //发现是 "#web_content#"
            if (s.trim().equals("#web_content#")) {
                s = collect.get(++i);
                if (!web) {
                    while (!s.trim().equals("#web_content#")) {
                        s = collect.get(++i);
                    }
                    continue;
                }
            }
            if (s.indexOf("#") != -1 || s.indexOf("【") != -1) {
                sb.append(replaceStr(s));
            } else {
                sb.append(s);
            }
            sb.append("\r");
            sb.append("\n");
        }
        fileReader.close();
        br.close();
        writeFile(sb, outputPath);
    }

    /**
     * 生成 vo dto model类
     *
     * @param filepath   模板位置
     * @param type       类型
     * @param outputPath 输出路径
     */
    public static void geneDomain(String filepath, String type, String outputPath) throws Exception {
        FileReader fileReader = new FileReader(filepath);
        BufferedReader br = new BufferedReader(fileReader);
        List<String> collect = br.lines().collect(Collectors.toList());
        StringBuffer sb = new StringBuffer();
        List<FieldVO> fields = null;
        if (type.equals("dto")) {
            fields = dtoFields;
        } else if (type.equals("vo")) {
            fields = voFields;
        }
        if (type.equals("model")) {
            fields = modelFields;
        }
        for (int i = 0; i < collect.size(); i++) {
            String s = collect.get(i);
            if (s.trim().equals("#Field_content_start#")) {
                //开始填充字段信息
                //从下一行开始
                int j = ++i;
                for (int i1 = 0; i1 < fields.size(); i1++) {

                    FieldVO field = fields.get(i1);
                    String fieldType = field.getJavaType();
                    String commont = field.getCommont();
                    String fieldName = field.getPropertyName();
                    //填充每个字段
                    s = collect.get(i);
                    j = i;
                    while (!s.trim().equals("#Field_content_end#")) {
                        String replace = "";
                        replace = s.replace("#commont#", commont).replace("#fieldType#", fieldType).replace("#fieldName#", fieldName);
                        s = collect.get(++j);
                        if (s.trim().equals("#Field_content_end#") && isDate(fieldType)) {
                            //在字段上面加一行
                            sb.append("    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\",timezone = \"GMT+8\")\n");
                        }
                        sb.append(replace);
                        sb.append("\r");
                        sb.append("\n");
                    }
                    sb.append("\r");
                    sb.append("\n");
                }
                i = j;
                continue;
            }

            if (s.trim().equals("#set_content#")) {
                i++;
                String str = collect.get(i);
                for (FieldVO field : dtoFields) {
                    if (type.equals("model")) {
                        //model 不需要时间的转换
                    }
                    //设置为set方法   #vo_low#.#setName#(this.#getName#());
                    String propertyName = field.getPropertyName();
                    String setName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length());
                    String getName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1, propertyName.length());
                    String replace = str.replace("#setName#", setName).replace("#getName#", getName);
                    sb.append(replaceStr(replace));
                    sb.append("\r");
                    sb.append("\n");
                }
                continue;
            }

            if (s.indexOf("#") != -1 || s.indexOf("【") != -1) {
                sb.append(replaceStr(s));
            } else {
                sb.append(s);
            }
            sb.append("\r");
            sb.append("\n");
        }
        fileReader.close();
        br.close();
        writeFile(sb, outputPath);
    }

    private static void writeFile(StringBuffer sb, String outputPath) throws IOException {
        new File(outputPath).getParentFile().mkdirs();
        FileWriter fileWriter = new FileWriter(outputPath, false);
        fileWriter.write(sb.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    private static boolean isDate(String fieldType) {
        return fieldType.equals("LocalDateTime") || fieldType.equals("LocalDate") || fieldType.equals("LocalTime");
    }

    private static String replaceStr(String s) {
        for (String s1 : varMap.keySet()) {
            s = s.replace(s1, varMap.get(s1));
        }
        return s;
    }

    /**
     * 初始化一些必须的字段名称
     * eg: 类名,变量名 etc
     */
    private static void initBaseStr(GeneParam geneParam) {
        //项目路径
        String bashdir = "#project.bashdir#";
        //model  首字母大写
        String model_up = "#model_up#";
        //model  首字母小写
        String model_low = "#model_low#";
        //dto  首字母大写
        String dto_up = "#dto_up#";
        //dto  首字母小写
        String dto_low = "#dto_low#";
        //vo  首字母大写
        String vo_up = "#vo_up#";
        //vo  首字母小写
        String vo_low = "#vo_low#";
        String tableName = "#tableName#";
        String main_author = "【请填写作者】";
        String main_date = "【请填写日期】";
        String main_desc = "【请填写功能描述】";
        String service_low = "#service_low#";
        String service_up = "#service_up#";
        String serviceImpl_up = "#serviceImpl_up#";
        String controller = "#controller#";
        String mapper_low = "#mapper_low#";
        String mapper_up = "#mapper_up#";


        //根据 表名生成 dto和vo,model等的名字
        String camerName = camerName(geneParam.getDatabasetTableName());
        String upperName = camerName.substring(0, 1).toUpperCase() + camerName.substring(1, camerName.length());
        String dtoNameUpper = upperName + "DTO";
        String voNameUpper = upperName + "VO";
        String modelNameUpper = upperName + "Model";
        String dtoNameLow = camerName + "DTO";
        String voNameLow = camerName + "VO";
        String modelNameLow = camerName + "Model";
        String serviceNameUpper = "I" + upperName + "Service";
        String serviceNameLow = camerName + "Service";
        String serviceImplUpper = upperName + "ServiceImpl";
        String mapperNameLow = camerName + "Mapper";
        String mapperNameUpper = upperName + "Mapper";
        String controllerNameUpper = upperName + "Rest";
        varMap.put(bashdir, geneParam.getBashdir());
        varMap.put(model_up, modelNameUpper);
        varMap.put(model_low, modelNameLow);
        varMap.put(dto_up, dtoNameUpper);
        varMap.put(dto_low, dtoNameLow);
        varMap.put(vo_up, voNameUpper);
        varMap.put(vo_low, voNameLow);
        varMap.put(service_low, serviceNameLow);
        varMap.put(service_up, serviceNameUpper);
        varMap.put(serviceImpl_up, serviceImplUpper);
        varMap.put(controller, controllerNameUpper);
        varMap.put(mapper_low, mapperNameLow);
        varMap.put(mapper_up, mapperNameUpper);
        varMap.put(main_author, geneParam.getAuthor());
        varMap.put(main_date, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        varMap.put(main_desc, geneParam.getDesc());
        varMap.put(tableName, geneParam.getDatabasetTableName());
    }

    static Map<String, String> varMap = new HashMap<>();

    //  t_a_b 转为驼峰命名  tAB
    private static String camerName(String name) {
        String[] s = name.trim().split("_");
        String str = "";
        for (int i = 0; i < s.length; i++) {
            if (s[i] == null || s[i] == "") {
                continue;
            }
            if (str.length() == 0) {
                //首个字母不变
                str += s[i];
                continue;
            } else {
                String substring = s[i].substring(0, 1).toUpperCase();
                //确保c为小写字母
                str += substring + s[i].substring(1, s[i].length());
            }

        }
        return str;
    }

    class FieldVO {
        private boolean isId;
        private String propertyName;
        private String columName;
        private String javaType;
        private String jdbcType;
        private String commont;

        public boolean isId() {
            return isId;
        }

        public void setId(boolean id) {
            isId = id;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getColumName() {
            return columName;
        }

        public void setColumName(String columName) {
            this.columName = columName;
        }

        public String getJavaType() {
            return javaType;
        }

        public void setJavaType(String javaType) {
            this.javaType = javaType;
        }

        public String getJdbcType() {
            return jdbcType;
        }

        public void setJdbcType(String jdbcType) {
            this.jdbcType = jdbcType;
        }

        public String getCommont() {
            return commont;
        }

        public void setCommont(String commont) {
            this.commont = commont;
        }

        @Override
        public String toString() {
            return "FieldVO{" +
                    "isId=" + isId +
                    ", propertyName='" + propertyName + '\'' +
                    ", columName='" + columName + '\'' +
                    ", javaType='" + javaType + '\'' +
                    ", jdbcType='" + jdbcType + '\'' +
                    ", commont='" + commont + '\'' +
                    '}';
        }
    }
}

