package com.example.motioner.infrastructure;

import com.example.motioner.domain.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, UUID> {

    public List<Alarm> findAllByOrderByTimestampDesc();

    public Optional<Alarm> findAlarmByFilename(String filename);
}
