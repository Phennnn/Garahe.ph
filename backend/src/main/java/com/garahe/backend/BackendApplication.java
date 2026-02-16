package com.garahe.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.garahe.backend.entity.Zone;
import com.garahe.backend.repository.ZoneRepository;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	
	/*
	@Bean
    CommandLineRunner init(ZoneRepository repository) {
        return args -> {
            repository.deleteAll(); 

            // MATCHING HTML NAME: "Divisoria Mall Complex"
            createZone(repository, "168 Mall Basement", 150, "INDOOR", "Divisoria Mall Complex");
            createZone(repository, "Tutuban Open Cluster", 80, "OUTDOOR", "Divisoria Mall Complex");

            // MATCHING HTML NAME: "Quiapo Market Area"
            createZone(repository, "Quinta Market Parking", 60, "INDOOR", "Quiapo Market Area");
            createZone(repository, "Raon Street Pay Parking", 40, "OUTDOOR", "Quiapo Market Area");

            // MATCHING HTML NAME: "Baclaran Church Area"
            createZone(repository, "Redemptorist Church", 120, "OUTDOOR", "Baclaran Church Area");
            createZone(repository, "LRT Baclaran Terminal", 50, "OUTDOOR", "Baclaran Church Area");

            // MATCHING HTML NAME: "Greenhills Shopping Center"
            createZone(repository, "Promenade Basement", 200, "INDOOR", "Greenhills Shopping Center");
            createZone(repository, "O-Square Open Lot", 150, "OUTDOOR", "Greenhills Shopping Center");

            // MATCHING HTML NAME: "Binondo Chinatown" (This was already matching)
            createZone(repository, "Lucky Chinatown Annex", 180, "INDOOR", "Binondo Chinatown");
            createZone(repository, "Ongpin Street Parking", 30, "OUTDOOR", "Binondo Chinatown");

            // MATCHING HTML NAME: "SM Mall of Asia" (This was already matching)
            createZone(repository, "South Wing Parking", 200, "INDOOR", "SM Mall of Asia");
            createZone(repository, "MAAX Open Lot", 150, "OUTDOOR", "SM Mall of Asia");

            System.out.println("✅ Database Updated: Names now match the Website!");
        };
    }
	*/

	// Helper function (kept for reference)
	private void createZone(ZoneRepository repo, String name, int cap, String type, String mall) {
		Zone z = new Zone();
		z.setName(name);
		z.setTotalCapacity(cap);
		z.setType(type);
		z.setMallName(mall); 
		repo.save(z);
	}
}