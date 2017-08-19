package com.jgkj.bxxc.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 我的预约实体类
 */
public class MyReservationAction implements Serializable {

	private int code;

	private String reason;

	private List<Result> result;

	public void setCode(int code){
		this.code = code;
	}
	public int getCode(){
		return this.code;
	}
	public void setReason(String reason){
		this.reason = reason;
	}
	public String getReason(){
		return this.reason;
	}
	public void setResult(List<Result> result){
		this.result = result;
	}
	public List<Result> getResult(){
		return this.result;
	}

	public class Result
	{
		private String day;

		private List<Res> res;

		public void setDay(String day){
			this.day = day;
		}
		public String getDay(){
			return this.day;
		}
		public void setRes(List<Res> res){
			this.res = res;
		}
		public List<Res> getRes(){
			return this.res;
		}

		public class Res
		{
			private String time_slot;

			private String school;

			private String name;

			private String class_style;

			private String cid;
			private String tid;

			public String getCid() {
				return cid;
			}

			public String getTid() {
				return tid;
			}

			public void setTime_slot(String time_slot){
				this.time_slot = time_slot;
			}
			public String getTime_slot(){
				return this.time_slot;
			}
			public void setSchool(String school){
				this.school = school;
			}
			public String getSchool(){
				return this.school;
			}
			public void setName(String name){
				this.name = name;
			}
			public String getName(){
				return this.name;
			}
			public void setClass_style(String class_style){
				this.class_style = class_style;
			}
			public String getClass_style(){
				return this.class_style;
			}
		}
	}

	

}
