package com.carenet.dto;

public class AuthResponse {
    private String token;
    private String role;
    private Long   id;
    private String name;

    public AuthResponse() {}
    public AuthResponse(String token, String role, Long id, String name) {
        this.token = token; this.role = role; this.id = id; this.name = name;
    }

    public String getToken() { return token; }
    public String getRole()  { return role; }
    public Long   getId()    { return id; }
    public String getName()  { return name; }
    public void setToken(String v) { this.token = v; }
    public void setRole(String v)  { this.role = v; }
    public void setId(Long v)      { this.id = v; }
    public void setName(String v)  { this.name = v; }
}
