package com.ideyatech.opentides.um.entity;

import com.couchbase.client.java.repository.annotation.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.ArrayList;

/**
 * Created by Gino on 8/30/2016.
 */
@Document
@Entity
@Table(name = "AUTHORITY")
public class Authority extends BaseUMEntity {

    /**
     * The key of the authority
     */
    @Field
    @Column(name = "KEY_", unique = true)
    private String key;

    /**
     * The level of authority
     */
    @Field
    @OrderBy
    @Column(name = "LEVEL")
    private String level;

    /**
     * The title of the authority
     */
    @Field
    @Column(name = "TITLE")
    private String title;
    
    /**
     * The parent authority used for tree hierarchy
     */
    @Field
    @Column(name = "PARENT")
    private String parent;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
     * Helper class for List of Authority
     */
    public static class Authorities extends ArrayList<Authority> {

    }

}
