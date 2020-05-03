package record02.dao;

import record02.pojo.Employee;


public interface EmployeeMapper {
    Employee findEmployeeBySn(String sn);
}
