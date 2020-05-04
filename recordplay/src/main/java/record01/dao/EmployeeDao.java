package record01.dao;

import record01.pojo.Employee;


public interface EmployeeDao {
    Employee findEmployeeBySn(String sn);
}
