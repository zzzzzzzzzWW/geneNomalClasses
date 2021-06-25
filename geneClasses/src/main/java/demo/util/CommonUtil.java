package demo.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommonUtil {

    //  t_a_b 转为驼峰命名  tAB
    public static String camerName(String name) {
        String[] s = name.trim().split("_");
        String str = "";
        for (int i = 0; i < s.length; i++) {
            if (s[i] == null || s[i] == "") {
                continue;
            }
            if(i==0 && s[i] != null && s[i].equals("t") && !s[i+1].matches("^[0-9].*")) {
                // t_9 这种不处理
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

    /**
     * 换队装箱类
     * */
    public static String toObjName(String str) {
        switch (str.toLowerCase()) {
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

    public static void writeFile(StringBuffer sb, String outputPath) throws IOException {
        new File(outputPath).getParentFile().mkdirs();
        FileWriter fileWriter = new FileWriter(outputPath, false);
        fileWriter.write(sb.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    public static boolean isStr(String javaType) {
        return javaType.equals("String");
    }

}
