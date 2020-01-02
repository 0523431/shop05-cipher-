package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import dao.mapper.SaleItemMapper;
import dao.mapper.SaleMapper;
import logic.SaleItem;

// 장바구니에서 
// @Component + dao 기능
@Repository
public class SaleItemDao {

	@Autowired
	private SqlSessionTemplate sqlSession;
	private Map<String, Object> param = new HashMap<>();
	
	public void insert(SaleItem si) {
//		param.clear();
//		param.put("si", si);
		sqlSession.getMapper(SaleItemMapper.class).saleItemInsert(si);
	}

	public List<SaleItem> list(int saleid) {
		param.clear();
		param.put("saleid", saleid);
		return sqlSession.getMapper(SaleItemMapper.class).SaleItemSelect(param);
	}
}
