package com.spectra.sports;

import com.spectra.sports.entity.Role;
import com.spectra.sports.entity.RoleType;
import com.spectra.sports.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@EnableScheduling
@SpringBootApplication
public class SpectraSportsApplication implements CommandLineRunner {
	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpectraSportsApplication.class, args);
	}

	public void run(String... args) {
		this.roleRepository.saveAll( List.of(
			new Role(1L, RoleType.ACADEMY, (byte)1),
			new Role(2L, RoleType.COMMUNITY, (byte)2),
			new Role(3L, RoleType.MENTOR, (byte)3),
			new Role(4L, RoleType.COACH, (byte)4),
			new Role(5L, RoleType.USER, (byte)5)
		));
	}
}
