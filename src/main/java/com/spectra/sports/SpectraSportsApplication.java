package com.spectra.sports;

import com.spectra.sports.entity.Role;
import com.spectra.sports.entity.RoleType;
import com.spectra.sports.repository.RoleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

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
				new Role(2L, RoleType.MENTOR, (byte)2),
				new Role(3L, RoleType.USER, (byte)3)
			));
		}
}
