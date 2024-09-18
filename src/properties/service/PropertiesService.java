package properties.service;

import db.DB;
import properties.model.Properties;
import properties.repository.PropertiesRepository;
import properties.service.exception.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class PropertiesService {
    PropertiesRepository propertiesRepository;

    public PropertiesService() throws SQLException {
        Connection connection = DB.connect();
        this.propertiesRepository = new PropertiesRepository(connection);
    }

    public List<Properties> getAll() {
        return this.propertiesRepository.getAll();
    }

    public List<Properties> getListBy(String name) {
        return this.propertiesRepository.findListBy(name);
    }

    public List<Properties> getListByLocation(String name) {
        return this.propertiesRepository.findListByLocation(name);
    }

    public Properties getDetailBy(int id) {
        return this.propertiesRepository.findDetailBy(id).orElseThrow(PropertiesNotFoundException::new);
    }

    public Properties getById(int id) {
        return this.propertiesRepository.findBy(id).orElseThrow(PropertiesNotFoundException::new);
    }

    public Properties getByName(String name) {
        return this.propertiesRepository.findBy(name).orElseThrow(PropertiesNotFoundException::new);
    }

    public int create(Properties properties) {
        int existingProperties = this.propertiesRepository.dataAvailabilityAdd(properties);
        if (existingProperties > 0) {
            throw new PropertiesHasBeenAddedAndActiveException();
        }

        int newUser = this.propertiesRepository.add(properties);
        if (newUser <= 0) {
            throw new FailedToAddPropertiesException();
        }
        return newUser;
    }

    public int update(Properties properties, Properties newProperties) {
        Properties existingProperties = this.getById(properties.getId());
        if (existingProperties == null) {
            throw new PropertiesNotFoundException();
        }

        Properties existingPropertiesCompare = new Properties(existingProperties.getName(), existingProperties.getLocation(), existingProperties.getDescription());
        boolean isChange = !newProperties.getLocation().equals(existingPropertiesCompare.getLocation()) ||
                           !newProperties.getDescription().equals(existingPropertiesCompare.getDescription());

        if (!isChange) {
            throw new PropertiesDataHasNotChangedException();
        }

        newProperties.setId(properties.getId());
        int result = this.propertiesRepository.update(newProperties);

        if (result <= 0) {
            throw new FailedToUpdatePropertiesException();
        }
        return result;
    }

    public void deleteBy(int id) {
        int existingUser = this.propertiesRepository.dataAvailabilityDelete(this.getById(id));
        if (existingUser > 0) {
            throw new LocationStillHaveListBookingException();
        }

        int result = this.propertiesRepository.delete(id);
        if (result <= 0) {
            throw new FailedToRemovePropertiesException();
        }
    }

    public List<Properties> getAllAvailableProperties(java.util.Date checkIn, java.util.Date checkOut) {
        return this.propertiesRepository.getAllAvailableProperties(checkIn, checkOut);
    }
}