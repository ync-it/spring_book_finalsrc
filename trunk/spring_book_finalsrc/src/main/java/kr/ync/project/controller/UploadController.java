package kr.ync.project.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.ync.project.util.MediaUtils;
import kr.ync.project.util.UploadFileUtils;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UploadController {

	// 아이디 > 자료형으로 찾아서 주입. 생성자에 사용이 불가하다.
	// 필드변수, method에 사용가능
	// 빈 컨테이너에서 uploadPath 이름의 객체를 파라미터 혹은 value로 넣어준다.
	// 여기선 value로 넣어준다.
	// uploadPath = G:\\ync\\2018\\spring\\upload 등등으로 개별PC에 설정도니 값
	// servelt-context.xml에 정의된 값
	@Resource(name = "uploadPath")
	private String uploadPath;

	@GetMapping(value = "/uploadForm")
	public void uploadForm() {
	}
	
	// ajax가 아닌 기존 방식으로 다중 업로드를 할때 필요한 method
	@GetMapping(value = "/uploadMultiForm")
	public void uploadMultiForm() {
	}

	@PostMapping(value = "/uploadForm")
	public void uploadForm(MultipartFile file, Model model) throws Exception {

		log.info("originalName: " + file.getOriginalFilename());
		log.info("size: " + file.getSize() + "is empty? : " + file.isEmpty());
		log.info("contentType: " + file.getContentType());
		
		// uploadPath에 바로 저장시키기
		String savedName = uploadFile(file.getOriginalFilename(), file.getBytes());
		// String savedName = UploadFileUtils.uploadFile(uploadPath, file.getOriginalFilename(), file.getBytes());
		
		// 실제 저장된 file명을 view에 전달한다.
		model.addAttribute("savedName", savedName);

		// return "uploadResult";
	}

	private String uploadFile(String originalName, byte[] fileData) throws Exception {

		UUID uid = UUID.randomUUID();
		String savedName = uid.toString() + "_" + originalName;
		File target = new File(uploadPath, savedName);
		FileCopyUtils.copy(fileData, target);

		return savedName;
	}
	
	@GetMapping(value = "/uploadAjax")
	public void uploadAjax() {
	}
	
	// @ResponseBody는 return type이 HTTP 응답 메시지로 전송시킬때 사용
	// ReplyController에선 class를 @RestController로 선언해서 안썼지만
	// UploadController는 일반 @Controller로 선언하여 ResponseEntity type으로 return하는 method에서
	// 사용해주었다.
	@ResponseBody
	@PostMapping(value = "/uploadAjax", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> uploadAjax(MultipartFile file) throws Exception {

		log.info("originalName: " + file.getOriginalFilename());
		
		// 최종적으로 uploadFile method를 이용해 파일을 업로드 시키고 최종 파일명을 return 받는다.
		//return new ResponseEntity<>(file.getOriginalFilename(), HttpStatus.CREATED);
		return new ResponseEntity<>(UploadFileUtils.uploadFile(uploadPath, file.getOriginalFilename(), file.getBytes()),
				HttpStatus.CREATED);
		
	}
	
	@ResponseBody
	@GetMapping("/displayFile")
	public ResponseEntity<byte[]> displayFile(String fileName) throws Exception {

		InputStream in = null;
		ResponseEntity<byte[]> entity = null;

		log.info("FILE NAME: " + fileName);

		try {
			// 파일 확장자를 추출. +1 해서 '.'을 뺀 뒤의 확장자만 추출
			String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);

			MediaType mType = MediaUtils.getMediaType(formatName);

			HttpHeaders headers = new HttpHeaders();

			in = new FileInputStream(uploadPath + fileName);

			if (mType != null) { // 이미지일 경우
				headers.setContentType(mType);
			} else { // 일반 파일이면 다운로드 받게 한다.
				fileName = fileName.substring(fileName.indexOf("_") + 1);
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.add("Content-Disposition",
						"attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
			}

			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		} finally {
			in.close();
		}
		return entity;
	}
	
	@ResponseBody
	@PostMapping(value = "/deleteFile")
	public ResponseEntity<String> deleteFile(String fileName) {

		log.info("delete file: " + fileName);

		String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);

		MediaType mType = MediaUtils.getMediaType(formatName);
		
		// 이미지일 경우 썸네일도 삭제해준다.
		if (mType != null) {
			String front = fileName.substring(0, 12);
			String end = fileName.substring(14);
			// 원본 이미지 삭제
			new File(uploadPath + (front + end).replace('/', File.separatorChar)).delete();
		}
		// 썸네일 이미지 삭제
		new File(uploadPath + fileName.replace('/', File.separatorChar)).delete();

		return new ResponseEntity<String>("deleted", HttpStatus.OK);
	}
	
	@ResponseBody
	@PostMapping(value = "/deleteAllFiles")
	public ResponseEntity<String> deleteFile(@RequestParam("files[]") String[] files) {

		log.info("delete all files: " + files);

		if (files == null || files.length == 0) {
			return new ResponseEntity<String>("deleted", HttpStatus.OK);
		}

		for (String fileName : files) {
			String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);

			MediaType mType = MediaUtils.getMediaType(formatName);

			if (mType != null) {

				String front = fileName.substring(0, 12);
				String end = fileName.substring(14);
				new File(uploadPath + (front + end).replace('/', File.separatorChar)).delete();
			}

			new File(uploadPath + fileName.replace('/', File.separatorChar)).delete();

		}
		return new ResponseEntity<String>("deleted", HttpStatus.OK);
	}
	
	@PostMapping(value = "/uploadMultiForm")
	public String uploadMultiForm(@RequestParam("files") MultipartFile[] files, Model model) throws Exception {

		// logger.info("originalName: " + file.getOriginalFilename());
		// logger.info("size: " + file.getSize());
		// logger.info("contentType: " + file.getContentType());

		List<String> list = new ArrayList<String>();

		for (MultipartFile multipartFile : files) {
			log.info(multipartFile.getName());

			list.add(UploadFileUtils.uploadFile(uploadPath, multipartFile.getOriginalFilename(),
					multipartFile.getBytes()));
		}
		model.addAttribute("list", list);
		return "uploadMultiForm";
	}
}
