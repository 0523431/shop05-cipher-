package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource; // db Connection 객체

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import dao.mapper.ItemMapper;
import logic.Item;

//@Component + dao 기능
@Repository
public class ItemDao {
	// 현재 내 컨테이너 중에 SqlSessionTemplate객체를 가져와서 나한테 주입해
	// SqlSessionTemplate객체는 어디있어? spring-db.xml에서 확인 가능
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	private Map<String, Object> param = new HashMap<>();
	
	// 전체 아이템 목록
	public List<Item> list() {
		// 이렇게 null을 넣어서 쓰거나
		// return sqlSession.getMapper(ItemMapper.class).select(null);
		param.clear();
		return sqlSession.getMapper(ItemMapper.class).select(param);
	}

	public void insert(Item item) {
		param.clear();
		int id = sqlSession.getMapper(ItemMapper.class).maxid();
		
		item.setId(++id+""); // "": String type으로 형변화 시켜줌
		sqlSession.getMapper(ItemMapper.class).insert(item);
	}

	public Item itemInfo(String id) {
		param.clear();
		param.put("selectid", id); // 키값 selectid
		
		// 에러야, 왜? list타입인데 값은 Item 객체 1개만 나와야하니까
		// return sqlSession.getMapper(ItemMapper.class).select(param);
		   return sqlSession.getMapper(ItemMapper.class).select(param).get(0);
	}

	public void update(Item item) {
		sqlSession.getMapper(ItemMapper.class).update(item);
	}

	public void itemDelete(String id) {
		param.clear();
		param.put("id", id);
		sqlSession.getMapper(ItemMapper.class).delete(param);
	}
}
