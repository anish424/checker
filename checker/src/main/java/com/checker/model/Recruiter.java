package com.checker.model;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "RECRUITER")
@ToString
public class Recruiter implements UserDetails {

	private static final long serialVersionUID = 4654459230304225197L;

	@Id
	@Column(name = "ID")
	private int id;

	@Column(name = "USERNAME", nullable = false)
	private String username;

	@Column(name = "PASSWORD", nullable = false)
	private String password;
	
	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "RECRUITER_CREATE_DATE", nullable = false)
	private Timestamp createDate;

	@Column(name = "RECRUITER_LAST_LOGIN_DATE")
	private Timestamp lastLoginDate;

	@Column(name = "RECRUITER_ACTIVE")
	private boolean active;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public boolean isAccountNonExpired() {
		return active;
	}

	@Override
	public boolean isAccountNonLocked() {
		return isAccountNonExpired();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isAccountNonLocked();
	}

	@Override
	public boolean isEnabled() {
		return isCredentialsNonExpired();
	}
}
