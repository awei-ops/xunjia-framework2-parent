package com.xunjia.framework.usermanage.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class LoggingEventExceptionKey implements Serializable {

	private static final long serialVersionUID = -3729063928993282608L;

	private long eventId;
	
	private short i;
}
