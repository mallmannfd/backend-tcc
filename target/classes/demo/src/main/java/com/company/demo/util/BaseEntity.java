package com.company.demo.util;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class BaseEntity<I extends Serializable> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2164745858816155953L;

    protected boolean deleted;

    public abstract I getId();

    public abstract void setId(I id);
}
