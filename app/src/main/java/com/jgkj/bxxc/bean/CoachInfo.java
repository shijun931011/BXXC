package com.jgkj.bxxc.bean;

import com.jgkj.bxxc.bean.entity.CommentEntity.CommentEntity;

import java.io.Serializable;
import java.util.List;

public class CoachInfo implements Serializable {
	/**
	 * 教练页面详细信息，可以预约，展示以前学员对教练的一些评价
	 */
	//返回码
	private int code;
	//result
	private List<Result> result;
	//返回信息
	private String reason;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<Result> getResult() {
		return result;
	}

	public void setResult(List<Result> result) {
		this.result = result;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public class Comment{
		private String comment;
		private String comment_time;
		private String default_file;

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getComment_time() {
			return comment_time;
		}

		public void setComment_time(String comment_time) {
			this.comment_time = comment_time;
		}

		public String getDefault_file() {
			return default_file;
		}

		public void setDefault_file(String default_file) {
			this.default_file = default_file;
		}
	}

	public class Result{
		//教练id
		private int cid;
		private String chexing;
		//地址
		private String faddress;
		//校区
		private String address;
		private String latitude;
		private String longitude;
		//教练姓名
		private String coachname;

		public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		//教练教学质量
		private String teach;
		//教练服务态度
		private String wait;
		//教练头像
		private String file;
		//班型
		private String class_type;
		//车牌号
		private String number_plates;
		//信誉
		private int credit;
		//好评率
		private int praise;
		//通过率
		private int pass;
		//好评率
		private String haopin;
		//通过率
		private String tguo;
		//综合率
		private String zonghe;
		//所带学员数
		private int stunum;
		//评价时间
		private String date;
		//评价的学员的默认头像
		private String default_file;
		//价格
		private double price;
		//市场价
		private double market_price;
		private int count_stu;

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {

			this.address = address;
		}

		private List<CommentEntity> comment ;

		public List<CommentEntity> getComment() {
			return comment;
		}

		public void setComment(List<CommentEntity> comment) {
			this.comment = comment;
		}

		public String getWait() {
			return wait;
		}

		public String getTeach() {
			return teach;
		}

		public int getPraise() {
			return praise;
		}

		public int getPass() {
			return pass;
		}

		public String getTguo() {
			return tguo;
		}

		public void setTguo(String tguo) {
			this.tguo = tguo;
		}

		public String getHaopin() {
			return haopin;
		}

		public void setHaopin(String haopin) {
			this.haopin = haopin;
		}

		public int getCount_stu() {
			return count_stu;
		}

		public int getCid() {
			return cid;
		}

		public String getChexing() {
			return chexing;
		}

		public String getFaddress() {
			return faddress;
		}

		public String getCoachname() {
			return coachname;
		}

		public String getFile() {
			return file;
		}

		public String getNumber_plates() {
			return number_plates;
		}

		public String getClass_type() {
			return class_type;
		}

		public int getCredit() {
			return credit;
		}

		public String getZonghe() {
			return zonghe;
		}

		public int getStunum() {
			return stunum;
		}

		public String getDate() {
			return date;
		}

		public String getDefault_file() {
			return default_file;
		}

		public double getPrice() {
			return price;
		}

		public double getMarket_price() {
			return market_price;
		}

	}
}
