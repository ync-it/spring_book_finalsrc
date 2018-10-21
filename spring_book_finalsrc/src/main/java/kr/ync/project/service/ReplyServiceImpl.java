package kr.ync.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.ync.project.domain.Criteria;
import kr.ync.project.domain.ReplyVO;
import kr.ync.project.persistence.ReplyDAO;
import kr.ync.project.persistence.BoardDAO;

@Service
public class ReplyServiceImpl implements ReplyService {

	// 댓글을 1개 작성하거나 삭제할때 tbl_board table의 replycnt 컬럼의 값을
	// +1(댓글작성 시) 혹은 -1(댓글 삭제 시) 해줘야하기에
	// ReplyDAO, BoardDAO 객체를 주입받아서 사용하여야 한다. 
	@Autowired
	private ReplyDAO dao;
	
	@Autowired
	private BoardDAO boardDAO;

	@Override
	public List<ReplyVO> listReply(Integer bno) throws Exception {

		return dao.list(bno);
	}

	@Override
	public void modifyReply(ReplyVO vo) throws Exception {

		dao.update(vo);
	}
	@Override
	public List<ReplyVO> listReplyPage(Integer bno, Criteria cri) throws Exception {

		return dao.listPage(bno, cri);
	}

	@Override
	public int count(Integer bno) throws Exception {

		return dao.count(bno);
	}
	
	@Transactional
	@Override
	public void addReply(ReplyVO vo) throws Exception {

		dao.create(vo);
		boardDAO.updateReplyCnt(vo.getBno(), 1);
	}
	
	@Transactional
	@Override
	public void removeReply(Integer rno) throws Exception {

		int bno = dao.getBno(rno);
		dao.delete(rno);
		boardDAO.updateReplyCnt(bno, -1);
	}

}
