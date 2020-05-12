package day03.framework.config;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装了(全局配置文件)mybatis-config.xml和（映射文件）EmployeeMapper.xml中所有数据
 */
public class Configuration {
    //数据源
    private DataSource dataSource;
    //映射文件中的CRUD片段
    private Map<String, MappedStatement> mappedStatements = new HashMap<>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

//    public Map<String, MappedStatement> getMappedStatements() {
//        return mappedStatements;
//    }

    public MappedStatement getMappedStatementById(String statementId) {
        return mappedStatements.get(statementId);
    }

//    public void setMappedStatements(Map<String, MappedStatement> mappedStatements) {
//        this.mappedStatements = mappedStatements;
//    }

    public void addMappedStatements(String statementId, MappedStatement mappedStatement) {
        this.mappedStatements.put(statementId, mappedStatement);
    }
}
