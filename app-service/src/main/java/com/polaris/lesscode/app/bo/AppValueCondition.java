package com.polaris.lesscode.app.bo;

import java.util.List;

import lombok.Data;

@Data
public class AppValueCondition {

	private String field;
	
	private Object value;
	
	private String type;
	
	private Object left;
	
	private Object right;
	
	private List<Object> values;
	
}
