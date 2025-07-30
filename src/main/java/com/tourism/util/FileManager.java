package com.tourism.util;

import com.tourism.model.User;
import com.tourism.model.Attraction;
import com.tourism.model.Booking;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String TOURISTS_FILE = "tourists.txt";
    private static final String GUIDES_FILE = "guides.txt";
    private static final String ATTRACTIONS_FILE = "attractions.txt";
    private static final String BOOKINGS_FILE = "bookings.txt";
    
    public static void initializeFiles() {
        createFileIfNotExists(TOURISTS_FILE);
        createFileIfNotExists(GUIDES_FILE);
        createFileIfNotExists(ATTRACTIONS_FILE);
        createFileIfNotExists(BOOKINGS_FILE);
        
        // Initialize with sample attractions if file is empty
        if (getAttractions().isEmpty()) {
            initializeSampleAttractions();
        }
    }
    
    private static void createFileIfNotExists(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void initializeSampleAttractions() {
        List<Attraction> attractions = new ArrayList<>();
        attractions.add(new Attraction("Everest Base Camp", "High", "Hard", 1000));
        attractions.add(new Attraction("Annapurna Circuit", "High", "Medium", 800));
        attractions.add(new Attraction("Chitwan National Park", "Low", "Easy", 300));
        attractions.add(new Attraction("Pokhara Lake", "Low", "Easy", 200));
        attractions.add(new Attraction("Langtang Valley", "High", "Medium", 600));
        
        for (Attraction attraction : attractions) {
            saveAttraction(attraction);
        }
    }
    
    public static void saveUser(User user) {
        String filename = user.getRole().equals("Tourist") ? TOURISTS_FILE : GUIDES_FILE;
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(user.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static User authenticateUser(String username, String password, String role) {
        String filename = role.equals("Admin") ? null : 
                         (role.equals("Tourist") ? TOURISTS_FILE : GUIDES_FILE);
        
        // Admin authentication - Changed to "admin"
        if (role.equals("Admin")) {
            if (username.equals("admin") && password.equals("123")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setFullName("Administrator");
                admin.setRole("Admin");
                return admin;
            }
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            User user = new User();
            boolean userFound = false;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username: ")) {
                    user = new User();
                    user.setUsername(line.substring(10));
                    userFound = user.getUsername().equals(username);
                } else if (line.startsWith("Password: ") && userFound) {
                    String storedPassword = line.substring(10);
                    if (storedPassword.equals(password)) { // Direct password comparison - no hashing
                        // Continue reading user details
                        while ((line = reader.readLine()) != null && !line.equals("------------------------")) {
                            if (line.startsWith("Full Name: ")) {
                                user.setFullName(line.substring(11));
                            } else if (line.startsWith("Email: ")) {
                                user.setEmail(line.substring(7));
                            } else if (line.startsWith("Phone: ")) {
                                user.setPhone(line.substring(7));
                            } else if (line.startsWith("Role: ")) {
                                user.setRole(line.substring(6));
                            } else if (line.startsWith("Languages: ")) {
                                user.setLanguages(line.substring(11));
                            } else if (line.startsWith("Experience: ")) {
                                user.setExperience(line.substring(12));
                            }
                        }
                        return user;
                    } else {
                        userFound = false;
                    }
                } else if (line.equals("------------------------")) {
                    userFound = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static List<Attraction> getAttractions() {
        List<Attraction> attractions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ATTRACTIONS_FILE))) {
            String line;
            Attraction attraction = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Name: ")) {
                    attraction = new Attraction();
                    attraction.setName(line.substring(6));
                } else if (line.startsWith("Altitude: ") && attraction != null) {
                    attraction.setAltitude(line.substring(10));
                } else if (line.startsWith("Difficulty: ") && attraction != null) {
                    attraction.setDifficulty(line.substring(12));
                } else if (line.startsWith("Base Price: $") && attraction != null) {
                    attraction.setBasePrice(Double.parseDouble(line.substring(13)));
                } else if (line.equals("------------------------") && attraction != null) {
                    attractions.add(attraction);
                    attraction = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return attractions;
    }
    
    public static void saveAttraction(Attraction attraction) {
        try (FileWriter writer = new FileWriter(ATTRACTIONS_FILE, true)) {
            writer.write(attraction.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveBooking(Booking booking) {
        try (FileWriter writer = new FileWriter(BOOKINGS_FILE, true)) {
            writer.write(booking.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            Booking booking = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Booking ID: ")) {
                    booking = new Booking();
                    booking.setBookingId(line.substring(12));
                } else if (line.startsWith("Tourist: ") && booking != null) {
                    booking.setTourist(line.substring(9));
                } else if (line.startsWith("Attraction: ") && booking != null) {
                    booking.setAttraction(line.substring(12));
                } else if (line.startsWith("Date: ") && booking != null) {
                    booking.setDate(LocalDate.parse(line.substring(6)));
                } else if (line.startsWith("Difficulty: ") && booking != null) {
                    booking.setDifficulty(line.substring(12));
                } else if (line.startsWith("Price: $") && booking != null) {
                    String priceStr = line.substring(8);
                    if (priceStr.contains("(")) {
                        priceStr = priceStr.substring(0, priceStr.indexOf("(")).trim();
                        booking.setFestivalDiscount(true);
                    }
                    booking.setPrice(Double.parseDouble(priceStr));
                } else if (line.startsWith("Status: ") && booking != null) {
                    booking.setStatus(line.substring(8));
                } else if (line.startsWith("Guide: ") && booking != null) {
                    booking.setGuide(line.substring(7));
                } else if (line.equals("------------------------") && booking != null) {
                    bookings.add(booking);
                    booking = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public static List<Booking> getUserBookings(String username) {
        List<Booking> userBookings = new ArrayList<>();
        List<Booking> allBookings = getBookings();
        
        for (Booking booking : allBookings) {
            if (booking.getTourist().equals(username)) {
                userBookings.add(booking);
            }
        }
        return userBookings;
    }
    
    public static String generateBookingId() {
        List<Booking> bookings = getBookings();
        return String.format("%03d", bookings.size() + 1);
    }
}
