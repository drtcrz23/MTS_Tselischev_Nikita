package com.example.bookService.data.User;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;

import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "users")
public class Users {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "username")
  private String username;
  @Column(name = "password")
  private String password;

  @JdbcTypeCode(SqlTypes.JSON)
  private Set<Role> roles;

  public enum Role {
    STUDENT, ADMIN
  }
  protected Users() {}
  public Users(String username, String password, Set<Role> roles) {
    this.username = username;
    this.password = password;
    this.roles = roles;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
}
