package dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import logic.Board;

public interface BoardMapper {
	
	@Select("select ifnull(max(num),0) from board")
	Integer maxnum();

	@Insert("insert into board "
			+ " (num,name,pass,title,content,file1,regdate,readcnt,grp,grplevel,grpstep) "
			+ " values (#{num},#{name},#{pass},#{title},#{content},#{fileurl},now(),0,#{grp},#{grplevel},#{grpstep}) ")
	void insert(Board board);

	// 조회수 증가
	@Update("update board set readcnt=readcnt +1 where num =#{num}")
	void readcnt(Map<String, Object> param);
	
	// 기존글 순서를 밀어냄
	@Update("update board set grpstep=grpstep+1 where grp=#{grp} and grpstep > #{grpstep} ")
	void grpstep(Map<String, Object> param);
	
	// 글수정
	@Update("update board set "
			+ " name=#{board.name},title=#{board.title},content=#{board.content},file1=#{board.fileurl} "
			+ " where num=#{board.num} ")
	void update(Map<String, Object> param);
	
	@Delete("delete from board where num=#{num}")
	void delete(Map<String, Object> param);

	@Select("<script>"
			+ " select count(*) from board "
			+ " <if test='searchtype !=null'>"
			+ " 	where ${searchtype} like '%${searchcontent}%' "
			+ " </if>"
			+ "</script>")
	Integer count(Map<String, Object> param);
	
	@Select({"<script>",
				"select num,name,pass,title,content,file1 as fileurl, ",
				" regdate,readcnt,grp,grplevel,grpstep from board ",
				" where 1=1 ",
				" <if test='searchtype !=null'>",
				" 		and ${searchtype} like '%${searchcontent}%' ",
				" </if>",
				" <choose>",
				" 	<when test='num !=null'> and num = #{num} </when> ",
				" 	<otherwise>",
				" 		order by grp desc, grpstep limit #{startrow}, #{limit} ",
				" 	</otherwise>",
				" </choose>",
			 "</script>"})
	List<Board> select(Map<String, Object> param);

}
