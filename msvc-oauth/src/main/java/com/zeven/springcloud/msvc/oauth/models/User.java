package com.zeven.springcloud.msvc.oauth.models;

import java.util.List;

public class User {
    private Long id;

    private String userName;

    private String password;

    private boolean admin;

    private Boolean enabled;

    private String email;

    private List<Role> roles;

    public User() {}

    public User(Long id, String userName, String password, Boolean enabled, String email) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean isEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Role> getRoles() {return roles;}

    public void setRoles(List<Role> roles) {this.roles = roles;}

    public boolean isAdmin() {return admin;}

    public void setAdmin(boolean admin) {this.admin = admin;}
}
