package com.kwic.util;

public class PageUtil {
	public static final int ROW_PER_PAGE	= 10;
	public static final int PAGE_PER_BLOCK	= 10;
	
	public static String getPagingHTML(int pageNo,int totRowCnt){
		return getPagingHTML(pageNo,totRowCnt,ROW_PER_PAGE,PAGE_PER_BLOCK);
	}	
	
	public static String getPagingHTML(int pageNo,int totRowCnt,int rowPerPage,int pagePerBlock){
		StringBuffer sb	= new StringBuffer();
		
		if(totRowCnt==0)
			return "";
		
		int totPage	= (totRowCnt/rowPerPage)+((totRowCnt%rowPerPage==0)?0:1);
		int blockNo	= (pageNo/pagePerBlock)+((pageNo%pagePerBlock==0)?0:1);
		int totBlock	= (totPage/pagePerBlock)+((totPage%pagePerBlock==0)?0:1);
		
		if(pageNo>1){
			sb.append("<a href='javascript:fn_gopage(1);'><img src='/images/btn_prev2.gif' alt='맨처음으로' /></a>");
		}else{
			sb.append("<a href='#'><img src='/images/btn_prev2.gif' alt='맨처음으로' /></a>");
		}
		
		if(blockNo>1){
			sb.append("<a href='javascript:fn_gopage("+((blockNo-1)*pagePerBlock)+");'><img src='/images/btn_prev1.gif' alt='이전으로' /></a>");
		}else{
			sb.append("<a href='#'><img src='/images/btn_prev1.gif' alt='이전으로' /></a>");
		}
		
		sb.append("<span>");
		for(int i=(blockNo-1)*pagePerBlock+1;i<=blockNo*pagePerBlock;i++){
			if(totPage<i)
				break;
			if(i==pageNo)
				sb.append("<a href='#' class='on'>"+i+"</a>");
			else
				sb.append("<a href='javascript:fn_gopage("+i+");'>"+i+"</a>");
		}
		sb.append("</span>");
		
		if(blockNo<totBlock){
			sb.append("<a href='javascript:fn_gopage("+(blockNo*pagePerBlock+1)+");'><img src='images/btn_next1.gif' alt='다음으로' /></a>");
		}else{
			sb.append("<a href='#'><img src='images/btn_next1.gif' alt='다음으로' /></a>");
		}

		if(pageNo<totPage){
			sb.append("<a href='javascript:fn_gopage("+totPage+");'><img src='images/btn_next2.gif' alt='맨마지막으로' /></a>");
		}else{
			sb.append("<a href='#'><img src='images/btn_next2.gif' alt='맨마지막으로' /></a>");
		}
		
		return sb.toString();
	}
	
}
