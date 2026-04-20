package org.sang.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private String image;

	private Long clinicId;

}
