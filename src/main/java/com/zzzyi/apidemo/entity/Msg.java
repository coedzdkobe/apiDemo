package com.zzzyi.apidemo.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 返回信息的实体类
 * @author Administrator
 */
public class Msg implements Serializable {

	/**
	 * 状态码 0：表示成功
	 */
	public static final int SUCCESS_CODE=0;

	/**
	 * 状态码 1：表示失败
	 */
	public static final int FAILURE_CODE=1;


	private int code;

	private String msg;

	private Object data;

	private Date date;

	public Msg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Msg(int code, String msg, Object data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public Msg(int code, String msg, Object data, Date date) {
		this.code = code;
		this.msg = msg;
		this.data = data;
		this.date = date;
	}

	public boolean success(){
		return code == SUCCESS_CODE;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Msg msg1 = (Msg) o;
		return code == msg1.code &&
				Objects.equals(msg, msg1.msg) &&
				Objects.equals(data, msg1.data) &&
				Objects.equals(date, msg1.date);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, msg, data, date);
	}
}
