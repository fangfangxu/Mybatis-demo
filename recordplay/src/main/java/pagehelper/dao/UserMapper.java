package pagehelper.dao;


import pagehelper.pojo.User;

import java.util.List;

public interface UserMapper {
   List<User> selectUsers();
}
