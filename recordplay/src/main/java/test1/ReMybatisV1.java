package test1;


import org.junit.Test;

import javax.swing.plaf.synth.SynthStyle;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class ReMybatisV1 {

    private Properties properties=new Properties();

    private void loadProperties(){
        String location="parse01/jdbc.properties";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            properties.load(ins);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test(){
        loadProperties();
        List<Employee> employeeList=selectEmployee("selectEmployee","10001");
        System.out.println(employeeList);
        Map<String,Object> param=new HashMap<String, Object>();
        param.put("name","孙尚香");
        param.put("sn","10002");
        List<Employee> employeeList2=selectEmployee("selectEmployee",param);
        System.out.println(employeeList2);

        Employee emp=new Employee();
        emp.setSn("10003");
        List<Employee> employeeList3=selectEmployee("selectEmployee",emp);
        System.out.println(employeeList3);
    }



    public <T> List<T> selectEmployee(String statementId,Object param) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<T> results=new ArrayList<T>();
        try {
            //加载数据库驱动
            Class.forName(properties.getProperty("db.driver"));
            //通过驱动管理器获取数据库连接
            connection = DriverManager.getConnection(properties.getProperty("db.url"),properties.getProperty("db.username") , properties.getProperty("db.password"));
            //定义sql语句
            String sql = properties.getProperty("db."+statementId+".sql");
            //获取预处理statement
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            if(SimpleTypeRegistry.isSimpleType(param.getClass())){
                //一个参数
                preparedStatement.setObject(1, param);
            }else if(param instanceof Map){
                //
                String requestParams=properties.getProperty("db."+statementId+".requestParams");
                String[] requestArray = requestParams.split(",");
                for(int i=0;i<requestArray.length;i++){
                    String requesName= requestArray[i];
                    preparedStatement.setObject(i+1, ((Map) param).get(requesName));
                }
            }else{
               //POJO
                String parameterTypeName=properties.getProperty("db."+statementId+".parameterType");
                Class<?> parameterTypeClass = Class.forName(parameterTypeName);

                String requestParams=properties.getProperty("db."+statementId+".requestParams");
                String[] requestArray = requestParams.split(",");

                for(int i=0;i<requestArray.length;i++){
                    String requesName= requestArray[i];
                    Field parameterField = parameterTypeClass.getDeclaredField(requesName);
                    parameterField.setAccessible(true);
                    Object value= parameterField.get(param);
                    preparedStatement.setObject(i+1,value);
                }
            }
            //向数据库发起sql执行
            rs = preparedStatement.executeQuery();
            String returnTypeName=properties.getProperty("db."+statementId+".returnType");
            Class<?> returnTypeClass = Class.forName(returnTypeName);
            //结果映射
            while (rs.next()) {
                Object o = returnTypeClass.newInstance();
                ResultSetMetaData metaData = rs.getMetaData();
                int count=metaData.getColumnCount();
                for(int i=0;i<count;i++){
                  String columnName= metaData.getColumnName(i+1);
                  Object value=rs.getObject(columnName);
                  Field filed = returnTypeClass.getDeclaredField(columnName);
                  filed.setAccessible(true);
                  filed.set(o,value);
                }
                results.add((T) o);
            }
            return results;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //资源释放
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }


}
