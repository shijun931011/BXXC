package com.jgkj.bxxc.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 获取教练所有信息
 */
public class CoachDetailAction implements Serializable {
	//返回码
	private int code;
	//result
	private List<Result> result;
	//resultarr
	private List<Resultarr> resultarr;
	//返回信息
	private String reason;

	public String getReason() {
		return reason;
	}
	public int getCode() {
		return code;
	}
	public List<Result> getResult() {
		return result;
	}
	public List<Resultarr> getResultarr(){
		return this.resultarr;
	}
	public class Resultarr
	{
		private String pttid;

		private String name;

		private String file;

		private String ptschool;

		private String ptzonghe;

		private String ptpraise;

		private String ptpass;

		private String totalnum;

		public void setPttid(String pttid){
			this.pttid = pttid;
		}
		public String getPttid(){
			return this.pttid;
		}
		public void setName(String name){
			this.name = name;
		}
		public String getName(){
			return this.name;
		}
		public void setFile(String file){
			this.file = file;
		}
		public String getFile(){
			return this.file;
		}
		public void setPtschool(String ptschool){
			this.ptschool = ptschool;
		}
		public String getPtschool(){
			return this.ptschool;
		}
		public void setPtzonghe(String ptzonghe){
			this.ptzonghe = ptzonghe;
		}
		public String getPtzonghe(){
			return this.ptzonghe;
		}
		public void setPtpraise(String ptpraise){
			this.ptpraise = ptpraise;
		}
		public String getPtpraise(){
			return this.ptpraise;
		}
		public void setPtpass(String ptpass){
			this.ptpass = ptpass;
		}
		public String getPtpass(){
			return this.ptpass;
		}
		public void setTotalnum(String totalnum){
			this.totalnum = totalnum;
		}
		public String getTotalnum(){
			return this.totalnum;
		}
	}
	public class Result{
		//教练id
		private String cid;
		private String chexing;
		//校区
		private String faddress;
		//教练姓名
		private String coachname;
		//教练头像
		private String file;
		//教练科目信息
		private String class_type;
		//班型
		private String class_class;
		//车牌号
		private String number_plates;
		//信誉
		private String credit;
		//好评率
		private String praise;
		//通过率
		private String pass;
		//综合率
		private String zonghe;
		//所带学员数
		private String stunum;

		private String  clastatus;

		//教练中心的ID
		private String tid;
		//教练中心团队名称
		private String name;
		private String school;
		private String totalnum;
		public String getTid() {
			return tid;
		}
		public String getName() {
			return name;
		}
		public String getSchool() {
			return school;
		}
		public String getTotalnum() {
			return totalnum;
		}
		public String getClastatus() {
			return clastatus;
		}
		public void setClastatus(String clastatus) {
			this.clastatus = clastatus;
		}
		public String getClass_class() {
			return class_class;
		}
		public String getChexing() {
			return chexing;
		}
		public String getCid() {
			return cid;
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

		public String getClass_type() {
			return class_type;
		}

		public String getNumber_plates() {
			return number_plates;
		}

		public String getCredit() {
			return credit;
		}

		public String getPass() {
			return pass;
		}

		public String getPraise() {
			return praise;
		}

		public String getZonghe() {
			return zonghe;
		}

		public String getStunum() {
			return stunum;
		}

	}
}
