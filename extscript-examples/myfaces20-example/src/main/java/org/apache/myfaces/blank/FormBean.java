/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.myfaces.blank;


import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.script.ScriptException;
import java.io.File;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 */

/**
 * @author Ramo Karahasan
 *
 */
@ManagedBean
@SessionScoped
public class FormBean {
	private String company = "Maritim Rhein-Main Hotel Darmstadt";
	private String branche = "Hotellerie";
	private String position = "Hotelier";
	private String surname = "Mustermann";
	private String firstname = "Max";
	private String name;
	private String street = "Am Kavalleriesand 6";
	private String zipCode = "64293";
	private String city = "Darmstadt";
	private String country = "Deutschland";
	private String phonenumber = "+49 6151 303-0";
	private String faxnumber = "+49 6151 303-111";
	private Boolean rendered = Boolean.FALSE;

	public String getCompany() {

		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getBranche() {

		return branche;
	}
	public void setBranche(String branche) {
		this.branche = branche;
	}
	public String getSurname() {

		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getFirstname() {

		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getStreet() {

		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getZipCode() {

		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getCity() {

		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {

		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPosition() {

		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getName() {
		//name = firstname + " " + surname;
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhonenumber() {

		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getFaxnumber() {

		return faxnumber;
	}
	public void setFaxnumber(String faxnumber) {
		this.faxnumber = faxnumber;
	}




	public Boolean getRendered() {

		return rendered;
	}
	public void setRendered(Boolean rendered) {
		this.rendered = rendered;
	}

	public String changeRendered(){
		if(this.rendered == false){
			this.rendered = true;
		}else{
			this.rendered = false;
		}
		return null;
	}

}
