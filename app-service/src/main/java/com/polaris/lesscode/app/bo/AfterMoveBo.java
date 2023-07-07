package com.polaris.lesscode.app.bo;

import lombok.Data;

/**
 * 移动信息，用来传递移动后的数据信息
 *
 * @date 2020年8月19日
 */
@Data
public class AfterMoveBo {
	
	private Long parentId;
	
	private Long sort;

	public AfterMoveBo(Long parentId, Long sort) {
		this.parentId = parentId;
		this.sort = sort;
	}

	public AfterMoveBo() {
	}
}
