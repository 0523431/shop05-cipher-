package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import dao.mapper.BoardMapper;
import logic.Board;

//@Component + dao 기능 (객체화가 됨)
@Repository
public class BoardDao {	
	@Autowired
	private SqlSessionTemplate sqlSession;
	private Map<String, Object> param = new HashMap<>();
	
	
	public int count(String searchtype, String searchcontent) {
//		String sql = "select count(*) from board";
//		if(searchtype !=null) {
//			sql += " where " + searchtype + " like :searchcontent ";
//		}
//		param.put("searchcontent", "%"+searchcontent+"%");
		
		param.clear();
		param.put("searchtype", searchtype);
		param.put("searchcontent", searchcontent);
		Integer count = sqlSession.getMapper(BoardMapper.class).count(param);
		return count;
	}

	public List<Board> list(Integer pageNum, int limit, String searchtype, String searchcontent) {
//		String sql = boardcolumn;
//		param.clear();
//		if(searchtype != null) {
//			sql += " where "+ searchtype + " like '%" + searchcontent + "%' ";
//			param.put("searchcontent", searchcontent);
//		}	
//		sql += " order by grp desc, grpstep limit :startrow, :limit ";
		
		param.clear();
		param.put("searchtype", searchtype);
		param.put("searchcontent", searchcontent);
		param.put("startrow", (pageNum -1) *limit);
		param.put("limit", limit);
		return sqlSession.getMapper(BoardMapper.class).select(param);
	}

	public Board selectOne(Integer num) {
		param.clear();
		param.put("num", num);
		return sqlSession.getMapper(BoardMapper.class).select(param).get(0);
	}
	
	public int maxnum() {
		Integer max = sqlSession.getMapper(BoardMapper.class).maxnum();
		return max;
	}

	public void insert(Board board) {
		param.clear();
//		param.put("board", board);
		sqlSession.getMapper(BoardMapper.class).insert(board);
	}

	// 조회수 증가
	public void readcntadd(Integer num) {
		param.clear();
		param.put("num", num);
		sqlSession.getMapper(BoardMapper.class).readcnt(param);
	}
	
	// 기존글들의 순서를 밀어냄
	public void grpstepAdd(int grp, int grpstep) {
		param.clear();
		param.put("grp", grp);
		param.put("grpstep", grpstep);
		sqlSession.getMapper(BoardMapper.class).grpstep(param);
	}

	public void update(Board board) {
		param.clear();
		param.put("board", board);
		sqlSession.getMapper(BoardMapper.class).update(param);
	}

	public void delete(int num) {
		param.clear();
		param.put("num", num);
		sqlSession.getMapper(BoardMapper.class).delete(param);
	}
}
