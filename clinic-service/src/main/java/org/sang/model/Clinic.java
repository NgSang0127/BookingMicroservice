package org.sang.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "clinics")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Clinic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@ElementCollection
	private List<String> images;

	@Column(nullable = false, length = 255)
	private String address;

	@Column(nullable = false, length = 15)
	private String phoneNumber;

	@Column(nullable = false, length = 255)
	private String email;

	@Column(nullable = false, length = 50)
	private String city;

	@Column(nullable = false, length = 50)
	private boolean isOpen;

	@Column(nullable = false)
	private boolean homeService;

	@Column(nullable = false)
	private boolean active;

	@Column(nullable = false)
	private Long ownerId;

	@Column(nullable = false)
	private LocalTime openTime;

	@Column(nullable = false)
	private LocalTime closeTime;

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
				: o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
				: this.getClass();
		if (thisEffectiveClass != oEffectiveClass) {
			return false;
		}
		Clinic clinic = (Clinic) o;
		return getId() != null && Objects.equals(getId(), clinic.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
		                                        .getPersistentClass()
												.hashCode() : getClass().hashCode();
	}
}
