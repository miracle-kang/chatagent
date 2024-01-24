package com.miraclekang.chatgpt.common.model;

import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

@MappedSuperclass
public class AggregateRoot extends BaseEntity implements Serializable {

}
