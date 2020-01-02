package dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import logic.User;

public interface UserMapper {
	
	@Insert(" insert into userbackup "
			+ " (userid,password,username,phoneno,postcode,address,email,birthday) "
			+ " values (#{userid},#{password},#{username},#{phoneno},#{postcode},#{address},#{email},#{birthday}) ")
	void userInsert(User user);
	
	@Update("update userbackup set "
			+ " username=#{username}, phoneno=#{phoneno}, postcode=#{postcode}, "
			+ " address=#{address}, email=#{email}, birthday=#{birthday} "
			+ " where userid=#{userid} ")
	void userUpdate(User user);
	
	@Delete("delete from userbackup where userid=#{userid}")
	void userDelete(Map<String, Object> param);
	
	@Select({"<script>",
				"select * from userbackup ",
				" <if test='userid !=null'> where 1=1 and userid=#{userid} </if> ",
				" <if test='idchks !=null'> where userid in ",
					"<foreach collection='idchks' item='id' open='(' close=')' separator=','>",
						"#{id}",
					"</foreach>",
				" </if> ",
			 "</script>"})
	List<User> select(Map<String, Object> param);
	
	@Update("update userbackup set"
			+ " password=#{newpass} "
			+ " where userid=#{userid} ")
	void userPasswordUpdate(Map<String, Object> param);
	
//	@Select({"<script>",
//				"select * from userbackup where userid in ",
//				"<foreach collection='idchks' item='id' open='(' close=')' separator=','>",
//					"${id}",
//				"</foreach>",
//			 "</script>"})
//	List<User> selectEmail(Map<String, Object> param);

//	@Select({"<script>",
//		"select * from userbackup where userid in (",
//		"<foreach collection='idchks' item='id' open=''' close=''' separator=','>",
//			"#{id}",
//		"</foreach>",
//		")",
//	 "</script>"})
//	List<User> selectEmail2(Map<String, Object> param);
}
