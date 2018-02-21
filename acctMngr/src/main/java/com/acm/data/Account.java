package com.acm.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "ACCOUNTS")
public class Account {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
    @NotNull
    @Size(min=2, max=255)
	private String fName;
    @NotNull
    @Size(min=2, max=255)
	private String lName;
    @NotNull
    @Size(min=5, max=255)
    @Pattern(regexp = ".+@.+\\..+")
	private String email;
    @NotNull
    @Past
    @Type(type="date")
    @DateTimeFormat(pattern="dd/MM/yyyy")
	private Date birth;
	
	private Account () {}
	
	public Account (String fName, String lName, String email, Date birth) {
		this.fName = fName;
		this.lName = lName;
		this.email = email;
		this.birth = birth;
	}
	
	public Long getId() {
		return id;
	}

	public String getfName() {
		return fName;
	}

	public String getlName() {
		return lName;
	}

	public String getEmail() {
		return email;
	}

	public Date getBirth() {
		return birth;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}
	
	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this,  that, new String[] {"id", "email"});
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, new String[] {"id", "email"});
	}

}
