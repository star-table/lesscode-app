package com.polaris.lesscode.app.config;


import com.polaris.lesscode.exception.CommonsExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.polaris.lesscode.exception.SysErrorException;
import com.polaris.lesscode.vo.Result;
import com.polaris.lesscode.app.vo.ResultCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice extends CommonsExceptionHandler {

	/*@ResponseBody
	@ExceptionHandler(value = Exception.class)
	public Result<?> errorHandler(Exception ex) {
		log.error(ex.getMessage(), ex);
		return Result.error(ResultCode.SYS_ERROR.getCode(), ResultCode.SYS_ERROR.getMessage());
	}
	
	@ResponseBody
	@ExceptionHandler(value = SysErrorException.class)
	public Result<?> errorHandler(SysErrorException ex) {
		log.error(ex.getMessage(), ex);
		return Result.error(ex.getCode(), ex.getMessage());
	}*/
	
}
