package com.tourism.model;

public class User {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String languages;
    private String experience;

    public User() {}

    public User(String username, String password, String fullName, String email, String phone, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public User(String username, String password, String fullName, String email, String phone, String role, String languages, String experience) {
        this(username, password, fullName, email, phone, role);
        this.languages = languages;
        this.experience = experience;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Username: ").append(username).append("\n");
        sb.append("Password: ").append(password).append("\n");
        sb.append("Full Name: ").append(fullName).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Phone: ").append(phone).append("\n");
        sb.append("Role: ").append(role).append("\n");
        if (languages != null) sb.append("Languages: ").append(languages).append("\n");
        if (experience != null) sb.append("Experience: ").append(experience).append("\n");
        sb.append("------------------------\n");
        return sb.toString();
    }
}
