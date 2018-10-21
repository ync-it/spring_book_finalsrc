/*
* @FixMethodOrder 옵션
* MethodSorters.DEFAULT
*  - 기본값 -> 순서를 보장하지 않는다.
*  MethodSorters.JVM
*  - JVM이 반환하는 순서이나, 이 순서는 실행마다 다를 수 있다.
*  MethodSorters.NAME_ASCENDING
*  - 각 메소드의 이름 순으로 정렬
*/
package kr.ync.project;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kr.ync.project.domain.BoardVO;
import kr.ync.project.service.BoardService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/root-context.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // junit test metho의 순서를 지정해준다.
public class BoardServiceTest {

	@Autowired
	private BoardService service;
	
	@Test
	public void t1_testCreate() throws Exception {
	
		BoardVO board = new BoardVO();
		board.setTitle("새로운 글을 넣습니다. ");
		board.setContent("새로운 글을 넣습니다. ");
		board.setWriter("user00");
		service.regist(board);
	}

	@Test
	public void t2_testRead() throws Exception {
		log.info(service.read(6).toString());
	}

	@Test
	public void t3_testUpdate() throws Exception {
	
		BoardVO board = new BoardVO();
		board.setBno(6);
		board.setTitle("수정된 글입니다.");
		board.setContent("수정 테스트 ");
		board.setWriter("수정 테스트");
		service.modify(board);
	}

	@Test
	public void t4_testDelete() throws Exception {

	  service.remove(8);
	}
}
