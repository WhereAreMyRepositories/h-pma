package com.mateuszbuczek.pm.repositories;

import com.mateuszbuczek.pm.domain.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    Project findByProjectIdentifier(String projectIdentifier);

    @Override
    Iterable<Project> findAll();

    Iterable<Project> findAllByProjectLeader(String username);
}
