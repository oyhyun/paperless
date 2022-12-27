package com.example.paperless;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class Util {
	public static Map<String, Integer> getPagingMap(int selectPageNo, int rowCntPerPage, int totCnt){
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			
			if(totCnt == 0) {
				map.put("selectPageNo", selectPageNo);
				map.put("rowCntPerPage", rowCntPerPage);
				map.put("begin_rowNo", 0);
				map.put("end_rowNo", 0);
				map.put("last_pageNo", 0);
				map.put("begin_pageNo_perPage", 0);
				map.put("end_pageNo_perPage", 0);
				map.put("serialNo", 0);
				return map;
			}
			
			if(selectPageNo <= 0) selectPageNo = 1;
			if(rowCntPerPage <= 0) rowCntPerPage = 10;
			
			// 페이지 갯수
			int last_pageNo = totCnt / rowCntPerPage;
				if(totCnt % rowCntPerPage > 0) last_pageNo++;
				if(last_pageNo < selectPageNo) selectPageNo = last_pageNo;
			int end_rowNo = selectPageNo * rowCntPerPage;
			int begin_rowNo = end_rowNo - rowCntPerPage + 1;
				if(end_rowNo > totCnt) end_rowNo = totCnt;
			
			
			// 한 페이지당 출력될 게시물 번호
			int pageNoCnt_perPage = 10;
			int begin_pageNo_perPage = (int)Math.floor( (selectPageNo - 1) / pageNoCnt_perPage ) * pageNoCnt_perPage + 1;
			int end_pageNo_perPage = begin_pageNo_perPage + pageNoCnt_perPage - 1;
				if(end_pageNo_perPage > last_pageNo) end_pageNo_perPage = last_pageNo;
			
			int serialNo = totCnt - (selectPageNo - 1) * rowCntPerPage;
			map.put("selectPageNo", selectPageNo);
			map.put("rowCntPerPage", rowCntPerPage);
			map.put("begin_rowNo", begin_rowNo);
			map.put("end_rowNo", end_rowNo);
			map.put("last_pageNo", last_pageNo);
			map.put("begin_pageNo_perPage", begin_pageNo_perPage);
			map.put("end_pageNo_perPage", end_pageNo_perPage);
			map.put("serialNo", serialNo);
			
			return map;
			
		}catch(Exception e) {
			return new HashMap<String, Integer>();
		}
	}
	
	
	
	// 업로드 파일의 크기, 확장자, 저장 경로 체크
	public static String checkUploadFile(
		MultipartFile string
		, String uploadDir		// 업로드되는 파일의 [최종 저장 폴더명] 저장
		, int fileSize			// 업로드 파일의 최대 크기
		, String[] extensions	// 업로드 파일의 확장자
	) throws Exception{
		
		String errorMsg = "";
		
		if(uploadDir != null && uploadDir.length() > 0) { // uploadDir에 문자열이 존재
			if(new File(uploadDir).isDirectory() == false) { // uploadDir 문자열의 폴더경로가 없으면 에러문구 저장
				errorMsg = errorMsg + uploadDir + "라는 폴더가 없습니다.";
			}
		}
		
		
		if(string != null && string.isEmpty() == false) { // 업로드된 파일이 존재
			
			if(string.getSize() > fileSize) { // 업로드된 파일 크기 체크
				errorMsg = errorMsg + "업로드 파일이 " + fileSize + "kb 보다 크면 안됩니다.";
			}
			
			String originalFilename = string.getOriginalFilename();
			int cnt = 0;
			String str = "";
			for(int i = 0; i < extensions.length; i++) {
				str = str + " " + extensions[i];
				if(originalFilename.endsWith("." + extensions[i])) cnt++;
			}
			if(cnt == 0) {
				errorMsg = errorMsg + "업로드 파일의 확장자는 " + str + "이어야합니다.";
			}
		}
		return errorMsg;
	}
	
	
	// 업로드된 파일을 웹서버에 저장
	public static void saveUploadFile(
		MultipartFile multi		// 업로드 되는 파일을 관리하는 MultipartFile 객체
		, String uploadDir		// 업로드 되는 파일의 [저장 폴더명]
		, String newFileName	// 업로드된 파일에 부여할 새파일명
		, String oldFileName	// 이전 파일명
		, boolean isDel			// 파일 삭제 여부
	) throws Exception {
		
		if(multi == null || multi.isEmpty()) return;
		
		
		if(isDel) { // 파일 삭제 모드
			if(oldFileName != null && oldFileName.length() > 0) {
				File oldFile = new File(uploadDir + oldFileName);
				oldFile.delete();
			}
		}
		else { // 파일 덮어쓰기
			if(uploadDir.endsWith("/") == false) uploadDir = uploadDir + "/";
			
			File newfile = new File(uploadDir + newFileName);
			multi.transferTo(newfile);
			if(oldFileName != null && oldFileName.length() > 0) {
				File oldFile = new File(uploadDir + oldFileName);
				oldFile.delete();
			}
		}
	}
	
	
	// 파일 업로드
	public static void saveUploadFile(
		MultipartFile multi
		, String uploadDir
		, String newFileName
	) throws Exception{
		saveUploadFile(multi, uploadDir, newFileName, null, false);
	}
	
	public static void saveUploadFile(
		MultipartFile multi
		, String uploadDir
		, String newFileName
		, boolean isDel
	) throws Exception{
		saveUploadFile(multi, uploadDir, newFileName, null, isDel);
	}
	
	
	public static String getNewFileName(MultipartFile multi) throws Exception{
		
		String newFileName = null;
		if(multi == null || multi.isEmpty()) {
			return newFileName; // 업로드된 파일이 없음
		}
		
		String oriFileName = multi.getOriginalFilename();
		String file_extension = oriFileName.substring(oriFileName.lastIndexOf(".") + 1);
		return UUID.randomUUID() + "." + file_extension; // 고유한 새파일명 반환
	}
	
	
	// 파일 삭제
	public static void delFile(String dir, String fileName) {
		if(isNull(fileName) || isNull(dir)) return;
		
		if(dir.endsWith("/") == false) dir = dir + "/";
		File file = new File(dir + fileName);
		if(file.exists()) file.delete();
	}
	
	// 문자열이 null이거나 길이가 없으면 true 반환
	public static boolean isNull(String str) {
		return str == null || str.length() == 0;
	}
}
