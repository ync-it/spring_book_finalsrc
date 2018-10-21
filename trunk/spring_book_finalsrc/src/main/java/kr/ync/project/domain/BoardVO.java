package kr.ync.project.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardVO {
    private Integer bno;

    private String title;

    private String content;

    private String writer;

    private Date regdate;

    private int viewcnt;
    
    private int replycnt;
    
    private String[] files;

}