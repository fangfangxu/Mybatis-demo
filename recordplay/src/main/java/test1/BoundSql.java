package test1;

import java.util.ArrayList;
import java.util.List;

public class BoundSql {
    private String sql;

    List<ParameterMapping> parameterMappings=new ArrayList<ParameterMapping>();

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void addParameterMappings(ParameterMapping parameterMapping) {
        this.parameterMappings.add(parameterMapping);
    }
}
