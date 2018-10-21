package kr.ync.project.service;

import java.util.Date;

import kr.ync.project.domain.UserVO;
import kr.ync.project.dto.LoginDTO;

public interface UserService {

	public UserVO login(LoginDTO dto) throws Exception;

	public void keepLogin(String uids, String sessionId, Date next) throws Exception;

	public UserVO checkLoginBefore(String value);
}
