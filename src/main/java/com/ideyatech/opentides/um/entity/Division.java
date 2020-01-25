package com.ideyatech.opentides.um.entity;

import com.couchbase.client.java.repository.annotation.Field;
import com.ideyatech.opentides.core.annotation.Auditable;

import org.hibernate.annotations.Type;
import org.springframework.data.couchbase.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * @author jpereira on 3/15/2017.
 */
@Document
@Auditable
@Entity
@Table(name = "DIVISION")
public class Division extends BaseUMEntity{
    @Column
    @Field
    private String name;

    @Column
    @Field
    private String description;

	@Column
	@Field
	private Long numberValue;

    @Column(name = "KEY_", unique = true)
    @Field
    private String key;

    @Field
	@org.hibernate.annotations.Type(type = "json")
    @Column(columnDefinition = "json")
    private Division parent;
    
    @Column
    @Enumerated(EnumType.STRING)
    @Field
    private Type type;
    
    private enum Type {
    	DEPARTMENT,
    	SECTION
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public Long getNumberValue() {
		return numberValue;
	}

	public void setNumberValue(Long numberValue) {
		this.numberValue = numberValue;
	}

	public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Division getParent() {
        return parent;
    }

    public void setParent(Division parent) {
        this.parent = parent;
    }

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((numberValue == null) ? 0 : numberValue.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Division other = (Division) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (numberValue == null) {
			if (other.numberValue != null)
				return false;
		} else if (!numberValue.equals(other.numberValue))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
}
