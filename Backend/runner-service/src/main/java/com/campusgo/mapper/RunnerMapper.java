package com.campusgo.mapper;

import com.campusgo.domain.Runner;
import com.campusgo.enums.RunnerStatus;
import com.campusgo.enums.VehicleType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RunnerMapper {

    int insert(Runner r);                  // useGeneratedKeys=true

    Optional<Runner> findById(@Param("id") Long id);

    Optional<Runner> findByUsername(@Param("username") String username);

    List<Runner> findAll();

    List<Runner> findByStatus(@Param("status") RunnerStatus status);

    Optional<Runner> findAnyAvailable();   // status = AVAILABLE

    int updateBasic(@Param("id") Long id,
                    @Param("phone") String phone,
                    @Param("vehicleType") VehicleType vehicleType);

    int updateStatus(@Param("id") Long id,
                     @Param("status") RunnerStatus status);

    int updateLocation(@Param("id") Long id,
                       @Param("lat") Double lat,
                       @Param("lng") Double lng);

    int deleteById(@Param("id") Long id);
}
