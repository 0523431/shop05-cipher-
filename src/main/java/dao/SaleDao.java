package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dao.mapper.SaleMapper;
import logic.Sale;

// 마이페이지 > 주문정보보기
// @Component + dao 기능
@Repository
public class SaleDao {
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	private Map<String, Object> param = new HashMap<>();
	
	public int getMaxSaleId() {
		// max는 sale테이블에 저장된 saleid값의 최대값
		Integer max = sqlSession.getMapper(SaleMapper.class).maxsaleid();
		return max +1;
	}
	
	public void insert(Sale sale) {		
		param.clear();
		sqlSession.getMapper(SaleMapper.class).saleInsert(sale);
	}

	public List<Sale> list(String id) {
		param.clear();
		param.put("userid", id);
		return sqlSession.getMapper(SaleMapper.class).saleList(param);
	}

	// 관리자 주문목록
	public List<Sale> list() {
		param.clear();
		return sqlSession.getMapper(SaleMapper.class).saleList(param);
	}
}
