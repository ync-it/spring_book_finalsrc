<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
  "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<style>
.fileDrop {
	width: 100%;
	height: 200px;
	border: 1px dotted blue;
}

small {
	margin-left: 3px;
	font-weight: bold;
	color: gray;
}
</style>
</head>
<body>

	<h3>Ajax File Upload</h3>
	<div class='fileDrop'></div>

	<div class='uploadedList'></div>

	<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
	<script>
		// 확장자가 이미지인지 체크하는 함수
		function checkImageType(fileName){
			var pattern = /jpg|gif|png|jpeg/i;
			
			return fileName.match(pattern);
			
		}

		function getOriginalName(fileName){	
	
			if(checkImageType(fileName)){
				return;
			}
			// 전체 파일명에서 가장 앞에 붙은 '_' 가 fileName string에서 몇번째 위치하는지 체크한 뒤 +1을 해주면 '_' 뒤의 string 모두를 가져올 수 있다.
			// 즉 원래 upload한 원본이름을 알 수 있다. (/2018/01/01/uuis_uploadfile.ext 가 전체 경로이다.)
			// 여기서 uploadfile.ext 순수 파일 이름만 가져온다.
			var idx = fileName.indexOf("_") + 1 ;
			return fileName.substr(idx);
			
		}

		// 썸네일 이미지에서 원본(s_가 빠진) 파일의 전체 경로를 가져오는 함수
		function getImageLink(fileName){
			
			if(!checkImageType(fileName)){
				return;
			}
			var front = fileName.substr(0,12); // /2018/01/01/ => fileName에서 앞의 /년/월/일/ sting만 떼어낸다.
			var end = fileName.substr(14); // /년/월/일/s_ 까지가 fileName에서 14자리. 14자리 그 뒤로 모든 string을 가져온다. 
			console.log(front);
			console.log(end);
			return front + end;
			
}

		$(".fileDrop").on("dragenter dragover", function(event) {
			event.preventDefault();
		});

		$(".fileDrop").on("drop", function(event){
			event.preventDefault();
			
			var files = event.originalEvent.dataTransfer.files;
			
			var file = files[0];

			//console.log(file);
			
			var formData = new FormData();
			
			formData.append("file", file);
			
			$.ajax({
				url: '/uploadAjax',
				data: formData,
				dataType:'text',
				processData: false,
				contentType: false,
				type: 'POST',
				success: function(data){
					var str ="";
					  
					if(checkImageType(data)) {
						str ="<div><a href=displayFile?fileName="+getImageLink(data)+">"
							+"<img src='displayFile?fileName="+data+"'/>"
							+"</a><small data-src="+data+">X</small></div>";
					} else {
						str = "<div><a href='displayFile?fileName="+data+"'>" 
								+ getOriginalName(data)+"</a>"
								+"<small data-src="+data+">X</small></div></div>";
						}

					$(".uploadedList").append(str);
				}
			});	
		});

		$(".uploadedList").on("click", "small", function(event){
			
			var that = $(this);
			
			$.ajax({
				url:"deleteFile",
				type:"post",
				data: {fileName:$(this).attr("data-src")},
				dataType:"text",
				success:function(result){

					if(result == 'deleted'){
						that.parent("div").remove();
					}
				}
			});
		});
	</script>

</body>
</html>