package com.mateuszbuczek.pm.services;

import com.mateuszbuczek.pm.domain.Backlog;
import com.mateuszbuczek.pm.domain.Project;
import com.mateuszbuczek.pm.domain.ProjectTask;
import com.mateuszbuczek.pm.exceptions.ProjectNotFoundException;
import com.mateuszbuczek.pm.repositories.BacklogRepository;
import com.mateuszbuczek.pm.repositories.ProjectRepository;
import com.mateuszbuczek.pm.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {


            // PTs to be added to a specific project, project != null, BL exists
            Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();//backlogRepository.findByProjectIdentifier(projectIdentifier);
            // set the BL to projecttask
            projectTask.setBacklog(backlog);
            // we want our project sequence to be like this IDPRO-1 IDPRO-2
            Integer BacklogSequence = backlog.getPTSequence();
            // update the BL sequence
            BacklogSequence++;

            backlog.setPTSequence(BacklogSequence);

            // add sequence to project task
            projectTask.setProjectSequence(projectIdentifier + "-" + BacklogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            // initial priority when priority null
            if(projectTask.getPriority() == null || projectTask.getPriority() == 0) {
                projectTask.setPriority(3);
            }
            // initial status when status is null
            if(projectTask.getStatus() == "" || projectTask.getStatus()== null) {
                projectTask.setStatus("TO_DO");
            }

            return projectTaskRepository.save(projectTask);
    }


    public Iterable<ProjectTask> findBacklogById(String id, String username) {

        Project project = projectService.findProjectByIdentifier(id, username);

        if(project==null) {
            throw new ProjectNotFoundException("Project with ID: '" + id + "' doest not exist");
        }

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id, String username) {

        // we are searching on the right backlog
        projectService.findProjectByIdentifier(backlog_id, username);


        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if(projectTask == null) {
            throw new ProjectNotFoundException("Project task with ID: '" + pt_id + "' doest not exist");
        }

        if(!projectTask.getProjectIdentifier().equals(backlog_id)) {
            throw new ProjectNotFoundException("Project task with ID: '" + pt_id + "' doest not exist in project: " + backlog_id);
        }

        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask,String backlog_id, String pt_id, String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);

        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id, String pt_id, String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);

//        Backlog backlog = projectTask.getBacklog();
//        List<ProjectTask> pts = backlog.getProjectTasks();
//        pts.remove(projectTask);
//        backlogRepository.save(backlog);

        projectTaskRepository.delete(projectTask);
    }
}
