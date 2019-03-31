package com.mateuszbuczek.pm.services;

import com.mateuszbuczek.pm.domain.Backlog;
import com.mateuszbuczek.pm.domain.Project;
import com.mateuszbuczek.pm.domain.User;
import com.mateuszbuczek.pm.exceptions.ProjectIdentifierException;
import com.mateuszbuczek.pm.exceptions.ProjectNotFoundException;
import com.mateuszbuczek.pm.repositories.BacklogRepository;
import com.mateuszbuczek.pm.repositories.ProjectRepository;
import com.mateuszbuczek.pm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdate(Project project, String username) {

        if(project.getId() != null) {
            Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());

            if(existingProject != null && (!existingProject.getProjectLeader().equals(username))) {
                throw new ProjectNotFoundException("Project not found in your account");
            } else if(existingProject == null) {
                throw new ProjectNotFoundException("Project with ID: '" + project.getProjectIdentifier()
                        + "' cannot be updated because it doesn't exist");
            }
        }

        try {
            User user = userRepository.findByUsername(username);

            project.setUser(user);
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());

            // create object
            if(project.getId() == null) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            }

            // update object
            if(project.getId() != null) {
                project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
            }

            return projectRepository.save(project);
        } catch (Exception e) {
            throw new ProjectIdentifierException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exits");
        }
    }

    public Project findProjectByIdentifier(String projectIdentifier, String username) {
        Project project = projectRepository.findByProjectIdentifier(projectIdentifier.toUpperCase());

        if(project == null)
            throw new ProjectIdentifierException("Project ID '" + projectIdentifier + "' doest not exits");

        if(!project.getProjectLeader().equals(username)) {
            throw new ProjectNotFoundException("Project not found in your account");
        }

        return project;
    }

    public Iterable<Project> findAllProjects(String username) {
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectIdentifier,String username) {


        projectRepository.delete(findProjectByIdentifier(projectIdentifier, username));
    }
}
