package day02.framework.config;

import day02.framework.sqlsource.iface.SqlSource;

/**
 * 映射文件中的CRUD片段
 */
public class MappedStatement {
    private String statementId;
    private SqlSource sqlSource;
    private String statementType;
    private Class<?> parameterTypeClass;
    private Class<?> resultTypeClass;

    public MappedStatement(String statementId, SqlSource sqlSource, String statementType, Class<?> parameterTypeClass, Class<?> resultTypeClass) {
        this.statementId = statementId;
        this.sqlSource = sqlSource;
        this.statementType = statementType;
        this.parameterTypeClass = parameterTypeClass;
        this.resultTypeClass = resultTypeClass;
    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public void setSqlSource(SqlSource sqlSource) {
        this.sqlSource = sqlSource;
    }

    public String getStatementType() {
        return statementType;
    }

    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }

    public Class<?> getParameterTypeClass() {
        return parameterTypeClass;
    }

    public void setParameterTypeClass(Class<?> parameterTypeClass) {
        this.parameterTypeClass = parameterTypeClass;
    }

    public Class<?> getResultTypeClass() {
        return resultTypeClass;
    }

    public void setResultTypeClass(Class<?> resultTypeClass) {
        this.resultTypeClass = resultTypeClass;
    }
}
