package com.garahe.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "zones")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int totalCapacity;
    private String type;  // "Indoor" or "Outdoor"
    private String mallName;
    private Integer hourlyRate;  // Optional: Price per hour (e.g., 60 for ₱60/hr)
    private Integer availableSlots;  // Optional: Real-time available slots

    // Default constructor (required by JPA)
    public Zone() {}

    // Constructor with all fields
    public Zone(String name, int totalCapacity, String type, String mallName, Integer hourlyRate, Integer availableSlots) {
        this.name = name;
        this.totalCapacity = totalCapacity;
        this.type = type;
        this.mallName = mallName;
        this.hourlyRate = hourlyRate;
        this.availableSlots = availableSlots;
    }

    // Getters and Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }

    public int getTotalCapacity() { 
        return totalCapacity; 
    }
    
    public void setTotalCapacity(int totalCapacity) { 
        this.totalCapacity = totalCapacity; 
    }

    public String getType() { 
        return type; 
    }
    
    public void setType(String type) { 
        this.type = type; 
    }

    public String getMallName() { 
        return mallName; 
    }
    
    public void setMallName(String mallName) { 
        this.mallName = mallName; 
    }

    public Integer getHourlyRate() { 
        return hourlyRate; 
    }
    
    public void setHourlyRate(Integer hourlyRate) { 
        this.hourlyRate = hourlyRate; 
    }

    public Integer getAvailableSlots() { 
        return availableSlots; 
    }
    
    public void setAvailableSlots(Integer availableSlots) { 
        this.availableSlots = availableSlots; 
    }

    // Helper method to calculate percentage available
    public int getPercentageAvailable() {
        if (availableSlots == null || totalCapacity == 0) {
            return 0;
        }
        return (availableSlots * 100) / totalCapacity;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalCapacity=" + totalCapacity +
                ", type='" + type + '\'' +
                ", mallName='" + mallName + '\'' +
                ", hourlyRate=" + hourlyRate +
                ", availableSlots=" + availableSlots +
                '}';
    }
}