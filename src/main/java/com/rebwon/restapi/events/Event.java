package com.rebwon.restapi.events;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rebwon.restapi.accounts.Account;
import com.rebwon.restapi.accounts.AccountSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String description;
	private LocalDateTime beginEnrollmentDateTime;
	private LocalDateTime closeEnrollmentDateTime;
	private LocalDateTime beginEventDateTime;
	private LocalDateTime endEventDateTime;
	private String location;
	private int basePrice;
	private int maxPrice;
	private int limitOfEnrollment;
	private boolean offline;
	private boolean free;
	@Enumerated(EnumType.STRING)
	private EventStatus eventStatus = EventStatus.DRAFT;
	@ManyToOne
	@JsonSerialize(using = AccountSerializer.class)
	private Account manager;

	public void update() {
		if(this.basePrice == 0 && this.maxPrice == 0) {
			this.free = true;
		} else{
			this.free = false;
		}

		if(this.location == null || this.location.isBlank()) {
			this.offline = false;
		} else{
			this.offline = true;
		}
	}
}
