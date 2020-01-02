package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dao.mapper.UserMapper;
import exception.LoginException;
import logic.User;

//@Component + dao 기능 (객체화가 됨)
@Repository
public class UserDao {
	@Autowired
	private SqlSessionTemplate sqlSession;
	private Map<String, Object> param = new HashMap<>();
	
	public void userInsert(User user) {
		param.clear();
		sqlSession.getMapper(UserMapper.class).userInsert(user);
	}

	public User selectOne(String userid) {
		param.clear();
		param.put("userid", userid);
		
		// 아이디가 없어서 get(0)이 null이면 오류가 발생
		// return sqlSession.getMapper(UserMapper.class).select(param).get(0);
		List<User> list = sqlSession.getMapper(UserMapper.class).select(param);
		if(list ==null || list.isEmpty()) {
			throw new LoginException("로그인하려고하는데 해당 아이디가 없습니다(UserDao)", "");
		} else {
			return list.get(0);
		}
	}

	public void update(User user) {
		param.clear();
		sqlSession.getMapper(UserMapper.class).userUpdate(user);
	}

	public void delete(String userid) {
		param.clear();
		param.put("userid", userid);
		sqlSession.getMapper(UserMapper.class).userDelete(param);
	}

	public List<User> adminlist() {
		param.clear();
		return sqlSession.getMapper(UserMapper.class).select(param);
	}
	
	public List<User> adminlist(String[] idchks) {
		// List<String> ids = Arrays.asList(idchks);
		
		param.clear();
		param.put("idchks", idchks);
		return sqlSession.getMapper(UserMapper.class).select(param);
	}

	public void userPasswordUpdate(String userid, String newpass) {
		param.clear();
		param.put("userid", userid);
		param.put("newpass", newpass);
		 sqlSession.getMapper(UserMapper.class).userPasswordUpdate(param);
	}
	
//	public List<User> adminlist2(String[] idchks) {
//		String sql = "select * from useraccount where userid in (";
//		for(int i=0; i<idchks.length; i++) {
//			sql += "'" + idchks[i] + ( (i==idchks.length-1)? "'":"'," );
//		}
//		sql += ")";
//		
//		param.clear();
//		param.put("idchks", idchks);
//		return sqlSession.getMapper(UserMapper.class).selectEmail2(param);
//	}
}
