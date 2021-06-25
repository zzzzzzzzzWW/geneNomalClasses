package demo.woker;

import demo.GeneMainApplication;
import demo.domain.FieldVO;
import demo.domain.GeneParam;
import demo.util.CommonUtil;
import demo.util.JavaTypeUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class GeneWorker {

    public static final String TYPE_MYSQL = "mySql";
    public static final String TYPE_PGSQL = "postgreSql";

    protected  Map<String, String> varMap = new HashMap<>();

    protected  List<FieldVO> dtoFields = new ArrayList<>();
    protected  List<FieldVO> voFields = new ArrayList<>();
    protected  List<FieldVO> modelFields = new ArrayList<>();

    protected GeneParam geneParam = null;

    public void setGeneParam(GeneParam geneParam) {
        this.geneParam = geneParam;
    }

    public static GeneWorker getWorker(String type) {
        GeneWorker geneWorker = null;
        switch (type){
            case TYPE_MYSQL:
                geneWorker =  new MysqlGeneWorker();
                break;
            case TYPE_PGSQL:
                geneWorker =  new PostgresqlGeneWorker();
                break;
        }
        return geneWorker;
    }

    protected GeneWorker() {

    }

    public void gene() throws Exception {
        initFiledInfo();
        initBaseStr();
        geneCode();
    }

    /**
     * 初始化字段信息
     * */
    protected abstract void initFiledInfo() throws  Exception;

    /**
     * 获取主键
     */
    protected abstract String getPrimarykey() throws Exception;

    /**
     * 初始化基础变量
     * */
    private  void initBaseStr() {

        String allFiledStr = "";
        String idKeyProperty = "";
        String idKeyJavaType = "";
        //插入数据时需要
        String insertFiledStr = "";
        StringBuffer insertProperties = new StringBuffer();
        for (FieldVO fieldVO : dtoFields) {
            String fiedlName = fieldVO.getColumName();
            allFiledStr += "," + fiedlName;
            if (!fieldVO.isId()) {
                insertFiledStr += "," + fiedlName;
                insertProperties.append(",#{item.");
                insertProperties.append(fieldVO.getPropertyName());
                insertProperties.append("}");
            } else {
                //主键字段
                varMap.put("#idKey_column#", fieldVO.getColumName());
                idKeyProperty = CommonUtil.camerName(fieldVO.getPropertyName());
                idKeyJavaType = CommonUtil.toObjName(fieldVO.getJavaType());
                if (idKeyJavaType.equals("Integer")) {

                }else if (idKeyJavaType.equals("String")) {
                    //非自增主键
                    insertFiledStr += "," + fiedlName;
                    insertProperties.append(",#{item.");
                    insertProperties.append(fieldVO.getPropertyName());
                    insertProperties.append("}");
                }else if (idKeyJavaType.equals("Long")) {

                }
            }
            System.out.println(fieldVO);
        }
        //如果是 数字,默认按自增处理;如果是字符串,
        varMap.put("#idKey_property#", idKeyProperty);
        varMap.put("#idKey_javaType#", idKeyJavaType);
        String typeTramfer = "";

        if (idKeyJavaType.equals("Integer")) {
            typeTramfer = "Integer.valueOf(id)";
        }else if (idKeyJavaType.equals("String")) {
            typeTramfer = "id";
        }else if (idKeyJavaType.equals("Long")) {
            typeTramfer = "Long.valueOf(id)";
        }
        varMap.put("#insert_fields#", insertFiledStr.replaceFirst(",", ""));
        varMap.put("#insert_properties#", insertProperties.toString().replaceFirst(",", ""));
        varMap.put("#all_fields#", allFiledStr.replaceFirst(",", ""));
        varMap.put("#id_type_tranfer#", typeTramfer);

        //项目路径
        String bashdir = "#project.bashdir#";
        //model  首字母大写
        String modelUp = "#model_up#";
        //model  首字母小写
        String modelLow = "#model_low#";
        //dto  首字母大写
        String dtoUp = "#dto_up#";
        //dto  首字母小写
        String dtoLow = "#dto_low#";
        //vo  首字母大写
        String voUp = "#vo_up#";
        //vo  首字母小写
        String voLow = "#vo_low#";
        String tableName = "#tableName#";
        String mainAuthor = "【请填写作者】";
        String mainDate = "【请填写日期】";
        String mainDesc = "【请填写功能描述】";
        String requestUrl = "#request_url#";
        String serviceLow = "#service_low#";
        String serviceUp = "#service_up#";
        String serviceImplUp = "#serviceImpl_up#";
        String controller = "#controller#";
        String mapperLow = "#mapper_low#";
        String mapperUp = "#mapper_up#";

        //根据 表名生成 dto和vo,model等的名字
        String camerName = CommonUtil.camerName(geneParam.getDatabasetTableName());
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
        varMap.put(modelUp, modelNameUpper);
        varMap.put(modelLow, modelNameLow);
        varMap.put(dtoUp, dtoNameUpper);
        varMap.put(dtoLow, dtoNameLow);
        varMap.put(voUp, voNameUpper);
        varMap.put(voLow, voNameLow);
        varMap.put(serviceLow, serviceNameLow);
        varMap.put(serviceUp, serviceNameUpper);
        varMap.put(serviceImplUp, serviceImplUpper);
        varMap.put(controller, controllerNameUpper);
        varMap.put(mapperLow, mapperNameLow);
        varMap.put(mapperUp, mapperNameUpper);
        varMap.put(mainAuthor, geneParam.getAuthor());
        varMap.put(mainDate, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        varMap.put(mainDesc, geneParam.getDesc());
        varMap.put(tableName, geneParam.getDatabasetTableName());
        varMap.put(requestUrl, geneParam.getQueyrUrl());
    };

    /**
     * 生成代码
     * */
    private  void geneCode() throws Exception {
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
    };

    /**
     * 生成 vo dto model类
     *
     * @param filepath   模板位置
     * @param type       类型
     * @param outputPath 输出路径
     */
    private   void geneDomain(String filepath, String type, String outputPath) throws Exception {
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
                        if (s.trim().equals("#Field_content_end#") && JavaTypeUtil.isDate(fieldType)) {
                            //在字段上面加一行
                            sb.append("    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\",timezone = \"GMT+8\")");
                            sb.append("\r");
                            sb.append("\n");
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
        CommonUtil.writeFile(sb, outputPath);
    }

    private  String replaceStr(String s) {
        for (String s1 : varMap.keySet()) {
            s = s.replace(s1, varMap.get(s1));
        }
        return s;
    }


    //生成service
    private  void geneOtherIfNotWeb(String filepath, String outputPath) throws Exception {
        geneOther(filepath, outputPath, false);
    }

    //生成 controller
    private  void geneOther(String filepath, String outputPath, boolean web) throws Exception {
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
            } else if (s.contains("#idList#")){
                //判断 不同id 的类型
                String idKeType = varMap.get("idKey_javaType");
                if (idKeType.equals("Integer")) {

                }else if (idKeType.equals("String")) {

                }else if (idKeType.equals("Long")) {

                }
                sb.append(s);
            } else {
                sb.append(s);
            }
            sb.append("\r");
            sb.append("\n");
        }
        fileReader.close();
        br.close();
        CommonUtil.writeFile(sb, outputPath);
    }

    //生成mapper
    private  void geneMapper(String filepath, String outputPath) throws Exception {
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
                    if (CommonUtil.isStr(javaType)) {
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
                    if (JavaTypeUtil.isDate(javaType)) {
                        replace = s.replace("#propertyName#", propertyName).replace("#columName#", columName);
                        //"<if test='dto.#startDate#!=null and dto.#endDate#!=null'> and #columName#=between #{dto.#startDate#} and #{dto.#endDate#}}</if>" +
                        replace = replace.replace("#startDate#", propertyName + "Start").replace("#endDate#", propertyName + "End").replace("#columName#", columName);
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
                    if (CommonUtil.isStr(javaType)) {
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
        CommonUtil.writeFile(sb, outputPath);
    }

}
