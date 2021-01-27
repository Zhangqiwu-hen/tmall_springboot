package com.hen.tmall_springboot.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String name;
    private String password;
    private String salt;

    @Transient
    private String anonymousName;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return this.salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAnonymousName() {
        if (null != this.anonymousName) {
            return this.anonymousName;
        } else {
            if (null == this.name) {
                this.anonymousName = null;
            } else if (this.name.length() <= 1) {
                this.anonymousName = "*";
            } else if (this.name.length() == 2) {
                this.anonymousName = this.name.substring(0, 1) + "*";
            } else {
                char[] cs = this.name.toCharArray();

                for (int i = 1; i < cs.length - 1; ++i) {
                    cs[i] = '*';
                }

                this.anonymousName = new String(cs);
            }

            return this.anonymousName;
        }
    }

    public void setAnonymousName(String anonymousName) {
        this.anonymousName = anonymousName;
    }
}