package demo.domain;

import org.aspectj.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class FieldVO {
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

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\wei\\Desktop\\1111\\ec");
        File file2 = new File("C:\\Users\\wei\\Desktop\\1111\\ec2");
        File[] listFiles = file.listFiles();
        for (File listFile : listFiles) {
            String replace = listFile.getName().replace(".t2", ".000");
            File file1 = new File(file2, replace);
            FileUtil.copyFile(listFile,file1);
        }
    }
}
